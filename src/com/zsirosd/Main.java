package com.zsirosd;

import com.zsirosd.executor.ArgumentPreprocessor;
import com.zsirosd.thread.FileReader;
import com.zsirosd.thread.CollectAndWriteBuffers;
import com.zsirosd.thread.ConvertAndSortByteBufferToStringArray;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {


    public static void main(String[] args) {

        String processingMode;

        // path of the file, queue that contains the buffers that have been read
        ConcurrentMap<Path, BlockingQueue<ByteBuffer>> readChunkQueuesByPath;
        ArgumentPreprocessor argumentPreprocessor = new ArgumentPreprocessor();
        if (args.length > 0) {
            processingMode = args[0];
            if (ProcessingMode.THREAD.equals(processingMode)) {

                readChunkQueuesByPath = argumentPreprocessor.createPathAndBufferQueueFromArgs(Arrays.copyOfRange(args, 1, args.length));

                // start to read the chunks of each file
                readChunkQueuesByPath.forEach((path, byteBuffers) -> {

                    // read chunks of files
                    BlockingQueue<ByteBuffer> readChunks = byteBuffers;
                    Thread fileReader = new Thread(new FileReader(path, readChunks));
                    fileReader.start();

                    // sort the chunks one by one
                    BlockingQueue<String[]> sortedChunks = new LinkedBlockingQueue<>();
                    Thread queueProcessor = new Thread(new ConvertAndSortByteBufferToStringArray(readChunks, sortedChunks, fileReader, path));
                    queueProcessor.start();

                    // merge sort the sorted chunks
                    Thread mergeSort = new Thread(new CollectAndWriteBuffers(queueProcessor, sortedChunks, path));
                    mergeSort.start();
                });


            } else if (ProcessingMode.EXECUTOR.equals(processingMode)) {
                // TODO implement processing with executors
            } else {
                System.out.println("The first argument should be the processing mode");
                System.out.println("thread/executor");
                return;
            }


        } else {
            // default processing without args
            // read all files and process them with the executor mode
            //
            processingMode = "executor";
            System.out.println("Default processing mode is executor.");
            System.out.println("Reading all of the files in the current directory");
        }

    }

    private static Map<Path, ConcurrentLinkedQueue<ByteBuffer>> addBufferQueueToPath(Map<Path, ConcurrentLinkedQueue<ByteBuffer>> readChunksByPath, Path path) {
        readChunksByPath.put(path, new ConcurrentLinkedQueue<>());
        return readChunksByPath;
    }


}
