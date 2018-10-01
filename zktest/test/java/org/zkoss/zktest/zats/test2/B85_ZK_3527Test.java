package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author bob peng
 */
public class B85_ZK_3527Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		for (int i = 0; i < 4; i++) {
			String str = jq(".z-datebox-input:eq(" + i + ")").val();
			assertThat("Wrong hours or minutes : " + str, str, CoreMatchers.endsWith("00:00"));
		}
	}
}
