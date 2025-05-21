package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.Block;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.repository.BlockRepository;
import com.example.tomatomall.service.BlockService;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.BlockVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import com.example.tomatomall.util.TokenUtil;
import java.util.Optional;

@Service
public class BlockServiceImpl implements BlockService {
    @Autowired
    BlockRepository blockRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    SecurityUtil securityUtil;

    @Override
    public void addBlock(Integer userId){
        Optional<Block> thisBlock=blockRepository.findByUserId(userId);
        if(!thisBlock.isPresent()){
            BlockVO blockVO=new BlockVO();
            blockVO.setUserId(userId);
            blockVO.setUserName(accountRepository.findById(userId).get().getUsername());
            blockRepository.save(blockVO.toPO());
        }
    }

    @Override
    public List<BlockVO> getAllBlock(){
        List<Block> allBlock=blockRepository.findAll();
        List<BlockVO> allBlockVO=new ArrayList<>();
        for(Block block:allBlock){
            allBlockVO.add(block.toVO());
        }
        return allBlockVO;
    }

    @Override
    public Boolean judgeBlock(Integer userId){
        Optional<Block> thisBlock=blockRepository.findByUserId(userId);
        if(thisBlock.isPresent()){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void deleteBlock(Integer id){
        Optional<Block> thisBlock=blockRepository.findById(id);
        if(thisBlock.isPresent()){
            blockRepository.deleteById(id);
        }
    }
}
