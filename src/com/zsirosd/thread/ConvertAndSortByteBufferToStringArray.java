package com.zsirosd.thread;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

public class ConvertAndSortByteBufferToStringArray implements Runnable {

    private final BlockingQueue<ByteBuffer> inputBufferQueue;
    private final BlockingQueue<String[]> outputBufferQueue;
    private final Thread fileReader;
    private final Path currentFile;

    public ConvertAndSortByteBufferToStringArray(BlockingQueue<ByteBuffer> inputBufferQueue, BlockingQueue<String[]> outputBufferQueue, Thread fileReader, Path currentFile) {
        this.inputBufferQueue = inputBufferQueue;
        this.outputBufferQueue = outputBufferQueue;
        this.fileReader = fileReader;
        this.currentFile = currentFile;
    }

    // sort a chunk of input from a file
    //
    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        int amountOfProcessedBuffers = 0;
        System.out.println("(2) Sorting buffers of " + currentFile.getFileName() + " started in " + threadName);
        while (fileReader.isAlive()) {
            try {
                while (!inputBufferQueue.isEmpty()) {
                    String textChunkOfBuffer = new String(inputBufferQueue.take().array(), StandardCharsets.UTF_8);
                    String specialCharactersRegex = "[ |\n|\r|\r\n|,|\\.|\"|(|)|*|$|[0-9]|%|#|:|/|;|=|-]+";
                    String[] arrayOfWords = textChunkOfBuffer.split(specialCharactersRegex);
                    Arrays.sort(arrayOfWords);
                    outputBufferQueue.put(arrayOfWords);
                    amountOfProcessedBuffers++;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("(2) All queues of " + currentFile.getFileName() + " (" + amountOfProcessedBuffers + ") processed in thread: " + threadName);
    }

}
