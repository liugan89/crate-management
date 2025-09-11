package com.tk.cratemanagement.util;

import com.tk.cratemanagement.config.FileUploadConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/**
 * 文件上传工具类
 * 处理图片上传、存储、删除等操作
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileUploadUtil {
    
    private final FileUploadConfig fileUploadConfig;
    
    /**
     * 上传商品图片
     * 
     * @param file 上传的文件
     * @param tenantId 租户ID
     * @param goodsId 商品ID
     * @return 图片URL路径
     * @throws IOException 文件操作异常
     */
    public String uploadGoodsImage(MultipartFile file, Long tenantId, Long goodsId) throws IOException {
        // 验证文件
        validateImageFile(file);
        
        // 生成文件名
        String fileName = generateImageFileName(file.getOriginalFilename(), tenantId, goodsId);
        
        // 创建目录
        Path uploadDir = createUploadDirectory(tenantId);
        
        // 保存文件
        Path filePath = uploadDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        
        // 生成访问URL
        String imageUrl = generateImageUrl(tenantId, fileName);
        
        log.info("商品图片上传成功: tenantId={}, goodsId={}, fileName={}, url={}", 
                tenantId, goodsId, fileName, imageUrl);
        
        return imageUrl;
    }
    
    /**
     * 删除商品图片
     * 
     * @param imageUrl 图片URL
     * @param tenantId 租户ID
     * @return 是否删除成功
     */
    public boolean deleteGoodsImage(String imageUrl, Long tenantId) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return true;
        }
        
        try {
            // 从URL中提取文件名
            String fileName = extractFileNameFromUrl(imageUrl);
            if (fileName == null) {
                log.warn("无法从URL中提取文件名: {}", imageUrl);
                return false;
            }
            
            // 构建文件路径
            Path filePath = Paths.get(fileUploadConfig.getGoodsImageFullPath(), 
                    tenantId.toString(), fileName);
            
            // 删除文件
            boolean deleted = Files.deleteIfExists(filePath);
            
            if (deleted) {
                log.info("商品图片删除成功: tenantId={}, fileName={}", tenantId, fileName);
            } else {
                log.warn("商品图片文件不存在: tenantId={}, fileName={}", tenantId, fileName);
            }
            
            return deleted;
            
        } catch (IOException e) {
            log.error("删除商品图片失败: tenantId={}, imageUrl={}", tenantId, imageUrl, e);
            return false;
        }
    }
    
    /**
     * 验证图片文件
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 检查文件大小
        if (file.getSize() > fileUploadConfig.getMaxFileSizeBytes()) {
            throw new IllegalArgumentException("文件大小不能超过 " + fileUploadConfig.getMaxFileSize() + "MB");
        }
        
        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!Arrays.asList(fileUploadConfig.getAllowedImageTypes()).contains(extension)) {
            throw new IllegalArgumentException("不支持的文件类型: " + extension + 
                    "，支持的类型: " + Arrays.toString(fileUploadConfig.getAllowedImageTypes()));
        }
    }
    
    /**
     * 生成图片文件名
     * 格式: goods_{tenantId}_{goodsId}_{timestamp}_{uuid}.{ext}
     */
    private String generateImageFileName(String originalFilename, Long tenantId, Long goodsId) {
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        return String.format("goods_%d_%d_%s_%s.%s", 
                tenantId, goodsId, timestamp, uuid, extension);
    }
    
    /**
     * 创建上传目录
     */
    private Path createUploadDirectory(Long tenantId) throws IOException {
        Path uploadDir = Paths.get(fileUploadConfig.getGoodsImageFullPath(), tenantId.toString());
        Files.createDirectories(uploadDir);
        return uploadDir;
    }
    
    /**
     * 生成图片访问URL
     */
    private String generateImageUrl(Long tenantId, String fileName) {
        return String.format("/api/v1/images/goods/%d/%s", tenantId, fileName);
    }
    
    /**
     * 从URL中提取文件名
     */
    private String extractFileNameFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        
        // 提取URL中的文件名部分
        String[] parts = imageUrl.split("/");
        if (parts.length > 0) {
            return parts[parts.length - 1];
        }
        
        return null;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1);
        }
        
        return "";
    }
    
    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String imageUrl, Long tenantId) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }
        
        try {
            String fileName = extractFileNameFromUrl(imageUrl);
            if (fileName == null) {
                return false;
            }
            
            Path filePath = Paths.get(fileUploadConfig.getGoodsImageFullPath(), 
                    tenantId.toString(), fileName);
            
            return Files.exists(filePath);
            
        } catch (Exception e) {
            log.error("检查文件是否存在时发生错误: tenantId={}, imageUrl={}", tenantId, imageUrl, e);
            return false;
        }
    }
}
