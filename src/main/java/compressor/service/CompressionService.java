package compressor.service;

import compressor.domain.CompressionRequest;
import compressor.domain.DecompressionRequest;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Kshitiz on 2/24/2019.
 */
public interface CompressionService {

  void compress(CompressionRequest compressionRequest) throws InterruptedException, IOException, ExecutionException;

  void decompress(DecompressionRequest decompressionRequest) throws IOException;
}
