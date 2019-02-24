package compressor;

import compressor.util.FileUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by Kshitiz on 2/26/2019.
 */
public class FileUtilTest {

  @Test
  public void testIsSameDirectoryTrue() throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    File file1 = new File(classLoader.getResource("isSameDirTests/dirA").getFile());
    File file2 = new File(classLoader.getResource("isSameDirTests/dirACopy").getFile());
    boolean isSame = FileUtil.isSameDirectory(file1, file2);
    Assert.assertTrue(isSame);
  }

  @Test
  public void testIsSameDirectoryFalse() throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    File file1 = new File(classLoader.getResource("isSameDirTests/dirA").getFile());
    File file2 = new File(classLoader.getResource("isSameDirTests/dirACopyWithDiff").getFile());
    boolean isSame = FileUtil.isSameDirectory(file1,file2);
    Assert.assertFalse(isSame);
  }
}