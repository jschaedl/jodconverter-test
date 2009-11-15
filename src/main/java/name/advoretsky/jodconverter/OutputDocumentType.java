package name.advoretsky.jodconverter;

public enum OutputDocumentType {
  DOC("application/msword"),
  PDF("application/pdf"),
  RTF("text/rtf");

  private String contentType;

  OutputDocumentType(String contentType) {
    this.contentType = contentType;
  }

  public String getExtension() {
    return toString().toLowerCase();
  }

  public String getContentType() {
    return contentType;
  }

  public static OutputDocumentType find(String extension) {
    for (OutputDocumentType format : values()) {
      if (format.getExtension().equalsIgnoreCase(extension)) {
        return format;
      }
    }
    return null;
  }
}
