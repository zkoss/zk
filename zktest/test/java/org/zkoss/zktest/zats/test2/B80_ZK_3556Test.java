package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.zkoss.bind.annotation.Transient;
import org.zkoss.bind.proxy.ProxyHelper;

public class B80_ZK_3556Test {

	private PojoWithTransientField pojo;

	@Before
	public void setup() {
		pojo = new PojoWithTransientField("Peter", "Test");
	}

	@Test
	public void testBeanProxy() {
		PojoWithTransientField proxy = ProxyHelper.createProxyIfAny(pojo);
		testTransientPropertyUpdate(proxy);
	}

	@Test
	public void testFormProxy() {
		PojoWithTransientField proxy = ProxyHelper.createFormProxy(pojo, PojoWithTransientField.class); /* form proxy */
		testTransientPropertyUpdate(proxy);
	}

	@Test
	public void testBeanProxyNested() {
		NestedPojo formProxy = ProxyHelper.createProxyIfAny(new NestedPojo(pojo));
		testTransientPropertyUpdate(formProxy.getPojoWithTransientField());
	}

	@Test
	public void testFormProxyNested() {
		NestedPojo formProxy = ProxyHelper.createFormProxy(new NestedPojo(pojo), NestedPojo.class); /* form proxy */
		testTransientPropertyUpdate(formProxy.getPojoWithTransientField());
	}

	private void testTransientPropertyUpdate(PojoWithTransientField proxy) {
//		assertEquals("Test, Peter", proxy.getFormattedName());
//		proxy.setFirstName("Martin");
//		assertEquals("Test, Martin", proxy.getFormattedName());
	}

	public static class NestedPojo {
		private PojoWithTransientField pojoWithTransientField;

		public NestedPojo() {
		}

		public NestedPojo(PojoWithTransientField pojoWithTransientField) {
			this.pojoWithTransientField = pojoWithTransientField;
		}

		public PojoWithTransientField getPojoWithTransientField() {
			return pojoWithTransientField;
		}
	}

	public static class PojoWithTransientField {
		private String firstName;
		private String lastName;

		public PojoWithTransientField() {
		}

		public PojoWithTransientField(String firstName, String lastName) {
			super();
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		@Transient
		public String getFormattedName() {
			return String.format("%2$s, %1$s", this.getFirstName(), this.getLastName());
		}
	}
}
