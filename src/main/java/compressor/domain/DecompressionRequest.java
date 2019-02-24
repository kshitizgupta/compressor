package compressor.domain;

import compressor.domain.CompressionType;

public class DecompressionRequest {
  private final String inputDirPath;
  private final String outputDirPath;
  private final CompressionType compressionType;

  public DecompressionRequest(String inputDirPath, String outputDirPath, CompressionType compressionType) {
    this.inputDirPath = inputDirPath;
    this.outputDirPath = outputDirPath;
    this.compressionType = compressionType;
  }

  public String getInputDirPath() {
    return inputDirPath;
  }

  public String getOutputDirPath() {
    return outputDirPath;
  }

  public CompressionType getCompressionType() {
    return compressionType;
  }
}
