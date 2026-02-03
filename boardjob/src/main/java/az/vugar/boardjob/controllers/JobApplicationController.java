package az.vugar.boardjob.controllers;

import az.vugar.boardjob.entity.Job;
import az.vugar.boardjob.entity.JobApplication;
import az.vugar.boardjob.entity.User;
import az.vugar.boardjob.repository.JobApplicationRepository;
import az.vugar.boardjob.repository.JobRepository;
import az.vugar.boardjob.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    @PostMapping("/{id}/apply")
    @PreAuthorize("hasRole('CANDIDATE')")
    public String applyForJob(@PathVariable Long id, @RequestParam(required = false) String coverLetter) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User candidate = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setCandidate(candidate);
        application.setCoverLetter(coverLetter);

        jobApplicationRepository.save(application);

        return "redirect:/jobs/" + id + "?applied=true";
    }
}
