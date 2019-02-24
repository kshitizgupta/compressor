package compressor.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kshitiz on 2/20/2019.
 */
public class FileUtil {

  public static List<File> getFilesToProcess(String dirPath) throws IOException {
    File dir = new File(dirPath);
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

  public static void prepareFolder(String dirPath) throws IOException {
    File dir = new File(dirPath);

    if (!dir.exists()) {
      boolean result = dir.mkdirs();
      return;
    }
    emptyThisDir(dir);
  }

  public static long getDirSize(String dirPath) throws IOException {
    File dir = new File(dirPath);
    return FileUtils.sizeOf(dir);
  }

  public static void emptyThisDir(String dirPath) throws IOException {
    File dir = new File(dirPath);
    emptyThisDir(dir);
  }

  private static void emptyThisDir(File dir) throws IOException {
    File[] files = dir.listFiles();
    if (files != null) { //some JVMs return null for empty dirs
      for (File f : files) {
        if (f.isDirectory()) {
          prepareFolder(f.getPath());
        } else {
          f.delete();
        }
      }
    }
  }

  public static boolean exists(String dirPath) {
    File dir = new File(dirPath);
    return dir.exists();
  }

  public static boolean isSame(File file1, File file2) throws IOException {
    return FileUtils.contentEquals(file1, file2);
  }

  public static boolean isSameDirectory(File dir1, File dir2) throws IOException {
    File[] files1 = dir1.listFiles();
    File[] files2 = dir2.listFiles();

    if (files1.length != files2.length) return false;
    for (int i = 0; i < files1.length; i++) {
      File file1 = files1[i];
      for (int j = 0; j < files2.length; j++) {
        File file2 = files2[j];
        if (file1.isDirectory() && file2.isDirectory() && file2.getName().equals(file2.getName())) {
          boolean isSame = isSameDirectory(file1.getPath(), file2.getPath());
          if (!isSame) return false;
        } else if (file1.getName().equals(file2.getName())) {
          boolean isSame = isSame(file1, file2);
          if (!isSame) return false;
        }
      }
    }
    return true;
  }

  public static boolean isSameDirectory(String path1, String path2) throws IOException {
    File dir1 = new File(path1);
    File dir2 = new File(path2);
    return isSameDirectory(dir1, dir2);
  }
}
