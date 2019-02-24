package compressor.service;

import compressor.domain.CompressionTaskByBucket;
import compressor.domain.DecompressionTask;

import java.io.IOException;

/**
 * Created by Kshitiz on 2/19/2019.
 */
public interface Compressor {
  /**
   * @param compressionTask @see
   * @throws IOException
   * @See #Comp
   */
  boolean compress(CompressionTaskByBucket compressionTask) throws IOException;

  /**
   * @param decompressionTask
   * @throws IOException
   */
  boolean decompress(DecompressionTask decompressionTask) throws IOException;
}
