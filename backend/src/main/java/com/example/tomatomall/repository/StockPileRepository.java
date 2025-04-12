package com.example.tomatomall.repository;

import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.vo.StockpileVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface StockPileRepository extends JpaRepository<Stockpile, Integer> {
    Stockpile findByProductId(Integer productId);
}
