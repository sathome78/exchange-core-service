package me.exrates.controller.ngcontroller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import me.exrates.controller.ngcontroller.model.enums.VerificationDocumentType;

@Data
@Builder
@AllArgsConstructor//(suppressConstructorProperties = true)
public class UserDocVerificationDto {

    private Integer userId;
    private VerificationDocumentType documentType;
    private String encoded;
}
