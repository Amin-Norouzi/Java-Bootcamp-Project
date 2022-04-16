package com.aminnorouzi.accountservice.model.customer;

import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Customer {

    private Long id;
    private String nationalCode;
    private String phoneNumber;
    private String fullName;
    private Status status;
    private Type type;
    private LocalDate birthDate;
    private LocalDate createdAt;
}