package com.adobe.test.webserver.http.handler.http1x;

import com.adobe.test.webserver.http.handler.HttpMethodNotSupportedHandler;
import com.adobe.test.webserver.http.spec.ClientHeaders;
import com.adobe.test.webserver.http.spec.ContentType;
import com.adobe.test.webserver.http.spec.HttpStatusCode;
import com.adobe.test.webserver.io.WebContentFile;
import com.adobe.test.webserver.io.exception.FileNotFoundUnreadableException;
import com.adobe.test.webserver.server.WebServerConfigs;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Builder
public class Http1xMethodNotSupportedHandler extends BaseHttp1xHandler implements HttpMethodNotSupportedHandler {

    final int HTTP_CODE = HttpStatusCode.OK_200.getCode();

    @Override
    public void handle(ClientHeaders clientHeaders,
                       BufferedReader requestStream,
                       PrintWriter headerResponseStream,
                       BufferedOutputStream payloadResponseStream) {

        log.info(String.format("Received a OPTIONS request %s", clientHeaders.getUrl()));
        String uri = clientHeaders.getUrl();

        if (ContentType.byExtension(extractExtension(uri)).equals(ContentType.DEFAULT)) {
            uri += ContentType.HTML.getExtension();
        }
        WebContentFile content = WebContentFile
                .builder()
                .lenght(0)
                .content(payloadResponseStream)
                .build();
        printHeaders(
                HTTP_CODE,
                headerResponseStream,
                ContentType.byExtension(extractExtension(uri)),
                content.getLenght(),
                clientHeaders.getProtocolVersion());

        try {
            headerResponseStream.flush();
            payloadResponseStream.flush();
            headerResponseStream.close();
            payloadResponseStream.close();
        } catch (IOException e) {
            log.error("Error closing client request", e);
        }
    }
}
