package com.synchftp.model;

import net.schmizz.sshj.xfer.InMemorySourceFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InMemoryFile extends InMemorySourceFile {

    private final String name;
    private final byte[] fileContent;

    public InMemoryFile(String name, byte[] fileContent) {
        this.name = name;
        this.fileContent = fileContent;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getLength() {
        return fileContent.length;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(fileContent);
    }
}
