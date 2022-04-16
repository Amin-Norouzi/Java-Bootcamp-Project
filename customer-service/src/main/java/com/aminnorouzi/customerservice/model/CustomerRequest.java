package com.aminnorouzi.customerservice.model;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public final class CustomerRequest {

    private String fullName;
    private String nationalCode;
    private String phoneNumber;
    private Type type;
    private LocalDate birthDate;
}
