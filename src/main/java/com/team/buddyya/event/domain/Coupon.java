package com.team.buddyya.event.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "code", length = 10, nullable = false, unique = true)
    private String code;

    @Column(name = "used", nullable = false)
    private Boolean isUsed;

    @Builder
    public Coupon(String code) {
        this.code = code;
        this.isUsed = false;
    }

    public void markAsUsed() {
        this.isUsed = true;
    }
}
