package compressor.service;

import compressor.domain.CompressionRequest;
import compressor.domain.DecompressionRequest;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Kshitiz on 2/24/2019.
 */
public interface CompressionService {

  /**
   * Compresses based on compression request sent
   * @param compressionRequest defines the params of compression
   * @throws InterruptedException
   * @throws IOException
   * @throws ExecutionException
   */
  void compress(CompressionRequest compressionRequest) throws InterruptedException, IOException, ExecutionException;

  /**
   * Decompresses based on decompression request
   * @param decompressionRequest defines the params of decompression
   * @throws IOException
   */
  void decompress(DecompressionRequest decompressionRequest) throws IOException;
}
