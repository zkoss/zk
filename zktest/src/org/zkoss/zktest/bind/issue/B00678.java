package org.zkoss.zktest.bind.issue;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B00678 {

	Map map = new HashMap<String,String>();
	String msg = null;
	
	public B00678(){
		map.put("value", "Value A");
		msg = "msg A";
	}
	
	public Map getMap(){
		return map;
	}
	
	public String getMsg() {
		return msg;
	}

	@Command @NotifyChange({"map","msg"})
	public void cmd1(){
		map.put("value", "Value B");
		msg = "msg B";
	}
	
	@Command @NotifyChange({"map","msg"})
	public void cmd2(){
		map.put("value", "Value C");
		msg = "msg C";
	}
}
