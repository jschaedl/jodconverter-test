package jodconverter.proof;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;

import java.util.concurrent.*;

public class HttpProcessor {

    private final ExecutorService executors;
    private final ExecutorCompletionService<byte[]> completions;
    private final HttpClient client;

    public HttpProcessor(ExecutorService executors) {
        this.executors = executors;
        completions = new ExecutorCompletionService<byte[]>(executors);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 8080));
        BasicHttpParams params = new BasicHttpParams();
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        client = new DefaultHttpClient(cm, params);
    }

    public HttpProcessor(int workers) {
        this(Executors.newFixedThreadPool(workers));
    }

    public HttpProcessor() {
        this(Runtime.getRuntime().availableProcessors());
    }


    public void shutdown() {
        executors.shutdown();
        client.getConnectionManager().shutdown();
    }

    public Future<byte[]> submit(HttpPostProvider httpPostProvider) {
        return completions.submit(new Poster(client, httpPostProvider));
    }

    public Future<byte[]> pool(long timeout, TimeUnit unit) throws InterruptedException {
        return completions.poll(timeout, unit);
    }

}