package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.CollectService;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Collect;
import com.example.tomatomall.repository.CollectRepository;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.CollectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.tomatomall.util.TokenUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class CollectServiceImpl implements CollectService {
    @Autowired
    CollectRepository collectRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    SecurityUtil securityUtil;

    @Override
    public String addCollect(Integer userId,Integer productId){
        Optional<Product> thisProduct=productRepository.findById(productId);
        if(!thisProduct.isPresent()){
            return "该商品不存在";
        }
        Optional<Collect> thisCollect=collectRepository.findByUserIdAndProductId(userId,productId);
        if(thisCollect.isPresent()){
            return "该商品已被收藏";
        }else{
            CollectVO collectVO=new CollectVO();
            collectVO.setUserId(userId);
            collectVO.setProductId(productId);
            collectRepository.save(collectVO.toPO());
            return "收藏成功";
        }
    }

    @Override
    public List<CollectVO> getAllCollect(Integer userId){
        List<Collect> collectList=collectRepository.findByUserId(userId);
        List<CollectVO> collectVOList=new ArrayList<>();
        for(Collect collect: collectList){
            collectVOList.add(collect.toVO());
        }
        return collectVOList;
    }

    @Override
    public void deleteCollect(Integer id){
        Optional<Collect> thisCollect=collectRepository.findById(id);
        collectRepository.delete(thisCollect.get());
    }
}
