package cn.edgelone.timeline.dao;

import cn.edgelone.timeline.model.Image;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lk
 * @date 2018/9/12
 */
@Repository
@Primary
public interface ImageDao extends ReactiveMongoRepository<Image,String> {

}
