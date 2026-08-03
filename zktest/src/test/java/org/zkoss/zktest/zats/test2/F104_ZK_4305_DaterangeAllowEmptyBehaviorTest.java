/* F104_ZK_4305_DaterangeAllowEmptyBehaviorTest.java

	Purpose:

	Description:

	History:
		Thu May 29 16:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.Test;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zkmax.zul.Daterangebox;
import org.zkoss.zul.DateRange;

/**
 * Pure-JUnit behavioural matrix for {@code allowEmpty}:
 *
 * <p>Pairs with the WebDriver-side {@link F104_ZK_4305_DaterangeAllowEmptyTest}
 * (which only checks the four boxes <em>render</em>). This class exercises the
 * actual server-side {@code WrongValueException} behaviour for every
 * (mode × emptiness) cell of the matrix, so a refactor that flips a rule
 * fails fast without spinning a browser.
 *
 * <p>The allowEmpty modes:
 * <pre>
 * "none"  -> both required;  empty begin OR end -> throw
 * "begin" -> begin may be null; empty end -> throw
 * "end"   -> end may be null;   empty begin -> throw
 * "both"  -> either may be null (default)
 * </pre>
 */
public class F104_ZK_4305_DaterangeAllowEmptyBehaviorTest {

	private static Date d(int y, int m, int day) {
		return new GregorianCalendar(y, m, day, 0, 0, 0).getTime();
	}

	private static Daterangebox box(String mode) {
		Daterangebox box = new Daterangebox();
		box.setAllowEmpty(mode);
		return box;
	}

	// ===== "none" — both endpoints required =====

	@Test
	public void none_bothPresent_passes() {
		assertDoesNotThrow(() -> box("none").setValue(
				new DateRange(d(2026, Calendar.JANUARY, 1), d(2026, Calendar.JANUARY, 5))));
	}

	@Test
	public void none_emptyBegin_throws() {
		assertThrows(WrongValueException.class, () -> box("none").setValue(
				new DateRange(null, d(2026, Calendar.JANUARY, 5))),
				"allowEmpty=none must reject a null begin");
	}

	@Test
	public void none_emptyEnd_throws() {
		assertThrows(WrongValueException.class, () -> box("none").setValue(
				new DateRange(d(2026, Calendar.JANUARY, 1), null)),
				"allowEmpty=none must reject a null end");
	}

	@Test
	public void none_bothEmpty_throws() {
		// Stage a valid range first so the (null, null) call is a real
		// state-change; setValue() short-circuits the no-op (default-to-default)
		// case before reaching the allowEmpty gate. We're testing the gate,
		// not the no-op fast-path.
		Daterangebox box = box("none");
		box.setValue(new DateRange(d(2026, Calendar.JANUARY, 1), d(2026, Calendar.JANUARY, 5)));
		assertThrows(WrongValueException.class,
				() -> box.setValue(new DateRange(null, null)),
				"allowEmpty=none must reject (null, null)");
	}

	// ===== "begin" — begin nullable, end required =====

	@Test
	public void begin_nullBegin_passes() {
		assertDoesNotThrow(() -> box("begin").setValue(
				new DateRange(null, d(2026, Calendar.JANUARY, 5))));
	}

	@Test
	public void begin_emptyEnd_throws() {
		assertThrows(WrongValueException.class, () -> box("begin").setValue(
				new DateRange(d(2026, Calendar.JANUARY, 1), null)),
				"allowEmpty=begin must still require end");
	}

	// ===== "end" — end nullable, begin required =====

	@Test
	public void end_nullEnd_passes() {
		assertDoesNotThrow(() -> box("end").setValue(
				new DateRange(d(2026, Calendar.JANUARY, 1), null)));
	}

	@Test
	public void end_emptyBegin_throws() {
		assertThrows(WrongValueException.class, () -> box("end").setValue(
				new DateRange(null, d(2026, Calendar.JANUARY, 5))),
				"allowEmpty=end must still require begin");
	}

	// ===== "both" — default, anything goes =====

	@Test
	public void both_isDefault() {
		assertEquals("both", new Daterangebox().getAllowEmpty(),
				"allowEmpty default must be 'both'");
	}

	@Test
	public void both_nullBegin_passes() {
		assertDoesNotThrow(() -> box("both").setValue(
				new DateRange(null, d(2026, Calendar.JANUARY, 5))));
	}

	@Test
	public void both_nullEnd_passes() {
		assertDoesNotThrow(() -> box("both").setValue(
				new DateRange(d(2026, Calendar.JANUARY, 1), null)));
	}

	@Test
	public void both_bothNull_passes() {
		assertDoesNotThrow(() -> box("both").setValue(new DateRange(null, null)));
	}

	// ===== Invalid mode is rejected at setter =====

	@Test
	public void setAllowEmpty_unknownMode_throws() {
		// Daterangebox raises the standard ZK input-rejection exception
		// (WrongValueException) for unknown property values, matching the
		// pattern used by other zul setters.
		assertThrows(WrongValueException.class,
				() -> new Daterangebox().setAllowEmpty("maybe"),
				"setAllowEmpty must whitelist none|begin|end|both");
	}
}
