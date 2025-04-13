package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Specification;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.SpecificationRepository;
import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.SpecificationVO;
import com.example.tomatomall.vo.StockpileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.tomatomall.util.TokenUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @Author: ZhuYehang&zzc
 * @Date: 2025/4/9
 *
 * 商品增删改查功能实现
 */

@Service

public class ProductSreviceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StockpileRepository stockpileRepository;

    @Autowired
    SpecificationRepository specificationRepository;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    SecurityUtil securityUtil;

    @Override
    public List<ProductVO> getAllProducts() {
        List<Product> allProducts=productRepository.findAll();
        List<ProductVO> allProductVOs=new ArrayList<>();
        for(Product product:allProducts){
            ProductVO productVO=getProduct(product.getId());
            allProductVOs.add(productVO);
        }
        return allProductVOs;
    }

    @Override
    public ProductVO getProduct(Integer id) {
        Product thisProduct= productRepository.findById(id).get();
        List<Specification> specifications = specificationRepository.findByProductId(id);
        if(specifications!=null&!specifications.isEmpty()){
            for(Specification specification:specifications){
                thisProduct.addSpecification(specification);
            }
        }
        return thisProduct.toVO();
    }

    @Override
    @Transactional
    public String updateInformation(ProductVO productVO) {
        Product thisProduct= productRepository.findById(productVO.getId()).get();

        if(productVO.getTitle()!=null){
            thisProduct.setTitle(productVO.getTitle());
        }
        if(productVO.getPrice()!=null){
            if(productVO.getPrice().compareTo(BigDecimal.ZERO)<0){
                return "价格不能小于0";
            }
            thisProduct.setPrice(productVO.getPrice());
        }
        if(productVO.getRate()!=null){
            if(productVO.getRate()<0||productVO.getRate()>10){
                return "商品评分区间为0-10";
            }
            thisProduct.setRate(productVO.getRate());
        }
        if(productVO.getDescription()!=null){
            thisProduct.setDescription(productVO.getDescription());
        }
        if(productVO.getCover()!=null){
            thisProduct.setCover(productVO.getCover());
        }
        if(productVO.getDetail()!=null){
            thisProduct.setDetail(productVO.getDetail());
        }
        specificationRepository.deleteByProductId(thisProduct.getId());
        thisProduct.setSpecifications(new ArrayList<>());
        if(productVO.getSpecifications()!=null){
            for(SpecificationVO specificationVO:productVO.getSpecifications()){
                Specification specification = specificationVO.toPO();
                specification.setProduct(thisProduct); // 设置关联的商品对象
                //specificationRepository.save(specification); // 保存规格信息
                thisProduct.addSpecification(specification);
            }
        }
        productRepository.save(thisProduct);
        return "更新成功";
    }

    @Override
    @Transactional
    public ProductVO register(ProductVO productVO) {
        Product product = productRepository.findByTitle(productVO.getTitle());
        if(product != null) {
            return null;
        }
        Product newProduct = productVO.toPO(); // 转换时确保双向关联正确设置
        productRepository.save(newProduct);
        List<SpecificationVO> newSpecificationVOs = productVO.getSpecifications();
        if (newSpecificationVOs != null && !newSpecificationVOs.isEmpty()) {
            for (SpecificationVO specificationVO : newSpecificationVOs) {
                Specification specification = specificationVO.toPO();
                specification.setProduct(newProduct); // 设置关联的商品对象
                specificationRepository.save(specification); // 保存规格信息
            }
        }
        Stockpile stockpile=new Stockpile();
        stockpile.setProductId(newProduct.getId());
        stockpile.setAmount(0);
        stockpile.setFrozen(0);
        stockpileRepository.save(stockpile);

        return getProduct(newProduct.getId());
    }

    @Override
    public String delete(Integer id) {
        productRepository.deleteById(id);
        List<Specification> specifications=specificationRepository.findByProductId(id);
        for(Specification specification:specifications){
            specificationRepository.deleteById(specification.getId());
        }
        return "删除成功";
    }

    @Override
    public String stockChange(Integer productId, Integer amount) {
        // 查找对应的库存对象
        Stockpile stockpile = stockpileRepository.findByProductId(productId);

        if (stockpile == null) {
            return "商品不存在";
        }

        // 获取当前库存数量
        Integer currentAmount = stockpile.getAmount();

        stockpile.setAmount(amount);

        // 调用 save 方法保存更新（修改原有记录）
        try {
            stockpileRepository.save(stockpile);
        } catch (Exception e) {
            return "更新库存失败：" + e.getMessage();
        }

        return "库存更新成功";
    }

    @Override
    public StockpileVO getStock(Integer productId){
        Stockpile stockpile=stockpileRepository.findByProductId(productId);
        return stockpile.toVO();
    }
}
