package compressor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kshitiz on 2/19/2019.
 */
public class CompressionRequest {
  private File zipInputDir;
  private String zipOutputDir;
  private List<String> filesListInDir = new ArrayList<String>();
  private int part;
  private int maxCompressedSize;

  public CompressionRequest(List<String> filesListInDir, File zipDirName, String outputDir, int part, int maxCompressedSize) {
    this.zipInputDir = zipDirName;
    this.filesListInDir = filesListInDir;
    this.zipOutputDir = outputDir;
    this.part = part;
    this.maxCompressedSize = maxCompressedSize;
  }

  public int getMaxCompressedSize() {
    return maxCompressedSize;
  }

  public int getPart() {
    return part;
  }

  public File getZipInputDir() {
    return zipInputDir;
  }

  public List<String> getFilesListInDir() {
    return filesListInDir;
  }

  public String getZipOutputDir() {
    return zipOutputDir;
  }

  public String toString(){
    return Arrays.toString(filesListInDir.toArray());
  }
}
