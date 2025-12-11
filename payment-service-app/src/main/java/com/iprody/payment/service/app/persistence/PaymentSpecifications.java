package com.iprody.payment.service.app.persistence;

import com.iprody.payment.service.app.models.Payment;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class PaymentSpecifications {
    private PaymentSpecifications() {
    }

    public static Specification<Payment> hasCurrency(String currency) {
        return (root, query, cb) -> cb.equal(root.get("currency"), currency);
    }

    public static Specification<Payment> amountBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> cb.between(root.get("amount"), min, max);
    }

    public static Specification<Payment> amountGreaterThanOrEqual(BigDecimal min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("amount"), min);
    }

    public static Specification<Payment> amountLessThanOrEqual(BigDecimal max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("amount"), max);
    }

    public static Specification<Payment> createdBetween(OffsetDateTime after, OffsetDateTime before) {
        return (root, query, cb) -> cb.between(root.get("createdAt"), after, before);
    }

    public static Specification<Payment> createdAfter(OffsetDateTime after) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), after);
    }

    public static Specification<Payment> createdBefore(OffsetDateTime before) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), before);
    }

    public static Specification<Payment> hasStatus(String status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
}
