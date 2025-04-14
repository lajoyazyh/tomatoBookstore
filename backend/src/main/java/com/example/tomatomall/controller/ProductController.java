package com.example.tomatomall.controller;

import com.example.tomatomall.po.Product;

import com.example.tomatomall.po.Specification;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.SpecificationVO;
import com.example.tomatomall.vo.StockpileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import com.example.tomatomall.util.TokenUtil;

import javax.annotation.Resource;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Resource
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private TokenUtil tokenUtil;
  
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
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            return Response.buildSuccess(productService.getProduct(id));
        } else {
            return Response.buildFailure("400", "商品不存在");
        }
    }

    /**
     * 更新商品信息
     */
    @PutMapping
    public Response updateInformation(@RequestBody ProductVO productVO){
        Optional<Product> productOptional = productRepository.findById(productVO.getId());
        if(productOptional.isPresent()){
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

    /**
     * 创建新的商品
     */
    @PostMapping()
    public Response createProduct(@RequestBody ProductVO productVO) {
        ProductVO res = productService.register(productVO);
        if(res == null) {
            return Response.buildFailure("400", "该商品已存在");
        }else{
            return Response.buildSuccess(res);
        }
        //return Response.buildFailure("400", "你的后端方法实现错了，再回去沉淀沉淀！");
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public  Response delete(@RequestHeader("token") String token,@PathVariable(value = "id")Integer id) {
        String res = productService.delete(id);
        if(res == "商品不存在") {
            return Response.buildFailure("400", res);
        }else if(res == "删除成功"){
            return Response.buildSuccess(res);
        }
        return Response.buildFailure("400", "商品不存在");

    }

    /**
     * 调整库存
     */
    @PatchMapping("/stockpile/{productId}")
    public Response stockChange(@RequestHeader("token") String token,@PathVariable(value = "productId")Integer productId,@RequestParam(value = "amount")Integer amount) {
        String res = productService.stockChange(productId, amount);
        if(res.equals("商品不存在")) {
            return Response.buildFailure("400", res);
        }else if(res.equals("库存更新成功")){
            return Response.buildSuccess(res);
        }
        return Response.buildFailure("400", res);

    }

    /**
     * 调整库存
     */
    @GetMapping("/stockpile/{productId}")
    public Response getStock(@PathVariable(value = "productId")Integer productId){
        StockpileVO thisStockpileVO= productService.getStock(productId);
        return Response.buildSuccess(thisStockpileVO);
    }
}
