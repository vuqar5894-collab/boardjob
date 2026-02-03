package az.vugar.boardjob.repository;

import az.vugar.boardjob.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import az.vugar.boardjob.entity.User;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.employer LEFT JOIN FETCH j.category WHERE j.employer = :employer")
    List<Job> findByEmployer(User employer);

    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.employer LEFT JOIN FETCH j.category WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%')) AND LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Job> findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(String title, String location);

    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.employer LEFT JOIN FETCH j.category WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%')) AND LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')) AND j.category = :category")
    List<Job> findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCaseAndCategory(
            String title, String location, az.vugar.boardjob.entity.Category category);

    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.employer LEFT JOIN FETCH j.category")
    List<Job> findAllWithEmployer();

    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.employer LEFT JOIN FETCH j.category WHERE j.id = :id")
    java.util.Optional<Job> findByIdWithRelations(Long id);
}
