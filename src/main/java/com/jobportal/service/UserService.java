package com.jobportal.service;

import com.jobportal.dto.RegisterDto;
import com.jobportal.entity.User;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UserAlreadyExistsException;
import com.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered: " + dto.getEmail());
        }

        User user = User.builder()
            .fullName(dto.getFullName())
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))
            .role(dto.getRole())
            .companyName(dto.getCompanyName())
            .phone(dto.getPhone())
            .location(dto.getLocation())
            .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public User updateProfile(User user, User updates) {
        user.setFullName(updates.getFullName());
        user.setPhone(updates.getPhone());
        user.setLocation(updates.getLocation());
        user.setSkills(updates.getSkills());
        user.setExperience(updates.getExperience());
        user.setEducation(updates.getEducation());
        user.setProfileSummary(updates.getProfileSummary());
        user.setLinkedinUrl(updates.getLinkedinUrl());
        user.setGithubUrl(updates.getGithubUrl());
        if (user.getRole() == User.Role.EMPLOYER) {
            user.setCompanyName(updates.getCompanyName());
            user.setCompanyDescription(updates.getCompanyDescription());
            user.setCompanyWebsite(updates.getCompanyWebsite());
            user.setIndustry(updates.getIndustry());
        }
        return userRepository.save(user);
    }

    public String uploadResume(User user, MultipartFile file, String uploadDir) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        String resumePath = "/uploads/resumes/" + filename;
        user.setResumePath(resumePath);
        userRepository.save(user);
        return resumePath;
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<User> findByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    public void toggleUserStatus(Long id) {
        User user = findById(id);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
