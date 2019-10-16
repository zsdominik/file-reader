package com.zsirosd.thread;

import java.io.File;

public class FileReader implements Runnable {

    private final File file;

    public FileReader(File file) {
        this.file = file;
    }

    @Override
    public void run() {

    }
}
