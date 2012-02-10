package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B00847FormInit {
	private Attr attr = new Attr("blue");
	
	public Attr getAttr() {
		return attr;
	}

	public static class Attr {
		private String color;
		public Attr(String color) {
			this.color = color;
		}
		
		public void setColor(String color) {
			this.color = color;
		}
		
		public String getColor() {
			return color;
		}
	}
	
	@Command @NotifyChange("attr")
	public void red(){
		attr.setColor("red");
	}
	
	@Command @NotifyChange("attr")
	public void yellow(){
		attr.setColor("yellow");
	}
}
