package az.vugar.boardjob.controllers;

import az.vugar.boardjob.entity.Job;
import az.vugar.boardjob.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final az.vugar.boardjob.repository.UserRepository userRepository;
    private final az.vugar.boardjob.repository.CategoryRepository categoryRepository;
    private final az.vugar.boardjob.repository.JobApplicationRepository jobApplicationRepository;

    @GetMapping
    public String listJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Long categoryId,
            Model model) {

        java.util.List<az.vugar.boardjob.entity.Job> jobs;

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasLocation = location != null && !location.trim().isEmpty();
        boolean hasCategory = categoryId != null;

        if (hasKeyword || hasLocation || hasCategory) {
            String searchKeyword = keyword != null ? keyword : "";
            String searchLocation = location != null ? location : "";

            if (hasCategory) {
                az.vugar.boardjob.entity.Category category = categoryRepository.findById(categoryId).orElse(null);
                if (category != null) {
                    jobs = jobService.searchJobsWithCategory(searchKeyword, searchLocation, category);
                } else {
                    jobs = jobService.searchJobs(searchKeyword, searchLocation);
                }
            } else {
                jobs = jobService.searchJobs(searchKeyword, searchLocation);
            }
        } else {
            jobs = jobService.getAllJobs();
        }

        model.addAttribute("jobs", jobs);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("searchKeyword", keyword);
        model.addAttribute("searchLocation", location);
        return "job-listings";
    }

    @GetMapping("/{id}")
    public String getJobById(@PathVariable Long id, Model model) {
        az.vugar.boardjob.entity.Job job = jobService.getJobById(id);
        model.addAttribute("job", job);

        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        boolean isCandidate = auth != null
                && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CANDIDATE"));
        boolean alreadyApplied = false;

        if (isCandidate) {
            String email = auth.getName();
            az.vugar.boardjob.entity.User currentUser = userRepository.findByEmail(email).orElse(null);
            if (currentUser != null) {
                alreadyApplied = jobApplicationRepository.existsByJobAndCandidate(job, currentUser);
            }
        }

        model.addAttribute("canApply", isCandidate && !alreadyApplied);
        model.addAttribute("alreadyApplied", alreadyApplied);
        return "job-single";
    }

    @GetMapping("/post")
    public String showPostJobForm(Model model) {
        model.addAttribute("job", new Job());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("jobTypes", az.vugar.boardjob.entity.JobType.values());
        return "post-job";
    }

    @PostMapping("/post")
    public String saveJob(@ModelAttribute Job job) {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        String email = authentication.getName();
        az.vugar.boardjob.entity.User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        job.setEmployer(currentUser);
        jobService.saveJob(job);
        return "redirect:/jobs";
    }
}
