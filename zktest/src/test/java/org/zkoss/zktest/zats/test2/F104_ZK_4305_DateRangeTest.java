/* F104_ZK_4305_DateRangeTest.java

        Purpose:
                
        Description:
                
        History:
                Fri May 08 15:14:52 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.zkoss.zul.DateRange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DateRange}: null handling, boolean state matrix,
 * containment, day count, overlaps, equality, ordering, toString and
 * round-trip serialization.
 */
public class F104_ZK_4305_DateRangeTest {

	private static Date date(String yyyymmdd) {
		try {
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
			return fmt.parse(yyyymmdd);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Test
	public void constructorAcceptsNulls() {
		DateRange empty = new DateRange(null, null);
		assertNull(empty.getBegin());
		assertNull(empty.getEnd());

		DateRange openStart = new DateRange(null, date("2026-01-31"));
		assertNull(openStart.getBegin());
		assertNotNull(openStart.getEnd());
	}

	@Test
	public void factoryEquivalentToConstructor() {
		DateRange a = DateRange.of(date("2026-01-01"), date("2026-01-31"));
		DateRange b = new DateRange(date("2026-01-01"), date("2026-01-31"));
		assertEquals(a, b);
	}

	@Test
	public void defensiveCopyOnConstructor() {
		Date begin = date("2026-01-01");
		DateRange r = new DateRange(begin, date("2026-01-31"));
		begin.setTime(0L);
		assertEquals(date("2026-01-01"), r.getBegin());
	}

	@Test
	public void defensiveCopyOnGetter() {
		DateRange r = new DateRange(date("2026-01-01"), date("2026-01-31"));
		assertNotSame(r.getBegin(), r.getBegin());
	}

	@Test
	public void stateMatrix() {
		DateRange empty = new DateRange(null, null);
		assertTrue(empty.isEmpty());
		assertFalse(empty.isOpenStart());
		assertFalse(empty.isOpenEnd());
		assertFalse(empty.isClosed());

		DateRange openStart = new DateRange(null, date("2026-01-31"));
		assertFalse(openStart.isEmpty());
		assertTrue(openStart.isOpenStart());
		assertFalse(openStart.isOpenEnd());
		assertFalse(openStart.isClosed());

		DateRange openEnd = new DateRange(date("2026-01-01"), null);
		assertFalse(openEnd.isEmpty());
		assertFalse(openEnd.isOpenStart());
		assertTrue(openEnd.isOpenEnd());
		assertFalse(openEnd.isClosed());

		DateRange closed = new DateRange(date("2026-01-01"), date("2026-01-31"));
		assertFalse(closed.isEmpty());
		assertFalse(closed.isOpenStart());
		assertFalse(closed.isOpenEnd());
		assertTrue(closed.isClosed());
	}

	@Test
	public void containsBoundary() {
		DateRange r = new DateRange(date("2026-01-01"), date("2026-01-31"));
		assertTrue(r.contains(date("2026-01-01")));
		assertTrue(r.contains(date("2026-01-15")));
		assertTrue(r.contains(date("2026-01-31")));
		assertFalse(r.contains(date("2025-12-31")));
		assertFalse(r.contains(date("2026-02-01")));
		assertFalse(r.contains(null));
	}

	@Test
	public void containsOpenEnded() {
		DateRange openEnd = new DateRange(date("2026-01-01"), null);
		assertTrue(openEnd.contains(date("2026-01-01")));
		assertTrue(openEnd.contains(date("9999-12-31")));
		assertFalse(openEnd.contains(date("2025-12-31")));

		DateRange openStart = new DateRange(null, date("2026-01-31"));
		assertTrue(openStart.contains(date("1900-01-01")));
		assertTrue(openStart.contains(date("2026-01-31")));
		assertFalse(openStart.contains(date("2026-02-01")));

		DateRange empty = new DateRange(null, null);
		assertFalse(empty.contains(date("2026-06-15")));
	}

	@Test
	public void getDaysClosedRange() {
		assertEquals(1L, new DateRange(date("2026-01-01"), date("2026-01-01")).getDays().getAsLong());
		assertEquals(31L, new DateRange(date("2026-01-01"), date("2026-01-31")).getDays().getAsLong());
	}

	@Test
	public void getDaysOpenReturnsEmpty() {
		assertFalse(new DateRange(null, null).getDays().isPresent());
		assertFalse(new DateRange(null, date("2026-01-31")).getDays().isPresent());
		assertFalse(new DateRange(date("2026-01-01"), null).getDays().isPresent());
	}

	@Test
	public void getDaysSubDayInvertedDoesNotCollide() {
		// inverted range whose duration is less than 24h yields days=-1;
		// non-closed returns OptionalLong.empty() so the two cases stay distinct
		// at the type level — callers can't accidentally treat "open-ended" as
		// "inverted" or vice versa.
		Date begin = date("2026-01-02");
		Date end = new Date(begin.getTime() - 60_000); // 1 minute before
		DateRange inverted = new DateRange(begin, end);
		assertTrue(inverted.isClosed(), "both ends non-null = closed range");
		assertTrue(inverted.getDays().isPresent(), "closed range yields a present optional");
		assertEquals(-1L, inverted.getDays().getAsLong(),
				"sub-24h inverted returns -1 (still wrapped in OptionalLong)");
		assertFalse(new DateRange(null, null).getDays().isPresent(),
				"non-closed range must yield empty optional, not a sentinel that collides with -1");
	}

	@Test
	public void overlapsTypicalCases() {
		DateRange jan = new DateRange(date("2026-01-01"), date("2026-01-31"));
		DateRange feb = new DateRange(date("2026-02-01"), date("2026-02-28"));
		DateRange mid = new DateRange(date("2026-01-15"), date("2026-02-15"));

		assertFalse(jan.overlaps(feb));
		assertTrue(jan.overlaps(mid));
		assertTrue(mid.overlaps(jan));
		assertFalse(jan.overlaps(null));

		// Touching boundary on different days does not count as overlap.
		DateRange janPrefix = new DateRange(date("2026-01-01"), date("2026-01-15"));
		DateRange janSuffix = new DateRange(date("2026-01-16"), date("2026-01-31"));
		assertFalse(janPrefix.overlaps(janSuffix));
	}

	@Test
	public void overlapsOpenEnded() {
		DateRange openEnd = new DateRange(date("2026-01-01"), null);
		DateRange openStart = new DateRange(null, date("2026-06-30"));
		assertTrue(openEnd.overlaps(openStart));

		DateRange empty = new DateRange(null, null);
		DateRange any = new DateRange(date("2026-01-01"), date("2026-01-31"));
		assertFalse(empty.overlaps(any));
		assertFalse(any.overlaps(empty));
	}

	@Test
	public void equalsAndHashCode() {
		DateRange a = new DateRange(date("2026-01-01"), date("2026-01-31"));
		DateRange b = new DateRange(date("2026-01-01"), date("2026-01-31"));
		DateRange c = new DateRange(date("2026-01-01"), date("2026-02-28"));

		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a, c);
		assertNotEquals(a, null);
		assertNotEquals(a, "not a range");

		DateRange empty1 = new DateRange(null, null);
		DateRange empty2 = new DateRange(null, null);
		assertEquals(empty1, empty2);
	}

	@Test
	public void toStringFormat() {
		DateRange r = new DateRange(date("2026-01-01"), date("2026-01-31"));
		assertEquals("2026-01-01 ~ 2026-01-31", r.toString());

		DateRange openEnd = new DateRange(date("2026-01-01"), null);
		assertEquals("2026-01-01 ~ ", openEnd.toString());

		DateRange openStart = new DateRange(null, date("2026-01-31"));
		assertEquals(" ~ 2026-01-31", openStart.toString());

		DateRange empty = new DateRange(null, null);
		assertEquals(" ~ ", empty.toString());
	}

	@Test
	public void compareToOrdersByBeginThenEnd() {
		DateRange a = new DateRange(date("2026-01-01"), date("2026-01-31"));
		DateRange b = new DateRange(date("2026-02-01"), date("2026-02-28"));
		DateRange c = new DateRange(date("2026-01-01"), date("2026-02-28"));

		assertTrue(a.compareTo(b) < 0);
		assertTrue(b.compareTo(a) > 0);
		assertEquals(0, a.compareTo(new DateRange(date("2026-01-01"), date("2026-01-31"))));
		assertTrue(a.compareTo(c) < 0); // same begin, earlier end
	}

	@Test
	public void compareToTreatsNullAsSmallest() {
		DateRange nullBegin = new DateRange(null, date("2026-01-31"));
		DateRange concrete = new DateRange(date("2026-01-01"), date("2026-01-31"));
		assertTrue(nullBegin.compareTo(concrete) < 0);
		assertTrue(concrete.compareTo(nullBegin) > 0);

		DateRange nullEnd = new DateRange(date("2026-01-01"), null);
		DateRange withEnd = new DateRange(date("2026-01-01"), date("2026-01-31"));
		assertTrue(nullEnd.compareTo(withEnd) < 0);

		assertThrows(NullPointerException.class, () -> concrete.compareTo(null));
	}

	@Test
	public void serializableRoundTrip() throws Exception {
		DateRange original = new DateRange(date("2026-01-01"), date("2026-01-31"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(original);
		}
		Object copy;
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
			copy = ois.readObject();
		}
		assertEquals(original, copy);

		DateRange empty = new DateRange(null, null);
		baos.reset();
		try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(empty);
		}
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
			assertEquals(empty, ois.readObject());
		}
	}
}
