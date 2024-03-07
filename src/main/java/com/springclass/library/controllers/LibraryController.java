package com.springclass.library.controllers;

import com.springclass.library.dtos.AuthorDTO;
import com.springclass.library.dtos.BookDTO;
import com.springclass.library.dtos.BookSearch;
import com.springclass.library.dtos.CreateBookDTO;
import com.springclass.library.services.LibraryService;
import com.springclass.library.services.errors.ServiceError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/library")
public class LibraryController {
    @Autowired
    LibraryService service;

    private <T> ResponseEntity handleRequest(Supplier<T> supplier){
        try{
            return ResponseEntity.ok(supplier.get());
        }catch(ServiceError e){
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(e.getErrorCode().getHttpErrorCode()));
        }
    }
    @PostMapping("/books/search")
    public ResponseEntity<List<BookDTO>> findBooks(@RequestBody BookSearch search){
        return handleRequest( ()-> service.findBooks(search) );
    }
    @GetMapping("/books/{id}")
    public ResponseEntity<BookDTO> loadBook(@PathVariable Integer id){
        return handleRequest( ()-> service.loadBook(id) );
    }
    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id){
        return handleRequest( ()-> service.deleteBook(id) );
    }

    @PostMapping("/books")
    public ResponseEntity<BookDTO> createBook(@RequestBody CreateBookDTO input){
        return handleRequest( ()-> service.createBook(input) );
    }

    @GetMapping("/authors/{id}")
    public ResponseEntity<AuthorDTO> loadAuthor(@PathVariable Integer id){
        return handleRequest( ()-> service.loadAuthor(id) );
    }
}
