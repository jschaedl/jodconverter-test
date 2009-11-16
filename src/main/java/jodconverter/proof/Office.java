package jodconverter.proof;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Office {
    private Runtime runtime = Runtime.getRuntime();
    private boolean osSupported = System.getProperty("os.name").matches(("(?i).*Linux.*"));
    private int port = 8100;

    //since OpenOffice.org 2.3 an X server server is no longer needed - "unset DISPLAY"
    private String[] EMPTY_ENV = new String[0];

    public boolean isOsSupported() {
        return osSupported;
    }

    public boolean start() {
        return exec("soffice", "-headless", "-nofirststartwizard",
                "-accept=socket,host=localhost,port=" + port + ";urp;");
    }

    public boolean isRunning() {
        return exec("pgrep", "soffice");
    }

    public boolean kill() {
        return exec("pkill", "soffice");
    }

    private boolean exec(String... command) {
        try {
            Process proc = runtime.exec(command, EMPTY_ENV);
            new Globber(proc.getErrorStream(), "ERROR").start();
            new Globber(proc.getInputStream(), "OUTPUT").start();
            return proc.waitFor() == 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class Globber extends Thread {
        private InputStream is;
        private String type;

        Globber(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(type + " > " + line);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
