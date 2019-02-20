package compressor;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kshitiz on 2/20/2019.
 */
public class TaskSplitter {
  private static final Logger LOGGER = Logger.getLogger(TaskSplitter.class);

  public static List<CompressionTask> splitIntoTasks(File inputDir, String zipDirName, List<File> filesToProcess, int maxPermissibleCompressedSize) {
    LOGGER.debug("Splitting the compression task into chunks");
    List<CompressionTask> tasks = new ArrayList<CompressionTask>();
    int part = 0;
    for (File file : filesToProcess) {
      CompressionRequest compressionTask = new CompressionRequest(
          Collections.singletonList(file.getPath()),
          inputDir,
          zipDirName,
          part,
          maxPermissibleCompressedSize
      );
      CompressionTask t = new CompressionTask(compressionTask);
      tasks.add(t);
      part++;
    }
    LOGGER.debug("Created " + tasks.size() + " tasks for this compression");
    return tasks;
  }

}
