/* F104_ZK_4305_ZonedDateTimeRangeTest.java

        Purpose:
                
        Description:
                
        History:
                Fri May 08 15:15:05 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.zkoss.zul.ZonedDateTimeRange;

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
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ZonedDateTimeRange}.
 */
public class F104_ZK_4305_ZonedDateTimeRangeTest {

	private static final ZoneId UTC = ZoneId.of("UTC");
	private static final ZoneId TPE = ZoneId.of("Asia/Taipei");

	private static final ZonedDateTime Z1 = ZonedDateTime.of(2026, 1, 1, 9, 0, 0, 0, UTC);
	private static final ZonedDateTime Z2 = ZonedDateTime.of(2026, 1, 1, 17, 30, 0, 0, UTC);
	private static final ZonedDateTime Z3 = ZonedDateTime.of(2026, 1, 2, 9, 0, 0, 0, UTC);
	private static final ZonedDateTime Z4 = ZonedDateTime.of(2026, 1, 3, 17, 30, 0, 0, UTC);

	@Test
	public void constructorAcceptsNulls() {
		ZonedDateTimeRange empty = new ZonedDateTimeRange(null, null);
		assertNull(empty.getBegin());
		assertNull(empty.getEnd());
	}

	@Test
	public void factoryEquivalentToConstructor() {
		assertEquals(new ZonedDateTimeRange(Z1, Z2), ZonedDateTimeRange.of(Z1, Z2));
	}

	@Test
	public void stateMatrix() {
		assertTrue(new ZonedDateTimeRange(null, null).isEmpty());
		assertTrue(new ZonedDateTimeRange(null, Z2).isOpenStart());
		assertTrue(new ZonedDateTimeRange(Z1, null).isOpenEnd());
		assertTrue(new ZonedDateTimeRange(Z1, Z2).isClosed());
	}

	@Test
	public void containsBoundary() {
		ZonedDateTimeRange r = new ZonedDateTimeRange(Z1, Z2);
		assertTrue(r.contains(Z1));
		assertTrue(r.contains(Z2));
		assertTrue(r.contains(ZonedDateTime.of(2026, 1, 1, 12, 0, 0, 0, UTC)));
		assertFalse(r.contains(ZonedDateTime.of(2026, 1, 1, 8, 59, 0, 0, UTC)));
		assertFalse(r.contains(ZonedDateTime.of(2026, 1, 1, 17, 31, 0, 0, UTC)));
		assertFalse(r.contains(null));
	}

	@Test
	public void containsAcrossZones() {
		// Z1 is 2026-01-01T09:00 UTC == 2026-01-01T17:00 Taipei.
		// A Taipei-zoned value at 17:00 should be in [Z1, Z2].
		ZonedDateTimeRange r = new ZonedDateTimeRange(Z1, Z2);
		ZonedDateTime tpe = ZonedDateTime.of(2026, 1, 1, 17, 0, 0, 0, TPE);
		assertTrue(r.contains(tpe));
	}

	@Test
	public void containsOpenEnded() {
		ZonedDateTimeRange openEnd = new ZonedDateTimeRange(Z1, null);
		assertTrue(openEnd.contains(Z1));
		assertTrue(openEnd.contains(Z4));
		assertFalse(openEnd.contains(ZonedDateTime.of(2025, 1, 1, 0, 0, 0, 0, UTC)));

		ZonedDateTimeRange empty = new ZonedDateTimeRange(null, null);
		assertFalse(empty.contains(Z1));
	}

	@Test
	public void getDurationClosedRange() {
		assertEquals(Duration.ofHours(8).plusMinutes(30), new ZonedDateTimeRange(Z1, Z2).getDuration());
		assertEquals(Duration.ZERO, new ZonedDateTimeRange(Z1, Z1).getDuration());
	}

	@Test
	public void getDurationOpenReturnsNull() {
		assertNull(new ZonedDateTimeRange(null, null).getDuration());
		assertNull(new ZonedDateTimeRange(null, Z2).getDuration());
		assertNull(new ZonedDateTimeRange(Z1, null).getDuration());
	}

	@Test
	public void overlapsTypicalCases() {
		ZonedDateTimeRange day1 = new ZonedDateTimeRange(Z1, Z2);
		ZonedDateTimeRange day2 = new ZonedDateTimeRange(Z3, Z4);
		ZonedDateTimeRange straddle =
				new ZonedDateTimeRange(ZonedDateTime.of(2026, 1, 1, 10, 0, 0, 0, UTC), Z3);

		assertFalse(day1.overlaps(day2));
		assertTrue(day1.overlaps(straddle));
		assertFalse(day1.overlaps(null));
	}

	@Test
	public void overlapsOpenEnded() {
		ZonedDateTimeRange openEnd = new ZonedDateTimeRange(Z1, null);
		ZonedDateTimeRange openStart = new ZonedDateTimeRange(null, Z4);
		assertTrue(openEnd.overlaps(openStart));

		ZonedDateTimeRange empty = new ZonedDateTimeRange(null, null);
		assertFalse(empty.overlaps(new ZonedDateTimeRange(Z1, Z2)));
	}

	@Test
	public void equalsAndHashCode() {
		ZonedDateTimeRange a = new ZonedDateTimeRange(Z1, Z2);
		ZonedDateTimeRange b = new ZonedDateTimeRange(Z1, Z2);
		ZonedDateTimeRange c = new ZonedDateTimeRange(Z1, Z4);

		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a, c);
		assertNotEquals(a, null);
		assertNotEquals(a, "x");
	}

	@Test
	public void toStringFormat() {
		ZonedDateTimeRange r = new ZonedDateTimeRange(Z1, Z2);
		assertEquals(Z1.toString() + " ~ " + Z2.toString(), r.toString());
		assertEquals(" ~ ", new ZonedDateTimeRange(null, null).toString());
	}

	@Test
	public void compareToOrdersByBeginThenEnd() {
		ZonedDateTimeRange a = new ZonedDateTimeRange(Z1, Z2);
		ZonedDateTimeRange b = new ZonedDateTimeRange(Z3, Z4);
		ZonedDateTimeRange c = new ZonedDateTimeRange(Z1, Z4);

		assertTrue(a.compareTo(b) < 0);
		assertEquals(0, a.compareTo(new ZonedDateTimeRange(Z1, Z2)));
		assertTrue(a.compareTo(c) < 0);
	}

	@Test
	public void compareToTreatsNullAsSmallest() {
		ZonedDateTimeRange nullBegin = new ZonedDateTimeRange(null, Z2);
		ZonedDateTimeRange concrete = new ZonedDateTimeRange(Z1, Z2);
		assertTrue(nullBegin.compareTo(concrete) < 0);

		ZonedDateTimeRange nullEnd = new ZonedDateTimeRange(Z1, null);
		assertTrue(nullEnd.compareTo(concrete) < 0);

		assertThrows(NullPointerException.class, () -> concrete.compareTo(null));
	}

	@Test
	public void serializableRoundTrip() throws Exception {
		ZonedDateTimeRange original = new ZonedDateTimeRange(Z1, Z2);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(original);
		}
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
			assertEquals(original, ois.readObject());
		}

		ZonedDateTimeRange openEnd = new ZonedDateTimeRange(Z1, null);
		baos.reset();
		try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(openEnd);
		}
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
			assertEquals(openEnd, ois.readObject());
		}
	}
}
