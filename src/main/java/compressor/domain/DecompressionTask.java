package compressor.domain;

/**
 * Created by Kshitiz on 2/24/2019.
 */
public class DecompressionTask {
  private String zipDir;
  private String outputPath;
  private CompressionType compressionType;

  public DecompressionTask(String inputDirPath, String outputDirPath, CompressionType compressionType) {
    this.zipDir = inputDirPath;
    this.outputPath = outputDirPath;
    this.compressionType = compressionType;
  }

  public String getZipDir() {
    return zipDir;
  }

  public String getOutputPath() {
    return outputPath;
  }

  public CompressionType getCompressionType() {
    return compressionType;
  }
}
