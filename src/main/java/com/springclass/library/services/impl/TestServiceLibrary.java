package com.springclass.library.services.impl;

import com.github.javafaker.Faker;
import com.springclass.library.dtos.AuthorDTO;
import com.springclass.library.dtos.BookDTO;
import com.springclass.library.dtos.BookSearch;
import com.springclass.library.dtos.CreateBookDTO;
import com.springclass.library.entities.Book;
import com.springclass.library.services.LibraryService;
import com.springclass.library.services.errors.ErrorCode;
import com.springclass.library.services.errors.ServiceError;
import org.springframework.stereotype.Service;

import java.util.*;


public class TestServiceLibrary implements LibraryService {
    private int nextBookId;

    private Faker faker = Faker.instance();
    private List<BookDTO> books;
    private Set<String> authors;
    private Set<String> subjects;

    private <T> T randomItem(List<T> list){
        int index = (int) (Math.random() * Math.ceil(list.size()));
        return list.get(index);
    }

    public TestServiceLibrary(){
        this.books = new ArrayList<>();
        /**
         * Creamos 25 géneros literarios, 25 autores y 10 libros por autor.
         */
        subjects = new HashSet<>();
        authors = new HashSet<>();

        for(int i = 0; i < 25; i ++) subjects.add(faker.book().genre());
        for(int i = 0; i < 25; i ++) authors.add(faker.book().author());

        List<String> authorList = authors.stream().toList();
        List<String> subjectList = subjects.stream().toList();

        for(int i = 0; i < 25; i++){
            String author = randomItem(authorList);

            for(int j = 0; j < 10; j++){
                Set<String> bookSubjects = new HashSet<>();
                for(int k = 0; k < 3; k++) {
                    bookSubjects.add(randomItem(subjectList));
                }

                nextBookId++;
                BookDTO book = new BookDTO();
                book.setId(nextBookId);
                book.setTitle(faker.book().title());
                book.setAuthor(author);
                book.setSubjects(bookSubjects.stream().toList());
                books.add(book);
            }
        }
    }
    @Override
    public BookDTO loadBook(Integer id) throws ServiceError {
        return books
            .stream()
            .filter( item -> item.getId() == id)
            .findFirst()
            .orElseThrow(
                    ()-> new ServiceError(
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "No se ha encontrado el libro"
                    )
            );
    }

    @Override
    public boolean deleteBook(Integer id) throws ServiceError {
        BookDTO book = this.loadBook(id); // Esto lanzará un error si no existe
        this.books.removeIf( item -> item.getId() == book.getId());
        return true;
    }

    @Override
    public BookDTO createBook(CreateBookDTO input) throws ServiceError {
        BookDTO dto = new BookDTO();
        dto.setId(++nextBookId);
        dto.setSubjects(input.getSubjects());
        dto.setAuthor(input.getAuthor());
        dto.setTitle(input.getTitle());
        books.add(dto);

        return dto;
    }

    @Override
    public List<BookDTO> findBooks(BookSearch search) throws ServiceError {
        List<BookDTO> results = books.stream().filter( book ->
            book.getAuthor().toLowerCase().contains(search.getAuthor().toLowerCase()) &&
            book.getSubjects().stream().anyMatch( subject -> subject.toLowerCase().contains(search.getSubject().toLowerCase())) &&
            book.getTitle().toLowerCase().contains(search.getTitle().toLowerCase())
        ).toList();

        return results;
    }

    @Override
    public AuthorDTO loadAuthor(Integer id) throws ServiceError {
        return null;
    }
}
