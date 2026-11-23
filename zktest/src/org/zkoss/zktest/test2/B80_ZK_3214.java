/* B80_ZK_3214.java

	Purpose:
		
	Description:
		
	History:
		Tue, Jun 28, 2016 12:15:43 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Sefi
 */
public class B80_ZK_3214 extends SelectorComposer{
    private static final String SESSION_LIST = "SessionList";
    @Wire
    private Listbox lb;
    @Wire
    private Label lbl;

    private ListModelList<HttpSession> mySessionLmL;

    private List<HttpSession> getAllSessions(){
        return (List<HttpSession>) WebApps.getCurrent().getAttribute(SESSION_LIST);
    }

    private HttpSession getCurrentSession(){
        return (HttpSession) Sessions.getCurrent().getNativeSession();
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        if(getAllSessions()==null){
            WebApps.getCurrent().setAttribute(SESSION_LIST,new ArrayList<HttpSession>());
        }
        if(!getAllSessions().contains(getCurrentSession())){
            getAllSessions().add(getCurrentSession());
        }
        lb.setItemRenderer(new ListitemRenderer<HttpSession>() {
            public void render(Listitem arg0, HttpSession arg1, int arg2) throws Exception {
                String sessionId = arg1.getId();
                Listcell cell = new Listcell(sessionId);
                arg0.appendChild(cell);
            }
        });
        mySessionLmL = new ListModelList<HttpSession>(getAllSessions());
        lb.setModel(mySessionLmL);
        lbl.setValue("current Id : "+getCurrentSession().getId());
    }

    @Listen("onClick=#btn")
    public void handleClick(){
        final HttpSession selectedHttpSession = mySessionLmL.getSelection().iterator().next();
        Execution exe = Executions.getCurrent();
        if(selectedHttpSession!=null){
            selectedHttpSession.invalidate();
            getAllSessions().remove(selectedHttpSession);
        }
        if (Executions.getCurrent() == null) {
            ExecutionsCtrl.setCurrent(exe);
            Clients.log("null");
        } else
            Clients.log(exe.toString());
    }
}
