**PARALLELISED COMPRESSOR - MAKE MOST OF THE RESOURCES**

Compresses files and folders into a set of compressed files such that of each
compressed file doesn’t exceed a maximum size. The same program can be used for
decompressing the files that it has generated earlier. 

**GETTING STARTED**

How to run

**Compress**

_java -jar compressor.jar #inputDirPath #outputDirPath #maxCompressionSize_

**Decompress**

input dir should be an output of compression done by this lib

_java -jar compressor.jar #inputDir #outputDir_

**SPLITTING LOGIC**
1. Creates a heap of k buckets, each bucket holding a list of files
2. Reads all the files in the input directory using File.listFiles and 
adds each file to the bucket with lowest size of files
3. Ensures equal distrbution of files across the buckets
4. Since compressing is a cpu intensive process, we should divide the 
process into n (= No of cores of system) parallel processes, which is 
also the no of buckets our program should have i.e. k.
5. Usage of heap as it always gives me the lowest bucket in O(1) time


**ASSUMPTIONS**
1. Not compressing directories with no files
2. Assuming on an average, as I saw with various files I compressed, that
any file after compressing would yield 95% of actual size. Although this
no. will vary with the content of different files, we can always build a
smarter logic to do so. Or we can also override native classes to know the
size of data written post compression