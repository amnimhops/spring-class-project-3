package com.springclass.library.services.impl;

import com.springclass.library.dtos.AuthorDTO;
import com.springclass.library.dtos.BookDTO;
import com.springclass.library.dtos.BookSearch;
import com.springclass.library.dtos.CreateBookDTO;
import com.springclass.library.model.Author;
import com.springclass.library.model.Book;
import com.springclass.library.model.Subject;
import com.springclass.library.repositories.AuthorRepository;
import com.springclass.library.repositories.BookRepository;
import com.springclass.library.repositories.SubjectRepository;
import com.springclass.library.services.LibraryService;
import com.springclass.library.services.errors.ErrorCode;
import com.springclass.library.services.errors.ServiceError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibraryServiceImpl implements LibraryService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Override
    public BookDTO loadBook(Integer id) throws ServiceError {

        if (id == null) {
            throw new ServiceError(ErrorCode.INVALID_INPUT, "El ID del libro no puede ser nulo.");
        }

        if (!isNumeric(id.toString())) {
            throw new ServiceError(ErrorCode.INVALID_INPUT, "El ID del libro debe ser un valor numérico.");
        }

        Optional<Book> opcionalBook = bookRepository.findById(id);
        Book book = opcionalBook.orElseThrow(() -> new ServiceError(ErrorCode.RESOURCE_NOT_FOUND,"Libro no encontrado"));
        return convertBookDto(book);
    }

    private boolean isNumeric(String str) {

        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private BookDTO convertBookDto(Book book){
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAutor().getName());

        List<String> subjectList = new ArrayList<>();
        for(Subject subject : book.getSubjectList()){
            subjectList.add(subject.getTitle());
        }
        bookDTO.setSubjects(subjectList);
        return bookDTO;
    }

    @Override
    public boolean deleteBook(Integer id) throws ServiceError {

        if (id == null) {
            throw new ServiceError(ErrorCode.INVALID_INPUT, "El ID del libro no puede ser nulo.");
        }

        if (!isNumeric(id.toString())) {
            throw new ServiceError(ErrorCode.INVALID_INPUT, "El ID del libro debe ser un valor numérico.");
        }

        if(!bookRepository.existsById(id)){
            throw new ServiceError(ErrorCode.RESOURCE_NOT_FOUND,"Libro no encontrado");
        }
        bookRepository.deleteById(id);
        return true;
    }

    @Override
    public BookDTO createBook(CreateBookDTO input) throws ServiceError {

        if (input.getTitle() == null || input.getAuthor() == null || input.getSubjects() == null) {
            throw new ServiceError(ErrorCode.INVALID_INPUT, "Los parámetros no pueden ser nulos.");
        }

        Author author = getOrCreateAuthor(input.getAuthor());
        List<Subject> subjects = getOrCreateSubjects(input.getSubjects());

        Book book = new Book();
        book.setAutor(author);
        book.setTitle(input.getTitle());
        book.setSubjectList(subjects);

        bookRepository.save(book);

        return convertBookDto(book);
    }

    private Author getOrCreateAuthor(String authorName){
        Optional<Author> optionalAuthor = authorRepository.findByName(authorName);
        return optionalAuthor.orElseGet(() -> {
            Author newAuthor = new Author();
            newAuthor.setName(authorName);
            return authorRepository.save(newAuthor);
        });
    }

    private List<Subject> getOrCreateSubjects(List<String> subjects) {
        List<Subject> subjectList = new ArrayList<>();

        for(String subject : subjects){
            Optional<Subject> optionalSubject = subjectRepository.findByTitle(subject);
            if (optionalSubject.isPresent()) {
                subjectList.add(optionalSubject.get());
            } else {
                Subject newSubject = new Subject();
                newSubject.setTitle(subject);
                subjectList.add(subjectRepository.save(newSubject));
            }
        }
        return subjectList;
    }

    @Override
    public List<BookDTO> findBooks(BookSearch search) throws ServiceError {

        if (search == null || search.getTitle() == null && search.getAuthor() == null && search.getSubject() == null) {
            List<Book> allBooks = bookRepository.findAll();
            return allBooks.stream()
                    .map(this::convertBookDto)
                    .toList();
        }

        List<Book> bookList = bookRepository.findAll();
        List<Book> filterBookList = bookList.stream()
                .filter(book -> isBookMatchingCriteria(book, search))
                .toList();
        return filterBookList.stream()
                .map(this::convertBookDto).toList();
    }

    private boolean isBookMatchingCriteria(Book book, BookSearch search) {
        boolean titleMatches = search.getTitle() == null || book.getTitle().toLowerCase().contains(search.getTitle().toLowerCase());
        boolean authorMatches = search.getAuthor() == null || book.getAutor().getName().toLowerCase().contains(search.getAuthor().toLowerCase());
        boolean anySubjectMatches = search.getSubject() == null || book.getSubjectList().stream()
                .anyMatch(subject -> subject.getTitle().equalsIgnoreCase(search.getSubject()));
        return titleMatches && authorMatches && anySubjectMatches;
    }

    @Override
    public AuthorDTO loadAuthor(Integer id) throws ServiceError {

        /*if (id == null) {
            throw new ServiceError(ErrorCode.INVALID_INPUT, "El ID del libro no puede ser nulo.");
        }

        if (!isNumeric(id.toString())) {
            throw new ServiceError(ErrorCode.INVALID_INPUT, "El ID del autor debe ser un valor numérico.");
        }*/

        Optional<Author> optionalAuthor = authorRepository.findById(id);
        Author author = optionalAuthor.orElseThrow(() -> new ServiceError(ErrorCode.RESOURCE_NOT_FOUND,"Autor no encotrado"));
        return convertToAuthorDTO(author);
    }

    private AuthorDTO convertToAuthorDTO(Author author) {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(author.getId());
        authorDTO.setName(author.getName());
        List<AuthorDTO.AuthorBook> authorBookList = new ArrayList<>();
        for(Book book : author.getBookList()){
            AuthorDTO.AuthorBook authorBook = new AuthorDTO.AuthorBook();
            authorBook.setId(book.getId());
            authorBook.setTitle(book.getTitle());
            authorBookList.add(authorBook);
        }
        authorDTO.setBooks(authorBookList);
        return authorDTO;
    }
}
