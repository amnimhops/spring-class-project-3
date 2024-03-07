package com.springclass.library.services;

import com.springclass.library.dtos.AuthorDTO;
import com.springclass.library.dtos.BookDTO;
import com.springclass.library.dtos.BookSearch;
import com.springclass.library.dtos.CreateBookDTO;
import com.springclass.library.services.errors.ServiceError;

import java.util.List;

public interface LibraryService {
    BookDTO loadBook(Integer id) throws ServiceError;

    boolean deleteBook(Integer id) throws ServiceError;

    BookDTO createBook(CreateBookDTO input) throws ServiceError;

    List<BookDTO> findBooks(BookSearch search) throws ServiceError;

    AuthorDTO loadAuthor(Integer id) throws ServiceError;
}
