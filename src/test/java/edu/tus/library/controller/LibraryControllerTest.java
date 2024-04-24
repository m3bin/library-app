package edu.tus.library.controller;
import org.junit.jupiter.api.Test;
import org.mockito.Captor;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import edu.tus.library.dto.Book;
import edu.tus.library.message.AddResponse;
import edu.tus.library.repository.LibraryRepository;
import edu.tus.library.service.LibraryService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.ArgumentCaptor;

@SpringBootTest
public class LibraryControllerTest {
	
	@Autowired
	LibraryController libraryController;
	
	@MockBean
	LibraryRepository libraryRepository;
	
	@MockBean
	LibraryService libraryService;
	
	@Captor
	ArgumentCaptor<Book> captor;
	
	@Test
	public void addBookTestSuccess()
	{
		Book book = buildBook();
		when(libraryService.buildId(book.getIsbn(),book.getAisle())).thenReturn(book.getId());
		when(libraryService.checkBookAlreadyExist(book.getId())).thenReturn(false);
		when(libraryRepository.save(any())).thenReturn(book);
		ResponseEntity response	=libraryController.addBookImplementation(book);
		assertEquals(response.getStatusCode(),HttpStatus.CREATED);
		AddResponse ad= (AddResponse) response.getBody();
		ad.getId();
		assertEquals(book.getId(),ad.getId());
		assertEquals("Success Book is Added",ad.getMsg());
		verify(libraryRepository,new Times(1)).save(captor.capture());
		Book savedBook=captor.getValue();
		assertTrue(book.equals(savedBook));
	}
	
	@Test
	public void addBookTestAlreadyExists()
	{
		Book book = buildBook();
		when(libraryService.buildId(book.getIsbn(),book.getAisle())).thenReturn(book.getId());
		when(libraryService.checkBookAlreadyExist(book.getId())).thenReturn(true);
		ResponseEntity response	=libraryController.addBookImplementation(book);
		assertEquals(response.getStatusCode(),HttpStatus.ACCEPTED);
		AddResponse ad= (AddResponse) response.getBody();
		ad.getId();
		assertEquals(book.getId(),ad.getId());
		assertEquals("Book already exist",ad.getMsg());
		verify(libraryRepository,new Times(0)).save(any());
	}
	
	public Book buildBook()
	{
		Book book =new Book();
		book.setAisle(322);
		book.setBook_name("Springboot");
		book.setIsbn("sfe");
		book.setAuthor("Joe Bloggs");
		book.setId("sfe3b");
		return book;
		
	}

}
