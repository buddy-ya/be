package com.team.buddyya.feed.respository;

import com.team.buddyya.feed.domain.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String category);

    boolean existsByName(String category);
}
