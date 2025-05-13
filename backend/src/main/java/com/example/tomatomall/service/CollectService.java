package com.example.tomatomall.service;

import com.example.tomatomall.po.Collect;
import com.example.tomatomall.vo.CollectVO;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
public interface CollectService {
    String addCollect(Integer userId,Integer productId);
    List<CollectVO> getAllCollect(Integer userId);
    void deleteCollect(Integer id);
}
