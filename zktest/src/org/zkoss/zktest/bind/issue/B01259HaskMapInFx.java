package org.zkoss.zktest.bind.issue;
import java.util.HashMap;
import java.util.Map;
public class B01259HaskMapInFx {
	private Map<String, String> hash = new HashMap<String, String>();
	private String key = "key";

	public B01259HaskMapInFx() {
		this.hash.put("key", "Hello Word!!!");
	}

	public Map<String, String> getHash() {
		return this.hash;
	}

	public String getKey() {
		return this.key;
	}

	public void setHash(final Map<String, String> hash) {
		this.hash = hash;
	}

	public void setKey(final String key) {
		this.key = key;
	}
}
