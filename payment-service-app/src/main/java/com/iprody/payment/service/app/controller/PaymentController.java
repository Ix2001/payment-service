package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.dto.NoteUpdateRequest;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "API for managing payments")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Get payment by ID", description = "Retrieve a payment by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = @Content(schema = @Schema(implementation = PaymentDto.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto get(
            @Parameter(description = "Payment UUID", required = true)
            @PathVariable UUID id) {
        return paymentService.get(id);
    }

    @Operation(summary = "Search payments", description = "Search payments with filtering, sorting and pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments found",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<PaymentDto> searchPayments(
            @Parameter(description = "Filter criteria")
            @ModelAttribute PaymentFilter filter,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "25")
            @RequestParam(defaultValue = "25") int size,
            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return paymentService.search(filter, pageable);
    }

    @Operation(summary = "Create a new payment", description = "Create a new payment with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment created successfully",
                    content = @Content(schema = @Schema(implementation = PaymentDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDto create(
            @Parameter(description = "Payment data", required = true)
            @RequestBody PaymentDto paymentDto) {
        return paymentService.create(paymentDto);
    }

    @Operation(summary = "Update payment", description = "Update an existing payment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment updated successfully",
                    content = @Content(schema = @Schema(implementation = PaymentDto.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto update(
            @Parameter(description = "Payment UUID", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Updated payment data", required = true)
            @RequestBody PaymentDto paymentDto) {
        return paymentService.update(id, paymentDto);
    }

    @Operation(summary = "Update payment note", description = "Update only the note field of a payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment note updated successfully",
                    content = @Content(schema = @Schema(implementation = PaymentDto.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PatchMapping("/{id}/note")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto updateNote(
            @Parameter(description = "Payment UUID", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Note update request", required = true)
            @RequestBody NoteUpdateRequest request) {
        return paymentService.updateNote(id, request.getNote());
    }

    @Operation(summary = "Delete payment", description = "Delete a payment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Payment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Payment UUID", required = true)
            @PathVariable UUID id) {
        paymentService.delete(id);
    }
}
