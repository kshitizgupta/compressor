package compressor.service;

import compressor.helper.CompressionTaskRunner;
import compressor.util.FileUtil;
import compressor.helper.TaskSplitter;
import compressor.domain.CompressionRequest;
import compressor.domain.DecompressionRequest;
import compressor.domain.DecompressionTask;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Kshitiz on 2/19/2019.
 */
public class CompressionServiceImpl implements CompressionService {
  private static final Logger LOGGER = Logger.getLogger(CompressionServiceImpl.class);

  /**
   * @param compressionRequest
   * @throws InterruptedException
   * @throws IOException
   */
  @Override
  public void compress(CompressionRequest compressionRequest) throws InterruptedException, IOException, ExecutionException {
    if (!FileUtil.exists(compressionRequest.getInputDirPath())) {
      LOGGER.info("Input dir is empty, nothing to compress");
      return;
    }

    //Prepare output folder
    FileUtil.prepareFolder(compressionRequest.getOutputDirPath());

    //Parallelism the system can afford
    int concurrencyFactor = Runtime.getRuntime().availableProcessors();

    //Split the compression into sub tasks
    List<CompressionTaskRunner> tasks = TaskSplitter.splitIntoTasks(compressionRequest, 2);

    Date startTime = new Date();

    //Execute sub-tasks using thread pool
    ExecutorService executorService = Executors.newFixedThreadPool(concurrencyFactor);
    List<Future<Boolean>> answers = executorService.invokeAll(tasks);
    executorService.shutdown();

    Date endTime = new Date();

    //Check if all the jobs completed successfully, and log a message accordingly
    boolean isSuccessful = true;
    for (Future<Boolean> answer : answers) {
      if (!answer.get()) {
        isSuccessful = false;
        break;
      }
    }

    LOGGER.info("************************STATS************************");
    if (isSuccessful) {
      LOGGER.info("***************Compression successful****************");
      LOGGER.info("***************Concurrently = " + tasks.size() + "***************");
      LOGGER.info("***************Time taken = " + (endTime.getTime() - startTime.getTime()) / 1000 + " seconds***************");
      long initSize = FileUtil.getDirSize(compressionRequest.getInputDirPath());
      long compressedSize = FileUtil.getDirSize(compressionRequest.getOutputDirPath());
      float percentage = (100 * compressedSize) / initSize;
      LOGGER.info("***************Compression Percentage = " + percentage + "***************");
    } else {
      LOGGER.info("***************Compression failed****************");
      LOGGER.error("***************Please retry or look into the error in logs**********");
    }

  }

  @Override
  public void decompress(DecompressionRequest decompressionRequest) throws IOException {
    FileUtil.prepareFolder(decompressionRequest.getOutputDirPath());
    Compressor compressor = new ZipCompressor();
    DecompressionTask decompressionTask = new DecompressionTask(
        decompressionRequest.getInputDirPath(),
        decompressionRequest.getOutputDirPath(),
        decompressionRequest.getCompressionType()
    );
    compressor.decompress(decompressionTask);
  }
}
