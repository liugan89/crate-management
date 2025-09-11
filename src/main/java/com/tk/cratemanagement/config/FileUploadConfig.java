package com.tk.cratemanagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传配置类
 * 配置图片上传的相关参数
 */
@Configuration
@ConfigurationProperties(prefix = "app.file-upload")
public class FileUploadConfig {
    
    /**
     * 图片存储根路径
     */
    private String imageBasePath = "/home/ubuntu/app/server/images";
    
    /**
     * 商品图片子路径
     */
    private String goodsImagePath = "goods";
    
    /**
     * 允许的图片类型
     */
    private String[] allowedImageTypes = {"jpg", "jpeg", "png", "gif", "webp"};
    
    /**
     * 最大文件大小 (MB)
     */
    private long maxFileSize = 10;
    
    /**
     * 图片质量 (0-100)
     */
    private int imageQuality = 85;
    
    /**
     * 缩略图尺寸
     */
    private int thumbnailSize = 200;
    
    // Getters and Setters
    public String getImageBasePath() {
        return imageBasePath;
    }
    
    public void setImageBasePath(String imageBasePath) {
        this.imageBasePath = imageBasePath;
    }
    
    public String getGoodsImagePath() {
        return goodsImagePath;
    }
    
    public void setGoodsImagePath(String goodsImagePath) {
        this.goodsImagePath = goodsImagePath;
    }
    
    public String[] getAllowedImageTypes() {
        return allowedImageTypes;
    }
    
    public void setAllowedImageTypes(String[] allowedImageTypes) {
        this.allowedImageTypes = allowedImageTypes;
    }
    
    public long getMaxFileSize() {
        return maxFileSize;
    }
    
    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
    
    public int getImageQuality() {
        return imageQuality;
    }
    
    public void setImageQuality(int imageQuality) {
        this.imageQuality = imageQuality;
    }
    
    public int getThumbnailSize() {
        return thumbnailSize;
    }
    
    public void setThumbnailSize(int thumbnailSize) {
        this.thumbnailSize = thumbnailSize;
    }
    
    /**
     * 获取商品图片完整路径
     */
    public String getGoodsImageFullPath() {
        return imageBasePath + "/" + goodsImagePath;
    }
    
    /**
     * 获取最大文件大小（字节）
     */
    public long getMaxFileSizeBytes() {
        return maxFileSize * 1024 * 1024;
    }
}
