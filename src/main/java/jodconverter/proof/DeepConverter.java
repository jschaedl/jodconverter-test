package jodconverter.proof;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.IOException;

class DeepConverter {
    final HttpClient client = new DefaultHttpClient();
    final String url;
    final int limit;

    int count = 0;

    public DeepConverter(int limit, String url) {
        this.limit = limit;
        this.url = url;
    }

    public void convert(String file) {
        File startPoint = new File(file);
        if (!startPoint.exists()) {
            throw new RuntimeException("Start point " + file + " not exists");
        }
        while (ltLimit()) {
            if (nextFile(startPoint) == 0) {
                throw new IllegalArgumentException("Can not find any documents in " + file);
            }
        }
    }

    public int nextFile(File file) {
        if (!file.exists() || !ltLimit()) {
            return 0;
        }
        if (file.isDirectory()) {
            int localCount = 0;
            for (File child : file.listFiles()) {
                localCount += nextFile(child);
            }
            return localCount;
        }
        if (file.isFile() && MIME.isSupported(file.getName())) {
            count++;
            long start = System.currentTimeMillis();
            String type = MIME.typeOf(file.getName());
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HTTP.CONTENT_TYPE, type);
            httpPost.addHeader("Accept", "application/pdf");
            httpPost.setEntity(new FileEntity(file, type));
            try {
                HttpResponse response = client.execute(httpPost);
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new RuntimeException("Conversion fail for " + file + "\nInvalid status " + response.getStatusLine() + "\n" +
                    IOUtils.toString(response.getEntity().getContent()));
                }
                IOUtils.toByteArray(response.getEntity().getContent());
            } catch (IOException e) {
                throw new RuntimeException("Conversion fail for " + file, e);
            }
            System.out.println(String.format(
                    "%6d %4.0f kB/s - %s",
                    count,
                    1000.0/1024.0 * file.length() / (System.currentTimeMillis()-start),
                    file.getName()));
            return 1;
        }
        return 0;
    }

    boolean ltLimit() {
        return count < limit;
    }

}
