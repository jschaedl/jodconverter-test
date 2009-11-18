package org.artofsolving.jodconverter.web;

import org.apache.commons.io.IOUtils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * This servlet offers a document converter service suitable for remote invocation
 * by HTTP clients written in any language.
 * <p/>
 * To be valid a request to service must:
 * <ul>
 * <li>use the POST method and send the input document data as the request body</li>
 * <li>specify the correct restful path /service/ext1/ext2
 * </ul>
 * <p/>
 * As a very simple example, a request to convert a text document into PDF would
 * look something like
 * <pre>
 * POST /jooconverter/service/txt/pdf HTTP/1.1
 * <p/>
 * Hello world!
 * </pre>
 */
public class ServiceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String[] part = request.getRequestURI().split("/");
        String[] format = {part[part.length - 2], part[part.length - 1]};
        OfficeDocumentConverter documentConverter = WebContext.get(getServletContext()).getDocumentConverter();
        for (String ext : format) {
            if (documentConverter.getFormatRegistry().getFormatByExtension(ext) == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unsupported extension " + ext);
        	    return;
            }
        }
        File inFile = null;
        File outFile = null;
        try {
            inFile = File.createTempFile("convert-from-", "." + format[0]);
            outFile = File.createTempFile("convert-to-", "." + format[1]);
            downloadFile(request, inFile);
            documentConverter.convert(inFile, outFile);
            uploadFile(response, outFile);
        } catch (Exception exception) {
            throw new ServletException("conversion failed", exception);
        } finally {
            if (inFile != null) {
                inFile.delete();
            }
            if (outFile != null) {
                outFile.delete();
            }
        }
    }

    private void uploadFile(HttpServletResponse response, File file) throws IOException {
        response.setContentLength((int) file.length());
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            IOUtils.copy(inputStream, response.getOutputStream());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private void downloadFile(HttpServletRequest request, File file) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            IOUtils.copy(request.getInputStream(), outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }
}
