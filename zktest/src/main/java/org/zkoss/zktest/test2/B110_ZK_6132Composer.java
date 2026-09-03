/* B110_ZK_6132Composer.java

	Purpose:

	Description:

	History:
		Mon Aug 31 11:55:41 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Badge;
import org.zkoss.zul.Chip;
import org.zkoss.zul.Confirmpopup;
import org.zkoss.zul.Label;

public class B110_ZK_6132Composer extends SelectorComposer<Component> {

	@Wire
	private Chip chipError;
	@Wire
	private Badge badgeError;
	@Wire
	private Confirmpopup cpError;
	@Wire
	private Label chipResult;
	@Wire
	private Label badgeResult;
	@Wire
	private Label popupResult;

	@Listen("onClick = #btnChipOld")
	public void chipOld() {
		chipResult.setValue(probe(chipError::setSeverity, chipError::getSeverity));
	}

	@Listen("onClick = #btnBadgeOld")
	public void badgeOld() {
		badgeResult.setValue(probe(badgeError::setSeverity, badgeError::getSeverity));
	}

	@Listen("onClick = #btnPopupOld")
	public void popupOld() {
		popupResult.setValue(probe(cpError::setSeverity, cpError::getSeverity));
	}

	@Listen("onClick = #btnChipNew")
	public void chipNew() {
		chipResult.setValue(probeAccepted(chipError::setSeverity, chipError::getSeverity));
	}

	@Listen("onClick = #btnBadgeNew")
	public void badgeNew() {
		badgeResult.setValue(probeAccepted(badgeError::setSeverity, badgeError::getSeverity));
	}

	@Listen("onClick = #btnPopupNew")
	public void popupNew() {
		popupResult.setValue(probeAccepted(cpError::setSeverity, cpError::getSeverity));
	}

	// Reports per value so a partially-applied rename (one token still accepted)
	// is visible in the assertion message instead of collapsing to a bare false.
	private static String probe(Consumer<String> setter, Supplier<String> getter) {
		return apply(setter, getter, "danger", "secondary");
	}

	private static String probeAccepted(Consumer<String> setter, Supplier<String> getter) {
		return apply(setter, getter, "error", "neutral");
	}

	private static String apply(Consumer<String> setter, Supplier<String> getter, String... values) {
		StringBuilder sb = new StringBuilder();
		for (String value : values) {
			try {
				setter.accept(value);
				sb.append(value).append("=ACCEPTED ");
			} catch (WrongValueException ex) {
				sb.append(value).append("=REJECTED ");
			}
		}
		return sb.append("severity=").append(getter.get()).toString();
	}
}
