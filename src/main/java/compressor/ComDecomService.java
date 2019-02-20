package compressor;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Kshitiz on 2/19/2019.
 */
public class ComDecomService {
  private static final Logger LOGGER = Logger.getLogger(ComDecomService.class);

  public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
    final int DEFAULT_MAX_SIZE = 1024 * 4; //4KB

    if (args.length != 3) {
      LOGGER.error("INVALID INPUT");
      System.exit(1);
    }
    String reqType = args[0];
    String inputDir = args[1];
    String outputDirPath = args[2];

    ComDecomService comDecomService = new ComDecomService();

    if (reqType.equals("compress")) {
      comDecomService.zip(inputDir, outputDirPath, DEFAULT_MAX_SIZE);
    } else {
      comDecomService.unzip(inputDir, outputDirPath);
    }
  }

  /**
   * @param inputDirPath
   * @param outputDirPath
   * @param maxPermissibleCompressSize
   * @throws InterruptedException
   * @throws IOException
   */
  private void zip(String inputDirPath, String outputDirPath, int maxPermissibleCompressSize) throws InterruptedException, IOException, ExecutionException {
    File inputDir = new File(inputDirPath);
    FileUtil.emptyFolder(outputDirPath);

    List<File> inputFileList = FileUtil.getFilesToProcess(inputDir);
    if (inputFileList.size() == 0) LOGGER.debug("No Files found");

    List<CompressionTask> tasks = TaskSplitter.splitIntoTasks(inputDir, outputDirPath, inputFileList, maxPermissibleCompressSize);

    ExecutorService executorService = Executors.newFixedThreadPool(2);
    List<Future<Boolean>> answers = executorService.invokeAll(tasks);

    executorService.shutdown();
    boolean isSuccessful = true;
    for (Future<Boolean> answer : answers) {
      if (!answer.get()) {
        isSuccessful = false;
        break;
      }
    }

    if (isSuccessful) LOGGER.info("Compression successful");
    else LOGGER.error("Compression Failed, Please retry or look into the error in logs");

  }

  private void unzip(String inputDirPath, String outputDirPath) throws IOException {
    FileUtil.emptyFolder(outputDirPath);
    CompressorImpl compressor = new CompressorImpl();
    compressor.decompress(inputDirPath, "unzippedOutput");
  }
}
