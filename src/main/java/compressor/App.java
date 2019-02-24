package compressor;

import compressor.domain.CompressionType;
import compressor.domain.FileDistributionType;
import compressor.domain.RequestType;
import compressor.factory.ComDecomServiceFactory;
import compressor.service.CompressionService;
import compressor.domain.CompressionRequest;
import compressor.domain.DecompressionRequest;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Kshitiz on 2/24/2019.
 */
public class App {
  private static final Logger LOGGER = Logger.getLogger(App.class);

  public static void main(String[] args) {
    try {
      runApp(args);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }

  private static void runApp(String[] args) throws InterruptedException, IOException, ExecutionException {
    RequestType requestType = RequestType.getType(args[0]);
    if (
        (requestType == null) ||
            ((args.length < 3) || (args.length > 4)) ||
            ((args.length == 3) && (!requestType.equals(RequestType.decompress))) ||
            ((args.length == 4) && (!requestType.equals(RequestType.compress)))
        ) {
      LOGGER.error("INVALID INPUT");
      System.exit(1);
    }

    String inputDirPath = args[1];
    if (inputDirPath.charAt(inputDirPath.length() - 1) != File.separatorChar) inputDirPath += File.separatorChar;
    String outputDirPath = args[2];
    if (outputDirPath.charAt(outputDirPath.length() - 1) != File.separatorChar) outputDirPath += File.separatorChar;

    CompressionService compressionService = ComDecomServiceFactory.getService(FileDistributionType.VERSION1);

    if (requestType.equals(RequestType.compress)) {
      int maxOutputSize = 0;
      try {
        maxOutputSize = Integer.parseInt(args[3]);
      } catch (Exception e) {
        LOGGER.error("INVALID maxOutputSize provided");
      }
      LOGGER.info("*******************COMPRESSION REQUEST RECEIVED***********************");
      compressionService.compress(new CompressionRequest(inputDirPath, outputDirPath, maxOutputSize, CompressionType.zip));
    } else if (requestType.equals(RequestType.decompress)) {
      LOGGER.info("*******************DECOMPRESSION REQUEST RECEIVED***********************");
      compressionService.decompress(new DecompressionRequest(inputDirPath, outputDirPath, CompressionType.zip));
    }
  }

}
