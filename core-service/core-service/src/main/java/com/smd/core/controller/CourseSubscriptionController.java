package com.smd.core.controller;

import com.smd.core.dto.CourseSimpleDto;
import com.smd.core.dto.CourseSubscriptionResponse;
import com.smd.core.dto.CourseUnsubscriptionResponse;
import com.smd.core.service.CourseSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Validated
@Tag(name = "Course Subscriptions", description = "Manage course follow/unfollow for students")
public class CourseSubscriptionController {

    private final CourseSubscriptionService subscriptionService;

    @PostMapping("/{courseId}/follow")
    @Operation(summary = "Follow a course", description = "Subscribe to receive notifications for course updates")
    public ResponseEntity<CourseSubscriptionResponse> followCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        CourseSubscriptionResponse response = subscriptionService.followCourse(userDetails.getUsername(), courseId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{courseId}/follow")
    @Operation(summary = "Unfollow a course", description = "Unsubscribe from course updates")
    public ResponseEntity<CourseUnsubscriptionResponse> unfollowCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        CourseUnsubscriptionResponse response = subscriptionService.unfollowCourse(userDetails.getUsername(), courseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/following")
    @Operation(summary = "Get followed courses", description = "List all courses the current user is following")
    public ResponseEntity<List<CourseSimpleDto>> getFollowedCourses(
            @AuthenticationPrincipal UserDetails userDetails,
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Positive @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(subscriptionService.getFollowedCourses(userDetails.getUsername(), page, size));
    }
}