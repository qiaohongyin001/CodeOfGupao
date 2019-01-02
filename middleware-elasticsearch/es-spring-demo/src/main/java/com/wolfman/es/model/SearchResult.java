package com.wolfman.es.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class SearchResult {

  private Long total;//记录总数

  private Float useTime;//搜索花费时间(毫秒)

  private String distance;//距离单位(米)

  private List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();//数据集合

  public List<Map<String, Object>> getData() {
    return data;
  }

  public void setData(List<Map<String, Object>> data) {
    this.data = data;
  }


}
