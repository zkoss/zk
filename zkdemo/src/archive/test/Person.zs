public class Address {
	private String _street = "Holulu";
	private String _city = "LA";
	
	//accessor
	public void setStreet(String f) {
		_street = f;
	}
	
	public String getStreet() {
		return _street;
	}
	
	public void setCity(String l) {
		_city = l;
	}
	
	public String getCity() {
		return _city;
	}
}

public class Person {
	private String _firstName = "Hello";
	private String _lastName = "ZK";
	private String _email = "hellozk@potix.com";
	private Address _address;
	
	//accessor
	public void setFirstName(String f) {
		_firstName = f;
	}
	
	public String getFirstName() {
		return _firstName;
	}
	
	public void setLastName(String l) {
		_lastName = l;
	}
	
	public String getLastName() {
		return _lastName;
	}
	
	public String getFullName() {
		return _firstName + " " + _lastName;
	}
	
	public void setEmail(String e) {
		_email = e;
	}

	public String getEmail() {
		return _email;
	}
	
	public void setAddress(Address adr) {
		_address = adr;
	}
	
	public Address getAddress() {
		return _address;
	}
}
