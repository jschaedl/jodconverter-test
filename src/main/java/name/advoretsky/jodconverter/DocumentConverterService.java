package name.advoretsky.jodconverter;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

public class DocumentConverterService {
  private ExecutorService transformatorsPool = Executors.newCachedThreadPool();

  private HttpClient client = new DefaultHttpClient();

  private String jodServerUrl;
  private Integer timeout;

  public DocumentConverterService(String jodServerUrl, Integer timeout) {
    this.jodServerUrl = jodServerUrl;
    this.timeout = timeout;
  }

  public void destroy() {
    client.getConnectionManager().shutdown();
  }

  public InputStream convert(byte[] input, OutputDocumentType format) throws ExecutionException, InterruptedException {
    OdtConverter converter = new OdtConverter(input, format);
    Future<InputStream> future = transformatorsPool.submit(converter);
    try {
      return future.get(timeout, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      converter.release();
      throw new IllegalStateException("Couldn't convert document in given time frame");
    }
  }

  class OdtConverter implements Callable<InputStream> {
    private static final String odtMimeType = "application/vnd.oasis.opendocument.text";

    private HttpPost httpPost;

    OdtConverter(byte[] input, OutputDocumentType format) {
      httpPost = new HttpPost(jodServerUrl);
      httpPost.addHeader(HTTP.CONTENT_TYPE, odtMimeType);
      httpPost.addHeader("Accept", format.getContentType());
      httpPost.setEntity(new ByteArrayEntity(input));
    }

    public InputStream call() throws IOException {
      HttpResponse response = client.execute(httpPost);
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        throw new IOException("Bad status code received from " + httpPost.getURI() + '\n' + response.getStatusLine());
      }
      return response.getEntity().getContent();
    }

    public void release() {
      httpPost.abort();
    }
  }
}
