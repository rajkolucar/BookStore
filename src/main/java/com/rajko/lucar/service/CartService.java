package com.rajko.lucar.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.rajko.lucar.model.Book;
import com.rajko.lucar.model.Cart;
import com.rajko.lucar.model.Copy;
import com.rajko.lucar.model.Orders;
import com.rajko.lucar.model.User;
import com.rajko.lucar.repository.BookRepository;
import com.rajko.lucar.repository.CartRepository;
import com.rajko.lucar.repository.CopyRepository;
import com.rajko.lucar.repository.OrdersRepository;

@Service
public class CartService {

	public static final int AVAILABLE = 0;
	public static final int IN_CART = 1;
	public static final int SOLD = 2;

	public static final String PACKING = "PACKING";
	public static final String READY_FOR_DELIVERY = "READY_FOR_DELIVERY";
	public static final String DELIVERED = "DELIVERED";

	@Autowired
	CartRepository cartRepository;

	@Autowired
	CopyRepository copyRepository;

	@Autowired
	BookRepository bookRepository;

	@Autowired
	LoginService loginService;

	@Autowired
	OrdersRepository ordersRepository;

	public String showCart(Model model) {
		User currentUser = loginService.getUser();
		Cart cart = cartRepository.findCartByUserId(currentUser.getIdUser());
		if (cart == null) {
			cart = new Cart();
			cart.setIdUser(currentUser.getIdUser());
			cartRepository.save(cart);
		}
		List<Copy> listCopy = copyRepository.findByIdCartAndAvailability(cart.getIdCart());
		List<Book> books = new ArrayList<Book>();
		double price = 0;
		for (Copy c : listCopy) {
			Book book = c.getBook();
			books.add(book);
			price += book.getPrice();
		}
		model.addAttribute("username", currentUser.getUsername());
		model.addAttribute("books", books);
		model.addAttribute("sumPrice", price);
		model.addAttribute("cart", cart);
		return "cartUser";
	}

	public String buyOne(Integer idBook, Model model) {
		List<Copy> copies = copyRepository.findByIdbookAndstate(idBook);
		User currentUser = loginService.getUser();
		Cart cart = cartRepository.findCartByUserId(currentUser.getIdUser());
		if (cart == null) {
			cart = new Cart();
			cart.setIdUser(currentUser.getIdUser());
			cartRepository.save(cart);
		}
		Copy copy = null;
		for (Copy first : copies) {
			copy = first;
			break;
		}
		copy.setState(SOLD);
		Orders order = new Orders();
		order = ordersRepository.save(order);
		order.setStatus(PACKING);
		cart.setIdOrder(order.getIdOrder());
		order.setCart(cart);// ?????????????????????????
		ordersRepository.save(order);
		cartRepository.save(cart);
		copy.setIdOrder(order.getIdOrder());
		copyRepository.save(copy);
		return "completeUser";
	}
	
	public String buyNow(Integer idBook, Model model) {
		List<Copy> copies = copyRepository.checkAvailability(idBook);	
		User currentUser = loginService.getUser();
		Cart cart = cartRepository.findCartByUserId(currentUser.getIdUser());
		Copy copy = null;
		for (Copy first : copies) {
			copy = first;
			break;
		}
		Orders order = new Orders();
		order = ordersRepository.save(order);
		order.setStatus(PACKING);
		cart.setIdOrder(order.getIdOrder());
		order.setCart(cart);
		ordersRepository.save(order);
		cartRepository.save(cart);
		copy.setState(SOLD);
		copy.setCart(cart);
		Book b = copy.getBook();
		b.setQuantity(b.getQuantity() - 1);
		copy.setIdOrder(order.getIdOrder());
		copyRepository.save(copy);
		return "completeUser";
	}

	public String removeOne(Integer idBook) {
		List<Copy> copies = copyRepository.findByIdbookAndstate(idBook);
		Copy copy = null;
		for (Copy first : copies) {
			copy = first;
			break;
		}
		copy.setState(AVAILABLE);
		copy.setCart(null);
		Book b = bookRepository.findOne(idBook);
		b.setQuantity(b.getQuantity() + 1);
		bookRepository.save(b);
		copyRepository.save(copy);
		return "completeUser";
	}
	
	public void removeAll(Integer idCart) {
		List<Copy> copies = copyRepository.findByIdCartAndAvailability(idCart);
		for(Copy copy : copies) {
			copy.setCart(null);
			copy.setState(AVAILABLE);
			Book book = copy.getBook();
			book.setQuantity(book.getQuantity() + 1);
			copyRepository.save(copy);
		}
	}

	public void addToCart(Integer idBook, Model model) {
		List<Copy> availableCopies = copyRepository.checkAvailability(idBook);
		Copy copy = null;
		for (Copy f : availableCopies) {
			copy = f;
			break;
		}
		Book book = bookRepository.findOne(idBook);
		User user = loginService.getUser();
		Cart cart = cartRepository.findCartByUserId(user.getIdUser());
		model.addAttribute("cart", cart);
		cartRepository.save(cart);
		book.setQuantity(book.getQuantity() - 1);
		copy.setCart(cart);
		copy.setState(IN_CART);
		copyRepository.save(copy);
	}
	
	public String buyAll(Integer idCart) {
		Cart cart = cartRepository.findOne(idCart);
		List<Copy> listCopy = copyRepository.findByIdCartAndAvailability(cart.getIdCart());
		Copy copy = null;
		Orders order = new Orders();
		for (Copy first : listCopy) {
			copy = first;
			copy.setState(SOLD);
			ordersRepository.save(order);
			copy.setIdOrder(order.getIdOrder());
			copy.setCart(cart);
			order.setCart(cart);
			order.setStatus(PACKING);
			ordersRepository.save(order);
			copyRepository.save(copy);
		}
		return "result";
	}
}