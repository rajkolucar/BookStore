package com.rajko.lucar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rajko.lucar.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	User findByUsername(String username);
	
	@Query("select u.imagePath from User u")
	List<String> paths();
	
	@Query("select u from User u where u.role=:role")
	List<User> allUsers(@Param("role") String role);
	
}
