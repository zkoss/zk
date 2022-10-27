package org.zkoss.clientbind.test.mvvm.book.basic;

public class Book {
	
	String name;
	String author;
	String isbn;

	// An empty constructor is needed for FormProxy
	public Book() {
	}

	public Book(String name, String author, String isbn) {
		super();
		this.name = name;
		this.author = author;
		this.isbn = isbn;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
}
