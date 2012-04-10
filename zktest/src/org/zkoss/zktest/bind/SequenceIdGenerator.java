package org.zkoss.zktest.bind;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.sys.IdGenerator;

public class SequenceIdGenerator implements IdGenerator{
	
	
	
	
	public String nextComponentUuid(Desktop desktop, Component comp, ComponentInfo info) {
		
		Map<String,Integer> idcmap = (Map<String,Integer>)desktop.getAttribute("__sidg_idhash");
		if(idcmap==null){
			idcmap = new HashMap<String,Integer>();
			desktop.setAttribute("__sidg_idhash", idcmap);
		}
		
		String name = comp.getClass().getSimpleName().toLowerCase();
		if(name.length()>6){
			name = name.substring(0,6);
		}
		Integer n = idcmap.get(name);
		if(n==null){
			n = new Integer(0);
		}
		int i = n.intValue()+1;
		idcmap.put(name, i);
		return name + "_" + i;
	}

	public String nextDesktopId(Desktop desktop) {
		HttpServletRequest req = (HttpServletRequest)Executions.getCurrent().getNativeRequest();
		String dtid = req.getParameter("tdtid");
		if(dtid!=null){
//			System.out.println(" use client dtid "+dtid);
		}
		return dtid==null?null:dtid;
	}
	

	public String nextPageUuid(Page page) {
		// TODO Auto-generated method stub
		return null;
	}
}
