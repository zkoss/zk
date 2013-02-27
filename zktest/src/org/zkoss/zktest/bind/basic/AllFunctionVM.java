package org.zkoss.zktest.bind.basic;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Form;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DefaultCommand;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.zk.ui.util.Clients;

public class AllFunctionVM {

	String escValue1 = "'";
	String escValue2 = "\"";

	Element element;

	Element selected;

	List<Element> elements;

	public AllFunctionVM() {
		elements = new ArrayList<Element>();

		elements.add(element = selected = new Element("item 1"));
		elements.add(new Element("item 2"));
		elements.add(new Element("item 3"));
	}

	public String getEscValue1() {
		return escValue1;
	}

	public void setEscValue1(String escValue1) {
		this.escValue1 = escValue1;
	}

	public String getEscValue2() {
		return escValue2;
	}

	public void setEscValue2(String escValue2) {
		this.escValue2 = escValue2;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

	public List<Element> getElements() {
		return elements;
	}

	public Element getSelected() {
		return selected;
	}

	public void setSelected(Element selected) {
		this.selected = selected;
	}

	static public class Element {
		String name;

		public Element(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public Validator getValidator1() {
		return new AbstractValidator() {

			@Override
			public void validate(ValidationContext ctx) {
				if (!"A".equals(ctx.getProperty().getValue())) {
					ctx.setInvalid();
					Clients.showNotification("value must be 'A'");
				}
			}
		};
	}

	public Validator getValidator2() {
		return new AbstractValidator() {

			@Override
			public void validate(ValidationContext ctx) {
				Form fx = (Form) ctx.getProperty().getValue();
				if (!"B".equals(fx.getField("name"))) {
					ctx.setInvalid();
					Clients.showNotification("value must be 'B'");
				}
			}
		};
	}

	@DefaultCommand
	public void cmd() {

	}

	@Command
	public void cmd1() {

	}

	@GlobalCommand
	public void gcmd1() {
		element.setName(element.getName() + "-GCMD1");
		BindUtils.postNotifyChange(null, null, element, "*");
	}

	@Command
	public void cmd2() {
		BindUtils.postGlobalCommand(null, null, "gcmd2", null);
	}

	@GlobalCommand
	public void gcmd2() {
		element.setName(element.getName() + "-GCMD2");
		BindUtils.postNotifyChange(null, null, element, "*");
	}
}
