package az.vugar.boardjob.controllers;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    @GetMapping("/employer/jobs")
    @PreAuthorize("hasRole('EMPLOYER')")
    public String employerJobs(Model model) {
        User currentUser = getCurrentUser();
        java.util.List<az.vugar.boardjob.entity.Job> jobs = jobRepository.findByEmployer(currentUser);
        model.addAttribute("jobs", jobs);

        // Calculate statistics
        int totalJobsPosted = jobs.size();
        int totalApplicationsAcrossJobs = 0;
        java.util.Map<Long, Integer> applicationCounts = new java.util.HashMap<>();

        for (az.vugar.boardjob.entity.Job job : jobs) {
            int count = jobApplicationRepository.countByJob(job);
            applicationCounts.put(job.getId(), count);
            totalApplicationsAcrossJobs += count;
        }

        model.addAttribute("applicationCounts", applicationCounts);
        model.addAttribute("totalJobsPosted", totalJobsPosted);
        model.addAttribute("totalApplicationsAcrossJobs", totalApplicationsAcrossJobs);
        return "employer-jobs";
    }

    @PostMapping("/employer/jobs/{id}/delete")
    @PreAuthorize("hasRole('EMPLOYER')")
    public String deleteJob(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        az.vugar.boardjob.entity.Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Check ownership
        if (!job.getEmployer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized: You can only delete your own jobs");
        }

        jobRepository.delete(job);
        redirectAttributes.addFlashAttribute("successMessage", "Job deleted successfully");
        return "redirect:/employer/jobs";
    }

    @GetMapping("/employer/jobs/{id}/applications")
    @PreAuthorize("hasRole('EMPLOYER')")
    public String viewJobApplications(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        User currentUser = getCurrentUser();
        az.vugar.boardjob.entity.Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getEmployer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized access to this job's applications");
        }

        model.addAttribute("job", job);
        model.addAttribute("applications", jobApplicationRepository.findByJob(job));
        return "job-applications";
    }

    @GetMapping("/candidate/applications")
    @PreAuthorize("hasRole('CANDIDATE')")
    public String candidateApplications(Model model) {
        User currentUser = getCurrentUser();
        model.addAttribute("applications", jobApplicationRepository.findByCandidate(currentUser));
        return "candidate-applications";
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
