package com.example.tomatomall.controller;

import com.example.tomatomall.po.Advertisement;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.AdvertisementRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.AdvertisementService;
import com.example.tomatomall.vo.AdvertisementVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

@RestController
@RequestMapping("/api/advertisements")
public class AdvertisementController {
    @Resource
    AdvertisementService advertisementService;

    @Autowired
    AdvertisementRepository advertisementRepository;

    @Autowired
    ProductRepository productRepository;

    /**
     * 获取所有广告
     */
    @GetMapping
    public Response getAllAdvertisements(){
        return Response.buildSuccess(advertisementService.getAllAdvertisements());
    }

    /**
     * 更新广告信息
     */
    @PutMapping
    public Response updateAdvertisement(@RequestBody AdvertisementVO advertisementVO){
        Optional<Product> thisProduct=productRepository.findById(advertisementVO.getProductId());
        if(thisProduct.isPresent()){
            String res=advertisementService.updateAdvertisement(advertisementVO);
            if(res=="更新成功"){
                return Response.buildSuccess(res);
            }else{
                return Response.buildFailure("400",res);
            }
        }else{
            return Response.buildFailure("400","商品不存在");
        }
    }

    /**
     * 创建广告
     */
    @PostMapping
    public Response createAdvertisement(@RequestBody AdvertisementVO advertisementVO){
        Optional<Product> thisProduct=productRepository.findById(advertisementVO.getProductId());
        if(thisProduct.isPresent()){
            return Response.buildSuccess(advertisementService.createAdvertisement(advertisementVO));
        }else{
            return Response.buildFailure("400","商品不存在");
        }
    }

    /**
     * 删除广告
     */
    @DeleteMapping("/{id}")
    public Response deleteAdvertisement(@PathVariable("id") Integer id){
        Optional<Advertisement> advertisement=advertisementRepository.findById(id);
        if(advertisement.isPresent()){
            return Response.buildSuccess(advertisementService.deleteAdvertisement(id));
        }else{
            return Response.buildFailure("400","未找到该广告");
        }
    }
}
