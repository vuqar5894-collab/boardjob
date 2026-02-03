package az.vugar.boardjob.service;

import az.vugar.boardjob.entity.Job;
import az.vugar.boardjob.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public List<Job> getAllJobs() {
        return jobRepository.findAllWithEmployer();
    }

    public Job getJobById(Long id) {
        return jobRepository.findByIdWithRelations(id).orElse(null);
    }

    public void saveJob(Job job) {
        if (job.getCreatedAt() == null) {
            job.setCreatedAt(LocalDateTime.now());
        }
        jobRepository.save(job);
    }

    public List<Job> searchJobs(String keyword, String location) {
        return jobRepository.findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(keyword, location);
    }

    public List<Job> searchJobsWithCategory(String keyword, String location,
            az.vugar.boardjob.entity.Category category) {
        return jobRepository.findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCaseAndCategory(keyword,
                location, category);
    }

    public List<Job> getLatestJobs(int limit) {
        return jobRepository.findAllWithEmployer().stream()
                .sorted((j1, j2) -> j2.getCreatedAt().compareTo(j1.getCreatedAt()))
                .limit(limit)
                .toList();
    }
}
