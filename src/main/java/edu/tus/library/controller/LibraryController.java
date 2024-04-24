package edu.tus.library.controller;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tus.library.dto.Book;
import edu.tus.library.message.AddResponse;
import edu.tus.library.repository.LibraryRepository;
import edu.tus.library.service.LibraryService;


@RestController
public class LibraryController {

	@Autowired
	LibraryRepository repository;

	@Autowired
	LibraryService libraryService;

	private static final Logger logger = LoggerFactory.getLogger(LibraryController.class);

	@PostMapping("/books")
	public ResponseEntity addBookImplementation(@RequestBody Book book) {
		String id = libraryService.buildId(book.getIsbn(), book.getAisle());// dependenyMock
		AddResponse ad = new AddResponse();

		if (!libraryService.checkBookAlreadyExist(id))// mock
		{
			logger.info("Book do not exist so creating one");
			book.setId(id);
			repository.save(book);// mock
			HttpHeaders headers = new HttpHeaders();
			headers.add("unique", id);
			ad.setMsg("Success Book is Added");
			ad.setId(id);
			// return ad;
			return new ResponseEntity<AddResponse>(ad, headers, HttpStatus.CREATED);
		} else {
			logger.info("Book  exist so skipping creation");
			ad.setMsg("Book already exist");
			ad.setId(id);
			return new ResponseEntity<AddResponse>(ad, HttpStatus.ACCEPTED);
		}

	}
	
	@RequestMapping("/books/{id}")
	public Book getBookById(@PathVariable(value = "id") String id) {
		Book book;
		try {
			book = repository.findById(id).get();

		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
		}
		return book;
	}
	
	@RequestMapping("/books/author")
	public ResponseEntity<List<Book>> getBookByAuthor(@RequestParam(value = "authorname") String authorName) {
		List<Book> booksByAuthor = new ArrayList<>();
		booksByAuthor=repository.findByAuthor(authorName);
		if (booksByAuthor.size()>0) {
			return new ResponseEntity(booksByAuthor, HttpStatus.OK);
		}else {
			return new ResponseEntity(booksByAuthor, HttpStatus.NO_CONTENT);
		}
	}
	
	@PutMapping("/books/{id}")
	public ResponseEntity updateBook(@PathVariable("id") String id,@RequestBody Book book) {
		//String id = libraryService.buildId(book.getIsbn(), book.getAisle());// dependenyMock
		AddResponse ad = new AddResponse();

		if (libraryService.checkBookAlreadyExist(id))// mock
		{
			logger.info("Updating existing book");
			book.setId(id);
			repository.save(book);// mock
			HttpHeaders headers = new HttpHeaders();
			headers.add("unique", id);
			ad.setMsg("Success Book is Updated");
			ad.setId(id);
			// return ad;
			return new ResponseEntity<AddResponse>(ad, headers, HttpStatus.OK);
		} else {
			logger.info("Book does not exist");
			ad.setMsg("Book does not exist");
			ad.setId(id);
			return new ResponseEntity<AddResponse>(ad, HttpStatus.NOT_FOUND);
		}

	}
	
	@DeleteMapping("/books/{id}")
	public ResponseEntity deleteBook(@PathVariable("id") String id) {
		AddResponse ad = new AddResponse();
		Book book;
		try {
			book = repository.findById(id).get();
			repository.delete(book);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
		}
		return new ResponseEntity<>("Book is deleted", HttpStatus.OK);
	}

}
