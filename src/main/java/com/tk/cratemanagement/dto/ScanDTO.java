package com.tk.cratemanagement.dto;

import java.time.ZonedDateTime;

public record ScanDTO(
    Long id,
    String nfcUid,
    ZonedDateTime scanTimestamp,
    double quantity
) {}
