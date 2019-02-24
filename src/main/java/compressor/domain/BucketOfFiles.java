package compressor.domain;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static compressor.constants.Constants.*;

/**
 * Created by Kshitiz on 2/24/2019.
 */
public class BucketOfFiles implements Comparable<BucketOfFiles> {
  private List<File> fileList;
  private int id;
  private Long currSize = (long) 0;

  public BucketOfFiles(int id) {
    this.id = id;
    this.fileList = new ArrayList<File>();
  }

  public void addFile(File file) {
    fileList.add(file);
    long fileSize = FileUtils.sizeOf(file);
    currSize += fileSize;
  }

  @Override
  public int compareTo(BucketOfFiles o) {
    return currSize.compareTo(o.currSize);
  }

  public List<File> getFileList() {
    return fileList;
  }

  public int getId() {
    return id;
  }

  private Long getBucketSizeInMb() {
    return currSize / (ONE_MB_BYTES);
  }

  @Override
  public String toString() {
    return "Bucket id = " + id + " with input file size = " + getBucketSizeInMb() + "MB";
  }
}
