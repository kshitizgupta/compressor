package compressor.service;

import compressor.domain.CompressionTaskByBucket;
import compressor.domain.DecompressionTask;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static compressor.constants.Constants.*;

/**
 * Created by Kshitiz on 2/19/2019.
 */
public class ZipCompressor extends AbstractCompressor {
  private static final Logger LOGGER = Logger.getLogger(ZipCompressor.class);
  private final static String FILE_EXTENSION = ".zip";

  private final int COMPRESSION_BUFFER_SIZE = ONE_KB_BYTES * 16;
  private final int DE_COMPRESSION_BUFFER_SIZE = ONE_KB_BYTES * 4;
  private final int offset = 1000;

  private double currSplitSizeInKb = 0;
  private int splitIndex = 0;
  private FileOutputStream fos;
  private ZipOutputStream zos;

  public ZipCompressor() {
    super(FILE_EXTENSION);
  }

  @Override
  public boolean compress(CompressionTaskByBucket task) throws IOException {
    try {
      for (File file : task.getBucket().getFileList()) {
        String filePath = file.getPath();
        LOGGER.debug(Thread.currentThread().getName() + ": Zipping " + filePath);
        isNewFile = true;

        //read the file and write to ZipOutputStream
        FileInputStream fis = new FileInputStream(filePath);

        byte[] buffer = new byte[COMPRESSION_BUFFER_SIZE];
        int len;
        while ((len = fis.read(buffer)) > 0) {
          getOutputStream(task, filePath).write(buffer, 0, len);
          if (isNewFile) isNewFile = false;
          currSplitSizeInKb += COMPRESSION_FACTOR * (len / ONE_KB_BYTES);
        }
        zos.closeEntry();
        fis.close();
        LOGGER.debug("Done Zipping " + filePath);
      }
    } catch (Exception e) {
      LOGGER.error("Exception while zipping ", e);
      return false;
    } finally {
      if (zos != null) zos.close();
      if (fos != null) fos.close();
    }
    return true;

  }

  @Override
  public boolean decompress(DecompressionTask decompressionTask) throws IOException {
    String zipDir = decompressionTask.getZipDir();
    String outputPath = decompressionTask.getOutputPath();
    File[] files = new File(zipDir).listFiles();

    HashMap<String, TreeSet<File>> filesByBucketName = segregateFilesByBucket(files);

    for (Map.Entry<String, TreeSet<File>> entry : filesByBucketName.entrySet()) {
      LOGGER.debug("****************Decompressing bucket = " + entry.getKey() + "****************");
      for (File file : entry.getValue()) {
        try {
          unzipOneFile(outputPath, file);
        } catch (IOException e) {
          LOGGER.error("Caught exception while decompressing, e = ", e);
          return false;
        }
      }
    }
    return true;
  }

  /**
   * This function is responsible for unzipping a single zip file and create
   * output files corresponding to each zip entry in this file
   *
   * @param destPath
   * @param file
   * @throws IOException
   */
  private void unzipOneFile(String destPath, File file) throws IOException {
    LOGGER.debug("Unzipping file = " + file.getAbsolutePath());

    //buffer for read and write data to file
    byte[] buffer = new byte[DE_COMPRESSION_BUFFER_SIZE];

    FileInputStream fis = new FileInputStream(file.getPath());
    ZipInputStream zis = new ZipInputStream(fis);
    ZipEntry ze = zis.getNextEntry();


    while (ze != null) {
      String currFileName = ze.getName();

      File currFile = new File(destPath + File.separator + currFileName);

      //create directories for sub directories in zip
      boolean success = new File(currFile.getParent()).mkdirs();

      FileOutputStream foss = new FileOutputStream(currFile, true);

      int len;
      while ((len = zis.read(buffer)) > 0) {
        foss.write(buffer, 0, len);
      }
      foss.close();

      //close this ZipEntry
      zis.closeEntry();
      ze = zis.getNextEntry();
    }
    //close last ZipEntry
    zis.closeEntry();
    zis.close();
    fis.close();
  }

  /**
   * Gets the zip output stream depending on the size limitation.
   * Case 1 : Initializes the zip output stream in case it is still null
   * Case 2 : In case the split size has overrun the max permissible size,
   * closes the current stream, creates a new one
   * Case 3 : In case split size is well under the max permissible size, returns the current stream
   *
   * @param task
   * @param filePath
   * @return
   * @throws IOException
   */
  private ZipOutputStream getOutputStream(CompressionTaskByBucket task, String filePath) throws IOException {
    try {
      if (zos == null) {
        //if zos is null, means we have just started and we need to initialise it
        fos = new FileOutputStream(getOutputFileName(task.getOutputDir(), task.getBucket().getId(), splitIndex));
        splitIndex++;
        zos = new ZipOutputStream(fos);
        //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
        ZipEntry ze = new ZipEntry(filePath.substring(task.getInputDir().getAbsolutePath().length() + 1, filePath.length()));
        zos.putNextEntry(ze);
        return zos;
      }
      if (currSplitSizeInKb + offset >= task.getMaxCompressedSize() * ONE_MB_BYTES / ONE_KB_BYTES) {
        LOGGER.debug(Thread.currentThread().getName() + ": MaxSize exceeded, creating split no " + splitIndex + " for file " + filePath);
        zos.flush();
        zos.closeEntry();
        zos.close();
        fos.close();
        fos = new FileOutputStream(getOutputFileName(task.getOutputDir(), task.getBucket().getId(), splitIndex));
        splitIndex++;
        currSplitSizeInKb = 0;
        zos = new ZipOutputStream(fos);
        //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
        ZipEntry ze = new ZipEntry(filePath.substring(task.getInputDir().getAbsolutePath().length() + 1, filePath.length()));
        zos.putNextEntry(ze);
        return zos;
      } else if (zos != null && isNewFile) {
        //If zos is still existing and if its a new file then we have to make a new zip entry
        zos.closeEntry();
        ZipEntry ze = new ZipEntry(filePath.substring(task.getInputDir().getAbsolutePath().length() + 1, filePath.length()));
        zos.putNextEntry(ze);
        return zos;
      } else
        return zos;
    } catch (IOException e) {
      LOGGER.debug("ERROR in getting zip output stream = ",e);
      throw e;
    }
  }

}
