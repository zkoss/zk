package demo.combobox.simple_combobox;

import java.util.List;

import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;

public class ShirtViewModel {
	private String shirtColor, shirtSize;
	private final static String shirtLocation = "/zephyr-test/demo/combobox/simple_combobox/img/shirt_%s_%s.png";
	private final static String iconLocation = "/zephyr-test/demo//combobox/simple_combobox/img/shirt_icon_%s.png";
	
	public List<String> getColors() {
		return ShirtData.getColors();
	}
	
	public List<String> getSizes() {
		return ShirtData.getSizes();
	}
	
	@Init
	public void init() {
		setShirtColor("blue");
		setShirtSize("large");
	}

	public String getShirtColor() {
		return shirtColor;
	}
	
	public void setShirtColor(String shirtColor) {
		this.shirtColor = shirtColor;
	}
	
	public void setShirtSize(String shirtSize) {
		this.shirtSize = shirtSize;
	}
	
	public String getShirtSize() {
		return shirtSize;
	}
	
	@DependsOn({"shirtSize","shirtColor"})
	public String getShirtImage() {
		if(shirtSize==null || shirtColor==null){
			return String.format(shirtLocation, "unknow", "unknow");
		}
		return String.format(shirtLocation, shirtColor, shirtSize);
	}
	
	public String getIconImage(String icon) {
		return String.format(iconLocation, icon);
	}
}
