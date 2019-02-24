package compressor.factory;

import compressor.domain.FileDistributionType;
import compressor.service.CompressionService;
import compressor.service.CompressionServiceImpl;

/**
 * Created by Kshitiz on 2/24/2019.
 */
public class ComDecomServiceFactory {
  public static CompressionService getService(FileDistributionType version) {
    if (version.equals(FileDistributionType.VERSION1))
      return new CompressionServiceImpl();
    else return null;
  }
}
