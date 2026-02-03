package az.vugar.boardjob.repository;

import az.vugar.boardjob.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;

import az.vugar.boardjob.entity.Job;
import az.vugar.boardjob.entity.User;
import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    @Query("SELECT ja FROM JobApplication ja LEFT JOIN FETCH ja.candidate LEFT JOIN FETCH ja.job WHERE ja.candidate = :candidate")
    List<JobApplication> findByCandidate(User candidate);

    int countByJob(Job job);

    int countByCandidate(User candidate);

    @Query("SELECT ja FROM JobApplication ja LEFT JOIN FETCH ja.candidate LEFT JOIN FETCH ja.job WHERE ja.job = :job")
    List<JobApplication> findByJob(Job job);

    boolean existsByJobAndCandidate(Job job, User candidate);

    @Query("SELECT ja FROM JobApplication ja LEFT JOIN FETCH ja.candidate LEFT JOIN FETCH ja.job")
    List<JobApplication> findAllWithRelations();
}
