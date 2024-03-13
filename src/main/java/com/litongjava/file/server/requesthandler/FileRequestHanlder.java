package com.litongjava.file.server.requesthandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.litongjava.file.server.model.FileSaveResult;
import com.litongjava.file.server.services.FileService;
import com.litongjava.jfinal.aop.annotation.AAutowired;
import com.litongjava.tio.http.common.HttpRequest;
import com.litongjava.tio.http.common.HttpResponse;
import com.litongjava.tio.http.common.UploadFile;
import com.litongjava.tio.http.server.util.Resps;
import com.litongjava.tio.utils.resp.RespVo;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileRequestHanlder {
  @AAutowired
  private FileService fileService;

  public HttpResponse upload(HttpRequest request) {
    UploadFile file = request.getUploadFile("file");
    RespVo respVo;
    if (file != null) {
      String name = file.getName();
      byte[] fileData = file.getData();
      log.info("upload file size:{}", fileData.length);
      // save file
      FileSaveResult fileSaveResult = fileService.save(name, fileData);
      fileSaveResult.setFile(null);

      log.info("save file finished");
      respVo = RespVo.ok(fileSaveResult);

    } else {
      respVo = RespVo.fail("fail").code(-1);
    }

    return Resps.json(request, respVo);
  }

  public HttpResponse download(HttpRequest request) {
    String id = request.getParam("id");
    log.info("id:{}", id);
    File file = fileService.getUploadFile(id);
    int available;
    try {
      @Cleanup
      InputStream inputStream = new FileInputStream(file);
      available = inputStream.available();
      byte[] fileBytes = new byte[available];
      inputStream.read(fileBytes, 0, available);
      // String contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document; charset=utf-8";
      String contentType = "";
      HttpResponse response = Resps.bytesWithContentType(request, fileBytes, contentType);
      return response;
    } catch (IOException e) {
      e.printStackTrace();
      return Resps.json(request, RespVo.fail("Error generating captcha"));
    }
  }
}
