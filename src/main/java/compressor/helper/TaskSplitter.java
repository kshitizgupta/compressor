package compressor.helper;

import compressor.util.FileUtil;
import compressor.domain.CompressionRequest;
import compressor.domain.CompressionTaskByBucket;
import compressor.domain.BucketOfFiles;
import compressor.domain.CompressionType;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Kshitiz on 2/20/2019.
 */
public class TaskSplitter {
  private static final Logger LOGGER = Logger.getLogger(TaskSplitter.class);

  public static List<CompressionTaskRunner> splitIntoTasks(CompressionRequest compressionRequest, int concurrency) throws IOException {
    LOGGER.debug("Splitting the compression task into chunks");

    String inputDirPath = compressionRequest.getInputDirPath();
    String zipDirName = compressionRequest.getOutputDirPath();
    int maxPermissibleCompressedSize = compressionRequest.getMaxPermissibleCompressSize();
    CompressionType compressionType = compressionRequest.getCompressionType();

    //Get list of files in the input directory to process
    List<File> filesToProcess = FileUtil.getFilesToProcess(inputDirPath);
    if (filesToProcess.size() == 0) LOGGER.debug("No Files found");

    File inputDir = new File(inputDirPath);

    List<CompressionTaskRunner> tasks = new ArrayList<CompressionTaskRunner>();

    PriorityQueue<BucketOfFiles> jobBatchQueue = new PriorityQueue<BucketOfFiles>(concurrency);
    for (int bucketId = 0; bucketId < concurrency; bucketId++) {
      jobBatchQueue.offer(new BucketOfFiles(bucketId));
    }

    for (File file : filesToProcess) {
      BucketOfFiles b = jobBatchQueue.poll();
      b.addFile(file);
      jobBatchQueue.add(b);
    }

    for (BucketOfFiles currBucket : jobBatchQueue) {
      LOGGER.debug(currBucket);

      //If the no of files is less than the buckets we have, then few buckets will be empty
      //We should skip these buckets while creating compression tasks
      if (currBucket.getFileList().size() == 0) {
        LOGGER.debug("Skipping current bucket, it has nothing in it");
        continue;
      }
      CompressionTaskByBucket compressionTask = new CompressionTaskByBucket(
          currBucket,
          inputDir,
          zipDirName,
          maxPermissibleCompressedSize,
          compressionType
      );

      tasks.add(new CompressionTaskRunner(compressionTask));
    }

    LOGGER.debug("Created " + tasks.size() + " tasks for this compression");
    return tasks;
  }
}
