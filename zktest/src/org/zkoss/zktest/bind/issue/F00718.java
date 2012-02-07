package org.zkoss.zktest.bind.issue;

import java.util.Date;

import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class F00718 {
     
	User user = new User();
	
    public User getUser() {
        return user;
    }
    
    @Command @NotifyChange("user")
    public void reload(){
    	
    }
    
    
    public static class User{
    
	    private String _firstName ="Dennis";
	    private String _lastName = "Chen";
	    private String _email = "";
	    private Date _birthDate = new Date();
	     
	    // getter, setter //
	    @NotEmpty(message = "name can not be null")
	    public String getFirstName() {
	        return _firstName;
	    }
	     
	    public void setFirstName(String name) {
	        _firstName = name;
	    }
	     
	    @NotEmpty(message = "Last name can not be null")
	    public String getLastName() {
	        return _lastName;
	    }
	     
	    public void setLastName(String name) {
	        _lastName = name;
	    }
	    
	    
	    @NotEmpty(message = "email can not be null")
	    @Email
	    @Length(min=8,message="email lenght must large than 8")
	    public String getEmail(){
	    	return _email;
	    }
	    
	    public void setEmail(String email){
	    	_email = email;
	    }
	    
	     
	    @Past(message = "Birth date must be in the past")
	    public Date getBirthDate() {
	        return _birthDate;
	    }
	     
	    public void setBirthDate(Date date) {
	        _birthDate = date;
	    }
    }
}