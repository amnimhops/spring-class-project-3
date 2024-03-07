package com.springclass.library.entities;

import com.springclass.library.dtos.AuthorDTO;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "biography")
    private String bio;
    @OneToMany(mappedBy = "author")
    List<Book> books;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public AuthorDTO toDTO() {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setBooks(getBooks().stream().map(it->{
            AuthorDTO.AuthorBook bookDto = new AuthorDTO.AuthorBook();
            bookDto.setId(it.getId());
            bookDto.setTitle(it.getTitle());
            return bookDto;
        }).toList());

        return dto;
    }
}
