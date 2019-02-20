package compressor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kshitiz on 2/20/2019.
 */
public class FileUtil {

  public static List<File> getFilesToProcess(File dir) throws IOException {
    List<File> files = new ArrayList<File>();
    getFilesListHelper(dir, files);
    return files;
  }

  private static void getFilesListHelper(File dir, List<File> fileList) throws IOException {
    File[] files = dir.listFiles();
    for (File file : files) {
      if (file.isFile()) fileList.add(file);
      else getFilesListHelper(file, fileList);
    }
  }

  public static void emptyFolder(String dirPath) {
    File folder = new File(dirPath);
    File[] files = folder.listFiles();
    if (files != null) { //some JVMs return null for empty dirs
      for (File f : files) {
        if (f.isDirectory()) {
          emptyFolder(f.getPath());
        } else {
          f.delete();
        }
      }
    }
  }
}
