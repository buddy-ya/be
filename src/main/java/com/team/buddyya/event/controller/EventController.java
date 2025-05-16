package com.team.buddyya.event.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.event.dto.CouponRequest;
import com.team.buddyya.event.dto.CouponResponse;
import com.team.buddyya.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/coupon")
    public ResponseEntity<CouponResponse> registerCoupon(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @RequestBody CouponRequest request) {
        CouponResponse response = eventService.useCoupon(userDetails.getStudentInfo(), request);
        return ResponseEntity.ok(response);
    }
}
