package com.example.tomatomall.repository;

import com.example.tomatomall.po.Block;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface BlockRepository extends JpaRepository<Block, Integer>{
    Optional<Block> findByUserId(Integer userId);

}
