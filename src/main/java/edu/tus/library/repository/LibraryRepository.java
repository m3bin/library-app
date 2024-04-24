package edu.tus.library.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.tus.library.dto.*;

@Repository
public interface LibraryRepository extends JpaRepository<Book, String>{
	List<Book> findByAuthor(String author);

}
