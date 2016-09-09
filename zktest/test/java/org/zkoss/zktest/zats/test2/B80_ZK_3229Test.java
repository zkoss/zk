package org.zkoss.zktest.zats.test2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.bind.proxy.ProxyHelper;

public class B80_ZK_3229Test {
	private static final String VALUE1 = "value1";
	private static final String VALUE2 = "value2";

	@Test
	public void testProxyMap() {
		Pojo pojo = new Pojo();
		MyKey key1 = new MyKey();
		MyKey key2 = new MyKey();
		pojo.getTestMap().put(key1, VALUE1);
		pojo.getTestMap().put(key2, VALUE2);

		Assert.assertTrue(pojo.getTestMap().keySet().contains(key1));
		Assert.assertTrue(pojo.getTestMap().keySet().contains(key2));
		Assert.assertTrue(pojo.getTestMap().containsKey(key1));
		Assert.assertTrue(pojo.getTestMap().containsKey(key2));
		Assert.assertEquals(VALUE1, pojo.getTestMap().get(key1));
		Assert.assertEquals(VALUE2, pojo.getTestMap().get(key2));
		
		Pojo pojoProxy = ProxyHelper.createProxyIfAny(pojo);

		Assert.assertTrue(pojoProxy.getTestMap().keySet().contains(key1));
		Assert.assertTrue(pojoProxy.getTestMap().keySet().contains(key2));
		Assert.assertTrue(pojoProxy.getTestMap().containsKey(key1));
		Assert.assertTrue(pojoProxy.getTestMap().containsKey(key2));
		Assert.assertEquals(VALUE1, pojoProxy.getTestMap().get(key1));
		Assert.assertEquals(VALUE2, pojoProxy.getTestMap().get(key2));
	}

	@Test
	public void testProxyList() {
		Pojo pojo = new Pojo();
		MyKey key1 = new MyKey();
		MyKey key2 = new MyKey();
		pojo.getTestList().add(key1);
		pojo.getTestList().add(key2);
		
		Assert.assertTrue(pojo.getTestList().contains(key1));
		Assert.assertTrue(pojo.getTestList().contains(key2));
		Assert.assertEquals(0, pojo.getTestList().indexOf(key1));
		Assert.assertEquals(1, pojo.getTestList().indexOf(key2));
		
		Pojo pojoProxy = ProxyHelper.createProxyIfAny(pojo);
		
		Assert.assertTrue(pojoProxy.getTestList().contains(key1));
		Assert.assertTrue(pojoProxy.getTestList().contains(key2));
		Assert.assertEquals(0, pojoProxy.getTestList().indexOf(key1));
		Assert.assertEquals(1, pojoProxy.getTestList().indexOf(key2));
	}

	@Test
	public void testProxySet() {
		Pojo pojo = new Pojo();
		MyKey key1 = new MyKey();
		MyKey key2 = new MyKey();
		pojo.getTestSet().add(key1);
		pojo.getTestSet().add(key2);
		
		Assert.assertTrue(pojo.getTestSet().contains(key1));
		Assert.assertTrue(pojo.getTestSet().contains(key2));
		
		Pojo pojoProxy = ProxyHelper.createProxyIfAny(pojo);
		
		Assert.assertTrue(pojoProxy.getTestSet().contains(key1));
		Assert.assertTrue(pojoProxy.getTestSet().contains(key2));
		
		Assert.assertTrue(pojoProxy.getTestSet().remove(key1));
		Assert.assertTrue(pojoProxy.getTestSet().remove(key2));
		Assert.assertFalse(pojoProxy.getTestSet().remove(key1));
		Assert.assertFalse(pojoProxy.getTestSet().remove(key2));
	}
	
	public static class Pojo {
		public Pojo() {

		}
		private Set<MyKey> testSet = new HashSet<>();
		private List<MyKey> testList = new ArrayList<>();
		private Map<MyKey, String> testMap = new HashMap<>();

		public Map<MyKey, String> getTestMap() {
			return testMap;
		}

		public List<MyKey> getTestList() {
			return testList;
		}

		public Set<MyKey> getTestSet() {
			return testSet;
		}
	} 
	
	public static class MyKey {
		public MyKey() {

		}
	}
}
