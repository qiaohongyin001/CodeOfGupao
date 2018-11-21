package com.wolfman.mongodb.morphia.entity;

import org.mongodb.morphia.annotations.Id;

public class Member {

  @Id
  private String id;

  private String name;

  private int age;

  private String addr;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public String getAddr() {
    return addr;
  }

  public void setAddr(String addr) {
    this.addr = addr;
  }
}
