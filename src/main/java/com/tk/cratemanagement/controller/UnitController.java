package com.tk.cratemanagement.controller;

import com.tk.cratemanagement.domain.enumeration.ProductUnit;
import com.tk.cratemanagement.dto.UnitDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 单位管理控制器
 * 提供商品单位相关的查询接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/units")
@Tag(name = "单位管理", description = "商品单位查询相关API")
public class UnitController {
    
    /**
     * 获取所有商品单位
     * 
     * @return 所有单位列表
     */
    @GetMapping
    @Operation(summary = "获取所有商品单位", description = "获取系统中定义的所有商品单位")
    public ResponseEntity<List<UnitDTO>> getAllUnits() {
        log.debug("获取所有商品单位");
        
        List<UnitDTO> units = Arrays.stream(ProductUnit.values())
                .map(unit -> new UnitDTO(
                        unit.name(),
                        unit.getCode(),
                        unit.getDisplayName(),
                        unit.getCategory()
                ))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(units);
    }
    
    /**
     * 根据分类获取商品单位
     * 
     * @param category 单位分类
     * @return 指定分类的单位列表
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "根据分类获取商品单位", description = "根据指定分类获取商品单位列表")
    public ResponseEntity<List<UnitDTO>> getUnitsByCategory(@PathVariable String category) {
        log.debug("根据分类获取商品单位: category={}", category);
        
        try {
            ProductUnit[] units = ProductUnit.getByCategory(category);
            List<UnitDTO> unitDTOs = Arrays.stream(units)
                    .map(unit -> new UnitDTO(
                            unit.name(),
                            unit.getCode(),
                            unit.getDisplayName(),
                            unit.getCategory()
                    ))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(unitDTOs);
            
        } catch (Exception e) {
            log.warn("获取分类单位失败: category={}", category, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取所有单位分类
     * 
     * @return 所有分类列表
     */
    @GetMapping("/categories")
    @Operation(summary = "获取所有单位分类", description = "获取系统中定义的所有单位分类")
    public ResponseEntity<List<String>> getAllCategories() {
        log.debug("获取所有单位分类");
        
        List<String> categories = Arrays.asList(ProductUnit.getAllCategories());
        return ResponseEntity.ok(categories);
    }
    
    /**
     * 根据代码获取单位信息
     * 
     * @param code 单位代码
     * @return 单位信息
     */
    @GetMapping("/code/{code}")
    @Operation(summary = "根据代码获取单位信息", description = "根据单位代码获取具体的单位信息")
    public ResponseEntity<UnitDTO> getUnitByCode(@PathVariable String code) {
        log.debug("根据代码获取单位信息: code={}", code);
        
        try {
            ProductUnit unit = ProductUnit.fromCode(code);
            UnitDTO unitDTO = new UnitDTO(
                    unit.name(),
                    unit.getCode(),
                    unit.getDisplayName(),
                    unit.getCategory()
            );
            
            return ResponseEntity.ok(unitDTO);
            
        } catch (IllegalArgumentException e) {
            log.warn("单位代码不存在: code={}", code);
            return ResponseEntity.notFound().build();
        }
    }
}
