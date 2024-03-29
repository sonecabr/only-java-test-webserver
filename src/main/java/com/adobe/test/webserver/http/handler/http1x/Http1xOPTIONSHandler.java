package com.adobe.test.webserver.http.handler.http1x;

import com.adobe.test.webserver.http.handler.HttpOPTIONSHandler;
import com.adobe.test.webserver.http.spec.ClientHeader;
import com.adobe.test.webserver.http.spec.ContentType;
import com.adobe.test.webserver.http.spec.HttpStatusCode;
import com.adobe.test.webserver.io.WebContentFile;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Http1.x OPTIONS method dispatcher
 * <ul>
 *     <li>manage requests for OPTIONS method</li>
 *     <li>threat all headers from client side</li>
 *     <li>write headers and body (html)</li>
 *     <li>configure response headers and code</li>
 * </ul>
 *
 *
 * @author Andre Rocha
 * @since 2019-06-09
 */
@Slf4j
@Builder
public class Http1xOPTIONSHandler extends BaseHttp1xHandler implements HttpOPTIONSHandler {


    final int HTTP_CODE = HttpStatusCode.OK_200.getCode();

    @Override
    public void dispatch(ClientHeader clientHeaders,
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
            requestStream.close();
        } catch (IOException e) {
            log.error("Error closing client request", e);
        }
    }

    @Override
    public void printHeaders(int httpCode, PrintWriter headerResponseStream, ContentType contentType, int lenght, String clientVersion) {
        headerResponseStream.println(String.format("%s %d OK", clientVersion, httpCode));
        super.printHeaders(httpCode, headerResponseStream, contentType, lenght, clientVersion);
    }
}