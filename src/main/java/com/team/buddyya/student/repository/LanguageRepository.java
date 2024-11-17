package com.team.buddyya.student.repository;

import com.team.buddyya.student.domain.Interest;
import com.team.buddyya.student.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

    Optional<Language> findByLanguageName(String languageName);

}
