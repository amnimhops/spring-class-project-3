package com.springclass.library.entities;

import com.springclass.library.dtos.BookDTO;
import jakarta.persistence.*;
import org.apache.logging.log4j.util.Strings;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name="author_id",nullable = true)
    Author author;

    @ManyToMany
    @JoinTable(
            name = "books_subjects",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    Set<Subject> subjects;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public BookDTO toDTO(){
        BookDTO dto = new BookDTO();
        dto.setId(id);
        dto.setTitle(title);
        dto.setAuthor(author != null ? author.getName() : "Desconocido");
        dto.setSubjects(subjects != null ? subjects.stream().map( it->it.getTitle()).toList() : Collections.emptyList());

        return dto;
    }
}
