package compressor.helper;

import compressor.domain.CompressionTaskByBucket;
import compressor.factory.CompressionToolFactory;
import compressor.service.CompressionServiceImpl;
import compressor.service.Compressor;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by Kshitiz on 2/19/2019.
 */
public class CompressionTaskRunner implements Callable<Boolean>{
  private static final Logger LOGGER = Logger.getLogger(CompressionServiceImpl.class);

  CompressionTaskByBucket task;

  public CompressionTaskRunner(CompressionTaskByBucket task) {
    this.task = task;
  }

  public Boolean call() {
    try {
      Compressor compressor = CompressionToolFactory.get(task.getCompressionType());
      return compressor.compress(task);
    } catch (IOException e) {
      LOGGER.error("Error in completing the task = " + task.toString() + "Exception = " + e);
      return false;
    }
  }
}
