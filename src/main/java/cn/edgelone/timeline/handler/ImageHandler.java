package cn.edgelone.timeline.handler;

import cn.edgelone.timeline.dao.ImageDao;
import cn.edgelone.timeline.model.Image;
import cn.edgelone.timeline.util.QiniuUtil;
import cn.edgelone.timeline.vo.ImageInfo;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lk
 */
@Component
@Slf4j
public class ImageHandler {

  @Autowired
  private ImageDao imageDao;

  public Mono<ServerResponse> createImage(ServerRequest request) {

    String url = request.queryParam("url").get();
    String fileName = request.queryParam("file_name").get();

    File file = new File("../Image.jpg");

    try {
      URL r = new URL(url);
      FileUtils.copyURLToFile(r, file);
    } catch (Exception e) {
      log.error("copy url to file fail", e);
    }

    if (QiniuUtil.uploadFile(file, fileName)) {
      file.deleteOnExit();
      Image image = new Image(fileName);
      //FIXME why not effect?
      imageDao.save(image).then();
      return ServerResponse.ok()
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .body(BodyInserters.fromObject("success"));
    }
    return ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).body(BodyInserters.empty());

  }

  public Mono<ServerResponse> queryImages(ServerRequest request) {
    Map<String, String> params = request.queryParams().toSingleValueMap();
    String fileName = Strings.emptyToNull(params.get("file_name"));

    Flux<Image> images;
    if (StringUtils.isEmpty(fileName)) {
      images = imageDao.findAll();
    } else {
      Example<Image> example = Example.of(new Image(fileName));
      images = imageDao.findAll(example);
    }

    Flux<ImageInfo> ii = images.map(this::convertImageInfo).cache();

    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .body(ii, ImageInfo.class);
  }

  private ImageInfo convertImageInfo(Image image) {
    ImageInfo imageInfo = new ImageInfo();
    BeanUtils.copyProperties(image, imageInfo);
    imageInfo.setUrl(QiniuUtil.downloadImageFromPra(image.getFileName(), null));
    return imageInfo;
  }
}
