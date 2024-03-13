package com.litongjava.file.server.config;

import com.litongjava.file.server.requesthandler.FileRequestHanlder;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.jfinal.aop.annotation.AInitialization;
import com.litongjava.jfinal.aop.annotation.BeforeStartConfiguration;
import com.litongjava.tio.boot.server.TioBootServer;
import com.litongjava.tio.http.server.handler.SimpleHttpRoutes;

@BeforeStartConfiguration
public class HttpServerRequestHanlderConfig {

  @AInitialization
  public void httpRoutes() {

    // 创建simpleHttpRoutes
    SimpleHttpRoutes simpleHttpRoutes = new SimpleHttpRoutes();
    // 创建controller
    FileRequestHanlder asrSubmitRequestHanlder = Aop.get(FileRequestHanlder.class);

    // 添加action
    simpleHttpRoutes.add("/upload", asrSubmitRequestHanlder::upload);
    simpleHttpRoutes.add("/download", asrSubmitRequestHanlder::download);

    // 将simpleHttpRoutes添加到TioBootServer
    TioBootServer.me().setHttpRoutes(simpleHttpRoutes);
  }

}
