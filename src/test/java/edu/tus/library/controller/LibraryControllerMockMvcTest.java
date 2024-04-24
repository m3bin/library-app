package edu.tus.library.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tus.library.dto.Book;
import edu.tus.library.repository.LibraryRepository;
import edu.tus.library.service.LibraryService;

@SpringBootTest
@AutoConfigureMockMvc
public class LibraryControllerMockMvcTest {
	
	@Autowired
	LibraryController libraryController;
	
	@MockBean
	LibraryRepository libraryRepository;
	
	@MockBean
	LibraryService libraryService;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Captor
	ArgumentCaptor<Book> captor;
	
	@Test
	public void addBookControllerTest() throws Exception
	{
		Book book = buildBook();
		ObjectMapper map =new ObjectMapper();
		String jsonString = map.writeValueAsString(book);
		when(libraryService.buildId(book.getIsbn(),book.getAisle())).thenReturn(book.getId());
		when(libraryService.checkBookAlreadyExist(book.getId())).thenReturn(false);
		this.mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON)
				.content(jsonString)).andDo(print()).andExpect(status().isCreated())
		.andExpect(jsonPath("$.id").value(book.getId()))
		.andExpect(jsonPath("$.msg").value("Success Book is Added"));
		verify(libraryRepository,new Times(1)).save(captor.capture());
		Book savedBook=captor.getValue();
		assertEquals(book.getBook_name(),savedBook.getBook_name()); 
		
	}
	 
	@Test
	public void getBookByAuthorTest() throws Exception
	{
		List<Book> books =new ArrayList<Book>();
		books.add(buildBook());
		books.add(buildBook());
		when(libraryRepository.findByAuthor(any())).thenReturn(books);
		this.mockMvc.perform(get("/books/author").param("authorname", "Jane Bloggs"))
		.andDo(print()).andExpect(status().isOk()).
		andExpect(jsonPath("$.length()",is(2))).
		andExpect(jsonPath("$.[0].id").value("sfe3b"));
		
				}
	
	@Test
	public void updateBookTest() throws Exception
	{
		Book book =updateBook();
		ObjectMapper map =new ObjectMapper();
		String jsonString = map.writeValueAsString(book);
		when(libraryService.checkBookAlreadyExist(book.getId())).thenReturn(true);
		when(libraryService.getBookById(any())).thenReturn(book);
		this.mockMvc.perform(put("/books/"+book.getId()).contentType(MediaType.APPLICATION_JSON)
		.content(jsonString)).andDo(print()).andExpect(status().isOk())
		.andExpect(content().json("{\"msg\":\"Success Book is Updated\",\"id\":\"rain322\"}"));
	
	}

	@Test
	public void deleteBookOKControllerTest() throws Exception {
		Book book = buildBook();
		Optional<Book> bookOpt = Optional.of(book);
		when(libraryRepository.findById(book.getId())).thenReturn(bookOpt);
		doNothing().when(libraryRepository).delete(book);
		this.mockMvc
		.perform(delete("/books/" + book.getId()).contentType(MediaType.APPLICATION_JSON)
		.content("{\"id\" : \"sfe3b\"}"))
		.andDo(print()).andExpect(status().isOk()).andExpect(content().string("Book is deleted"));

	}
	
	@Test
	public void deleteBookExceptionControllerTest() throws Exception {
		Book book = buildBook();
		Optional<Book> bookOpt = Optional.of(book);
		when(libraryRepository.findById(book.getId())).thenReturn(bookOpt);
		doNothing().when(libraryRepository).delete(book);
		doThrow(new RuntimeException("Exception")).when(libraryRepository).delete(any());
		this.mockMvc
		.perform(delete("/books/" + book.getId()).contentType(MediaType.APPLICATION_JSON)
		.content("{\"id\" : \"sfe3b\"}"))
		.andDo(print()).andExpect(status().isNotFound());

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
	public Book updateBook()
	{
		Book book =new Book();
		book.setAisle(322);
		book.setBook_name("Microservices");
		book.setIsbn("rain");
		book.setAuthor("Jane Bloggs");
		book.setId("rain322");
		return book;
		
	}

}
