package com.rajko.lucar.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rajko.lucar.model.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {
	
//	@Query("select b from Book b where quantity > 0") 
//	List<Book> findAvailableBooks();

	@Query("select b from Book b where b.title like %:title% or b.author like %:author% or b.genre like %:genre%")
	List<Book> findByT(@Param("title") String title, @Param("author") String author, @Param("genre") String genre);
	
	@Query("select distinct b.genre from Book b")
	List<String> allGenres();
	
	@Query("select c.book from Copy c where c.state = 2 group by c.book.idBook order by count(c.book.idBook) desc")
	List<Book> topTen(Pageable pageable);

	@Query("select b.imagePath from Book b")
	List<String> paths();
	
	List<Book> findByGenre(String genre);
}
