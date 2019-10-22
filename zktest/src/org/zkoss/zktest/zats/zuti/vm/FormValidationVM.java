package org.zkoss.zktest.zats.zuti.vm;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.zk.ui.util.Clients;

public class FormValidationVM {
	private Pojo pojo;
	
	@Init
	public void init() {
		pojo = new Pojo();
	}
	
	@Command("submit")
	public void submit() {
		Clients.showNotification("submitted");
	}

	public Validator getPojoValidator() {
		return new AbstractValidator() {
			
			public void validate(ValidationContext ctx) {
				Pojo pojoProxy = (Pojo) ctx.getProperty().getValue(); 
				validateMinLength(ctx, "name", pojoProxy.getName(), 3);
				validateMinLength(ctx, "address", pojoProxy.getAddress(), 3);
			}

			private void validateMinLength(ValidationContext ctx, String field, String value, int minLength) {
				if(value == null || value.length() < minLength) {
					addInvalidMessage(ctx, field, field + " : too short (min length " + minLength + ")");
				};
			}
		};
	}
	
	public Pojo getPojo() {
		return pojo;
	}
	
	public static class Pojo {
		private String name;
		private String address;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
	}
}
