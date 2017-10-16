package com.rajko.lucar.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.rajko.lucar.model.Orders;
import com.rajko.lucar.model.User;
import com.rajko.lucar.repository.BookRepository;
import com.rajko.lucar.repository.CartRepository;
import com.rajko.lucar.repository.CopyRepository;
import com.rajko.lucar.repository.OrdersRepository;
import com.rajko.lucar.repository.UserRepository;
import com.rajko.lucar.service.UserService;

@Controller
public class UserController {

	@Autowired
	BookRepository bookRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	OrdersRepository ordersRepository;

	@Autowired
	CopyRepository copyRepository;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	UserService userService;

	@Autowired
	LoginController loginController;
	
	@GetMapping("/profile")
	public String profile(Model model) {
		userService.profile(model);
		return "profileUser";
	}

	@GetMapping("/logout")
	public String logout() {
		return "login";
	}

	@GetMapping("/ordersUser")
	public String ordersUser(@RequestParam("orderId") Integer idOrder, Model model) {
		userService.ordersUser(idOrder, model);
		return "ordersUser";
	}
	
	@GetMapping("/usersAdmin")
	public String usersAdmin(Model model) {
		List<User> users = userRepository.allUsers("USER");
		model.addAttribute("users", users);
		return "usersAdmin";
	}
	
	@RequestMapping(value="/updateStatus", method=RequestMethod.POST)
	public String updateStatus(@RequestParam("orderId") Integer idOrder, @RequestParam("status") String status) {
		userService.updateStatus(idOrder, status);
		return "indexAdmin";
	}
	
	@GetMapping("/ordersForUser")
	public String ordersForUser(@RequestParam("userId") Integer idUser, Model model) {
		userService.ordersForUser(idUser, model);
		return "ordersAdmin";
	}
	
	@GetMapping("/ordersAdmin")
	public String AllordersAdmin(Model model) {
		List<Orders> allOrders = ordersRepository.allOrders();
		model.addAttribute("orders", allOrders);
		return "allOrdersAdmin";
	}
	
	@GetMapping("/seeOrderAdmin")
	public String seeOrderAdmin(@RequestParam("orderId") Integer idOrder, Model model) {
		userService.ordersUser(idOrder, model);
		return "seeOrderAdmin";
	}
	
	@PostMapping(value = "/updateProfile", headers = "content-type=multipart/*")
	public String changeProfilePicture(@RequestParam("file") MultipartFile file, @RequestParam(name="path", required=false) String path, @RequestParam("userId") Integer idUser) throws IllegalStateException, IOException {
		userService.updateProfilePicture(file, path, idUser);
		return "completeUser";
				
	}	
}
