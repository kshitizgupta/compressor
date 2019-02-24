package compressor.domain;

/**
 * Created by Kshitiz on 2/21/2019.
 */
public enum RequestType {
  compress, decompress;

  public static RequestType getType(String s) {
    try{
      return valueOf(s);
    } catch (Exception e) {
      return null;
    }
  }
}
