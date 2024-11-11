package com.team.buddyya.student.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "interest")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Interest {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String interestName;
}
