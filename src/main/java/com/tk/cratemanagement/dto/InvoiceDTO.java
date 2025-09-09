package com.tk.cratemanagement.dto;

import com.tk.cratemanagement.domain.enumeration.InvoiceStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceDTO(
    Long id,
    LocalDate issueDate,
    LocalDate dueDate,
    BigDecimal amount,
    InvoiceStatus status
) {}
