package com.zsirosd.thread;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;

public class FileReadingDispatcher implements Runnable {

    private final Path filePath;
    private final BlockingQueue<ByteBuffer> outputBufferQueue;

    public FileReadingDispatcher(Path filePath, BlockingQueue<ByteBuffer> outputBufferQueue) {
        this.filePath = filePath;
        this.outputBufferQueue = outputBufferQueue;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();

        System.out.println(threadName + " started.");
        try {
            RandomAccessFile fileReader = new RandomAccessFile(filePath.toFile(), "r");
            FileChannel channel = fileReader.getChannel();
            double maxBufferSize = 1000;
            int maxAmountOfBuffers = (int) Math.ceil((double) channel.size() / maxBufferSize);

            // TODO what if maxAmountOfBuffers larger than Integer.MAX_VALUE?
            long currentPosition = 0L;
            for (int i = 0; i < maxAmountOfBuffers; i++) {
                ByteBuffer buffer = ByteBuffer.allocate((int) maxBufferSize);
                channel.read(buffer, currentPosition);
                //System.out.println("Add buffer to queue: " + outputBufferQueue.hashCode());
                outputBufferQueue.put(buffer);
                currentPosition += maxBufferSize;
                fileReader.seek(currentPosition);
            }

            System.out.println("All buffers read in: " + threadName + ".");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
