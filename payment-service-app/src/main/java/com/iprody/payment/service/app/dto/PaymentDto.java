package com.iprody.payment.service.app.dto;

import com.iprody.payment.service.app.models.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment data transfer object")
public class PaymentDto {
    @Schema(description = "Payment unique identifier", example = "ac328a1a-1e60-4dd3-bee5-ed573d74c841")
    private UUID guid;

    @Schema(description = "Inquiry reference ID", example = "607ed0ea-cb8a-4ff8-a694-1213c314e65c", required = true)
    private UUID inquiryRefId;

    @Schema(description = "Payment amount", example = "100.50", required = true)
    private BigDecimal amount;

    @Schema(description = "Currency code (ISO 4217)", example = "USD", required = true)
    private String currency;

    @Schema(description = "Transaction reference ID", example = "f113e373-b7b0-4f38-abf6-ccc3a89b8236")
    private UUID transactionRefId;

    @Schema(description = "Payment status", example = "APPROVED", required = true)
    private PaymentStatus status;

    @Schema(description = "Payment note", example = "Payment for order #12345")
    private String note;

    @Schema(description = "Creation timestamp", example = "2025-01-01T12:00:00Z")
    private Instant createdAt;

    @Schema(description = "Last update timestamp", example = "2025-01-01T12:00:00Z")
    private Instant updatedAt;
}
