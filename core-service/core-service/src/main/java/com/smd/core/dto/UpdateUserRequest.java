package com.smd.core.dto;

import com.smd.core.entity.User.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    @NotBlank(message = "Tên đầy đủ không được để trống")
    @Size(max = 255, message = "Tên đầy đủ không được vượt quá 255 ký tự")
    private String fullName;
    
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;
    
    private Long departmentId;
    
    private UserStatus status;
    
    private Long roleId;
}
