package demo.combobox.simple_combobox;

import java.util.ArrayList;
import java.util.List;

public class ShirtData {
	
	private static List<String> colors = new ArrayList<String>();
	private static List<String> sizes = new ArrayList<String>();
	
	static{
		colors.add("blue");
		colors.add("black");
		colors.add("white");
		
		sizes.add("small");
		sizes.add("medium");
		sizes.add("large");
	}
	
	public static final List<String> getColors() {
		return new ArrayList<String>(colors);
	}

	public static final List<String> getSizes() {
		return new ArrayList<String>(sizes);
	}
}
