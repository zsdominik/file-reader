package com.zsirosd;

import com.zsirosd.preprocessor.ArgumentPreprocessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String processingMode;
        List<Path> filePathsToRead;
        ArgumentPreprocessor argumentPreprocessor = new ArgumentPreprocessor();

        if (args.length > 0) {
            processingMode = args[0];
            if (!"thread".equals(processingMode) || !"executor".equals(processingMode)) {
                System.out.println("The first argument should be the processing mode");
                System.out.println("thread/executor");
                return;
            }

            filePathsToRead = argumentPreprocessor.getFilePathsByArgs(Arrays.copyOfRange(args, 1, args.length - 1));

            // TODO implement processing with native threads
            // TODO implement processing with executors

        } else {
            processingMode = "executor";
            System.out.println("Default processing mode is executor.");
            System.out.println("Reading all of the files in the current directory");
            try {
                filePathsToRead = argumentPreprocessor.getAllFilesOfCurrentDirectory();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }


}
