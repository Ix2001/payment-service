package com.iprody.payment.service.app.persistence;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
@Data
public class PaymentFilter {
    private String currency;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private OffsetDateTime createdAfter;
    private OffsetDateTime createdBefore;
    private String status;
}
