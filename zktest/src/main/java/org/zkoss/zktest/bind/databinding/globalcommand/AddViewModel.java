package org.zkoss.zktest.bind.databinding.globalcommand;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.validator.AbstractValidator;

public class AddViewModel {

	private String msg;
	private String item;
	private boolean visible = true;

	@Command
	@NotifyChange("msg")
	public void add() {
		ItemList.add(item);
		msg = "Added " + item;
	}

	public Validator getItemValidator() {
		return new AbstractValidator() {
			public void validate(ValidationContext arg0) {
			}
		};
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
