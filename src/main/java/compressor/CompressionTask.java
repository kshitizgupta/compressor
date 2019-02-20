package compressor;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by Kshitiz on 2/19/2019.
 */
public class CompressionTask implements Callable<Boolean>{
  private static final Logger LOGGER = Logger.getLogger(ComDecomService.class);

  CompressionRequest task;
  CompressorImpl compressor;

  public CompressionTask(CompressionRequest task) {
    this.task = task;
    compressor = new CompressorImpl();
  }

  public Boolean call() {
    try {
      compressor.compress(task);
      return true;
    } catch (IOException e) {
      LOGGER.error("Error in completing the task = " + task.toString());
      return false;
    }
  }
}
