package org.zkoss.zktest.test2;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.zk.ui.util.Clients;

public class B80_ZK_3196VM {

	private B80_ZK_3196Object person;

	@Init
	public void init() {
		person = new B80_ZK_3196Object("Hans");
		person.getAllFriends().add(new B80_ZK_3196Object("Marie"));
		person.getAllFriends().add(new B80_ZK_3196Object("Paul"));
		person.getAllFriends().add(new B80_ZK_3196Object("John"));
		person.setFavoriteBuddy(person.getAllFriends().get(1));
	}
	
	public B80_ZK_3196Object getPerson() {
		return person;
	}
	
	public Validator getFormValidator() {
		return new AbstractValidator() {
			@Override
			public void validate(ValidationContext ctx) {
				B80_ZK_3196Object personProxy = (B80_ZK_3196Object) ctx.getProperty().getValue();
				Clients.log(String.format("(proxy) favoriteBuddy: %s", personProxy.getFavoriteBuddy()));
			}
		};
	}

	@Command
	public void save() {
		Clients.log(String.format("(origin) favoriteBuddy: %s", person.getFavoriteBuddy()));
	}

	@Command
	public void show() {
		Clients.log(String.format("(origin) favoriteBuddy: %s", person.getFavoriteBuddy()));
	}

}
