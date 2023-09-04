package com.leucine.streem.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class CustomMultipartFile implements MultipartFile {
  private final byte[] data;
  private final String fileName;

  public CustomMultipartFile(InputStream stream, String name) throws IOException {
    data = stream.readAllBytes();
    fileName = name;
  }

  @Override
  public String getName() {
    return fileName;
  }

  @Override
  public String getOriginalFilename() {
    return fileName;
  }

  @Override
  public String getContentType() {
    return "application/octet-stream";
  }

  @Override
  public boolean isEmpty() {
    return data.length == 0;
  }

  @Override
  public long getSize() {
    return data.length;
  }

  @Override
  public byte[] getBytes() {
    return data;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(data);
  }

  @Override
  public void transferTo(File dest) {
    try (OutputStream outputStream = new FileOutputStream(dest)) {
      outputStream.write(data);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
