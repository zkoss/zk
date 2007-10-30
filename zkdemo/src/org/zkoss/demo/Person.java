package org.zkoss.demo;

public class Person {
	
	private String name="";
	
	private String email="";
	
	private String gender ="";
	
	public Person(int p)
	{
		name ="name:"+p;
		email = "mail:"+p;
		gender = "gender:" +p;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
