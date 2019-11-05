package com.zsirosd.executor;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class ArgumentPreprocessor {

    public ConcurrentMap<Path, BlockingQueue<ByteBuffer>> createPathAndBufferQueueFromArgs(String[] fileNamesFromArgs) {

        return Arrays.stream(fileNamesFromArgs)
                .map(fileName -> Paths.get(fileName))
                .collect(Collectors.toConcurrentMap(path -> path, o -> new LinkedBlockingQueue<>()));
    }

}
