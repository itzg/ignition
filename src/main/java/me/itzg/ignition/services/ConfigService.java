package me.itzg.ignition.services;

import me.itzg.ignition.ConfigStatus;
import me.itzg.ignition.IgnitionConstants;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeoutException;

/**
 * @author Geoff Bourne
 * @since 3/1/2015
 */
@Service
public class ConfigService {

    @Autowired
    private EtcdClient etcdClient;

    @Autowired
    @Qualifier("imagesBaseDirectory")
    private File imagesBaseDirectory;

    public ConfigStatus getConfigStatus() throws IOException, TimeoutException, EtcdException {
        final ConfigStatus status = new ConfigStatus();

        final EtcdResponsePromise<EtcdKeysResponse> responsePromise = etcdClient.get(IgnitionConstants.PATH_JOINER.join(IgnitionConstants.ETCD_BASE, IgnitionConstants.ETCD_MACHINES))
                .dir()
                .send();

        try {
            final EtcdKeysResponse response = responsePromise.get();
            status.setMachinesPresent(!response.node.nodes.isEmpty());
        } catch (EtcdException e) {
            if (e.errorCode != IgnitionConstants.ETCD_ERR_KEY_NOT_FOUND) {
                throw e;
            }
        }

        final Path imagesPath = imagesBaseDirectory.toPath();
        Files.walkFileTree(imagesPath, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                status.setImagesPopulated(true);
                return FileVisitResult.SKIP_SUBTREE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.TERMINATE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });

        return status;
    }
}
