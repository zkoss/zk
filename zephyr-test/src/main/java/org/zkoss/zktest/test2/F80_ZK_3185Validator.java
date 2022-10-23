package org.zkoss.zktest.test2;

import java.io.Serializable;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.zk.ui.util.Clients;

public class F80_ZK_3185Validator implements Validator, Serializable {
	public void validate(ValidationContext ctx) {
		Clients.log(String.valueOf(ctx.isValid()));
	}
}
