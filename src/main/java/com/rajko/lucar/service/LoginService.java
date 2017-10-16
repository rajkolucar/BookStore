package com.rajko.lucar.service;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.rajko.lucar.controller.LoginController;
import com.rajko.lucar.model.Book;
import com.rajko.lucar.model.Cart;
import com.rajko.lucar.model.User;
import com.rajko.lucar.repository.BookRepository;
import com.rajko.lucar.repository.CartRepository;

@Service
public class LoginService {

	@Autowired
	BookRepository bookRepository;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	UserService userService;

	@Autowired
	LoginController loginController;

	User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ModelAndView login() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}

	public ModelAndView registration() {
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("registration");
		return modelAndView;
	}

	public ModelAndView homePage(Book book, Model model) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		user = userService.findByUsername(auth.getName());
		//modelAndView.addObject("userName",
		//	"Welcome" + user.getFirstName() + " " + user.getLastName() + " (" + user.getUsername() + ").");
	//modelAndView.addObject("adminMessage", "Content Available Only form Users with Admin Role");
		model.addAttribute("genres", bookRepository.allGenres());
		model.addAttribute("books", bookRepository.topTen(new PageRequest(0, 10)));
		if (user.getRole().equals("ADMIN")) {
			modelAndView.setViewName("indexAdmin");
		} else if (user.getRole().equals("USER")) {
			userService.recommendation(model);
			modelAndView.setViewName("indexUser");
		}
		return modelAndView;
	}

	public ModelAndView createNewUser(MultipartFile file,@Valid User user, BindingResult bindingResult) throws IOException {
		ModelAndView modelAndView = new ModelAndView();
		User userExists = userService.findByUsername(user.getUsername());
		if (userExists != null) {
			bindingResult.rejectValue("username", "error.user", "There is already a user with this username");
		}
		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("registration");
		} else {
			Cart cart = new Cart();
			userService.addProfilePicture(file, user);
			userService.saveUser(user);
			cart.setIdUser(user.getIdUser());
			cartRepository.save(cart);
			modelAndView.addObject("successMessage", "User has been registered successfully");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("registration");
		}
		return modelAndView;
	}
}
