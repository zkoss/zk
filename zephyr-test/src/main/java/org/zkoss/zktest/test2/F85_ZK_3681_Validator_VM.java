package org.zkoss.zktest.test2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zul.Label;

public class F85_ZK_3681_Validator_VM implements Serializable {
	private static final Validator REGEX_VALIDATOR = new RegExValidator();

	private F85_ZK_3681_Issue selected = new F85_ZK_3681_Issue(
			true, "ZK-3274", "Allow infinite CometServerPush.retry.count");

	public F85_ZK_3681_Issue getSelected() {
		return selected;
	}

	public void setSelected(F85_ZK_3681_Issue selected) {
		this.selected = selected;
	}

	public Validator getRegexValidator() {
		return REGEX_VALIDATOR;
	}

	@Command
	@NotifyChange("selected")
	public void save() {
		// Do nothing just a trigger
	}

	private static class RegExValidator extends AbstractValidator {
		public void validate(ValidationContext ctx) {
			String regex = (String) ctx.getValidatorArg("regex");
			String key = (String) ctx.getValidatorArg("key");
			Object value = ctx.getProperty().getValue();
			if (value == null || !value.toString().matches(regex)) {
				addInvalidMessage(ctx, key, "Invalid: " + value);
			}
		}
	}

	byte[] _bytes;
	public void doSerialize(Component comp, Label msg) {
		try {
			doSerialize0(comp);
			doDeserialize0(comp, msg);
		} catch (Exception x) {
			x.printStackTrace();
			msg.setValue("error :" + x.getClass() + "," + x.getMessage());
		}
	}

	private void doSerialize0(Component comp) throws Exception {
		Page pg = comp.getPage();
		((ComponentCtrl) comp).sessionWillPassivate(pg); // simulate
		ByteArrayOutputStream oaos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(oaos);
		oos.writeObject(comp);
		oos.close();
		oaos.close();
		_bytes = oaos.toByteArray();
	}

	private void doDeserialize0(Component comp, Label msg) throws Exception {
		ByteArrayInputStream oaos = new ByteArrayInputStream(_bytes);
		ObjectInputStream oos = new ObjectInputStream(oaos);

		Component newcomp = (Component) oos.readObject();
		Component parent = comp.getParent();
		Component ref = comp.getNextSibling();
		comp.detach();
		oos.close();
		oaos.close();
		parent.insertBefore(newcomp, ref);
		// for loading component back.
		((ComponentCtrl) newcomp).sessionDidActivate(newcomp.getPage()); // simulate
		msg.setValue("done deserialize: " + _bytes.length);
	}

	public void doClone(Component comp) {
		replaceComponent(comp, (Component) comp.clone());
	}

	private void replaceComponent(Component oc, Component nc) {
		Component parent = oc.getParent();
		Component ref = oc.getNextSibling();
		oc.detach();
		parent.insertBefore(nc, ref);
	}
}
