package org.zkoss.zktest.bind.issue;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
public class B01259HaskMapInFx {
	private Map<String, String> hash = new HashMap<String, String>();
	private String key = "mykey";
	private String key2 = "mykey2";
	private String key3 = "mykey3";

	public B01259HaskMapInFx() {
		this.hash.put(key, "Hello World");
		this.hash.put(key2, "Hi Dennis");
		this.hash.put(key3, key2);
	}

	public Map<String, String> getHash() {
		return this.hash;
	}

	public String getKey() {
		return this.key;
	}
	public String getKey2() {
		return this.key2;
	}
	public String getKey3() {
		return this.key3;
	}
	
	@Command({"save","save2"}) @NotifyChange(".")
	public void save(){
		
	}
}
