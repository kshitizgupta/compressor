package compressor;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by Kshitiz on 2/19/2019.
 */
public class CompressorImpl implements Compressor{
  private static final Logger LOGGER = Logger.getLogger(CompressorImpl.class);
  private final int BUFFER_SIZE = 1024;
  private final int DEFAULT_MAX_PERMISSIBLE_COMPRESSED_SIZE = 1024 * 100;
  private final String OUTPUT_FILE_NAME = "output";
  private final String SEPARATOR = "_";
  private final String SPLIT_SUFFIX = "_split_";
  private final String FILE_EXTENSION = ".zip";
  private int maxPermissibleCompressedSize = 0;

  private int currSplitSizeInKb = 0;
  private int splitIndex = 0;
  private FileOutputStream fos;
  private ZipOutputStream zos;


  public void compress(CompressionRequest task) throws IOException {
    maxPermissibleCompressedSize = task.getMaxCompressedSize();
    compress(task.getFilesListInDir(), task.getZipInputDir(), task.getZipOutputDir(), task.getPart());
  }

  private void compress(List<String> filesListInDir, File zipDir, String outputDir, int part) throws IOException {
    for (String filePath : filesListInDir) {
      LOGGER.debug(Thread.currentThread().getName() + ": Zipping " + filePath);

      //read the file and write to ZipOutputStream
      FileInputStream fis = new FileInputStream(filePath);

      byte[] buffer = new byte[BUFFER_SIZE];
      int len;
      while ((len = fis.read(buffer)) > 0) {
        getZos(outputDir, filePath, zipDir, part).write(buffer, 0, len);
        currSplitSizeInKb += len;
      }
      zos.closeEntry();
      fis.close();
      LOGGER.debug("Done Zipping " + filePath);
    }
    zos.close();
    fos.close();
  }

  public void decompress(String zipDir, String destPath) throws IOException {
    File dir = new File(destPath);
    if (!dir.exists()) dir.mkdirs();

    //buffer for read and write data to file
    byte[] buffer = new byte[BUFFER_SIZE];

    File[] files = new File(zipDir).listFiles();

    HashMap<String, List<File>> filesByName = new HashMap<String, List<File>>();

    for (File file : files) {
      String fileName = file.getName();
      String fileNamePrefix = fileName.substring(0, fileName.indexOf(SEPARATOR, fileName.indexOf(SEPARATOR) + 1));
      if (!filesByName.containsKey(fileNamePrefix)) {
        filesByName.put(fileNamePrefix, new ArrayList<File>());
      }
      filesByName.get(fileNamePrefix).add(file);
    }

    for (Map.Entry<String, List<File>> entry : filesByName.entrySet()) {
      try {
        for (File file : entry.getValue()) {
          FileInputStream fis = new FileInputStream(file.getPath());
          ZipInputStream zis = new ZipInputStream(fis);
          ZipEntry ze = zis.getNextEntry();
          File newFile = null;
          while (ze != null) {
            String fileName = ze.getName();
            if (newFile == null) {
              newFile = new File(destPath + File.separator + fileName);
              LOGGER.debug("Unzipping to " + newFile.getAbsolutePath());

              //create directories for sub directories in zip
              new File(newFile.getParent()).mkdirs();
            }
            if (fos == null) fos = new FileOutputStream(newFile);

            int len;
            while ((len = zis.read(buffer)) > 0) {
              fos.write(buffer, 0, len);
            }
            //close this ZipEntry
            zis.closeEntry();
            ze = zis.getNextEntry();
          }
          //close last ZipEntry
          zis.closeEntry();
          zis.close();
          fis.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        closeFos();
      }
    }
  }

  void closeFos() throws IOException {
    fos.close();
    fos = null;
  }

  /**
   * Gets the zip output stream depending on the size limitation.
   * Case 1 : Initializes the zip output stream in case it is still null
   * Case 2 : In case the split size has overrun the max permissible size,
   * closes the current stream, creates a new one
   * Case 3 : In case split size is well under the max permissible size, returns the current stream
   *
   * @param outputDir
   * @param filePath
   * @param zipDir
   * @param part
   * @return
   * @throws IOException
   */
  private ZipOutputStream getZos(String outputDir, String filePath, File zipDir, int part) throws IOException {
    try {
      if (zos == null) {
        fos = new FileOutputStream(getOutputFileName(outputDir, part));
        splitIndex++;
        zos = new ZipOutputStream(fos);
        //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
        ZipEntry ze = new ZipEntry(filePath.substring(zipDir.getAbsolutePath().length() + 1, filePath.length()));
        zos.putNextEntry(ze);
        return zos;
      }
      if (currSplitSizeInKb >= getMaxPermissibleCompressedSize()) {
        LOGGER.debug(Thread.currentThread().getName() + ": MaxSize exceeded, creating split no " + splitIndex + " for file " + filePath);
        zos.flush();
        zos.closeEntry();
        zos.close();
        fos.close();
        fos = new FileOutputStream(getOutputFileName(outputDir, part));
        splitIndex++;
        currSplitSizeInKb = 0;
        zos = new ZipOutputStream(fos);
        //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
        ZipEntry ze = new ZipEntry(filePath.substring(zipDir.getAbsolutePath().length() + 1, filePath.length()));
        zos.putNextEntry(ze);
        return zos;
      } else return zos;

    } catch (IOException e) {
      LOGGER.debug("ERROR = " + e);
      throw e;
    }
  }

  private String getOutputFileName(String outputDir, int part) {
    return outputDir + OUTPUT_FILE_NAME + SEPARATOR + part + SPLIT_SUFFIX + splitIndex + FILE_EXTENSION;
  }

  private int getMaxPermissibleCompressedSize() {
    return maxPermissibleCompressedSize == 0 ? DEFAULT_MAX_PERMISSIBLE_COMPRESSED_SIZE : maxPermissibleCompressedSize;
  }

}
