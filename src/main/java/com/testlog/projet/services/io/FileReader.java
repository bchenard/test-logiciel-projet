package com.testlog.projet.services.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader implements IFileReader {
    @Override
    public String readAll(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }
}

