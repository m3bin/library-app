package edu.tus.library.controller;

import org.json.JSONException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
//import org.junit.Assert;
//import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.tus.library.dto.Book;
import edu.tus.library.repository.LibraryRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LibraryAppIT {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	LibraryRepository libraryRepository;
	
	@Value(value="${local.server.port}")
	private int port;
	
//	@BeforeEach
//	public void setUp() {
//		libraryRepository.deleteAll();
//		System.out.println("Testing before each");
//	}

	//mvn test
	//TestRestTemplate Rest Assured
	//@Sql({"classpath:data.sql"})
	@Test
	public void getAuthorNameBooksTest() throws JSONException
	{
		String expected= "[\r\n" + 
				"    {\r\n" + 
				"        \"book_name\": \"Java Programming\",\r\n" + 
				"        \"id\": \"abcd4\",\r\n" + 
				"        \"isbn\": \"abcd\",\r\n" + 
				"        \"aisle\": 4,\r\n" + 
				"        \"author\": \"Joe Bloggs\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"        \"book_name\": \"Devops\",\r\n" + 
				"        \"id\": \"fdsefr343\",\r\n" + 
				"        \"isbn\": \"fdsefr3\",\r\n" + 
				"        \"aisle\": 43,\r\n" + 
				"        \"author\": \"Joe Bloggs\"\r\n" + 
				"    }\r\n" + 
				"]";
	TestRestTemplate restTemplate =new TestRestTemplate();
	ResponseEntity<String>	response =restTemplate.getForEntity("http://localhost:"+port+"/books/author?authorname=Joe Bloggs", String.class);
	System.out.println(response.getStatusCode());
	System.out.println(response.getBody());
	JSONAssert.assertEquals(expected, response.getBody(), false); 
	
		
	}
	
    @Test
	@Sql({"/testdata.sql"})
	public void addBookIntegrationTest()
	{
		TestRestTemplate restTemplate =new TestRestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Book> request = new HttpEntity<Book>(buildBook(),headers);
		ResponseEntity<String>	response =	restTemplate.postForEntity("http://localhost:"+port+"/books", request, String.class);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(buildBook().getId(),response.getHeaders().get("unique").get(0));
	}
	

	
	public Book buildBook()
	{
		Book lib =new Book();
		lib.setAisle(322);
		lib.setBook_name("Springboot");
		lib.setIsbn("sfes");
		lib.setAuthor("Unique Author");
		lib.setId("sfes322");
		return lib;
		
	}
	
	
}
