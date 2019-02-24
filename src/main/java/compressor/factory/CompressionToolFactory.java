package compressor.factory;

import compressor.domain.CompressionType;
import compressor.service.Compressor;
import compressor.service.ZipCompressor;

/**
 * Created by Kshitiz on 2/24/2019.
 */
public class CompressionToolFactory {
  public static Compressor get(CompressionType compressionType) {
    if (compressionType.equals(CompressionType.zip))
      return new ZipCompressor();
    else return null;
  }

}
