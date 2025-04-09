package com.example.tomatomall.controller;

import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Specification;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import com.example.tomatomall.util.TokenUtil;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Resource
    ProductService productService;

    /**
     * 获取商品列表
     */
    @GetMapping()
    public Response getAllProducts(){
        return Response.buildSuccess(productService.getAllProducts());
    }

    /**
     * 获取特定id的商品
     */
    @GetMapping("/{id}")
    public Response getProduct(@PathVariable(value = "id")Integer id){
        ProductVO thisProductVO=productService.getProduct(id);
        if(thisProductVO!=null){
            return Response.buildSuccess(thisProductVO);
        }else{
            return Response.buildFailure("400","商品不存在");
        }
    }

    /**
     * 更新商品信息
     */
    @PutMapping
    public Response updateInformation(@RequestBody ProductVO productVO){
        ProductVO thisProductVO=productService.getProduct(productVO.getId());
        if(thisProductVO!=null){
            String res=productService.updateInformation(productVO);
            if(res=="更新成功"){
                return Response.buildSuccess(res);
            }else{
                return Response.buildFailure("400",res);
            }
        }else{
            return Response.buildFailure("400","商品不存在");
        }
    }
}
