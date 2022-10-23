package demo.grid.inline_editing;

import java.util.List;

public class InlineEditingViewModel {
	private BookData data = new BookData();

	public List<String> getAllStatuses() {
		return data.getStatuses();
	}

	public List<Book> getAllBooks() {
		return data.getAllBooks();
	}
}
