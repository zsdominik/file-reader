package com.zsirosd.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class MergeSortProcessor implements Runnable {
    private final Thread queueProcessor;
    private final BlockingQueue<List<String>> outputBufferQueue;

    public MergeSortProcessor(Thread queueProcessor, BlockingQueue<List<String>> outputBufferQueue) {
        this.queueProcessor = queueProcessor;
        this.outputBufferQueue = outputBufferQueue;
    }

    @Override
    public void run() {
        List<String> list = new ArrayList<>();
        while (queueProcessor.isAlive() && !outputBufferQueue.isEmpty()) {
            try {
                list.addAll(outputBufferQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Merge sort list here and write to file");
        System.out.println(list.size());
        //System.out.println(list.toString());
    }
}
