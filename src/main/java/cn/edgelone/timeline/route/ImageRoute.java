package cn.edgelone.timeline.route;

import cn.edgelone.timeline.handler.ImageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ImageRoute {

  @Bean
  public RouterFunction<ServerResponse> createImage(ImageHandler imageHandler) {
    return route(POST("/images").and(accept(MediaType.ALL)), imageHandler::createImage)
        .andRoute(GET("/images").and(accept(MediaType.APPLICATION_JSON)), imageHandler::queryImages);
  }
}
