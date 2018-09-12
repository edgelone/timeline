package cn.edgelone.timeline.util;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Properties;

/**
 * @author lk
 * @date 2018/9/12
 */
@Slf4j
public class QiniuUtil {
  private final static Properties PROPERTIES = new Properties();

  static {
    InputStream in = QiniuUtil.class.getResourceAsStream("/properties/qn.properties");
    try {
      PROPERTIES.load(in);
    } catch (IOException e) {
      log.error("qiniu util init fail", e);
    }
  }

  private static final String ak = PROPERTIES.getProperty("ak");
  private static final String sk = PROPERTIES.getProperty("sk");
  private static final Auth auth = Auth.create(ak, sk);
  private static final String host = PROPERTIES.getProperty("host");
  private static final String bucket = PROPERTIES.getProperty("bucket");
  private static final UploadManager uploadManager = new UploadManager(new Configuration());

  public static String downloadImageFromPra(String fileKey, String suffix) {
    if (StringUtils.isEmpty(fileKey)) {
      return null;
    }
    try {
      String encodedFileName = URLEncoder.encode(fileKey, "utf-8");
      if (suffix != null) {
        suffix = "-" + suffix;
      } else {
        suffix = "";
      }
      String publicUrl = String.format("%s/%s", host, encodedFileName) + suffix;

      return auth.privateDownloadUrl(publicUrl, 3600);
    } catch (Exception e) {
      log.error("download file from qiniu error", e);
    }
    return null;
  }

  public static boolean uploadFile(File file, String fileName) {

    Response response = null;
    try {
      response = uploadManager.put(file, StringUtils.isEmpty(fileName) ? file.getName() : fileName, auth.uploadToken(bucket));
    } catch (QiniuException e) {
      log.error("upload file fail", e);
      return false;
    }
    return response.isOK();
  }

  public static boolean uploadFile(File file) {
    return uploadFile(file, null);
  }
}
