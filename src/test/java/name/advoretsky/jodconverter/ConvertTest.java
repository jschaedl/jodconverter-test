package name.advoretsky.jodconverter;

import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ConvertTest {
  @Test
  public void testFileConvert() throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    byte[] template = IOUtils.toByteArray(classLoader.getResourceAsStream("deed.odt"));
    DocumentConverterService service = new DocumentConverterService("http://localhost:8080/jodconverter-webapp-2.2.2/service", 1);
    int errors = 0;
    int errorsTotal = 0;
    for (int n = 0; n < 100000; n++) {
      try {
        byte[] result = IOUtils.toByteArray(service.convert(template, OutputDocumentType.PDF));
        Assert.assertTrue(result.length > 1000);
        errors = 0;
      } catch (Exception e) {
        errors++;
        errorsTotal++;
        System.out.println("Exception occured (" + errorsTotal + ", " + errors + ")");
        throw e;
      }
    }
  }
}
