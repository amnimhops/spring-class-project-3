package com.springclass.library.services.impl;

import com.springclass.library.dtos.AuthorDTO;
import com.springclass.library.dtos.BookDTO;
import com.springclass.library.dtos.BookSearch;
import com.springclass.library.dtos.CreateBookDTO;
import com.springclass.library.entities.Author;
import com.springclass.library.entities.Book;
import com.springclass.library.entities.Subject;
import com.springclass.library.repositories.AuthorRepository;
import com.springclass.library.repositories.BookRepository;
import com.springclass.library.repositories.SubjectRepository;
import com.springclass.library.services.LibraryService;
import com.springclass.library.services.errors.ErrorCode;
import com.springclass.library.services.errors.ServiceError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DbLibraryService implements LibraryService {
    @Autowired BookRepository bookRepository;
    @Autowired
    AuthorRepository authorRepository;
    @Autowired
    SubjectRepository subjectRepository;

    @Override
    public BookDTO loadBook(Integer id) throws ServiceError {
        return bookRepository
                .findById(id)
                .orElseThrow( ()-> new ServiceError(ErrorCode.RESOURCE_NOT_FOUND,"No se ha encontrado el libro "+id))
                .toDTO();
    }

    @Override
    public boolean deleteBook(Integer id) throws ServiceError {
        Book book = bookRepository
                .findById(id)
                .orElseThrow( ()-> new ServiceError(ErrorCode.RESOURCE_NOT_FOUND,"No se ha encontrado el libro "+id));
        bookRepository.delete(book);
        return true;
    }

    private Author findOrCreateAuthor(String name){
        // Buscar un autor coincidente o crear uno nuevo
            List<Author> authors = authorRepository.findAll();
        Author author = authors
                .stream()
                .filter(it -> it.getName().equals(name))
                .findFirst().orElse(new Author());
        // Si no existe, se crea.
        if(author.getId() == null){
            author.setBio("");
            author.setName(name);
            author = authorRepository.save(author);
        }
        return author;
    }
    private Set<Subject> findOrCreateSubjects(List<String> subjectNames){
        List<Subject> allSubjects = subjectRepository.findAll();
        Set<Subject> subjectList = new HashSet<>();
        for(String name:subjectNames){
            Subject subject = allSubjects.stream().filter(it -> it.getTitle().equals(name)).findFirst().orElse(new Subject());
            // Si no existe, se crea.
            if(subject.getId() == null){
               subject.setTitle(name);
               subject = subjectRepository.save(subject);
            }
            // En este punto, "subject" o es una categoría existente o la que se acaba de crear
            subjectList.add(subject);
        }

        return subjectList;
    }
    @Override
    public BookDTO createBook(CreateBookDTO input) throws ServiceError {
        Book book = new Book();
        book.setAuthor(findOrCreateAuthor(input.getAuthor()));
        book.setSubjects(findOrCreateSubjects(input.getSubjects()));
        book.setTitle(input.getTitle());


        return bookRepository.save(book).toDTO();
    }

    @Override
    public List<BookDTO> findBooks(BookSearch search) throws ServiceError {
        return bookRepository
                .findAll()
                .stream().filter(it -> {
                    return it.getAuthor().getName().contains(search.getAuthor()) &&
                        it.getSubjects().stream().anyMatch( s -> s.getTitle().contains(search.getSubject())) &&
                        it.getTitle().contains(search.getTitle());
                })
                .map(Book::toDTO)
                .toList();
    }

    @Override
    public AuthorDTO loadAuthor(Integer id) throws ServiceError {
        Author author = authorRepository
                .findById(id)
                .orElseThrow( ()-> new ServiceError(ErrorCode.RESOURCE_NOT_FOUND,"No se ha encontrado el autor "+id));

        return author.toDTO();
    }
}
