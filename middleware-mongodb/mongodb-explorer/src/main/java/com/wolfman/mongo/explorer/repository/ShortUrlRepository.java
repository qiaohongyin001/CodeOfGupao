package com.wolfman.mongo.explorer.repository;

import com.wolfman.mongo.explorer.entity.ShortUrl;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShortUrlRepository extends MongoRepository<ShortUrl,String> {

//    ShortUrl findBySurl(String surl);

}
