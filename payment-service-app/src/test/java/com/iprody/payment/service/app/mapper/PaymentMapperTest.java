package com.iprody.payment.service.app.mapper;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.models.Payment;
import com.iprody.payment.service.app.models.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMapperTest {

    private final PaymentMapper mapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    void shouldMapToDto() {
        UUID id = UUID.randomUUID();
        UUID inquiryRefId = UUID.randomUUID();
        UUID transactionRefId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        OffsetDateTime updatedAt = OffsetDateTime.now();

        Payment payment = new Payment();
        payment.setGuid(id);
        payment.setInquiryRefId(inquiryRefId);
        payment.setAmount(new BigDecimal("123.45"));
        payment.setCurrency("USD");
        payment.setTransactionRefId(transactionRefId);
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setNote("Test payment");
        payment.setCreatedAt(createdAt);
        payment.setUpdatedAt(updatedAt);

        PaymentDto dto = mapper.toDto(payment);

        assertThat(dto).isNotNull();
        assertThat(dto.getGuid()).isEqualTo(payment.getGuid());
        assertThat(dto.getInquiryRefId()).isEqualTo(payment.getInquiryRefId());
        assertThat(dto.getAmount()).isEqualTo(payment.getAmount());
        assertThat(dto.getCurrency()).isEqualTo(payment.getCurrency());
        assertThat(dto.getTransactionRefId()).isEqualTo(payment.getTransactionRefId());
        assertThat(dto.getStatus()).isEqualTo(payment.getStatus());
        assertThat(dto.getNote()).isEqualTo(payment.getNote());
        assertThat(dto.getCreatedAt()).isEqualTo(payment.getCreatedAt().toInstant());
        assertThat(dto.getUpdatedAt()).isEqualTo(payment.getUpdatedAt().toInstant());
    }

    @Test
    void shouldMapToEntity() {
        UUID id = UUID.randomUUID();
        UUID inquiryRefId = UUID.randomUUID();
        UUID transactionRefId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();

        PaymentDto dto = new PaymentDto();
        dto.setGuid(id);
        dto.setInquiryRefId(inquiryRefId);
        dto.setAmount(new BigDecimal("999.99"));
        dto.setCurrency("EUR");
        dto.setTransactionRefId(transactionRefId);
        dto.setStatus(PaymentStatus.PROCESSING);
        dto.setNote("Test note");
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);

        Payment entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getGuid()).isEqualTo(dto.getGuid());
        assertThat(entity.getInquiryRefId()).isEqualTo(dto.getInquiryRefId());
        assertThat(entity.getAmount()).isEqualTo(dto.getAmount());
        assertThat(entity.getCurrency()).isEqualTo(dto.getCurrency());
        assertThat(entity.getTransactionRefId()).isEqualTo(dto.getTransactionRefId());
        assertThat(entity.getStatus()).isEqualTo(dto.getStatus());
        assertThat(entity.getNote()).isEqualTo(dto.getNote());
        assertThat(entity.getCreatedAt()).isEqualTo(dto.getCreatedAt().atOffset(java.time.ZoneOffset.UTC));
        assertThat(entity.getUpdatedAt()).isEqualTo(dto.getUpdatedAt().atOffset(java.time.ZoneOffset.UTC));
    }

    @Test
    void shouldUpdateEntity() {
        UUID id = UUID.randomUUID();
        UUID inquiryRefId = UUID.randomUUID();
        UUID transactionRefId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setGuid(id);
        paymentDto.setInquiryRefId(inquiryRefId);
        paymentDto.setAmount(new BigDecimal("999.99"));
        paymentDto.setCurrency("EUR");
        paymentDto.setTransactionRefId(transactionRefId);
        paymentDto.setStatus(PaymentStatus.APPROVED);
        paymentDto.setNote("Test note");
        paymentDto.setCreatedAt(createdAt);
        paymentDto.setUpdatedAt(updatedAt);

        Payment payment = new Payment();
        payment.setGuid(id);

        Payment entity = mapper.updateEntity(paymentDto, payment);

        assertThat(entity).isNotNull();
        assertThat(entity.getGuid()).isEqualTo(paymentDto.getGuid());
        assertThat(entity.getInquiryRefId()).isEqualTo(paymentDto.getInquiryRefId());
        assertThat(entity.getAmount()).isEqualTo(paymentDto.getAmount());
        assertThat(entity.getCurrency()).isEqualTo(paymentDto.getCurrency());
        assertThat(entity.getTransactionRefId()).isEqualTo(paymentDto.getTransactionRefId());
        assertThat(entity.getStatus()).isEqualTo(paymentDto.getStatus());
        assertThat(entity.getNote()).isEqualTo(paymentDto.getNote());
        assertThat(entity.getCreatedAt()).isEqualTo(paymentDto.getCreatedAt().atOffset(java.time.ZoneOffset.UTC));
        assertThat(entity.getUpdatedAt()).isEqualTo(paymentDto.getUpdatedAt().atOffset(java.time.ZoneOffset.UTC));
    }
}

