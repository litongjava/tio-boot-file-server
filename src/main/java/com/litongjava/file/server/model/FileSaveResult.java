package com.litongjava.file.server.model;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileSaveResult {
  private String id;
  private String saveFolder;
  private String suffixName;
  private String filename;
  private File file;
}
