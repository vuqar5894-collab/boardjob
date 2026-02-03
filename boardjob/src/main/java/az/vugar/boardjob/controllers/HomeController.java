package az.vugar.boardjob.controllers;

import az.vugar.boardjob.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final JobService jobService;

    @GetMapping({ "/", "/index" })
    public String index(Model model) {
        model.addAttribute("latestJobs", jobService.getLatestJobs(6));
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/job-listings")
    public String jobListings() {
        return "job-listings";
    }

    @GetMapping("/job-single")
    public String jobSingle() {
        return "job-single";
    }

    @GetMapping("/post-job")
    public String postJob() {
        return "post-job";
    }

    @GetMapping("/services")
    public String services() {
        return "services";
    }

    @GetMapping("/service-single")
    public String serviceSingle() {
        return "service-single";
    }

    @GetMapping("/blog-single")
    public String blogSingle() {
        return "blog-single";
    }

    @GetMapping("/portfolio")
    public String portfolio() {
        return "portfolio";
    }

    @GetMapping("/portfolio-single")
    public String portfolioSingle() {
        return "portfolio-single";
    }

    @GetMapping("/testimonials")
    public String testimonials() {
        return "testimonials";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/gallery")
    public String gallery() {
        return "gallery";
    }

    @GetMapping("/blog")
    public String blog() {
        return "blog";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

}
