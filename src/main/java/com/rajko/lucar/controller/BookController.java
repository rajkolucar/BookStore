package com.rajko.lucar.controller;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.rajko.lucar.model.Book;
import com.rajko.lucar.model.Copy;
import com.rajko.lucar.repository.BookRepository;
import com.rajko.lucar.repository.CartRepository;
import com.rajko.lucar.repository.CopyRepository;
import com.rajko.lucar.repository.OrdersRepository;
import com.rajko.lucar.repository.UserRepository;
import com.rajko.lucar.service.BookService;
import com.rajko.lucar.service.UserService;

@Controller
public class BookController {

	public static final int AVAILABLE = 0;
	public static final int IN_CART = 1;
	public static final int SOLD = 2;

	@Autowired
	BookService bookService;

	@Autowired
	BookRepository bookRepository;

	@Autowired
	CopyRepository copyRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	OrdersRepository ordersRepository;

	@Autowired
	UserService userService;

	@Autowired
	LoginController loginController;

	@GetMapping("booksUser")
	public String booksUser(Model model) {
		model.addAttribute("books", bookRepository.findAll());
		return "booksUser";
	}

	@GetMapping("add")
	public String addBook(Model model) {
		model.addAttribute("book", new Book());
		return "addBook";
	}

	@RequestMapping(method = RequestMethod.POST, value = "add", headers = "content-type=multipart/*")
	public String add(@RequestParam("file") MultipartFile file, @ModelAttribute final Book book,
			@ModelAttribute final Copy copy) throws IOException {
		bookService.addBook(file, book, copy);
		return "completeAdmin";
	}

	@GetMapping("/booksAdmin")
	public String showAllAdmin(Model model) {
		model.addAttribute("books", bookRepository.findAll());
		return "booksAdmin";
	}

	@PostMapping("/searchAdmin")
	public String findAdmin(@ModelAttribute("value") String value, Model model) {
		bookService.find(value, model);
		return "searchAdmin";
	}

	@PostMapping("/searchUser")
	public String findUser(@ModelAttribute("value") String value, Model model) {
		bookService.find(value, model);
		return "searchUser";
	}

	@GetMapping("/change")
	public String change(@RequestParam("bookId") Integer idBook, Model model) {
		model.addAttribute("book", bookRepository.findOne(idBook));
		return "change";
	}

	@PostMapping(value = "/change", headers = "content-type=multipart/*")
	public String changeApply(@RequestParam("file") MultipartFile file,
			@RequestParam(name = "path", required = false) String path, @RequestParam("bookId") Integer idBook,
			@ModelAttribute final Book book) throws IllegalStateException, IOException {
		return bookService.change(file, path, idBook, book);
	}

	@RequestMapping("/findByGenre")
	public String findByGenre(@RequestParam("genre") String genre, Model model) {
		bookService.findByGenre(genre, model);
		return "searchByGenre";
	}

	@RequestMapping("/findByGenreAdmin")
	public String findByGenreAdmin(@RequestParam("genre") String genre, Model model) {
		bookService.findByGenre(genre, model);
		return "searchByGenreAdmin";
	}
}
