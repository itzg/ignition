package me.itzg.ignition;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author Geoff Bourne
 * @since 3/2/2015
 */
public class ExtractResponseToFile implements ResponseExtractor<Path> {
    private final Path outputFile;

    public ExtractResponseToFile(Path outputFile) {
        this.outputFile = outputFile;
    }

    @Override
    public Path extractData(ClientHttpResponse clientHttpResponse) throws IOException {
        try {
            Files.copy(clientHttpResponse.getBody(), outputFile, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            clientHttpResponse.close();
        }

        return outputFile;
    }
}
