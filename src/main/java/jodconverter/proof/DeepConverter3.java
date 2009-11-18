package jodconverter.proof;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

class DeepConverter3 {

    static Set<String> extentions = new HashSet<String>();

    static {
        extentions.add("doc");
        extentions.add("rtf");
        extentions.add("odt");
    }

    final HttpClient client = new DefaultHttpClient();
    final String url;
    final int limit;

    int count = 0;


    public static String extensionOf(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0) {
            return "";
        }
        return filename.substring(dot + 1).toLowerCase();
    }

    public DeepConverter3(int limit, String url) {
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
        String ext = extensionOf(file.getName());
        if (file.isFile() && extentions.contains(ext)) {
            count++;
            long start = System.currentTimeMillis();
            HttpPost httpPost = new HttpPost(url + "/" + ext + "/pdf");
            httpPost.setEntity(new FileEntity(file, null));
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
                    1000.0 / 1024.0 * file.length() / (System.currentTimeMillis() - start),
                    file.getName()));
            return 1;
        }
        return 0;
    }

    boolean ltLimit() {
        return count < limit;
    }

}