package com.rajko.lucar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rajko.lucar.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer>{
	
	Cart findByidUser(Integer idUser);
	
	@Query("select c from Cart c where c.idUser =:idUser")
	Cart findCartByUserId(@Param("idUser") Integer idUser);
	
}
