package compressor.domain;

import java.io.File;
import java.util.Arrays;

/**
 * Created by Kshitiz on 2/19/2019.
 */
public class CompressionTaskByBucket {
  private File inputDir;
  private String outputDir;
  private BucketOfFiles bucket;
  private int maxCompressedSize;
  private CompressionType compressionType;

  public CompressionTaskByBucket(BucketOfFiles jobBatch, File zipDirName, String outputDir, int maxCompressedSize, CompressionType compressionType) {
    this.inputDir = zipDirName;
    this.bucket = jobBatch;
    this.outputDir = outputDir;
    this.maxCompressedSize = maxCompressedSize;
    this.compressionType = compressionType;
  }

  public CompressionType getCompressionType() {
    return compressionType;
  }

  public int getMaxCompressedSize() {
    return maxCompressedSize;
  }

  public File getInputDir() {
    return inputDir;
  }

  public BucketOfFiles getBucket() {
    return bucket;
  }

  public String getOutputDir() {
    return outputDir;
  }

  public String toString() {
    return Arrays.toString(bucket.getFileList().toArray());
  }
}
