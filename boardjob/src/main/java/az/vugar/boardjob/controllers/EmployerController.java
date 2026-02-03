package az.vugar.boardjob.controllers;

import az.vugar.boardjob.entity.Job;
import az.vugar.boardjob.entity.User;
import az.vugar.boardjob.repository.JobApplicationRepository;
import az.vugar.boardjob.repository.JobRepository;
import az.vugar.boardjob.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/employer")
@RequiredArgsConstructor
public class EmployerController {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('EMPLOYER')")
    public String employerProfile(Model model) {
        User currentUser = getCurrentUser();

        // Get jobs posted by this employer
        List<Job> jobs = jobRepository.findByEmployer(currentUser);
        int totalJobsPosted = jobs.size();

        // Count total applications across all jobs
        int totalApplications = 0;
        for (Job job : jobs) {
            totalApplications += jobApplicationRepository.countByJob(job);
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("totalJobsPosted", totalJobsPosted);
        model.addAttribute("totalApplications", totalApplications);

        return "employer-profile";
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
