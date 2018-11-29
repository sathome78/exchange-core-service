package me.exrates.controller.ngcontroller.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor //(suppressConstructorProperties = true) TODO find solution
public class UserInfoVerificationDto {

    private Integer userId;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate born;

    @NotNull
    private String residentialAddress;

    @NotNull
    private String postalCode;

    @NotNull
    private String country;

    @NotNull
    private String city;
}
