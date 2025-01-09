package com.testlog.projet.services.io;

import java.io.IOException;

public interface IFileReader {
    String readAll(String filePath) throws IOException;
}
