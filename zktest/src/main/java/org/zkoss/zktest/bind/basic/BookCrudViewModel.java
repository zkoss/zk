package org.zkoss.zktest.bind.basic;

import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class BookCrudViewModel {

	String message;
	String confirmMessage;
	boolean confirmWindowVisible;
	Book selectedBook;
	List<Book> bookList;

	public String getMessage() {
		return message;
	}

	public String getConfirmMessage() {
		return confirmMessage;
	}

	public boolean isConfirmWindowVisible() {
		return confirmWindowVisible;
	}
	
	public Book getSelectedBook() {
		return selectedBook;
	}

	public void setSelectedBook(Book selectedBook) {
		this.selectedBook = selectedBook;
	}

	public List<Book> getBookList() {
		return bookList;
	}

	@Init
	public void index() {
		bookList = BookDataProvider.list();
		selectedBook = bookList.size() > 0 ? bookList.get(0) : null;
	}

	@Command
	@NotifyChange({ "message", "selectedBook", "bookList" })
	public void save() {
		if (selectedBook == null) {
			throw new RuntimeException("selectedBook not found");
		}
		BookDataProvider.save(selectedBook);
		bookList = BookDataProvider.list();
		message = "save book " + selectedBook.getName();
	}

	@Command
	@NotifyChange({ "confirmMessage", "confirmWindowVisible" })
	public void delete() {
		confirmMessage = "Do you want to delete Book " + selectedBook.name
				+ "?";
		confirmWindowVisible = true;
	}

	@Command
	@NotifyChange({ "confirmMessage", "confirmWindowVisible","bookList","selectedBook" })
	public void confirmDelete(@BindingParam("confirm") boolean confirm) {
		if (confirm) {
			if (selectedBook == null) {
				throw new RuntimeException("selectedBook not found");
			}
			BookDataProvider.delete(selectedBook);// have to flush before list
			bookList = BookDataProvider.list();
			message = "Deleted book " + selectedBook.getName();
			selectedBook = null;
		}
		confirmWindowVisible = false;
	}

	@Command
	@NotifyChange({ "selectedBook" })
	public void create() {
		selectedBook = new Book("Name", "Author", "12345");
	}
}
