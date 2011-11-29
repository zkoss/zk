package org.zkoss.zktest.bind.viewmodel.form;

import java.util.Calendar;
import java.util.Date;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;

public class F1 {

	public class User{
		private String account;
		private String password;
		private String password2;
		private Date birthday;
		private boolean isAdult;

		public String getAccount() {
			return account;
		}
		public void setAccount(String account) {
			this.account = account;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getPassword2() {
			return password2;
		}
		public void setPassword2(String password2) {
			this.password2 = password2;
		}
		public Date getBirthday() {
			return birthday;
		}
		public void setBirthday(Date birthday) {
			this.birthday = birthday;
		}
		public boolean isAdult() {
			return isAdult;
		}
		public void setAdult(boolean isAdult) {
			this.isAdult = isAdult;
		}
	}

	private User user = new User();

	public User getUser(){
		return user;
	}

	class F1Validator implements Validator{

		public void validate(ValidationContext ctx) {
			String password = (String)ctx.getProperties("password")[0].getValue();
			String password2 = (String)ctx.getProperties("password2")[0].getValue();
			if (password==null || !password.equals(password2)){
				ctx.setInvalid();
				return;
			}
			String account = (String)ctx.getProperties("account")[0].getValue();
			if (account==null || account.length()==0){
				ctx.setInvalid();
			}
		}

	}

	public Validator getF1Validator(){
		return new F1Validator();
	}

	public class BirthdayAdultConverter implements Converter{

		public Object coerceToUi(Object val, Component component, BindContext ctx) {
			return val;
		}

		public Object coerceToBean(Object val, Component component, BindContext ctx) {

			boolean isAdult = false;
			if (val!=null){
				Calendar adultDay = Calendar.getInstance();
				adultDay.setTime((Date)val);
				adultDay.add(Calendar.YEAR, 18);
				Calendar now = Calendar.getInstance();

				if (now.after(adultDay)){
					isAdult = true;
				}
			}
			return isAdult;
		}

	}

	public Converter getBirthdayAdultConverter(){
		return new BirthdayAdultConverter();
	}

	@NotifyChange("message")
	public void register(){
		System.out.println();
	}

	public String getMessage(){
		String message ="";
		if (user.getAccount()!=null){
			message = "Hi, "+user.getAccount()+": You are ";
		}
		if (user.isAdult){
			message += "an adult.";
		}else{
			message += "NOT an adult.";
		}
		return message;
	}
}
