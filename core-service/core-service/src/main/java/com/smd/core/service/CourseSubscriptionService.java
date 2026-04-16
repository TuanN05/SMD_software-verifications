package com.smd.core.service;

import com.smd.core.dto.CourseSimpleDto;
import com.smd.core.dto.CourseSubscriptionResponse;
import com.smd.core.dto.CourseUnsubscriptionResponse;
import com.smd.core.entity.Course;
import com.smd.core.entity.CourseSubscription;
import com.smd.core.entity.User;
import com.smd.core.exception.DuplicateResourceException;
import com.smd.core.exception.ResourceNotFoundException;
import com.smd.core.repository.CourseRepository;
import com.smd.core.repository.CourseSubscriptionRepository;
import com.smd.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseSubscriptionService {

private final CourseSubscriptionRepository subscriptionRepository;
private final CourseRepository courseRepository;
private final UserRepository userRepository;

@Transactional
public CourseSubscriptionResponse followCourse(String username, Long courseId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (subscriptionRepository.existsByUser_UserIdAndCourse_CourseId(user.getUserId(), courseId)) {
                throw new DuplicateResourceException("You are already following this course");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        CourseSubscription subscription = CourseSubscription.builder()
                .user(user)
                .course(course)
                .build();

        CourseSubscription savedSubscription = subscriptionRepository.save(subscription);

        return CourseSubscriptionResponse.builder()
                .subscriptionId(savedSubscription.getId())
                .message("Successfully followed course: " + course.getCourseName())
                .course(CourseSimpleDto.builder()
                        .courseId(course.getCourseId())
                        .courseCode(course.getCourseCode())
                        .courseName(course.getCourseName())
                        .credits(course.getCredits())
                        .courseType(course.getCourseType().name())
                        .build())
                        .subscribedAt(savedSubscription.getSubscribedAt())
                        .build();
}

@Transactional
public CourseUnsubscriptionResponse unfollowCourse(String username, Long courseId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CourseSubscription subscription = subscriptionRepository
                .findByUser_UserIdAndCourse_CourseId(user.getUserId(), courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        Course course = subscription.getCourse();
        LocalDateTime unsubscribedAt = LocalDateTime.now();

        subscriptionRepository.delete(subscription);

        return CourseUnsubscriptionResponse.builder()
                .message("Successfully unfollowed course: " + course.getCourseName())
                .course(CourseSimpleDto.builder()
                        .courseId(course.getCourseId())
                        .courseCode(course.getCourseCode())
                        .courseName(course.getCourseName())
                        .credits(course.getCredits())
                        .courseType(course.getCourseType().name())
                        .build())
                .unsubscribedAt(unsubscribedAt)
                .build();
}

@Transactional(readOnly = true)
public List<CourseSimpleDto> getFollowedCourses(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Reuse CourseSimpleDto exist in your project
        List<CourseSimpleDto> allCourses = subscriptionRepository.findByUser_UserId(user.getUserId()).stream()
                .map(sub -> {
                Course c = sub.getCourse();
                return CourseSimpleDto.builder() // Giả sử bạn có builder này
                        .courseId(c.getCourseId())
                        .courseCode(c.getCourseCode())
                        .courseName(c.getCourseName())
                        .build();
                })
                .collect(Collectors.toList());

        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, allCourses.size());
        return allCourses.subList(start, end);
}
}