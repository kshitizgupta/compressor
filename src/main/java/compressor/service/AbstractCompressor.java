package compressor.service;

import compressor.constants.Constants;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

import static compressor.constants.Constants.SEPARATOR;

/**
 * Created by Kshitiz on 2/24/2019.
 */
public abstract class AbstractCompressor implements Compressor {
  private final String OUTPUT_FILE_NAME = "bucket";
  private final String SPLIT_SUFFIX = "_split_";
  private String fileExtension;
  protected boolean isNewFile = false;

//  private final String FILE_EXTENSION = ".zip";

  public AbstractCompressor(String fileExtension) {
    this.fileExtension = fileExtension;
  }

  protected HashMap<String, TreeSet<File>> segregateFilesByBucket(File[] files) {
    HashMap<String, TreeSet<File>> filesByBucketName = new HashMap<String, TreeSet<File>>();

    for (File file : files) {
      String fileName = file.getName();
      String batchNamePrefix = fileName.substring(0, fileName.indexOf(Constants.SEPARATOR, fileName.indexOf(Constants.SEPARATOR) + 1));
      if (!filesByBucketName.containsKey(batchNamePrefix)) {
        filesByBucketName.put(batchNamePrefix, new TreeSet<File>(new Comparator<File>() {
          public int compare(File o1, File o2) {
            Integer o1SplitNo = Integer.parseInt((o1.getName().split("split_")[1]).split("\\.")[0]);
            Integer o2SplitNo = Integer.parseInt((o2.getName().split("split_")[1]).split("\\.")[0]);
            return o1SplitNo.compareTo(o2SplitNo);
          }
        }));
      }
      filesByBucketName.get(batchNamePrefix).add(file);
    }
    return filesByBucketName;
  }

  protected String getOutputFileName(String outputDir, int bucket, int splitIndex) {
    return outputDir + OUTPUT_FILE_NAME + SEPARATOR + bucket + SPLIT_SUFFIX + splitIndex + fileExtension;
  }
}
