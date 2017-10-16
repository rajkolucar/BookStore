package com.rajko.lucar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rajko.lucar.model.Copy;

public interface CopyRepository extends JpaRepository<Copy, Integer>{

//	List<Copy> findByBook(Book book);

	@Query("select c from Copy c where c.book.idBook =:idBook and state = 1")
	List<Copy> findByIdbookAndstate(@Param("idBook") Integer idBook);
	
	
	@Query("select c from Copy c where c.book.idBook =:idBook and c.state = 0")
	List<Copy> checkAvailability(@Param("idBook") Integer idBook);
	
//	List<Cart> findByCart(Cart cart);
	
	@Query("select c from Copy c where c.cart.idCart =:idCart and state = 1")
	List<Copy> findByIdCartAndAvailability(@Param("idCart") Integer idCart);
	
	@Query("select c from Copy c where c.cart.idCart =:idCart and state = 2")
	List<Copy> findByIdCart(@Param("idCart") Integer idCart);
}
