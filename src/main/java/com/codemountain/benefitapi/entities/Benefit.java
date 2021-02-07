package com.codemountain.benefitapi.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@Table(name = "package_tbl", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
public class Benefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "benefit_id")
    private Long id;

    @NotBlank(message = "package name cannot be blank")
    private String name;

    private Float price;

    @JsonIgnore
    @OneToOne(mappedBy = "benefit")
    private User user;


    public Long getUser() {
        return user.getId();
    }
}
