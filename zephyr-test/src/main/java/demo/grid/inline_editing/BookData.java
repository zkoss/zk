package demo.grid.inline_editing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookData {
	private final Date today = new Date();
	private final List<Book> allBooks = new ArrayList<Book>();
	private final List<String> statuses = new ArrayList<String>();

	public BookData() {
		allBooks.add(new Book("Philip Hensher", "The Fit", today, 240, "Shipped"));
		allBooks.add(new Book("Philip Hensher", "Kitchen Venom", today, 336, "Awaiting"));
		allBooks.add(new Book("Michael Greenberg", "Hurry Down Sunshine", today, 245, "Awaiting"));
		allBooks.add(new Book("Michael Greenberg", "Painless Vocabulary", today, 292, "Ordered"));
		allBooks.add(new Book("Rick Perlstein", "Nixonland: The Rise of a President and the Fracturing", today, 896, "Unavailable"));
		allBooks.add(new Book("Rick Perlstein", "Nixonland", today, 845, "Awaiting"));

		statuses.add("Ordered");
		statuses.add("Shipped");
		statuses.add("Awaiting");
		statuses.add("Unavailable");
	}

	public List<Book> getAllBooks() {
		return allBooks;
	}

	public List<String> getStatuses() {
		return statuses;
	}
}
