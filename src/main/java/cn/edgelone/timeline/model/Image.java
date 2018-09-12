package cn.edgelone.timeline.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * @author lk
 * @date 2018/9/12
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Image {
  @Id
  private String id;
  private String fileName;
  private Instant createTime;

  public Image(String fileName) {
    this.id = UUID.randomUUID().toString();
    this.fileName = fileName;
    this.createTime = Instant.now();
  }
}
