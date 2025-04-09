package com.example.tomatomall.controller;

import com.example.tomatomall.po.Product;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.AccountVO;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.tomatomall.util.TokenUtil;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Resource
    ProductService productService;

    @Autowired
    private TokenUtil tokenUtil;

    /**
     * 创建新的商品
     */
    @PostMapping()
    public Response createProduct(@RequestBody ProductVO productVO) {
        String res = productService.register(productVO);
        if(res == "商品名已存在") {
            return Response.buildFailure("400", res);
        }else if(res == "创建成功"){
            return Response.buildSuccess(res);
        }
        return Response.buildFailure("400", "你的后端方法实现错了，再回去沉淀沉淀！");
    }


}
