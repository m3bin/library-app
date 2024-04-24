package edu.tus.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.tus.library.dto.Book;
import edu.tus.library.repository.LibraryRepository;
import java.util.Optional;

@Service
public class LibraryService {
	
	@Autowired
	LibraryRepository repository;
	public String buildId(String isbn,int aisle)
	{
		if(isbn.startsWith("Z"))
		{
			return "OLD"+isbn+aisle;
		}
		
		return isbn+aisle;
	}
	public boolean checkBookAlreadyExist(String id)
	{
		Optional<Book> lib=repository.findById(id);
		if(lib.isPresent())
			return true;
		else
			return false;
		
		
	}
	public Book getBookById(String id)
	{
		return repository.findById(id).get();
	}

}
