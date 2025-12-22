package com.iprody.payment.service.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request to update payment note")
public class NoteUpdateRequest {
    @Schema(description = "New note text", example = "Updated payment note", required = true)
    private String note;
}
