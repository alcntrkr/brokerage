package com.ing.brokerage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "CUSTOMERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "PASSWORD")
    private String passwordHash;

    @Column(name = "NAME")
    private String name;
}
