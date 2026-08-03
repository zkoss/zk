/* F104_ZK_4305_DaterangeboxTest.java

        Purpose:
                
        Description:
                
        History:
                Fri May 08 15:14:34 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.zkoss.zkmax.zul.Daterangebox;
import org.zkoss.zul.DateRange;
import org.zkoss.zul.LocalDateRange;
import org.zkoss.zul.LocalDateTimeRange;
import org.zkoss.zul.ZonedDateTimeRange;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.zkoss.lang.Library;
import org.zkoss.zk.ui.WrongValueException;

public class F104_ZK_4305_DaterangeboxTest {

	// ===== A2: defaults =====

	@Test
	public void testDefaults() {
		Daterangebox d = new Daterangebox();
		assertNull(d.getBeginValue(), "begin defaults to null");
		assertNull(d.getEndValue(), "end defaults to null");
		assertNotNull(d.getValue(), "getValue is never null");
		assertNull(d.getValue().getBegin(), "default range begin is null");
		assertNull(d.getValue().getEnd(), "default range end is null");
		assertEquals("both", d.getAllowEmpty(), "allowEmpty default = both");
		assertEquals("after_start", d.getPosition(), "position default = after_start");
		assertTrue(d.isButtonVisible(), "buttonVisible default = true");
		assertFalse(d.isWeekOfYear(), "weekOfYear default = false");
		assertFalse(d.isShowTodayLink(), "showTodayLink default = false");
		assertTrue(d.isLenient(), "lenient default = true");
		assertFalse(d.isStrictDate(), "strictDate default = false");
		assertFalse(d.isDisabled(), "disabled default = false");
		assertFalse(d.isReadonly(), "readonly default = false");
		assertFalse(d.isOpen(), "open default = false");
		assertEquals(0, d.getMinNights(), "minNights default = 0");
		assertEquals(0, d.getMaxNights(), "maxNights default = 0");
		assertEquals("~", d.getSeparator(), "separator default = ~");
		// numberOfMonths is resolved lazily; default lib fallback is 2.
		assertEquals(2, d.getNumberOfMonths(), "numberOfMonths default = 2");
	}

	// ===== A1: round-trip on simple typed setters =====

	@Test
	public void testBooleanSettersRoundTrip() {
		Daterangebox d = new Daterangebox();
		d.setButtonVisible(false);
		assertFalse(d.isButtonVisible());
		d.setButtonVisible(true);
		assertTrue(d.isButtonVisible());

		d.setWeekOfYear(true);
		assertTrue(d.isWeekOfYear());

		d.setShowTodayLink(true);
		assertTrue(d.isShowTodayLink());

		d.setLenient(false);
		assertFalse(d.isLenient());

		d.setStrictDate(true);
		assertTrue(d.isStrictDate());

		d.setDisabled(true);
		assertTrue(d.isDisabled());

		d.setReadonly(true);
		assertTrue(d.isReadonly());

		d.setHoverPreview(false);
		assertFalse(d.isHoverPreview());
	}

	@Test
	public void testNumericSettersRoundTrip() {
		Daterangebox d = new Daterangebox();
		d.setNumberOfMonths(3);
		assertEquals(3, d.getNumberOfMonths());
		d.setNumberOfMonths(1);
		assertEquals(1, d.getNumberOfMonths());
		d.setNumberOfMonths(12);
		assertEquals(12, d.getNumberOfMonths());

		d.setMinNights(2);
		assertEquals(2, d.getMinNights());
		d.setMaxNights(30);
		assertEquals(30, d.getMaxNights());
	}

	@Test
	public void testStringSettersRoundTrip() {
		Daterangebox d = new Daterangebox();
		d.setFormat("yyyy-MM-dd");
		assertEquals("yyyy-MM-dd", d.getFormat());

		d.setSeparator(" to ");
		assertEquals(" to ", d.getSeparator());

		d.setPlaceholder("pick");
		assertEquals("pick", d.getPlaceholder());
		d.setBeginPlaceholder("from");
		assertEquals("from", d.getBeginPlaceholder());
		d.setEndPlaceholder("until");
		assertEquals("until", d.getEndPlaceholder());

		d.setPosition("before_end");
		assertEquals("before_end", d.getPosition());
	}

	@Test
	public void testEnumAllowEmptyRoundTrip() {
		Daterangebox d = new Daterangebox();
		for (String v : new String[] {"none", "begin", "end", "both"}) {
			d.setAllowEmpty(v);
			assertEquals(v, d.getAllowEmpty());
		}
	}

	// ===== A3: validation =====

	@Test
	public void testNumberOfMonthsValidation() {
		Daterangebox d = new Daterangebox();
		assertThrows(WrongValueException.class, () -> d.setNumberOfMonths(0),
				"0 months must be rejected");
		assertThrows(WrongValueException.class, () -> d.setNumberOfMonths(-1),
				"negative months must be rejected");
		assertThrows(WrongValueException.class, () -> d.setNumberOfMonths(13),
				">12 months must be rejected");
	}

	@Test
	public void testDefaultNumberOfMonthsHonorsConfiguredMax() {
		String oldMax = Library.setProperty("org.zkoss.zul.daterangebox.numberOfMonths.max", "1");
		String oldDefault = Library.setProperty("org.zkoss.zul.daterangebox.numberOfMonths", null);
		try {
			Daterangebox d = new Daterangebox();
			assertEquals(1, d.getNumberOfMonths(),
					"default numberOfMonths must not exceed numberOfMonths.max");
		} finally {
			Library.setProperty("org.zkoss.zul.daterangebox.numberOfMonths.max", oldMax);
			Library.setProperty("org.zkoss.zul.daterangebox.numberOfMonths", oldDefault);
		}
	}

	@Test
	public void testMinMaxNightsValidation() {
		Daterangebox d = new Daterangebox();
		assertThrows(WrongValueException.class, () -> d.setMinNights(-1));
		assertThrows(WrongValueException.class, () -> d.setMaxNights(-1));
	}

	@Test
	public void testAllowEmptyValidation() {
		Daterangebox d = new Daterangebox();
		assertThrows(WrongValueException.class, () -> d.setAllowEmpty("invalid"));
		assertThrows(WrongValueException.class, () -> d.setAllowEmpty(""));
		// null is coerced to the default "both" (least-surprise reset), not "none".
		d.setAllowEmpty(null);
		assertEquals("both", d.getAllowEmpty());
	}

	// ===== A4: stringified overloads =====

	@Test
	public void testLocaleStringOverloadEquivalentToTypedOverload() {
		Daterangebox a = new Daterangebox();
		a.setLocale(Locale.US);
		Daterangebox b = new Daterangebox();
		b.setLocale("en_US");
		assertEquals(a.getLocale(), b.getLocale(),
				"setLocale(Locale) and setLocale(String) must converge");
	}

	@Test
	public void testTimeZoneStringOverloadEquivalentToTypedOverload() {
		Daterangebox a = new Daterangebox();
		a.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
		Daterangebox b = new Daterangebox();
		b.setTimeZone("Asia/Taipei");
		assertEquals(a.getTimeZone(), b.getTimeZone());
	}

	@Test
	public void testSetTimeZoneRejectsUnknownIdButKeepsValidZones() {
		// TimeZone.getTimeZone silently returns GMT for an unrecognised id, so a
		// typo would otherwise render dates hours off with no warning. The setter
		// detects that fallback and rejects it.
		assertThrows(WrongValueException.class,
				() -> new Daterangebox().setTimeZone("Asia/Topkyo"),
				"a typo'd zone id must be rejected, not silently resolved to GMT");
		assertThrows(WrongValueException.class,
				() -> new Daterangebox().setTimeZone("Not/AZone"),
				"garbage zone id must be rejected");

		// Genuine zones must still pass — including the literal GMT/UTC ids and
		// the Etc/* and legacy 3-letter ids whose getID() is NOT bare "GMT", so
		// the fallback guard does not misfire on them.
		assertDoesNotThrow(() -> new Daterangebox().setTimeZone("GMT"));
		assertDoesNotThrow(() -> new Daterangebox().setTimeZone("UTC"));
		assertDoesNotThrow(() -> new Daterangebox().setTimeZone("Etc/GMT"));
		assertDoesNotThrow(() -> new Daterangebox().setTimeZone("Etc/UTC"));
		assertDoesNotThrow(() -> new Daterangebox().setTimeZone("PST"));
		assertDoesNotThrow(() -> new Daterangebox().setTimeZone("Asia/Tokyo"));
	}

	@Test
	public void testShowTimeAppendsTimeToExplicitDateOnlyFormat() {
		// An explicit date-only format + showTime must append a time component
		// so the input shows the time the user picks in the popup.
		Daterangebox d = new Daterangebox();
		d.setFormat("yyyy-MM-dd");
		d.setShowTime(true);
		assertTrue(d.getRealFormat().contains("HH"),
				"showTime must append a time component to a date-only format; got " + d.getRealFormat());

		// An explicit format that already carries time is left untouched.
		Daterangebox withTime = new Daterangebox();
		withTime.setFormat("yyyy-MM-dd HH:mm");
		withTime.setShowTime(true);
		assertEquals("yyyy-MM-dd HH:mm", withTime.getRealFormat(),
				"a time-bearing explicit format must not gain a second time component");

		// showTime off leaves the explicit format as-is.
		Daterangebox noTime = new Daterangebox();
		noTime.setFormat("yyyy-MM-dd");
		assertEquals("yyyy-MM-dd", noTime.getRealFormat());
	}

	// ===== Value setters across types =====

	@Test
	public void testBeginEndValueSetters() {
		Daterangebox d = new Daterangebox();
		Date b = new Date(0L); // 1970-01-01 UTC
		Date e = new Date(86400_000L); // 1970-01-02 UTC
		d.setBeginValue(b);
		d.setEndValue(e);
		assertEquals(b, d.getBeginValue());
		assertEquals(e, d.getEndValue());
		// getValue() reflects the same range
		DateRange r = d.getValue();
		assertEquals(b, r.getBegin());
		assertEquals(e, r.getEnd());
	}

	@Test
	public void testSetValueDateRange() {
		Daterangebox d = new Daterangebox();
		Date b = new Date(1_700_000_000_000L);
		Date e = new Date(1_700_500_000_000L);
		d.setValue(new DateRange(b, e));
		assertEquals(b, d.getBeginValue());
		assertEquals(e, d.getEndValue());
	}

	@Test
	public void testSetValueLocalDateTriple() {
		Daterangebox d = new Daterangebox();
		LocalDate b = LocalDate.of(2026, 5, 10);
		LocalDate e = LocalDate.of(2026, 5, 15);
		d.setValueInLocalDate(new LocalDateRange(b, e));
		assertEquals(b, d.getBeginValueInLocalDate());
		assertEquals(e, d.getEndValueInLocalDate());
	}

	@Test
	public void testSetValueLocalDateTimeTriple() {
		Daterangebox d = new Daterangebox();
		LocalDateTime b = LocalDateTime.of(2026, 5, 10, 9, 0);
		LocalDateTime e = LocalDateTime.of(2026, 5, 10, 17, 30);
		d.setValueInLocalDateTime(new LocalDateTimeRange(b, e));
		assertEquals(b, d.getBeginValueInLocalDateTime());
		assertEquals(e, d.getEndValueInLocalDateTime());
	}

	@Test
	public void testSetValueZonedDateTimeTriple() {
		Daterangebox d = new Daterangebox();
		ZoneId tz = ZoneId.of("Asia/Taipei");
		ZonedDateTime b = ZonedDateTime.of(2026, 5, 10, 9, 0, 0, 0, tz);
		ZonedDateTime e = ZonedDateTime.of(2026, 5, 10, 17, 30, 0, 0, tz);
		d.setValueInZonedDateTime(new ZonedDateTimeRange(b, e));
		assertEquals(b, d.getBeginValueInZonedDateTime());
		assertEquals(e, d.getEndValueInZonedDateTime());
	}

	// ===== Construct-with-range convenience =====

	@Test
	public void testConstructorWithRangePopulatesBeginAndEnd() {
		Date b = new Date(0L);
		Date e = new Date(86400_000L);
		Daterangebox d = new Daterangebox(new DateRange(b, e));
		assertEquals(b, d.getBeginValue());
		assertEquals(e, d.getEndValue());
	}

	// ===== Setter chain (E from the matrix) =====

	@Test
	public void testSetterChainFinalStateWins() {
		Daterangebox d = new Daterangebox();
		d.setMinNights(3);
		d.setMinNights(0);
		d.setMinNights(7);
		assertEquals(7, d.getMinNights(),
				"final setter call must be the surviving state");
	}
}
