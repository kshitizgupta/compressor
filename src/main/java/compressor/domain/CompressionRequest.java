package compressor.domain;

import compressor.domain.CompressionType;

public class CompressionRequest {
  private final String inputDirPath;
  private final String outputDirPath;
  private final int maxPermissibleCompressSize;
  private final CompressionType compressionType;

  public CompressionRequest(String inputDirPath, String outputDirPath, int maxPermissibleCompressSize, CompressionType compressionType) {
    this.inputDirPath = inputDirPath;
    this.outputDirPath = outputDirPath;
    this.maxPermissibleCompressSize = maxPermissibleCompressSize;
    this.compressionType = compressionType;
  }

  public String getInputDirPath() {
    return inputDirPath;
  }

  public String getOutputDirPath() {
    return outputDirPath;
  }

  public int getMaxPermissibleCompressSize() {
    return maxPermissibleCompressSize;
  }

  public CompressionType getCompressionType() {
    return compressionType;
  }
}
