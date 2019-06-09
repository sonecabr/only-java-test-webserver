package com.adobe.test.webserver.io;

import com.adobe.test.webserver.io.exception.FileNotFoundUnreadableException;
import com.adobe.test.webserver.server.WebServerConfigs;
import lombok.extern.slf4j.Slf4j;

import javax.swing.text.html.Option;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
public class BlockingFileReader implements FileReader {

    private static final BlockingFileReader INSTANCE = new BlockingFileReader();

    public static BlockingFileReader getInstance() {
        return BlockingFileReader.INSTANCE;
    }

    @Override
    public WebContentFile readContent(String path, BufferedOutputStream outputStream) throws FileNotFoundUnreadableException {
        String canonicalPath = String.format("%s%s", WebServerConfigs.WEB_ROOT, path);
        if (!Files.isReadable(Paths.get(canonicalPath))) {
            throw new FileNotFoundUnreadableException(String.format("File %s is not acessible", path));
        }

        FileInputStream fin = null;
        File file = new File(canonicalPath);
        int lenght = (int) file.length();
        byte[] payload = new byte[lenght];
        try {
            fin = new FileInputStream(file);
            fin.read(payload);
            outputStream.write(payload, 0, lenght);
            return WebContentFile
                    .builder()
                    .content(outputStream)
                    .lenght(lenght)
                    .build();
        } catch (IOException e) {
            throw new FileNotFoundUnreadableException(e);
        } finally {
            Optional.ofNullable(fin).ifPresent(f -> {
                try {
                    f.close();
                } catch (IOException e) {
                    log.error("Error closing file");
                }

            });
        }


    }
}
