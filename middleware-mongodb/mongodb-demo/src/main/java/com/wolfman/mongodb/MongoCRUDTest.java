package com.wolfman.mongodb;

import com.mongodb.*;

public class MongoCRUDTest {

  public static void main(String[] args) {
    Mongo mongo = new Mongo("39.107.32.43",27017);
    DB db = new DB(mongo,"gupaoedu-demo");
    DBCollection collection =  db.getCollection("member");

    //类比法：JDBC，相对来说比较底层
    DBObject dbObject = new BasicDBObject();
    dbObject.put("name","huhao");
    dbObject.put("age",18);
    dbObject.put("addr","北京");
    collection.insert(dbObject);

    DBCursor cursor = collection.find();
    for (DBObject obj : cursor ){
      System.out.println(obj);
    }

    //Morphia,mongodb界的一个ORM鼻祖

    //MongoTemplate


  }



}
