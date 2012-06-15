/**
 * 
 */
package org.zkoss.zktest.bind.issue;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;



/**
 * @author Ian YT Tsai (Zanyking)
 *
 */
public class B01185TwoColsNPE {

	//create the entity
	 Contact contact;
	 List <Contact> model;
	 List<Address> address;
	 List<Address> otherAddress;

	 String deleteMessage;


	 //action commands
    @Command @NotifyChange({"contact","model"})
    public void newContact(){
    	Contact mycontact = new Contact();
    	//getModel().add(mycontact);
    	contact = mycontact;
    }
    
    @Command @NotifyChange("address")
    public void addMoreAddress(){
    	if(address != null && address.size()>0){
    		address.add(new Address());
    	}else{
    		address = new ArrayList<Address>();
    		address.add(new Address());
    	}
    }
    
    @Command @NotifyChange("address")
    public void removeAddress(
    	@BindingParam("index") int index, 
    	@BindingParam("comp") Component comp){
    	
    	address.remove(index);
    }
    
    @Command @NotifyChange("otherAddress")
    public void addMoreOtherAddress(){
    	if(otherAddress != null && otherAddress.size()>0){
    		otherAddress.add(new Address());
    	}else{
    		otherAddress = new ArrayList<Address>();
    		otherAddress.add(new Address());
    	}
    }
    
    @Command @NotifyChange("otherAddress")
    public void removeOtherAddress(
    	@BindingParam("index") int index, 
    	@BindingParam("comp") Component comp){
    	
    	otherAddress.remove(index);
    }
	
    //setter and getters
	public String getDeleteMessage() {
		return deleteMessage;
	}

	@NotifyChange("contact")
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	
	 public Contact getContact() {
		return contact;
	}

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

	public List<Address> getOtherAddress() {
		return otherAddress;
	}

	public void setOtherAddress(List<Address> otherAddress) {
		this.otherAddress = otherAddress;
	}
	
	 /**
	  * 
	  * @author Ian YT Tsai (Zanyking)
	  *
	  */
	public static class Contact {
	 
	    private Integer id;
	    
	    private Date birthday;
	    
	    private String firstname;
	    
	    private String lastname;
	 
	    private String email;
	 
	    private String telephone;
	 
	    public String getEmail() {
	        return email;
	    }
	    public String getTelephone() {
	        return telephone;
	    }
	    public void setEmail(String email) {
	        this.email = email;
	    }
	    public void setTelephone(String telephone) {
	        this.telephone = telephone;
	    }
	    public String getFirstname() {
	        return firstname;
	    }
	    public String getLastname() {
	        return lastname;
	    }
	    public void setFirstname(String firstname) {
	        this.firstname = firstname;
	    }
	    public void setLastname(String lastname) {
	        this.lastname = lastname;
	    }
	    public Integer getId() {
	        return id;
	    }
	    public void setId(Integer id) {
	        this.id = id;
	    }
	    public Date getBirthday() {
			return birthday;
		}
		public void setBirthday(Date birthday) {
			this.birthday = birthday;
		}
	}//end of class...	

	/**
	 * 
	 * @author Ian YT Tsai (Zanyking)
	 *
	 */
	public static class Address {
		private String address;
		private String postal;
		public String getAddress() {
			return address;
		}
		public String getPostal() {
			return postal;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public void setPostal(String postal) {
			this.postal = postal;
		}
	}//end of class...
}
