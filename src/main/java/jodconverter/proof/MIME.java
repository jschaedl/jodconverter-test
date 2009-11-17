package jodconverter.proof;

import java.util.HashMap;
import java.util.Map;

class MIME {

    static Map<String, String> FORMAT = create(
            ".odt:application/vnd.oasis.opendocument.text",
            ".doc:application/msword",
            ".rtf:text/rtf");

    static private Map<String, String> create(String... items) {
        Map<String, String> x = new HashMap<String, String>();
        for (String item : items) {
            String[] part = item.split(":");
            x.put(part[0], part[1]);
        }
        return x;
    }

    public static boolean isSupported(String filename) {
        return FORMAT.containsKey(extensionOf(filename));
    }

    public static String typeOf(String filename) {
        return FORMAT.get(extensionOf(filename));
    }

    public static String extensionOf(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0) {
            return "";
        }
        return filename.substring(dot).toLowerCase();
    }

}
