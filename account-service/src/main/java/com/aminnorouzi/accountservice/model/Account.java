package com.aminnorouzi.accountservice.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id; // account/deposit number

    private String title;
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private LocalDate createdAt;
    private LocalDate closedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "customer_ids", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "customer_id")
    private List<Long> customerIds;
}
