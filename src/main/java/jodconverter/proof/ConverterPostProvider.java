package jodconverter.proof;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HTTP;

public class ConverterPostProvider implements HttpPostProvider {

    final HttpPost httpPost;

    ConverterPostProvider(String service, byte[] input, String format) {
        httpPost = new HttpPost(service);
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/vnd.oasis.opendocument.text");
        httpPost.addHeader("Accept", format);
        httpPost.setEntity(new ByteArrayEntity(input));
    }

    public HttpPost get() {
        return httpPost;
    }
}
