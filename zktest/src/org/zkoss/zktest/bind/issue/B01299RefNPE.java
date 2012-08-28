package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zul.Messagebox;

public class B01299RefNPE {

	private String text = "abc";
	
	private Person person = new Person("Dennis");
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
	
	
	public Person getPerson() {
		return person;
	}

	public Validator getTextValidator(){
		return new Validator(){

			@Override
			public void validate(ValidationContext arg0) {
				Object base = arg0.getProperty().getBase();
				Object prop = arg0.getProperty().getProperty();
				if(base != B01299RefNPE.this){
					Messagebox.show(base + " is not "+B01299RefNPE.this);
					arg0.setInvalid();
				}
				if(!"text".equals(prop)){
					Messagebox.show(prop + " is not text");
					arg0.setInvalid();
				}
			}};
	}
	
	public Validator getAddressValidator(){
		return new Validator(){

			@Override
			public void validate(ValidationContext arg0) {
				Object base = arg0.getProperty().getBase();
				Object prop = arg0.getProperty().getProperty();
				if(base != person.getAddress()){
					Messagebox.show(base + " is not "+person.getAddress());
					arg0.setInvalid();
				}
				if(!"street".equals(prop)){
					Messagebox.show(prop + " is not street");
					arg0.setInvalid();
				}
			}};
	}

	@Command
	public void submit() {
		
	}
	
	public static class Person {
		String name;
		Address address = new Address("South Rd.");
		
		public Person(String name){
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public Address getAddress(){
			return address;
		}
		
	}
	
	public static class Address {
		String street;
		public Address(String street){
			this.street = street;
		}
		public String getStreet() {
			return street;
		}
		public void setStreet(String street) {
			this.street = street;
		}
	}
}