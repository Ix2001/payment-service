package com.iprody.payment.service.app.service;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.models.Payment;
import com.iprody.payment.service.app.models.PaymentStatus;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private PaymentDto paymentDto;
    private UUID guid;

    @BeforeEach
    void setUp() {
        guid = UUID.randomUUID();
        UUID inquiryRefId = UUID.randomUUID();
        UUID transactionRefId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();
        OffsetDateTime updatedAt = OffsetDateTime.now();

        payment = new Payment();
        payment.setGuid(guid);
        payment.setInquiryRefId(inquiryRefId);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCurrency("USD");
        payment.setTransactionRefId(transactionRefId);
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setNote("Test payment");
        payment.setCreatedAt(createdAt);
        payment.setUpdatedAt(updatedAt);

        paymentDto = new PaymentDto();
        paymentDto.setGuid(guid);
        paymentDto.setInquiryRefId(inquiryRefId);
        paymentDto.setAmount(new BigDecimal("100.00"));
        paymentDto.setCurrency("USD");
        paymentDto.setTransactionRefId(transactionRefId);
        paymentDto.setStatus(PaymentStatus.APPROVED);
        paymentDto.setNote("Test payment");
        paymentDto.setCreatedAt(createdAt.toInstant());
        paymentDto.setUpdatedAt(updatedAt.toInstant());
    }

    @Test
    void shouldReturnPaymentById() {
        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        PaymentDto result = paymentService.get(guid);

        assertThat(result).isNotNull();
        assertThat(result.getGuid()).isEqualTo(guid);
        assertThat(result.getCurrency()).isEqualTo("USD");
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.APPROVED);
        verify(paymentRepository).findById(guid);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void shouldThrowExceptionWhenPaymentNotFound() {
        when(paymentRepository.findById(guid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.get(guid))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Платеж не найден: " + guid);
        verify(paymentRepository).findById(guid);
    }

    @ParameterizedTest
    @MethodSource("statusProvider")
    void shouldMapDifferentPaymentStatuses(PaymentStatus status) {
        payment.setStatus(status);
        paymentDto.setStatus(status);
        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        PaymentDto result = paymentService.get(guid);

        assertThat(result.getStatus()).isEqualTo(status);
        verify(paymentRepository).findById(guid);
        verify(paymentMapper).toDto(payment);
    }

    static Stream<PaymentStatus> statusProvider() {
        return Stream.of(
                PaymentStatus.CREATED,
                PaymentStatus.RECEIVED,
                PaymentStatus.APPROVED,
                PaymentStatus.REJECTED,
                PaymentStatus.PROCESSING,
                PaymentStatus.COMPLETED,
                PaymentStatus.FAILED
        );
    }

    @Test
    void shouldSearchPaymentsByCurrency() {
        PaymentFilter filter = new PaymentFilter();
        filter.setCurrency("USD");
        Pageable pageable = PageRequest.of(0, 25);
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment));
        Page<PaymentDto> dtoPage = new PageImpl<>(List.of(paymentDto));

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.search(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCurrency()).isEqualTo("USD");
        verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldSearchPaymentsByMinAmount() {
        PaymentFilter filter = new PaymentFilter();
        filter.setMinAmount(new BigDecimal("50.00"));
        Pageable pageable = PageRequest.of(0, 25);
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment));
        Page<PaymentDto> dtoPage = new PageImpl<>(List.of(paymentDto));

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.search(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldSearchPaymentsByMaxAmount() {
        PaymentFilter filter = new PaymentFilter();
        filter.setMaxAmount(new BigDecimal("200.00"));
        Pageable pageable = PageRequest.of(0, 25);
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment));

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.search(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldSearchPaymentsByAmountRange() {
        PaymentFilter filter = new PaymentFilter();
        filter.setMinAmount(new BigDecimal("50.00"));
        filter.setMaxAmount(new BigDecimal("200.00"));
        Pageable pageable = PageRequest.of(0, 25);
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment));

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.search(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldSearchPaymentsByCreatedAfter() {
        PaymentFilter filter = new PaymentFilter();
        filter.setCreatedAfter(OffsetDateTime.now().minusDays(7));
        Pageable pageable = PageRequest.of(0, 25);
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment));

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.search(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldSearchPaymentsByCreatedBefore() {
        PaymentFilter filter = new PaymentFilter();
        filter.setCreatedBefore(OffsetDateTime.now().plusDays(7));
        Pageable pageable = PageRequest.of(0, 25);
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment));

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.search(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldSearchPaymentsByCreatedDateRange() {
        PaymentFilter filter = new PaymentFilter();
        filter.setCreatedAfter(OffsetDateTime.now().minusDays(7));
        filter.setCreatedBefore(OffsetDateTime.now().plusDays(7));
        Pageable pageable = PageRequest.of(0, 25);
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment));

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.search(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldSearchPaymentsByStatus() {
        PaymentFilter filter = new PaymentFilter();
        filter.setStatus(PaymentStatus.APPROVED);
        Pageable pageable = PageRequest.of(0, 25);
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment));

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.search(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(PaymentStatus.APPROVED);
        verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
    }

    @ParameterizedTest
    @MethodSource("sortingProvider")
    void shouldSearchPaymentsWithSorting(String sortBy, Sort.Direction direction) {
        PaymentFilter filter = new PaymentFilter();
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(0, 25, sort);
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment), pageable, 1);

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.search(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPageable().getSort()).isEqualTo(sort);
        verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
    }

    static Stream<Arguments> sortingProvider() {
        return Stream.of(
                Arguments.of("amount", Sort.Direction.ASC),
                Arguments.of("amount", Sort.Direction.DESC),
                Arguments.of("createdAt", Sort.Direction.ASC),
                Arguments.of("createdAt", Sort.Direction.DESC)
        );
    }

    @Test
    void shouldSearchPaymentsWithPagination() {
        PaymentFilter filter = new PaymentFilter();
        Pageable pageable = PageRequest.of(0, 25);
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment), pageable, 1);

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.search(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(25);
        verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldSearchPaymentsWithCustomPagination() {
        PaymentFilter filter = new PaymentFilter();
        Pageable pageable = PageRequest.of(1, 10);
        Page<Payment> paymentPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(paymentPage);

        Page<PaymentDto> result = paymentService.search(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getPageable().getPageNumber()).isEqualTo(1);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldSearchPaymentsWithMultipleFilters() {
        PaymentFilter filter = new PaymentFilter();
        filter.setCurrency("USD");
        filter.setMinAmount(new BigDecimal("50.00"));
        filter.setMaxAmount(new BigDecimal("200.00"));
        filter.setStatus(PaymentStatus.APPROVED);
        filter.setCreatedAfter(OffsetDateTime.now().minusDays(7));
        filter.setCreatedBefore(OffsetDateTime.now().plusDays(7));
        Pageable pageable = PageRequest.of(0, 25);
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment));

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(paymentPage);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        Page<PaymentDto> result = paymentService.search(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
    }
}
