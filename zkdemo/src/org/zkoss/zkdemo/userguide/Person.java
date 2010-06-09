/**
 * 
 */
package org.zkoss.zkdemo.userguide;

/**
 * Person class to be used with "Annotate Data Binding" 
 * @author henrichen
 */
public class Person {
	private String firstName;
	private String lastName;
	public Person(String f, String l) {
		setFirstName(f);
		setLastName(l);
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String f) {
		firstName = f;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String l) {
		lastName = l;
	}
	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}
}
