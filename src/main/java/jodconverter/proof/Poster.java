package jodconverter.proof;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.util.concurrent.Callable;

class Poster implements Callable<byte[]> {
    private HttpClient client;
    private HttpPostProvider httpPostPovider;

    public Poster(HttpClient client, HttpPostProvider httpPostPovider) {
        this.client = client;
        this.httpPostPovider = httpPostPovider;

    }

    public byte[] call() throws IOException {
        HttpPost httpPost = httpPostPovider.get();
        HttpResponse response = client.execute(httpPost);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new IOException("Bad status code received from " + httpPost.getURI() + '\n' + response.getStatusLine());
        }
        return IOUtils.toByteArray(response.getEntity().getContent());
    }
}
