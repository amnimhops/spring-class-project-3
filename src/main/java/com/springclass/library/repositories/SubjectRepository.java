package com.springclass.library.repositories;

import com.springclass.library.entities.Author;
import com.springclass.library.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject,Integer> {
}
