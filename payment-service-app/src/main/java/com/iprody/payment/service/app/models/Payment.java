package com.iprody.payment.service.app.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    private UUID id;

    private Double value;
}