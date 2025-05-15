package com.example.tomatomall.service;

import com.example.tomatomall.vo.BlockVO;

import java.util.List;
public interface BlockService {
    void addBlock(Integer userId);
    List<BlockVO> getAllBlock();
    Boolean judgeBlock(Integer userId);
    void deleteBlock(Integer id);
}
