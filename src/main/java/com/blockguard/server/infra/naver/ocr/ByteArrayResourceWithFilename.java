package com.blockguard.server.infra.naver.ocr;

import org.springframework.core.io.ByteArrayResource;

public class ByteArrayResourceWithFilename extends ByteArrayResource {
    private final String filename;

    public ByteArrayResourceWithFilename(byte[] byteArray, String filename) {
        super(byteArray);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return filename;
    }

}
