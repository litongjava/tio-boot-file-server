package com.litongjava.file.server;


import com.litongjava.hotswap.wrapper.tio.boot.TioApplicationWrapper;
import com.litongjava.jfinal.aop.annotation.AComponentScan;

@AComponentScan
public class TioBootFileApp {
  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    TioApplicationWrapper.run(TioBootFileApp.class, args);
    long end = System.currentTimeMillis();
    System.out.println((end - start) + "ms");
  }
}

