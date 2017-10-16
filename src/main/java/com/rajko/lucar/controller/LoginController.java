package com.rajko.lucar.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.rajko.lucar.model.Book;
import com.rajko.lucar.model.User;
import com.rajko.lucar.repository.BookRepository;
import com.rajko.lucar.repository.CartRepository;
import com.rajko.lucar.repository.RoleRepository;
import com.rajko.lucar.repository.UserRepository;
import com.rajko.lucar.service.LoginService;
import com.rajko.lucar.service.UserService;

@Controller
public class LoginController {

	
	@Autowired
	BookRepository bookRepository;
	
	@Autowired
	CartRepository cartRepository;
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	LoginService loginService;

	@RequestMapping(value="/login", method=RequestMethod.GET) 
	public ModelAndView login() {
		return loginService.login();
	}
	
	@RequestMapping(value="/registration", method = RequestMethod.GET)
	public ModelAndView registration() {
		return loginService.registration();
	}
	
	@RequestMapping(value="/registration", method = RequestMethod.POST, headers = "content-type=multipart/*")
	public ModelAndView createNewUser(@RequestParam("file") MultipartFile file, @Valid User user, BindingResult bindingResult) throws IOException {
		return loginService.createNewUser(file, user, bindingResult);
	}
		
	@RequestMapping(value="/index", method = RequestMethod.GET) 
	public ModelAndView home(@ModelAttribute Book book, Model model) {
		return loginService.homePage(book, model);
	}	
}
