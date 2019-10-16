package com.zsirosd.thread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Queue;

public class FileReadingDispatcher implements Runnable {

    private final Path filePath;
    private final Queue<ByteBuffer> bufferQueue;
    private final int maxBufferChunkSize = 100000;

    public FileReadingDispatcher(Path filePath, Queue<ByteBuffer> bufferQueue) {
        this.filePath = filePath;
        this.bufferQueue = bufferQueue;
    }

    @Override
    public void run() {

        try {
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                    filePath, StandardOpenOption.READ);
            long maxChannelPointerPosition = channel.size();

            ByteBuffer buffer;

            // execute in a single thread
            if (maxChannelPointerPosition < maxBufferChunkSize) {
                buffer = ByteBuffer.allocate((int) maxChannelPointerPosition); // maxPosition is lower than maxBufferChunkSize which is an int value so we can safely cast
                channel.read(buffer, 0);
                bufferQueue.add(buffer);
            } else {
                buffer = ByteBuffer.allocate(maxBufferChunkSize);
                channel.read(
                        buffer, 0, buffer, new CompletionHandler<>() {

                            @Override
                            public void completed(Integer result, ByteBuffer attachment) {
                                bufferQueue.add(attachment);
                            }

                            @Override
                            public void failed(Throwable exc, ByteBuffer attachment) {
                                exc.printStackTrace();
                            }
                        });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
