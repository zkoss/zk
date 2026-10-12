/* F110_ZK_6167_gsonTest.java

		Purpose:
				
		Description:
				
		History:
				Sun Sep 27 22:29:05 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import org.zkoss.bind.impl.BindContextImpl;
import org.zkoss.bind.impl.ParamCall;
import org.zkoss.json.JSONValue;
import org.zkoss.util.Pair;
import org.zkoss.zkmax.bind.GsonConverter;

public class F110_ZK_6167_gsonTest {
	// ZK-6167: the JSON Gson wrote by reflection on Java 11, except TimeZone, whose other fields were internal to the JDK
	private static final String JSON = "{\"day\":{\"year\":2024,\"month\":1,\"day\":2},"
			+ "\"time\":{\"dateTime\":{\"date\":{\"year\":2024,\"month\":1,\"day\":2},"
			+ "\"time\":{\"hour\":13,\"minute\":4,\"second\":5,\"nano\":6}},"
			+ "\"offset\":{\"totalSeconds\":28800},\"zone\":{\"id\":\"Asia/Taipei\"}},"
			+ "\"tz\":{\"rawOffset\":28800000,\"ID\":\"Asia/Taipei\"}}";

	public static class Param {
		public LocalDate day = LocalDate.of(2024, 1, 2);
		public ZonedDateTime time = ZonedDateTime.of(LocalDateTime.of(2024, 1, 2, 13, 4, 5, 6), ZoneId.of("Asia/Taipei"));
		public TimeZone tz = TimeZone.getTimeZone("Asia/Taipei");
	}

	@Test
	public void bindingParam() {
		GsonConverter converter = new GsonConverter();
		assertEquals(JSON, converter.coerceToUi(new Param(), null, null));

		BindContextImpl ctx = new BindContextImpl(null, null, false, null, null, null);
		ctx.setAttribute(ParamCall.BINDING_PARAM_CALL_TYPE, Param.class);
		assertParam((Param) converter.coerceToBean(JSONValue.parse(JSON), null, ctx));
	}

	@Test
	public void serviceParam() {
		assertParam((Param) new org.zkoss.zkmax.ui.GsonConverter().convert(new Pair<>(Param.class, JSON)));
	}

	private static void assertParam(Param param) {
		Param expected = new Param();
		assertEquals(expected.day, param.day);
		assertEquals(expected.time, param.time);
		assertEquals(expected.tz, param.tz);
	}
}
