package com.rajko.lucar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rajko.lucar.model.Book;
import com.rajko.lucar.model.Orders;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer> {

	@Query("select o from Orders o where o.cart.idCart=:idCart order by o.idOrder desc")
	List<Orders> findOrdersByIdCart(@Param("idCart") Integer idCart);

	@Query("select b from Book b, Copy c where c.book.idBook=b.idBook and c.cart.idCart=:idCart group by c.idOrder order by c.idOrder desc")
	List<Book> allOrderedBooks(@Param("idCart") Integer idCart);
	
	@Query("select distinct c.idOrder from Copy c where c.cart.idCart=:idCart and c.idOrder is not null order by c.idOrder desc")
	List<Integer> ordersIds(@Param("idCart") Integer idCart);
	
	@Query("select o from Orders o where o.idOrder=:idOrder")
	Orders orders(@Param("idOrder") Integer idOrder);
	
//	@Query("select b, count(b) from Book b where b.idBook in (select c.book.idBook from Copy c where c.idOrder=:idOrder) group by b.idBook having count(b) > 0")
//	List<Book> booksByOrdersId(@Param("idOrder") Integer idOrder);
	
	@Query("select c.idCopy from Copy c where c.idOrder=:idOrder")
	List<Integer> copyIdsFromOrdersId(@Param("idOrder") Integer idOrder);
	
	@Query("select c.book.idBook from Copy c where c.idCopy=:idCopy")
	Integer booksIds(@Param("idCopy") Integer idCopy);
		
//	@Query("select b from Book b, Copy c where c.idCopy=:idCopy group by b.idBook")
//	List<Book> orderedBooks(@Param("idCopy") Integer idCopy);
	
	@Query("select o from Orders o order by o.idOrder desc")
	List<Orders> allOrders();
	
	@Query("select b from Book b where b.idBook in (select c.book.idBook from Copy c where c.cart.idCart=:idCart)")
	List<Book> allOrderedBooksForIdCart(@Param("idCart") Integer idCart);
}
