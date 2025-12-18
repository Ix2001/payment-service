package com.iprody.payment.service.app.mapper;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.models.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "createdAt", expression = "java(payment.getCreatedAt() != null ? payment.getCreatedAt().toInstant() : null)")
    @Mapping(target = "updatedAt", expression = "java(payment.getUpdatedAt() != null ? payment.getUpdatedAt().toInstant() : null)")
    PaymentDto toDto(Payment payment);

    @Mapping(target = "createdAt", expression = "java(dto.getCreatedAt() != null ? dto.getCreatedAt().atOffset(java.time.ZoneOffset.UTC) : null)")
    @Mapping(target = "updatedAt", expression = "java(dto.getUpdatedAt() != null ? dto.getUpdatedAt().atOffset(java.time.ZoneOffset.UTC) : null)")
    Payment toEntity(PaymentDto dto);
}
