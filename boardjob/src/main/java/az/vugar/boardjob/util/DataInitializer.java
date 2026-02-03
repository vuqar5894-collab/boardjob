package az.vugar.boardjob.util;

import az.vugar.boardjob.entity.Category;
import az.vugar.boardjob.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void init() {
        if (categoryRepository.count() == 0) {
            List<String> categoryNames = Arrays.asList("Engineering", "Design", "Marketing", "DevOps", "Data Science");
            List<Category> categories = categoryNames.stream()
                    .map(name -> {
                        Category category = new Category();
                        category.setName(name);
                        return category;
                    })
                    .toList();
            categoryRepository.saveAll(categories);
        }
    }
}
