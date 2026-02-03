package az.vugar.boardjob.controllers;

import az.vugar.boardjob.entity.User;
import az.vugar.boardjob.repository.JobApplicationRepository;
import az.vugar.boardjob.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/candidate")
@RequiredArgsConstructor
public class CandidateController {

    private final UserRepository userRepository;
    private final JobApplicationRepository jobApplicationRepository;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CANDIDATE')")
    public String candidateProfile(Model model) {
        User currentUser = getCurrentUser();

        // Count applications made by this candidate
        int applicationCount = jobApplicationRepository.countByCandidate(currentUser);

        model.addAttribute("user", currentUser);
        model.addAttribute("applicationCount", applicationCount);

        return "candidate-profile";
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
