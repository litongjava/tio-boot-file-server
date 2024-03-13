package com.litongjava.file.server.services;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import com.litongjava.data.services.DbJsonService;
import com.litongjava.file.server.constants.AppConstants;
import com.litongjava.file.server.model.FileSaveResult;
import com.litongjava.jfinal.aop.annotation.AAutowired;
import com.litongjava.jfinal.plugin.activerecord.Db;
import com.litongjava.jfinal.plugin.activerecord.Record;
import com.litongjava.tio.utils.hutool.FileUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileService {
  @AAutowired
  private DbJsonService dbJsonService;

  public FileSaveResult save(String filename, byte[] fileData) {
    String newFilename = UUID.randomUUID().toString();

    FileSaveResult fileSaveResult = new FileSaveResult();
    fileSaveResult.setId(newFilename);
    fileSaveResult.setFilename(filename);

    int lastIndexOf = filename.lastIndexOf(".");
    if (lastIndexOf > 0) {
      String substring = filename.substring(lastIndexOf);
      fileSaveResult.setSuffixName(substring);
      newFilename += substring;
    }
    // create dirs
    File uploadFolderFile = new File(AppConstants.UPlOAD_FOLDER_NAME);
    if (!uploadFolderFile.exists()) {
      uploadFolderFile.mkdirs();
      log.info("crate upload dir:{}", uploadFolderFile.getAbsolutePath());
    }

    newFilename = AppConstants.UPlOAD_FOLDER_NAME + "/" + newFilename;
    fileSaveResult.setSaveFolder(AppConstants.UPlOAD_FOLDER_NAME);
    log.info("newFilename:{}", newFilename);

    // save to folder
    File file = new File(newFilename);
    try {
      FileUtil.writeBytes(fileData, file);
      fileSaveResult.setFile(file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // save to db
    // vresion 1
    Record record = new Record();
    record.set("id", fileSaveResult.getId());
    record.set("saveFolder", fileSaveResult.getSaveFolder());
    record.set("suffixName", fileSaveResult.getSuffixName());
    record.set("filename", filename);
    // version 2 test fail
    // Record record = Record.fromBean(fileSaveResult);
    boolean save = Db.save("sys_upload_file", record);
    if (save) {
      return fileSaveResult;
    } else {
      log.error("save file error:{}", filename);
    }
    return fileSaveResult;
  }

  public File getUploadFile(FileSaveResult result) {
    String filename = result.getSaveFolder() + "/" + result.getId() + result.getSuffixName();
    return new File(filename);
  }

  public File getUploadFile(String id) {
    Record record = Db.findById("sys_upload_file", id);
    FileSaveResult fileSaveResult = record.toBean(FileSaveResult.class);
    log.info("select from db:{}",fileSaveResult);
    return getUploadFile(fileSaveResult);
  }
}

