package demo.data.pojo;

import org.zkoss.bind.annotation.DependsOn;

public class Contributor {
	private String title;
	private String firstName;
	private String lastName;
	private Integer extension;

	public Contributor(String firstName, String lastName, String title, Integer ext) {
		setFirstName(firstName);
		setLastName(lastName);
		setTitle(title);
		setExtension(ext);
	}

	public Contributor() {
		setFirstName("");
		setLastName("");
		setTitle("Code");
		setExtension(12345);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String fn) {
		firstName = fn;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String ln) {
		lastName = ln;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String tl) {
		title = tl;
	}

	public Integer getExtension() {
		return extension;
	}

	public void setExtension(Integer ext) {
		extension = ext;
	}

	@DependsOn({ "firstName", "lastName" })
	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}
}
