package com.zsirosd.preprocessor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArgumentPreprocessor {

    public ConcurrentMap<Path, BlockingQueue<ByteBuffer>> createPathAndBufferQueueFromArgs(String[] fileNamesFromArgs) {

        return Arrays.stream(fileNamesFromArgs)
                .map(fileName -> Paths.get(fileName))
                .collect(Collectors.toConcurrentMap(path -> path, o -> new LinkedBlockingQueue<ByteBuffer>()));
    }

    public List<Path> getAllFilesOfCurrentDirectory() throws IOException {

        try (Stream<Path> walk = Files.walk(Paths.get("./"))) {
            return walk
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
    }
}
