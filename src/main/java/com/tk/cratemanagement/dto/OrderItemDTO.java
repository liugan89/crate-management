package com.tk.cratemanagement.dto;

import java.time.LocalDate;
import java.util.List;

public record OrderItemDTO(
    Long id,
    Long goodsId,
    String goodsName,
    Long supplierId,
    String supplierName,
    String batchNumber,
    LocalDate productionDate,
    double expectedQuantity,
    List<ScanDTO> scans
) {}
