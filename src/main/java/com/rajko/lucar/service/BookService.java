package com.rajko.lucar.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.rajko.lucar.model.Book;
import com.rajko.lucar.model.Copy;
import com.rajko.lucar.repository.BookRepository;
import com.rajko.lucar.repository.CartRepository;
import com.rajko.lucar.repository.CopyRepository;

@Service
public class BookService {

	public static final int AVAILABLE = 0;
	public static final int IN_CART = 1;
	public static final int SOLD = 2;
	private static String UPLOAD_ROOT = "src/main/webapp/";

	@Autowired
	BookRepository bookRepository;

	@Autowired
	CopyRepository copyRepository;

	@Autowired
	CartRepository cartRepository;

	public void addBook(MultipartFile file, Book book, Copy copy) throws IOException {
		List<String> paths = bookRepository.paths();
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
				book.setImagePath(fileF.getName());
			} else {
				Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, fileF.getName()),
						StandardCopyOption.REPLACE_EXISTING);
				book.setImagePath(fileF.getName());
			}
		} else {
			String defaultPath = "bookS.png";
			book.setImagePath(defaultPath);
		}
		bookRepository.save(book);
		int quantity = book.getQuantity();
		for (int i = 0; i < quantity; i++) {
			copy = new Copy();
			copy.setBook(book);
			copy.setState(AVAILABLE);
			copyRepository.save(copy);
		}
	}

	public void find(String value, Model model) {
		List<Book> books = bookRepository.findByT(value, value, value);
		model.addAttribute("books", books);
	}

	public void findByGenre(String genre, Model model) {
		List<Book> booksByGenre = bookRepository.findByGenre(genre);
		model.addAttribute("genre", genre);
		model.addAttribute("books", booksByGenre);
	}

	public String change(MultipartFile file, String path, Integer id, Book book)
			throws IllegalStateException, IOException {
		List<String> paths = bookRepository.paths();
		Book b = bookRepository.findOne(id);
		b.setTitle(book.getTitle());
		b.setAuthor(book.getAuthor());
		b.setGenre(book.getGenre());
		b.setPrice(book.getPrice());
		if (b.getQuantity() == null) {
			b.setQuantity(0);
		}
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
				b.setImagePath(fileF.getName());
			} else {
				Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, fileF.getName()),
						StandardCopyOption.REPLACE_EXISTING);
				b.setImagePath(fileF.getName());
			}
		}
		int quantity = book.getQuantity() - b.getQuantity();
		b.setQuantity(book.getQuantity());
		if (quantity >= 0) {
			for (int i = 0; i < quantity; i++) {
				Copy copy = new Copy();
				copy.setBook(b);
				copy.setState(AVAILABLE);
				copyRepository.save(copy);
			}

			bookRepository.save(b);
			return "completeAdmin";
		} else {
			return "wrongAdmin";
		}
	}
}
