/* F104_ZK_4305_LocalDateRangeTest.java

        Purpose:
                
        Description:
                
        History:
                Fri May 08 15:14:56 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.zkoss.zul.LocalDateRange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link LocalDateRange}.
 */
public class F104_ZK_4305_LocalDateRangeTest {

	private static final LocalDate D1 = LocalDate.of(2026, 1, 1);
	private static final LocalDate D15 = LocalDate.of(2026, 1, 15);
	private static final LocalDate D31 = LocalDate.of(2026, 1, 31);
	private static final LocalDate F1 = LocalDate.of(2026, 2, 1);
	private static final LocalDate F28 = LocalDate.of(2026, 2, 28);

	@Test
	public void constructorAcceptsNulls() {
		LocalDateRange empty = new LocalDateRange(null, null);
		assertNull(empty.getBegin());
		assertNull(empty.getEnd());

		LocalDateRange openStart = new LocalDateRange(null, D31);
		assertNull(openStart.getBegin());
		assertEquals(D31, openStart.getEnd());
	}

	@Test
	public void factoryEquivalentToConstructor() {
		assertEquals(new LocalDateRange(D1, D31), LocalDateRange.of(D1, D31));
	}

	@Test
	public void stateMatrix() {
		LocalDateRange empty = new LocalDateRange(null, null);
		assertTrue(empty.isEmpty());
		assertFalse(empty.isOpenStart());
		assertFalse(empty.isOpenEnd());
		assertFalse(empty.isClosed());

		LocalDateRange openStart = new LocalDateRange(null, D31);
		assertFalse(openStart.isEmpty());
		assertTrue(openStart.isOpenStart());
		assertFalse(openStart.isOpenEnd());
		assertFalse(openStart.isClosed());

		LocalDateRange openEnd = new LocalDateRange(D1, null);
		assertFalse(openEnd.isEmpty());
		assertFalse(openEnd.isOpenStart());
		assertTrue(openEnd.isOpenEnd());
		assertFalse(openEnd.isClosed());

		LocalDateRange closed = new LocalDateRange(D1, D31);
		assertFalse(closed.isEmpty());
		assertFalse(closed.isOpenStart());
		assertFalse(closed.isOpenEnd());
		assertTrue(closed.isClosed());
	}

	@Test
	public void containsBoundary() {
		LocalDateRange r = new LocalDateRange(D1, D31);
		assertTrue(r.contains(D1));
		assertTrue(r.contains(D15));
		assertTrue(r.contains(D31));
		assertFalse(r.contains(LocalDate.of(2025, 12, 31)));
		assertFalse(r.contains(F1));
		assertFalse(r.contains(null));
	}

	@Test
	public void containsOpenEnded() {
		LocalDateRange openEnd = new LocalDateRange(D1, null);
		assertTrue(openEnd.contains(D1));
		assertTrue(openEnd.contains(LocalDate.of(9999, 12, 31)));
		assertFalse(openEnd.contains(LocalDate.of(2025, 12, 31)));

		LocalDateRange openStart = new LocalDateRange(null, D31);
		assertTrue(openStart.contains(LocalDate.of(1900, 1, 1)));
		assertTrue(openStart.contains(D31));
		assertFalse(openStart.contains(F1));

		LocalDateRange empty = new LocalDateRange(null, null);
		assertFalse(empty.contains(D15));
	}

	@Test
	public void getDaysClosedRange() {
		assertEquals(1L, new LocalDateRange(D1, D1).getDays().getAsLong());
		assertEquals(31L, new LocalDateRange(D1, D31).getDays().getAsLong());
	}

	@Test
	public void getDaysOpenReturnsEmpty() {
		assertFalse(new LocalDateRange(null, null).getDays().isPresent());
		assertFalse(new LocalDateRange(null, D31).getDays().isPresent());
		assertFalse(new LocalDateRange(D1, null).getDays().isPresent());
	}

	@Test
	public void overlapsTypicalCases() {
		LocalDateRange jan = new LocalDateRange(D1, D31);
		LocalDateRange feb = new LocalDateRange(F1, F28);
		LocalDateRange mid = new LocalDateRange(D15, LocalDate.of(2026, 2, 15));

		assertFalse(jan.overlaps(feb));
		assertTrue(jan.overlaps(mid));
		assertTrue(mid.overlaps(jan));
		assertFalse(jan.overlaps(null));

		LocalDateRange janPrefix = new LocalDateRange(D1, D15);
		LocalDateRange janSuffix = new LocalDateRange(LocalDate.of(2026, 1, 16), D31);
		assertFalse(janPrefix.overlaps(janSuffix));
	}

	@Test
	public void overlapsOpenEnded() {
		LocalDateRange openEnd = new LocalDateRange(D1, null);
		LocalDateRange openStart = new LocalDateRange(null, LocalDate.of(2026, 6, 30));
		assertTrue(openEnd.overlaps(openStart));

		LocalDateRange empty = new LocalDateRange(null, null);
		assertFalse(empty.overlaps(new LocalDateRange(D1, D31)));
	}

	@Test
	public void equalsAndHashCode() {
		LocalDateRange a = new LocalDateRange(D1, D31);
		LocalDateRange b = new LocalDateRange(D1, D31);
		LocalDateRange c = new LocalDateRange(D1, F28);

		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a, c);
		assertNotEquals(a, null);
		assertNotEquals(a, "not a range");

		assertEquals(new LocalDateRange(null, null), new LocalDateRange(null, null));
	}

	@Test
	public void toStringFormat() {
		assertEquals("2026-01-01 ~ 2026-01-31", new LocalDateRange(D1, D31).toString());
		assertEquals("2026-01-01 ~ ", new LocalDateRange(D1, null).toString());
		assertEquals(" ~ 2026-01-31", new LocalDateRange(null, D31).toString());
		assertEquals(" ~ ", new LocalDateRange(null, null).toString());
	}

	@Test
	public void compareToOrdersByBeginThenEnd() {
		LocalDateRange a = new LocalDateRange(D1, D31);
		LocalDateRange b = new LocalDateRange(F1, F28);
		LocalDateRange c = new LocalDateRange(D1, F28);

		assertTrue(a.compareTo(b) < 0);
		assertTrue(b.compareTo(a) > 0);
		assertEquals(0, a.compareTo(new LocalDateRange(D1, D31)));
		assertTrue(a.compareTo(c) < 0);
	}

	@Test
	public void compareToTreatsNullAsSmallest() {
		LocalDateRange nullBegin = new LocalDateRange(null, D31);
		LocalDateRange concrete = new LocalDateRange(D1, D31);
		assertTrue(nullBegin.compareTo(concrete) < 0);

		LocalDateRange nullEnd = new LocalDateRange(D1, null);
		assertTrue(nullEnd.compareTo(concrete) < 0);

		assertThrows(NullPointerException.class, () -> concrete.compareTo(null));
	}

	@Test
	public void serializableRoundTrip() throws Exception {
		LocalDateRange original = new LocalDateRange(D1, D31);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(original);
		}
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
			assertEquals(original, ois.readObject());
		}

		LocalDateRange empty = new LocalDateRange(null, null);
		baos.reset();
		try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(empty);
		}
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
			assertEquals(empty, ois.readObject());
		}
	}
}
