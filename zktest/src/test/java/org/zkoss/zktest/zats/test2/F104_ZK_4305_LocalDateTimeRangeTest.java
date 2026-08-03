/* F104_ZK_4305_LocalDateTimeRangeTest.java

        Purpose:
                
        Description:
                
        History:
                Fri May 08 15:15:01 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.zkoss.zul.LocalDateTimeRange;

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
import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link LocalDateTimeRange}.
 */
public class F104_ZK_4305_LocalDateTimeRangeTest {

	private static final LocalDateTime T1 = LocalDateTime.of(2026, 1, 1, 9, 0);
	private static final LocalDateTime T2 = LocalDateTime.of(2026, 1, 1, 17, 30);
	private static final LocalDateTime T3 = LocalDateTime.of(2026, 1, 2, 9, 0);
	private static final LocalDateTime T4 = LocalDateTime.of(2026, 1, 3, 17, 30);

	@Test
	public void constructorAcceptsNulls() {
		LocalDateTimeRange empty = new LocalDateTimeRange(null, null);
		assertNull(empty.getBegin());
		assertNull(empty.getEnd());

		LocalDateTimeRange openStart = new LocalDateTimeRange(null, T2);
		assertNull(openStart.getBegin());
		assertEquals(T2, openStart.getEnd());
	}

	@Test
	public void factoryEquivalentToConstructor() {
		assertEquals(new LocalDateTimeRange(T1, T2), LocalDateTimeRange.of(T1, T2));
	}

	@Test
	public void stateMatrix() {
		assertTrue(new LocalDateTimeRange(null, null).isEmpty());
		assertTrue(new LocalDateTimeRange(null, T2).isOpenStart());
		assertTrue(new LocalDateTimeRange(T1, null).isOpenEnd());
		assertTrue(new LocalDateTimeRange(T1, T2).isClosed());

		LocalDateTimeRange closed = new LocalDateTimeRange(T1, T2);
		assertFalse(closed.isEmpty());
		assertFalse(closed.isOpenStart());
		assertFalse(closed.isOpenEnd());
	}

	@Test
	public void containsBoundary() {
		LocalDateTimeRange r = new LocalDateTimeRange(T1, T2);
		assertTrue(r.contains(T1));
		assertTrue(r.contains(LocalDateTime.of(2026, 1, 1, 12, 0)));
		assertTrue(r.contains(T2));
		assertFalse(r.contains(LocalDateTime.of(2026, 1, 1, 8, 59)));
		assertFalse(r.contains(LocalDateTime.of(2026, 1, 1, 17, 31)));
		assertFalse(r.contains(null));
	}

	@Test
	public void containsOpenEnded() {
		LocalDateTimeRange openEnd = new LocalDateTimeRange(T1, null);
		assertTrue(openEnd.contains(T1));
		assertTrue(openEnd.contains(LocalDateTime.of(9999, 1, 1, 0, 0)));
		assertFalse(openEnd.contains(LocalDateTime.of(2026, 1, 1, 8, 59)));

		LocalDateTimeRange empty = new LocalDateTimeRange(null, null);
		assertFalse(empty.contains(T1));
	}

	@Test
	public void getDurationClosedRange() {
		Duration d = new LocalDateTimeRange(T1, T2).getDuration();
		assertEquals(Duration.ofHours(8).plusMinutes(30), d);

		// Same instant -> ZERO.
		assertEquals(Duration.ZERO, new LocalDateTimeRange(T1, T1).getDuration());
	}

	@Test
	public void getDurationOpenReturnsNull() {
		assertNull(new LocalDateTimeRange(null, null).getDuration());
		assertNull(new LocalDateTimeRange(null, T2).getDuration());
		assertNull(new LocalDateTimeRange(T1, null).getDuration());
	}

	@Test
	public void overlapsTypicalCases() {
		LocalDateTimeRange day1 = new LocalDateTimeRange(T1, T2);
		LocalDateTimeRange day2 = new LocalDateTimeRange(T3, T4);
		LocalDateTimeRange straddle = new LocalDateTimeRange(LocalDateTime.of(2026, 1, 1, 10, 0), T3);

		assertFalse(day1.overlaps(day2));
		assertTrue(day1.overlaps(straddle));
		assertTrue(straddle.overlaps(day2));
		assertFalse(day1.overlaps(null));
	}

	@Test
	public void overlapsOpenEnded() {
		LocalDateTimeRange openEnd = new LocalDateTimeRange(T1, null);
		LocalDateTimeRange openStart = new LocalDateTimeRange(null, T4);
		assertTrue(openEnd.overlaps(openStart));

		LocalDateTimeRange empty = new LocalDateTimeRange(null, null);
		assertFalse(empty.overlaps(new LocalDateTimeRange(T1, T2)));
	}

	@Test
	public void equalsAndHashCode() {
		LocalDateTimeRange a = new LocalDateTimeRange(T1, T2);
		LocalDateTimeRange b = new LocalDateTimeRange(T1, T2);
		LocalDateTimeRange c = new LocalDateTimeRange(T1, T4);

		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a, c);
		assertNotEquals(a, null);
		assertNotEquals(a, "x");
	}

	@Test
	public void toStringFormat() {
		LocalDateTimeRange r = new LocalDateTimeRange(T1, T2);
		assertEquals(T1.toString() + " ~ " + T2.toString(), r.toString());
		assertEquals(" ~ ", new LocalDateTimeRange(null, null).toString());
		assertEquals(T1.toString() + " ~ ", new LocalDateTimeRange(T1, null).toString());
		assertEquals(" ~ " + T2.toString(), new LocalDateTimeRange(null, T2).toString());
	}

	@Test
	public void compareToOrdersByBeginThenEnd() {
		LocalDateTimeRange a = new LocalDateTimeRange(T1, T2);
		LocalDateTimeRange b = new LocalDateTimeRange(T3, T4);
		LocalDateTimeRange c = new LocalDateTimeRange(T1, T4);

		assertTrue(a.compareTo(b) < 0);
		assertEquals(0, a.compareTo(new LocalDateTimeRange(T1, T2)));
		assertTrue(a.compareTo(c) < 0);
	}

	@Test
	public void compareToTreatsNullAsSmallest() {
		LocalDateTimeRange nullBegin = new LocalDateTimeRange(null, T2);
		LocalDateTimeRange concrete = new LocalDateTimeRange(T1, T2);
		assertTrue(nullBegin.compareTo(concrete) < 0);

		LocalDateTimeRange nullEnd = new LocalDateTimeRange(T1, null);
		assertTrue(nullEnd.compareTo(concrete) < 0);

		assertThrows(NullPointerException.class, () -> concrete.compareTo(null));
	}

	@Test
	public void serializableRoundTrip() throws Exception {
		LocalDateTimeRange original = new LocalDateTimeRange(T1, T2);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(original);
		}
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
			assertEquals(original, ois.readObject());
		}

		LocalDateTimeRange openStart = new LocalDateTimeRange(null, T2);
		baos.reset();
		try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(openStart);
		}
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
			assertEquals(openStart, ois.readObject());
		}
	}
}
