package compressor;

import java.io.File;
import java.io.IOException;

/**
 * Created by Kshitiz on 2/19/2019.
 */
public interface Compressor {
  void compress(CompressionRequest compressionRequest) throws IOException;
  void decompress(String zipDir, String destPath) throws IOException ;
}
