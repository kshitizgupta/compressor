package compressor;


import compressor.domain.RequestType;
import compressor.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Kshitiz on 2/26/2019.
 */
@RunWith(Parameterized.class)
public class IntegrationTest {

  private String inputFilePath;
  private String outputBaseDir;
  private String outputUnZipPath;
  private String outputZipPath;
  private int maxPermissibleSize;

  public IntegrationTest(String inputFilePath, String outputBaseDir, String outputZipPath, String outputUnZipPath, int maxPermissibleSize) {
    this.inputFilePath = inputFilePath;
    this.outputBaseDir = outputBaseDir;
    this.outputZipPath = outputZipPath;
    this.outputUnZipPath = outputUnZipPath;
    this.maxPermissibleSize = maxPermissibleSize;
  }

  @Parameterized.Parameters
  public static Collection input() {
    return Arrays.asList(new Object[][]{
        {"isSameDirTests/dirA", "output", "zippedOutput", "unzippedOutput", 40},
        {"isSameDirTests/dirA", "output", "zippedOutput", "unzippedOutput", 4},
        {"isSameDirTests/dirA", "output", "zippedOutput", "unzippedOutput", 1}
    });
  }

  @Test
  public void test() throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    File file1 = new File(classLoader.getResource(inputFilePath).getFile());
    String workingDir = System.getProperty("user.dir");
    String zippedOutputPath = workingDir + File.separatorChar + outputBaseDir + File.separatorChar + outputZipPath;
    String inputFolderToBeCompressed = file1.getAbsolutePath();
    String argsCompress[] = {
        RequestType.compress.name(),
        inputFolderToBeCompressed,
        zippedOutputPath,
        String.valueOf(maxPermissibleSize)
    };
    App.main(argsCompress);

    String unzippedOutputPath = workingDir + File.separatorChar + outputBaseDir + File.separatorChar + outputUnZipPath;
    String argsDecompress[] = {
        RequestType.decompress.name(),
        zippedOutputPath,
        unzippedOutputPath,
    };
    App.main(argsDecompress);
    boolean isContentEqual = FileUtil.isSameDirectory(inputFolderToBeCompressed, unzippedOutputPath);
    Assert.assertTrue("Unzipped output is not same as input", isContentEqual);

    String outputPath = workingDir + File.separatorChar + outputBaseDir;
    FileUtils.deleteDirectory(new File(outputPath));
  }

}
