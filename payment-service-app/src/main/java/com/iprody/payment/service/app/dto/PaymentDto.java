package com.iprody.payment.service.app.dto;

import com.iprody.payment.service.app.models.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private UUID guid;
    private UUID inquiryRefId;
    private BigDecimal amount;
    private String currency;
    private UUID transactionRefId;
    private PaymentStatus status;
    private String note;
    private Instant createdAt;
    private Instant updatedAt;

    public PaymentDto(UUID id, BigDecimal bigDecimal, String eur, String ref456, PaymentStatus paymentStatus, OffsetDateTime now, OffsetDateTime now1) {
    }
}
