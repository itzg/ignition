package me.itzg.ignition.services;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import me.itzg.ignition.IgnitionConstants;
import me.itzg.ignition.etcd.EtcdError;
import me.itzg.ignition.etcd.EtcdException;
import me.itzg.ignition.etcd.NoUsableMachinesException;
import me.itzg.ignition.etcd.keys.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Geoff Bourne
 * @since 6/17/2015
 */
@Service
public class EtcdPathService {
    private static Logger LOG = LoggerFactory.getLogger(EtcdPathService.class);

    private static final HttpHeaders HEADERS_FORM_URLENCODED = new HttpHeaders();
    static {
        HEADERS_FORM_URLENCODED.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    public static final String SEP = "/";

    private Joiner pathJoiner = Joiner.on("/");

    @Autowired
    private EtcdProperties etcdProperties;

    private RestTemplate restTemplate = new RestTemplate();
    private int machineCount;
    private Iterator<URI> machines;
    private URI currentMachine;

    @PostConstruct
    public void init() {
        machineCount = etcdProperties.getMachines().length;
        machines = FluentIterable.from(Arrays.asList(etcdProperties.getMachines())).cycle().iterator();
        currentMachine = machines.next();
    }

    public String build(String... subParts) {
        StringBuilder sb = new StringBuilder(IgnitionConstants.ETCD_BASE).append(SEP);

        pathJoiner.appendTo(sb, subParts);

        return sb.toString();
    }

    public void delete(final String... path) throws IOException, EtcdException {
        access(new Accessor<Void>() {
            @Override
            public Void access(URI uri) throws RestClientException, IOException {
                final UriComponentsBuilder uriBuilder = createKeysUriBuilder(uri);
                uriBuilder.path(pathJoiner.join(path));
                final URI builtUri = uriBuilder.build().toUri();

                restTemplate.delete(builtUri);

                return null;
            }
        });
    }

    public void ensureDir(final String... parts) throws IOException, EtcdException {
        access(new Accessor<Void>() {
            @Override
            public Void access(URI uri) throws RestClientException, IOException {
                final UriComponentsBuilder uriComponentsBuilder = createKeysUriBuilder(uri);

                boolean makeTheRest = false;

                for (String part : parts) {
                    if (!part.startsWith(SEP)) {
                        part = SEP + part;
                    }
                    uriComponentsBuilder.path(part);

                    final URI partUri = uriComponentsBuilder.build().toUri();

                    if (makeTheRest) {
                        createDir(partUri);
                    } else {
                        try {
                            final ResponseEntity<Response> getResponse = restTemplate.getForEntity(partUri, Response.class);
                        } catch (HttpClientErrorException e) {
                            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                                makeTheRest = true;
                                createDir(partUri);
                            }
                        }
                    }

                }
                return null;
            }
        });
    }

    public void put(final String value, final String... path) throws IOException, EtcdException {
        access(new Accessor<Void>() {
            @Override
            public Void access(URI uri) throws RestClientException, IOException {
                final UriComponentsBuilder uriBuilder = createKeysUriBuilder(uri);
                uriBuilder.path(pathJoiner.join(path));
                final URI builtUri = uriBuilder.build().toUri();

                final ResponseEntity<Response> response = doPut(builtUri,
                        "value", value);

                return null;
            }
        });
    }

    /**
     *
     * @param value the value of the key
     * @param path path to the key to put
     * @return true if the key did not previously exist
     */
    public boolean putIfNotExists(final String value, final String... path) throws IOException, EtcdException {
        return access(new Accessor<Boolean>() {
            @Override
            public Boolean access(URI uri) throws RestClientException, IOException {
                final UriComponentsBuilder uriBuilder = createKeysUriBuilder(uri);
                uriBuilder.path(pathJoiner.join(path));
                final URI builtUri = uriBuilder.build().toUri();

                final ResponseEntity<Response> response = doPut(builtUri,
                        "prevExist", "false",
                        "value", value);
                final int errorCode = response.getBody().getErrorCode();

                return EtcdError.resolve(errorCode) != EtcdError.EcodeNodeExist;
            }
        });
    }

    public boolean createDirIfNotExists(final String... path) throws IOException, EtcdException {
        return access(new Accessor<Boolean>() {
            @Override
            public Boolean access(URI uri) throws RestClientException, IOException {
                final UriComponentsBuilder uriBuilder = createKeysUriBuilder(uri);
                uriBuilder.path(pathJoiner.join(path));
                final URI builtUri = uriBuilder.build().toUri();

                try {
                    doPut(builtUri,
                            "prevExist", "false",
                            "dir", "true");

                    return true;
                } catch (HttpClientErrorException e) {
                    if (e.getStatusCode() == HttpStatus.PRECONDITION_FAILED) {
                        return false;
                    }
                    throw e;
                }
            }
        });
    }

    protected <T> T access(Accessor<T> accessor) throws RestClientException, EtcdException, IOException {
        for (int tries = 0; tries < machineCount; ++tries) {
            try {
                return accessor.access(currentMachine);
            } catch (ConnectException e) {
                LOG.info("Connection to {} failed: {}", currentMachine, e.getMessage());
                currentMachine = machines.next();
            }
        }

        throw new NoUsableMachinesException();
    }

    private void createDir(URI builtUri) {
        final ResponseEntity<Response> createResponse = doPut(builtUri,
                "dir", "true",
                "prevExist", "false");
        LOG.debug("createDir of {} got {}", builtUri, createResponse);
    }

    private UriComponentsBuilder createKeysUriBuilder(URI uri) {
        return UriComponentsBuilder.fromUri(uri)
                .path("/v2/keys");
    }

    private ResponseEntity<Response> doPut(URI builtUri, String... params) {
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        for (int i = 0; i < params.length-1; ++i) {
            body.add(params[i], params[i+1]);
        }

        final RequestEntity<LinkedMultiValueMap<String, String>> req =
                new RequestEntity<>(body, HEADERS_FORM_URLENCODED, HttpMethod.PUT, builtUri);

        return restTemplate.exchange(builtUri, HttpMethod.PUT, req, Response.class);
    }

    protected interface Accessor<T> {
        T access(URI uri) throws RestClientException, IOException;
    }
}
