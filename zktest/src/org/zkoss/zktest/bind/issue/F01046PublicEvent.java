package org.zkoss.zktest.bind.issue;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.GlobalCommandEvent;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Subscribe;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;


public class F01046PublicEvent {

	static public class VM {
		String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
		
		@GlobalCommand("sayHello")
		@NotifyChange("message")
		public void sayHello(@BindingParam("param")String param){
			message = "Hello "+param;
		}
	}
	
	static public class Composesr extends SelectorComposer<Component>{
		
		@Wire("#msg2")
		Label msg2;
		
		@Subscribe("myqueue")
		public void sayHi(Event evt){
			if(evt instanceof GlobalCommandEvent){
				if("sayHi".equals(((GlobalCommandEvent)evt).getCommand())){
					String param = (String)((GlobalCommandEvent)evt).getArgs().get("param");
					msg2.setValue("Hello "+param);
				}				
			}
		}
		
		@Listen("onClick=#btn2")
		public void click(){
			Map args = new HashMap();
			args.put("param", "i am a composer");
			BindUtils.postGlobalCommand("myqueue", null, "sayHello", args);
		}
	}
	
	
}
