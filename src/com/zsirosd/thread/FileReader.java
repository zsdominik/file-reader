package com.zsirosd.thread;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;

public class FileReader implements Runnable {

    private final Path filePath;
    private final BlockingQueue<ByteBuffer> outputBufferQueue;
    private static final double MAX_BUFFER_SIZE = 1000;

    public FileReader(Path filePath, BlockingQueue<ByteBuffer> outputBufferQueue) {
        this.filePath = filePath;
        this.outputBufferQueue = outputBufferQueue;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.println("(1) File reading started in "+ threadName + " of file: " + filePath.getFileName().toString());

        try {
            RandomAccessFile fileReader = new RandomAccessFile(filePath.toFile(), "r");
            FileChannel channel = fileReader.getChannel();
            int maxAmountOfBuffers = (int) Math.ceil((double) channel.size() / MAX_BUFFER_SIZE);

            // TODO what if maxAmountOfBuffers larger than Integer.MAX_VALUE?
            // TODO check if utf-8, not to cut chars in the half
            long currentPosition = 0L;
            int amountOfBuffersRead = 0;
            for (int i = 0; i < maxAmountOfBuffers; i++) {
                ByteBuffer buffer = ByteBuffer.allocate((int) MAX_BUFFER_SIZE);
                channel.read(buffer, currentPosition);
                outputBufferQueue.put(buffer);
                currentPosition += MAX_BUFFER_SIZE;
                fileReader.seek(currentPosition);
                amountOfBuffersRead++;
            }

            System.out.println("(1) All buffers (" + amountOfBuffersRead + ") read in: " + threadName + ".");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
