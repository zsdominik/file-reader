package com.zsirosd.preprocessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArgumentPreprocessor {

    public List<Path> getFilePathsByArgs(String[] fileNamesFromArgs) {

        return Arrays.stream(fileNamesFromArgs)
                .map(fileName -> Paths.get(fileName))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
    }

    public List<Path> getAllFilesOfCurrentDirectory() throws IOException {

        try (Stream<Path> walk = Files.walk(Paths.get("./"))) {
            return walk
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
    }
}
