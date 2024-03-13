# tio-boot-file-server
- 文件上传
- 文件下载
- sqlite数据库
```
CREATE TABLE "sys_upload_file" (
  "id" text NOT NULL,
  "saveFolder" TEXT NOT NULL,
  "suffixName" TEXT NOT NULL,
  "filename" TEXT NOT NULL,
  PRIMARY KEY ("id")
);
 
```