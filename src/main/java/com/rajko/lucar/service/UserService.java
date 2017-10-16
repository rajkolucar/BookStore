package com.rajko.lucar.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.rajko.lucar.model.Book;
import com.rajko.lucar.model.Cart;
import com.rajko.lucar.model.Copy;
import com.rajko.lucar.model.Orders;
import com.rajko.lucar.model.Role;
import com.rajko.lucar.model.User;
import com.rajko.lucar.repository.BookRepository;
import com.rajko.lucar.repository.CartRepository;
import com.rajko.lucar.repository.CopyRepository;
import com.rajko.lucar.repository.OrdersRepository;
import com.rajko.lucar.repository.RoleRepository;
import com.rajko.lucar.repository.UserRepository;

@Service("userService")
public class UserService {

	private static String UPLOAD_ROOT = "src/main/webapp/";

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	BookRepository bookRepository;

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private OrdersRepository ordersRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	BookService bookService;

	@Autowired
	CopyRepository copyRepository;

	@Autowired
	LoginService loginService;

	
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	public void saveUser(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		Role userRole = roleRepository.findByRole("USER");
		user.setActive(1);
		user.setRole("USER");
		user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		userRepository.save(user);
	}
	
	public void ordersUser(Integer idOrder, Model model) {
		List<Integer> copiesIds = ordersRepository.copyIdsFromOrdersId(idOrder);
		List<Integer> booksIds = new ArrayList<>();
		for(int i = 0; i < copiesIds.size(); i++) {
			Integer bookId = ordersRepository.booksIds(copiesIds.get(i));
			booksIds.add(bookId);
		}
		List<Book> books = new ArrayList<>();
		for(int i = 0; i < booksIds.size(); i++) {
			Book b = bookRepository.findOne(booksIds.get(i));
			books.add(b);	
		}
		model.addAttribute("idOrder", idOrder);
		model.addAttribute("books", books);
	}
	
	public void profile(Model model) {
		User currentUser = loginService.getUser();
		Cart cart = cartRepository.findByidUser(currentUser.getIdUser());
//		if (cart == null) {
//			cart = new Cart();
//			cartRepository.save(cart);
//		}
		List<Integer> ordersIds = ordersRepository.ordersIds(cart.getIdCart());
		List<Orders> orders = new ArrayList<>();
		Orders order = null;
		for (int i = 0; i < ordersIds.size(); i++) {
			order = ordersRepository.orders(ordersIds.get(i));
			orders.add(order);
		}
		model.addAttribute("orders", orders);
		model.addAttribute("user", currentUser);
	}
	
	public void updateStatus(Integer idOrder, String status) {
		Orders order = ordersRepository.findOne(idOrder);
		order.setStatus(status);
		ordersRepository.save(order);
	}
	
	public void ordersForUser(Integer idUser, Model model) {
		Cart cart = cartRepository.findByidUser(idUser);
		List<Orders> orders = new ArrayList<>();
		orders = ordersRepository.findOrdersByIdCart(cart.getIdCart());
		User currentUser = userRepository.findOne(idUser);
		model.addAttribute("username", currentUser.getUsername());
		model.addAttribute("orders", orders);
	}

	public void addProfilePicture(MultipartFile file, User user) throws IOException {
		List<String> paths = userRepository.paths();
		File fileF = new File(file.getOriginalFilename());
		boolean fileNameExists = false;
		if (fileF.exists()) {
			file.transferTo(fileF);
			for (String pathS : paths) {
				if (fileF.getName().equals(pathS)) {
					fileNameExists = true;
					break;
				}
			}
			if (fileNameExists) {
				user.setImagePath(fileF.getName());
			} else {
				Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, fileF.getName()),
						StandardCopyOption.REPLACE_EXISTING);
				user.setImagePath(fileF.getName());
			}
		} else {
			String defaultPath = "defaultProfile.png";
			user.setImagePath(defaultPath);
		}
	}
	
	public void updateProfilePicture(MultipartFile file, String path, Integer idUser) throws IOException {
		List<String> paths = userRepository.paths();
		User currentUser = userRepository.findOne(idUser);
		File fileF = new File(file.getOriginalFilename());
		if (fileF.exists()) {
			file.transferTo(fileF);
			boolean fileNameExistsInDatabase = false;
			for (String pathS : paths) {
				if (fileF.getName().equals(pathS)) {
					fileNameExistsInDatabase = true;
					break;
				}
			}
			if (fileNameExistsInDatabase) {
				currentUser.setImagePath(fileF.getName());
			} else {
				Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, fileF.getName()),
						StandardCopyOption.REPLACE_EXISTING);
				currentUser.setImagePath(fileF.getName());
			}
		}
		userRepository.save(currentUser);
	}
	
//	public void recommendation(Model model) {
//		User userX = loginService.getUser();
//		List<User> allUsers = userRepository.allUsers("USER");
//		Map<Integer, Double> jaccard = new HashMap<Integer, Double>();
//		double jaccardCoefficient = 0;
//		// finding jaccard coefficient for all user and putting userId and jaccard coefficient into map
//		for (User user : allUsers) {
//			jaccardCoefficient = jaccardCoefficient(userX, user);
//			if (jaccardCoefficient != 1 && jaccardCoefficient != 0) {
//				jaccard.put(user.getIdUser(), jaccardCoefficient);
//			}
//		}
//		// sorting map IMPORTANT!!!
//		jaccard = sortByValue(jaccard);
//
//		//most similar users to current user
//		List<Integer> similarUserIds = new ArrayList<>();
//		Set<Integer> key = jaccard.keySet();
//		for (int i = 0; i < key.size(); i++) {
//			similarUserIds.add((Integer) key.toArray()[i]);
//		}
//		// books is the list that ordered by other users
//		List<Book> books = new ArrayList<>();
//		Cart cart = null;
//		for (int i = 0; i < similarUserIds.size(); i++) {
//			//finding carts for other users
//			cart = cartRepository.findCartByUserId(similarUserIds.get(i));
//			//finding copies bought by other users
//			List<Copy> copiesFromCart = copyRepository.findByIdCart(cart.getIdCart());
//			for (int j = 0; j < copiesFromCart.size(); j++) {
//				//finding book for id copy
//				Integer idBook = ordersRepository.booksIds(copiesFromCart.get(j).getIdCopy());
//				Book book = bookRepository.findOne(idBook);
//				books.add(book);
//			}
//		}
//		//cart for current user
//		Cart cartCurrent = cartRepository.findByidUser(userX.getIdUser());
//		//those are books ordered by current user
//		List<Book> booksForUser = ordersRepository.allOrderedBooks(cartCurrent.getIdCart());
//		//final list recommendation
//		List<Book> bookPrint = new ArrayList<>();
//		for(Book book : books) {
//			if(!booksForUser.contains(book)) {
//				bookPrint.add(book);
//			}			
//		}
//		//removing duplicates
//		Set<Book> hashSet = new HashSet<>();
//		hashSet.addAll(bookPrint);
//		bookPrint.clear();
//		bookPrint.addAll(hashSet);
//		
//		//selecting first 5 elements from list
//		if(bookPrint.size() >= 5) {
//			bookPrint = bookPrint.subList(0, 5);			
//		}
//		model.addAttribute("booksRecommendation", bookPrint);
//	}
	
	
	public void recommendation(Model model) {
		User userX = loginService.getUser();
		List<User> allUsers = userRepository.allUsers("USER");
		Map<Integer, Double> jaccard = new HashMap<Integer, Double>();
		double jaccardCoefficient = 0;
		for (User user : allUsers) {
			jaccardCoefficient = jaccardCoefficient(userX, user);
			if (jaccardCoefficient != 1 && jaccardCoefficient != 0) {
				jaccard.put(user.getIdUser(), jaccardCoefficient);
			}
		}
		jaccard = sortByValue(jaccard);
		List<Integer> similarUserIds = new ArrayList<>();
		Set<Integer> key = jaccard.keySet();
		for (int i = 0; i < key.size(); i++) {
			similarUserIds.add((Integer) key.toArray()[i]);
		}
		List<Book> books = new ArrayList<>();
		Cart cart = null;
		for (int i = 0; i < similarUserIds.size(); i++) {
			cart = cartRepository.findCartByUserId(similarUserIds.get(i));
			List<Copy> copiesFromCart = copyRepository.findByIdCart(cart.getIdCart());
			for (int j = 0; j < copiesFromCart.size(); j++) {
				Integer idBook = ordersRepository.booksIds(copiesFromCart.get(j).getIdCopy());
				Book book = bookRepository.findOne(idBook);
				books.add(book);
			}
		}
		Cart cartCurrent = cartRepository.findByidUser(userX.getIdUser());
		List<Book> booksForUser = ordersRepository.allOrderedBooks(cartCurrent.getIdCart());
		List<Book> bookPrint = new ArrayList<>();
		for(Book book : books) {
			if(!booksForUser.contains(book)) {
				bookPrint.add(book);
			}			
		}
		Set<Book> hashSet = new HashSet<>();
		hashSet.addAll(bookPrint);
		bookPrint.clear();
		bookPrint.addAll(hashSet);
		if(bookPrint.size() >= 5) {
			bookPrint = bookPrint.subList(0, 5);			
		}
		model.addAttribute("booksRecommendation", bookPrint);
	}
	
//	public Double jaccardCoefficient(User userX, User userY) {
//		//current user
//		Cart cartX = cartRepository.findByidUser(userX.getIdUser());
//		//cart from other user
//		Cart cartY = cartRepository.findByidUser(userY.getIdUser());
//		//books for current user
//		List<Book> booksForUserX = ordersRepository.allOrderedBooksForIdCart(cartX.getIdCart());
//		//books for other user
//		List<Book> booksForUserY = ordersRepository.allOrderedBooksForIdCart(cartY.getIdCart());
//		List<Book> intersection = intersection(booksForUserX, booksForUserY);
//		List<Book> union = union(booksForUserX, booksForUserY);
//		double jaccardCoefficient = (double)intersection.size()/(double)union.size();
//		return jaccardCoefficient;
//	}
	
	public Double jaccardCoefficient(User userX, User userY) {
		Cart cartX = cartRepository.findByidUser(userX.getIdUser());
		Cart cartY = cartRepository.findByidUser(userY.getIdUser());
		List<Book> booksForUserX = ordersRepository.allOrderedBooksForIdCart(cartX.getIdCart());
		List<Book> booksForUserY = ordersRepository.allOrderedBooksForIdCart(cartY.getIdCart());
		List<Book> intersection = intersection(booksForUserX, booksForUserY);
		List<Book> union = union(booksForUserX, booksForUserY);
		double jaccardCoefficient = (double)intersection.size()/(double)union.size();
		return jaccardCoefficient;
	}
	
	public <T> List<T> union(List<T> list1, List<T> list2) {
		Set<T> set = new HashSet<T>();
		set.addAll(list1);
		set.addAll(list2);
		return new ArrayList<T>(set);
	}
	
	public <T> List<T> intersection(List<T> list1, List<T> list2) {
		List<T> list = new ArrayList<T>();
		for(T t: list1) {
			if(list2.contains(t)) { 
				list.add(t);
			}
		}
		return list;
	}
	
	private static <K, V> Map<K, V> sortByValue(Map<K, V> map) {
		List<Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Object>() {
			@SuppressWarnings("unchecked")
			public int compare(Object o1, Object o2) {
				return ((Comparable<V>) ((Map.Entry<K, V>) (o2)).getValue())
						.compareTo(((Map.Entry<K, V>) (o1)).getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<>();
		for (Iterator<Entry<K, V>> it = list.iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}