package com.tk.cratemanagement.controller;

import com.tk.cratemanagement.config.FileUploadConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 图片访问控制器
 * 处理图片文件的访问和下载
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {
    
    private final FileUploadConfig fileUploadConfig;
    
    /**
     * 获取商品图片
     * 
     * @param tenantId 租户ID
     * @param fileName 文件名
     * @return 图片文件
     */
    @GetMapping("/goods/{tenantId}/{fileName}")
    public ResponseEntity<Resource> getGoodsImage(
            @PathVariable Long tenantId,
            @PathVariable String fileName) {
        
        try {
            // 构建文件路径
            Path filePath = Paths.get(fileUploadConfig.getGoodsImageFullPath(), 
                    tenantId.toString(), fileName);
            
            File file = filePath.toFile();
            
            if (!file.exists() || !file.isFile()) {
                log.warn("商品图片不存在: tenantId={}, fileName={}", tenantId, fileName);
                return ResponseEntity.notFound().build();
            }
            
            // 获取文件MIME类型
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            Resource resource = new FileSystemResource(file);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("获取商品图片失败: tenantId={}, fileName={}", tenantId, fileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取商品图片缩略图
     * 
     * @param tenantId 租户ID
     * @param fileName 文件名
     * @return 缩略图文件
     */
    @GetMapping("/goods/{tenantId}/thumb/{fileName}")
    public ResponseEntity<Resource> getGoodsImageThumbnail(
            @PathVariable Long tenantId,
            @PathVariable String fileName) {
        
        try {
            // 构建缩略图文件路径
            String thumbnailFileName = "thumb_" + fileName;
            Path filePath = Paths.get(fileUploadConfig.getGoodsImageFullPath(), 
                    tenantId.toString(), thumbnailFileName);
            
            File file = filePath.toFile();
            
            if (!file.exists() || !file.isFile()) {
                // 如果缩略图不存在，返回原图
                return getGoodsImage(tenantId, fileName);
            }
            
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            Resource resource = new FileSystemResource(file);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + thumbnailFileName + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("获取商品图片缩略图失败: tenantId={}, fileName={}", tenantId, fileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
