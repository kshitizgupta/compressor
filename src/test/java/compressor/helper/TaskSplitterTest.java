package compressor.helper;

import compressor.domain.CompressionRequest;
import compressor.domain.CompressionType;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Created by Kshitiz on 2/26/2019.
 */
public class TaskSplitterTest {

  @Test
  public void testSplitIntoTasksWhenSingleCoreSystem() throws Exception {
    CompressionRequest compressionRequest = getDummyCompressionRequest();
    List<CompressionTaskRunner> res = TaskSplitter.splitIntoTasks(compressionRequest, 1);
    int totalNoOfFiles = 0;
    for (CompressionTaskRunner c : res) {
      totalNoOfFiles += c.task.getBucket().getFileList().size();
    }

    Assert.assertEquals(1, res.size());
    Assert.assertEquals(3, totalNoOfFiles);
  }

  @Test
  public void testSplitIntoTasksWhenCoresMuchMoreThanNoOfFiles() throws Exception {
    CompressionRequest compressionRequest = getDummyCompressionRequest();
    //when concurrency is more than no of files
    List<CompressionTaskRunner> res = TaskSplitter.splitIntoTasks(compressionRequest, 6);
    int totalNoOfFiles = 0;
    for (CompressionTaskRunner c : res) {
      totalNoOfFiles += c.task.getBucket().getFileList().size();
    }

    Assert.assertEquals(3, res.size());
    Assert.assertEquals(3, totalNoOfFiles);

  }

  @Test
  public void testSplitIntoTasksWhenCoresMoreThanNoOfFilesSuchThatNoEmptyBuckets() throws Exception {
    CompressionRequest compressionRequest = getDummyCompressionRequest();

    List<CompressionTaskRunner> res = TaskSplitter.splitIntoTasks(compressionRequest, 2);
    int totalNoOfFiles = 0;
    for (CompressionTaskRunner c : res) {
      totalNoOfFiles += c.task.getBucket().getFileList().size();
    }

    Assert.assertEquals(2, res.size());
    Assert.assertEquals(3, totalNoOfFiles);
  }

  private CompressionRequest getDummyCompressionRequest() {
    ClassLoader classLoader = getClass().getClassLoader();
    File file1 = new File(classLoader.getResource("isSameDirTests/dirA").getFile());

    return new CompressionRequest(file1.getAbsolutePath(), "", 1, CompressionType.zip);
  }
}