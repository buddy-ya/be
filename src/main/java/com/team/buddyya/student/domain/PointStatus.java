package com.team.buddyya.student.domain;

import com.team.buddyya.common.domain.CreatedTime;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "point_status")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointStatus extends CreatedTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "point_id", nullable = false)
    private Point point;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_type", nullable = false)
    private PointType pointType;

    @Column(name = "changed_point", nullable = false)
    private Integer changedPoint;

    @Builder
    public PointStatus(Point point, PointType pointType, int changedPoint) {
        this.point = point;
        this.pointType = pointType;
        this.changedPoint = changedPoint;
    }
}
