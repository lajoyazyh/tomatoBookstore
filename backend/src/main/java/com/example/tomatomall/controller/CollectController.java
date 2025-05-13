package com.example.tomatomall.controller;

import com.example.tomatomall.po.Collect;

import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.CollectRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.CollectService;
import com.example.tomatomall.vo.CollectVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import com.example.tomatomall.util.TokenUtil;

import javax.annotation.Resource;
import java.util.Optional;

@RestController
@RequestMapping("/api/collects")
public class CollectController {
    @Resource
    CollectService collectService;

    @Autowired
    CollectRepository collectRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private TokenUtil tokenUtil;

    /**
     * 收藏商品
     */
    @PostMapping
    public Response addCollect(@RequestHeader("token") String token,@RequestParam(value = "productId")Integer productId){
        Integer thisUserId = tokenUtil.getAccount(token).getId();
        String res=collectService.addCollect(thisUserId,productId);
        if(res=="收藏成功"){
            return Response.buildSuccess(res);
        }else{
            return Response.buildFailure("400",res);
        }
    }

    /**
     * 获取所有收藏的商品
     */
    @GetMapping
    public Response getAllCollect(@RequestHeader("token") String token){
        Integer thisUserId = tokenUtil.getAccount(token).getId();
        return Response.buildSuccess(collectService.getAllCollect(thisUserId));
    }

    /**
     * 获取某一收藏的商品的信息
     */
    @GetMapping("/{id}")
    public Response getCollect(@PathVariable(value = "id")Integer id){
        Optional<Collect> thisCollect=collectRepository.findById(id);
        Optional<Product> thisProduct=productRepository.findById(thisCollect.get().getProductId());
        if(!thisProduct.isPresent()){
            collectRepository.delete(thisCollect.get());
            return Response.buildFailure("400","该商品已下架");
        }else{
            return Response.buildSuccess(thisProduct.get().toVO());
        }
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{id}")
    public Response deleteCollect(@PathVariable(value = "id")Integer id){
        Optional<Collect> thisCollect=collectRepository.findById(id);
        if(!thisCollect.isPresent()){
            return Response.buildFailure("400","未找到该收藏");
        }else{
            collectService.deleteCollect(id);
            return Response.buildSuccess("删除成功");
        }
    }
}
