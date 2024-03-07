package com.springclass.library.repositories;

import com.springclass.library.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book ,Integer>{

}
