package com.jobportal.dto;

import com.jobportal.entity.User;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterDto {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    @NotNull(message = "Role is required")
    private User.Role role;

    // Employer fields
    private String companyName;

    // Common fields
    private String phone;
    private String location;

    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
