package org.zkoss.zktest.bind.issue;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;

public class F00772InterComm extends SelectorComposer<Component>{

	@Listen("onClick=button#postx")
	public void postX(){
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("data", "postX");
		BindUtils.postGlobalCommand(null, null, "cmdX", args);
	}
	
	@Listen("onClick=button#posty")
	public void postY(){
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("data", "postY");
		BindUtils.postGlobalCommand(null, null, "cmdY", args);
	}
	
	@Listen("onClick=button#postz")
	public void postZ(){
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("data", "postZ");
		BindUtils.postGlobalCommand(null, null, "cmdZ", args);
	}
	
	@Listen("onClick=button#postmy")
	public void postMy(){
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("data", "postMy");
		BindUtils.postGlobalCommand("myqueue", null, "cmdX", args);
	}
	
}
