package me.itzg.ignition.services;

import com.google.common.base.Joiner;
import freemarker.cache.TemplateLoader;
import me.itzg.ignition.IgnitionConstants;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static me.itzg.ignition.IgnitionConstants.*;

/**
 * @author Geoff Bourne
 * @since 2/28/2015
 */
@Service
public class EtcdTemplateLoader implements TemplateLoader {

    @Autowired
    private EtcdClient etcdClient;

    @Override
    public Object findTemplateSource(String s) throws IOException {
        final URI templateUri = URI.create(s);
        final String scheme = templateUri.getScheme();
        if (scheme.equalsIgnoreCase(TEMPLATE_SCHEME_MACHINE)) {
            return performMachineLookup(templateUri);
        }
        return null;
    }

    private String performMachineLookup(URI templateUri) throws IOException {
        final UriComponents uriComponents = UriComponentsBuilder.fromUri(templateUri).build();
        final MultiValueMap<String, String> queryParams = uriComponents.getQueryParams();
        final String file = uriComponents.getPath();

        String content;

        content = tryLookup(queryParams, ETCD_BY_IP, file);
        if (content != null) {
            return content;
        }
        content = tryLookup(queryParams, ETCD_BY_ROLE, file);
        if (content != null) {
            return content;
        }

        return tryDefaultLookup(file);
    }

    private String tryDefaultLookup(String file) throws IOException {
        return tryLookup(null, null, file);
    }

    private String tryLookup(MultiValueMap<String, String> queryParams, String type, String file) throws IOException {
        String key;

        if (type != null) {
            final List<String> values = queryParams.get(type);
            if (values != null) {
                final String value = values.get(0);
                key = PATH_JOINER.join(ETCD_BASE, ETCD_MACHINES, type, value, file);
            }
            return null;
        }
        else {
            key = PATH_JOINER.join(ETCD_BASE, ETCD_MACHINES, ETCD_DEFAULT, file);
        }

        try {
            final EtcdResponsePromise<EtcdKeysResponse> promise = etcdClient.get(key).send();
            final EtcdKeysResponse response = promise.get();
            return response.node.value;
        } catch (TimeoutException e) {
            throw new IOException(e);
        } catch (EtcdException e) {
            if (e.errorCode != IgnitionConstants.ETCD_ERR_KEY_NOT_FOUND) {
                throw new IOException("etcd issue", e);
            }
        }

        return null;
    }

    @Override
    public long getLastModified(Object o) {
        return -1;
    }

    @Override
    public Reader getReader(Object o, String s) throws IOException {
        return new StringReader(((String) o));
    }

    @Override
    public void closeTemplateSource(Object o) throws IOException {
        // template source was string, so nothing to close
    }
}
