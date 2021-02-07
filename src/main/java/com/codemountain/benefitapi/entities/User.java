package com.codemountain.benefitapi.entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users_tbl", uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "username cannot be blank")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @NotBlank(message = "password cannot be blank")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "benefit_id")
    private Benefit benefit;

    private String role;

    @Column(name = "secret_question")
    private String secretQuestion;

    @Column(name = "secret_answer")
    private String secretAnswer;

    @Column(name = "referral_earnings")
    private Float referralEarnings;

    @Column(name = "activity_earnings")
    private Float activityEarnings;

    @Column(name = "referral_code")
    private String referralCode;

    @Column(name = "withdrawal_date")
    private Date withdrawalDate;

    @Column(name = "investment_cycle")
    private int investmentCycle;

    @Column(name = "withdrawal_status")
    @Enumerated(EnumType.STRING)
    private WithdrawalStatus withdrawalStatus;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

}
