package org.zkoss.zktest.test2;

import java.util.HashMap;
import java.util.Map;

public class F70_ZK_2483_VM {
	
	public Map<String, String> getEntries() {
		Map<String, String> entries = new HashMap<String, String>();
		for (int i = 0; i < 100; i++) {
			entries.put("key " + i, "value " + i);
		}
		return entries;
	}
}
