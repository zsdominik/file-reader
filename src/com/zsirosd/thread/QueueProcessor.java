package com.zsirosd.thread;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class QueueProcessor implements Runnable {

    private final BlockingQueue<ByteBuffer> inputBufferQueue;
    private final BlockingQueue<List<String>> outputBufferQueue;
    private final Thread dispatcher;

    public QueueProcessor(BlockingQueue<ByteBuffer> inputBufferQueue, BlockingQueue<List<String>> outputBufferQueue, Thread dispatcher) {
        this.inputBufferQueue = inputBufferQueue;
        this.outputBufferQueue = outputBufferQueue;
        this.dispatcher = dispatcher;
    }

    // sort a chunk of input from a file
    //
    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        while (dispatcher.isAlive()) {
            try {
                //System.out.println("Processing queue in thread: " + threadName);
                String textChunkOfBuffer = new String(inputBufferQueue.take().array(), StandardCharsets.UTF_8);
                String specialCharactersRegex = "[ |\n|\r|\r\n|,|\\.|\"|(|)|*|$|[0-9]|%|#|:|/|;|=|-]+"; // TODO refact this
                //System.out.println("Splitting queue in thread: " + threadName);
                List<String> listOfWords = Arrays.asList(textChunkOfBuffer.split(specialCharactersRegex));
                //System.out.println("Sorting queue in thread: " + threadName);
                listOfWords.sort(Comparator.comparing(String::toString));
                outputBufferQueue.put(listOfWords);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("All queues processed in thread: " + threadName);
    }

}
