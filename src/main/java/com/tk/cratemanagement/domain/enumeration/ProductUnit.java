package com.tk.cratemanagement.domain.enumeration;

/**
 * 商品单位枚举
 * 定义常用的产品计量单位
 */
public enum ProductUnit {
    
    // 重量单位
    KILOGRAM("kg", "千克", "重量"),
    GRAM("g", "克", "重量"),
    POUND("lb", "磅", "重量"),
    TON("t", "吨", "重量"),
    
    // 长度单位
    METER("m", "米", "长度"),
    CENTIMETER("cm", "厘米", "长度"),
    MILLIMETER("mm", "毫米", "长度"),
    INCH("in", "英寸", "长度"),
    FOOT("ft", "英尺", "长度"),
    
    // 体积单位
    LITER("L", "升", "体积"),
    MILLILITER("mL", "毫升", "体积"),
    CUBIC_METER("m³", "立方米", "体积"),
    CUBIC_CENTIMETER("cm³", "立方厘米", "体积"),
    
    // 数量单位
    PIECE("pcs", "个", "数量"),
    BOX("box", "箱", "数量"),
    PACK("pack", "包", "数量"),
    SET("set", "套", "数量"),
    PAIR("pair", "对", "数量"),
    DOZEN("dozen", "打", "数量"),
    HUNDRED("hundred", "百", "数量"),
    THOUSAND("thousand", "千", "数量"),
    
    // 面积单位
    SQUARE_METER("m²", "平方米", "面积"),
    SQUARE_CENTIMETER("cm²", "平方厘米", "面积"),
    SQUARE_FOOT("ft²", "平方英尺", "面积"),
    
    // 其他单位
    HOUR("h", "小时", "时间"),
    DAY("day", "天", "时间"),
    MONTH("month", "月", "时间"),
    YEAR("year", "年", "时间"),
    PERCENT("%", "百分比", "比例"),
    RATIO("ratio", "比例", "比例");
    
    private final String code;
    private final String displayName;
    private final String category;
    
    ProductUnit(String code, String displayName, String category) {
        this.code = code;
        this.displayName = displayName;
        this.category = category;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getCategory() {
        return category;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static ProductUnit fromCode(String code) {
        for (ProductUnit unit : values()) {
            if (unit.code.equals(code)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("未知的单位代码: " + code);
    }
    
    /**
     * 获取指定分类的所有单位
     */
    public static ProductUnit[] getByCategory(String category) {
        return java.util.Arrays.stream(values())
                .filter(unit -> unit.category.equals(category))
                .toArray(ProductUnit[]::new);
    }
    
    /**
     * 获取所有分类
     */
    public static String[] getAllCategories() {
        return java.util.Arrays.stream(values())
                .map(ProductUnit::getCategory)
                .distinct()
                .toArray(String[]::new);
    }
}
