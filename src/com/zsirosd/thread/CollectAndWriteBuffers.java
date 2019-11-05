package com.zsirosd.thread;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class CollectAndWriteBuffers implements Runnable {
    private final Thread queueProcessor;
    private final BlockingQueue<String[]> queueOfSortedWordArrays;
    private final Path newFilePath;
    private final Path oldFilePath;

    public CollectAndWriteBuffers(Thread queueProcessor, BlockingQueue<String[]> queueOfSortedWordArrays, Path oldFilePath) {
        this.queueProcessor = queueProcessor;
        this.queueOfSortedWordArrays = queueOfSortedWordArrays;
        this.oldFilePath = oldFilePath;
        this.newFilePath = new File("sorted_" + oldFilePath.getFileName().toString()).toPath();
    }

    @Override
    public void run() {
        List<String[]> list = new ArrayList<>();
        int amountOfElementsProcessed = 0;
        String threadName = Thread.currentThread().getName();

        System.out.println("(3) Collect buffer of file "+ oldFilePath.getFileName().toString() +" started in "+ threadName);

        // while the processing thread is alive the queue must get content that we should process
        while (queueProcessor.isAlive()) {
            try {
                while (!queueOfSortedWordArrays.isEmpty()) {
                    list.add(queueOfSortedWordArrays.take());
                    amountOfElementsProcessed++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("(3) Collect buffer of file "+ oldFilePath.getFileName().toString() +" finished in "+ threadName);

        System.out.println("(4) Sort list elements (" + amountOfElementsProcessed + ") and write to file in " + threadName);
        String[] sortedText = list
                .parallelStream()
                .flatMap(Arrays::stream)
                .sorted()
                .toArray(String[]::new);

        BufferedWriter outputWriter;
        System.out.println("(5) File writing started in "+ threadName + " to file: " + newFilePath.getFileName().toString());
        try {
            outputWriter = new BufferedWriter(new FileWriter(newFilePath.getFileName().toFile()));
            for (String text : sortedText) {
                outputWriter.write(text + " ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
