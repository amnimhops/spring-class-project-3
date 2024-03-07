package com.springclass.library.repositories;

import com.springclass.library.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author,Integer> {
    List<Author> findAll();
}
