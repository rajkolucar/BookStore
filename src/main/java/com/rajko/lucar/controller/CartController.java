package com.rajko.lucar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rajko.lucar.model.Cart;
import com.rajko.lucar.model.User;
import com.rajko.lucar.repository.CartRepository;
import com.rajko.lucar.service.BookService;
import com.rajko.lucar.service.CartService;
import com.rajko.lucar.service.LoginService;

@Controller
public class CartController {

	@Autowired
	CartService cartService;
	
	@Autowired
	BookService bookService;
	
	@Autowired
	LoginService loginService;
	
	@Autowired
	CartRepository cartRepository;
	
	
	@GetMapping("/cart")
	public String showCart(Model model) {
		cartService.showCart(model);
		return "cartUser";
	}
	
	@PostMapping("/buyOne")
	public String buyOne(@RequestParam("bookId") Integer idBook, Model model) {
		return cartService.buyOne(idBook, model);
	}
	
	@PostMapping("/addToCart")
	public String addToCart(@RequestParam("bookId") Integer idBook, Model model) {
		cartService.addToCart(idBook, model);
		return "completeUser";
	}
	
	@PostMapping("/buyNow")
	public String buyNow(@RequestParam("bookId") Integer idBook, Model model) {
		return cartService.buyNow(idBook, model);
	}
	
	@PostMapping("/removeOne")
	public String removeOne(@RequestParam("bookId") Integer idBook) {
		return cartService.removeOne(idBook);
	}
	
	@PostMapping("/removeAll")
	public String removeAll(@RequestParam("cartId") Integer idCart) {
		cartService.removeAll(idCart);
		return "completeUser";
	}
	
	@PostMapping("/buyAll")
	public String buyAll(@RequestParam("cartId") Integer idCart) {
		User currentUser = loginService.getUser();
		Cart cart = cartRepository.findCartByUserId(currentUser.getIdUser()); 
		cartService.buyAll(cart.getIdCart());
		return "completeUser";
	}
}
