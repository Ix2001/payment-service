package com.iprody.payment.service.app.persistence;

import com.iprody.payment.service.app.models.Payment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class PaymentFilterFactory {
    private PaymentFilterFactory() {
    }

    public static Specification<Payment> fromFilter(PaymentFilter filter) {
        Specification<Payment> spec = Specification.unrestricted();

        if (StringUtils.hasText(filter.getCurrency())) {
            spec = spec.and(PaymentSpecifications.hasCurrency(filter.getCurrency()));
        }

        if (filter.getMinAmount() != null && filter.getMaxAmount() != null) {
            spec = spec.and(PaymentSpecifications.amountBetween(
                    filter.getMinAmount(), filter.getMaxAmount()));
        } else if (filter.getMinAmount() != null) {
            spec = spec.and(PaymentSpecifications.amountGreaterThanOrEqual(filter.getMinAmount()));
        } else if (filter.getMaxAmount() != null) {
            spec = spec.and(PaymentSpecifications.amountLessThanOrEqual(filter.getMaxAmount()));
        }

        if (filter.getCreatedAfter() != null && filter.getCreatedBefore() != null) {
            spec = spec.and(PaymentSpecifications.createdBetween(
                    filter.getCreatedAfter(), filter.getCreatedBefore()));
        } else if (filter.getCreatedAfter() != null) {
            spec = spec.and(PaymentSpecifications.createdAfter(filter.getCreatedAfter()));
        } else if (filter.getCreatedBefore() != null) {
            spec = spec.and(PaymentSpecifications.createdBefore(filter.getCreatedBefore()));
        }

        if (StringUtils.hasText(filter.getStatus())) {
            spec = spec.and(PaymentSpecifications.hasStatus(filter.getStatus()));
        }

        return spec;
    }
}
