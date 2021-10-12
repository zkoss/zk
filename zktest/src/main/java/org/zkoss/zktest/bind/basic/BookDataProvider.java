package org.zkoss.zktest.bind.basic;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BookDataProvider {

	static List<Book> books = new ArrayList<Book>();

	static Random r = new Random(System.currentTimeMillis());
	static NumberFormat nf = new DecimalFormat("00000000");

	static String nextIsbn() {
		return nf.format(r.nextInt(99999999));
	}

	static {
		for (int i = 0; i < 200; i++) {
			for (int j=0;j<3;j++) {
				books.add(new Book("Book " + i + "_" + j, "Author " + i,
						nextIsbn()));
			}
		}
	}
	
	static public List<Book> list(){
		return books;
	}
	
	
	static public void save(Book book){
		if(books.indexOf(book)==-1){
			books.add(book);
		}
	}
	
	static public void delete(Book book){
		if(books.indexOf(book)>=0){
			books.remove(book);
		}
	}
	
	
}
