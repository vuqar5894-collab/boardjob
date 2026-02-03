package az.vugar.boardjob.controllers;

import az.vugar.boardjob.repository.ContactMessageRepository;
import az.vugar.boardjob.repository.JobApplicationRepository;
import az.vugar.boardjob.repository.JobRepository;
import az.vugar.boardjob.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ContactMessageRepository contactMessageRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model) {
        long totalUsers = userRepository.count();
        long totalJobs = jobRepository.count();
        long totalApplications = jobApplicationRepository.count();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalJobs", totalJobs);
        model.addAttribute("totalApplications", totalApplications);

        return "admin/dashboard";
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String manageUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @GetMapping("/jobs")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String manageJobs(Model model) {
        model.addAttribute("jobs", jobRepository.findAllWithEmployer());
        return "admin/jobs";
    }

    @GetMapping("/applications")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String viewApplications(Model model) {
        model.addAttribute("applications", jobApplicationRepository.findAllWithRelations());
        return "admin/applications";
    }

    @GetMapping("/messages")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewContactMessages(Model model) {
        model.addAttribute("messages", contactMessageRepository.findAllByOrderByCreatedAtDesc());
        return "admin/messages";
    }

    @PostMapping("/users/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            az.vugar.boardjob.entity.User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

            // Prevent deleting admin users
            if (user.getRole() == az.vugar.boardjob.entity.Role.ADMIN) {
                redirectAttributes.addFlashAttribute("error", "Admin istifadəçilərini silmək olmaz!");
                return "redirect:/admin/users";
            }

            // Delete related job applications first to avoid foreign key constraint
            // violation
            jobApplicationRepository.deleteAll(
                    jobApplicationRepository.findAll().stream()
                            .filter(app -> app.getCandidate().getId().equals(id))
                            .toList());

            // If user is an employer, delete their jobs and related applications
            if (user.getRole() == az.vugar.boardjob.entity.Role.EMPLOYER) {
                jobRepository.findAll().stream()
                        .filter(job -> job.getEmployer().getId().equals(id))
                        .forEach(job -> {
                            // Delete applications for this job
                            jobApplicationRepository.deleteAll(
                                    jobApplicationRepository.findAll().stream()
                                            .filter(app -> app.getJob().getId().equals(job.getId()))
                                            .toList());
                            // Delete the job
                            jobRepository.delete(job);
                        });
            }

            userRepository.delete(user);
            redirectAttributes.addFlashAttribute("success", "İstifadəçi və əlaqəli məlumatlar uğurla silindi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Xəta baş verdi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/change-role/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String changeUserRole(@PathVariable Long id,
            @RequestParam String role,
            RedirectAttributes redirectAttributes) {
        try {
            az.vugar.boardjob.entity.User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

            // Prevent changing admin role
            if (user.getRole() == az.vugar.boardjob.entity.Role.ADMIN) {
                redirectAttributes.addFlashAttribute("error", "Admin rolunu dəyişmək olmaz!");
                return "redirect:/admin/users";
            }

            az.vugar.boardjob.entity.Role newRole = az.vugar.boardjob.entity.Role.valueOf(role);
            user.setRole(newRole);
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("success", "İstifadəçi rolu uğurla dəyişdirildi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Xəta baş verdi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/jobs/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String deleteJob(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            az.vugar.boardjob.entity.Job job = jobRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("İş elanı tapılmadı"));

            // Delete related job applications first to avoid foreign key constraint
            // violation
            jobApplicationRepository.deleteAll(
                    jobApplicationRepository.findAll().stream()
                            .filter(app -> app.getJob().getId().equals(id))
                            .toList());

            jobRepository.delete(job);
            redirectAttributes.addFlashAttribute("success", "İş elanı və əlaqəli müraciətlər uğurla silindi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Xəta baş verdi: " + e.getMessage());
        }
        return "redirect:/admin/jobs";
    }
}
