package com.team.buddyya.event.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.event.domain.Coupon;
import com.team.buddyya.event.dto.CouponRequest;
import com.team.buddyya.event.dto.CouponResponse;
import com.team.buddyya.event.exception.EventException;
import com.team.buddyya.event.exception.EventExceptionType;
import com.team.buddyya.event.repository.CouponRepository;
import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointType;
import com.team.buddyya.point.service.UpdatePointService;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final FindStudentService findStudentService;
    private final UpdatePointService updatePointService;
    private final CouponRepository couponRepository;

    public CouponResponse useCoupon(StudentInfo studentInfo, CouponRequest request) {
        Coupon coupon = couponRepository.findByCode(request.code())
                .orElseThrow(() -> new EventException(EventExceptionType.COUPON_NOT_FOUND));
        if (coupon.getIsUsed()) {
            throw new EventException(EventExceptionType.COUPON_ALREADY_USED);
        }
        Student student = findStudentService.findByStudentId(studentInfo.id());
        Point updatedPoint = updatePointService.updatePoint(student, PointType.COUPON_EVENT_REWARD);
        coupon.markAsUsed();
        return CouponResponse.from(updatedPoint, PointType.COUPON_EVENT_REWARD);
    }
}
