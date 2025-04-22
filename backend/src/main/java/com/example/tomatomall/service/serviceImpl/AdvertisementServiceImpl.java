package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Advertisement;
import com.example.tomatomall.repository.AdvertisementRepository;
import com.example.tomatomall.service.AdvertisementService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.AdvertisementVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdvertisementServiceImpl implements AdvertisementService{
    @Autowired
    AdvertisementRepository advertisementRepository;

    public List<AdvertisementVO> getAllAdvertisements(){
        List<Advertisement> allAdvertisements=advertisementRepository.findAll();
        List<AdvertisementVO> allAdvertisementVOs=new ArrayList<>();
        for(Advertisement advertisement:allAdvertisements){
            AdvertisementVO advertisementVO=advertisement.toVO();
            allAdvertisementVOs.add(advertisementVO);
        }
        return allAdvertisementVOs;
    }

    public String updateAdvertisement(AdvertisementVO advertisementVO){
        Optional<Advertisement> thisAdvertisement=advertisementRepository.findById(advertisementVO.getId());
        if(!thisAdvertisement.isPresent()){
            return "未找到该广告";
        }
        Advertisement advertisement=advertisementRepository.findById(advertisementVO.getId()).get();
        if(advertisementVO.getTitle()!=null){
            advertisement.setTitle(advertisementVO.getTitle());
        }
        if(advertisementVO.getContent()!=null){
            advertisement.setContent(advertisementVO.getContent());
        }
        if(advertisementVO.getImgUrl()!=null){
            advertisement.setImageUrl(advertisementVO.getImgUrl());
        }
        if(advertisementVO.getProductId()!=null){
            advertisement.setProductId(advertisementVO.getProductId());
        }else{
            return "广告对应的商品id不能为空";
        }
        advertisementRepository.save(advertisement);
        return "更新成功";
    }

    public AdvertisementVO createAdvertisement(AdvertisementVO advertisementVO){
        Advertisement advertisement=advertisementVO.toPO();
        advertisementRepository.save(advertisement);
        return advertisement.toVO();
    }

    public String deleteAdvertisement(Integer id){
        advertisementRepository.deleteById(id);
        return "删除成功";
    }
}
