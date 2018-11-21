package com.wolfman.mongodb.morphia;

import com.mongodb.MongoClient;
import com.wolfman.mongodb.morphia.entity.Member;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;

public class MorphiaTest {

  public static void main(String[] args) {
    //吗啡
    Morphia morphia = new Morphia();
    Datastore datastore = morphia.createDatastore(new MongoClient("39.107.32.43",27017),"wolfman-test");
    Member member = new Member();
    member.setName("胡昊");
    member.setAge(28);
    member.setAddr("双井");
    Key<Member> key = datastore.save(member);

    System.out.println(key.getId());



  }


}
