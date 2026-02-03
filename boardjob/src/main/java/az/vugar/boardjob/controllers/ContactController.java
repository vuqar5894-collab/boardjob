package az.vugar.boardjob.controllers;

import az.vugar.boardjob.entity.ContactMessage;
import az.vugar.boardjob.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ContactController {

    private final ContactMessageRepository contactMessageRepository;

    @PostMapping("/contact")
    public String submitContactForm(@ModelAttribute ContactMessage contactMessage,
            RedirectAttributes redirectAttributes) {
        try {
            contactMessageRepository.save(contactMessage);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Thank you for contacting us! We will get back to you soon.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Sorry, there was an error sending your message. Please try again.");
        }

        return "redirect:/contact";
    }
}
