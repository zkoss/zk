/**
 * 
 */
package org.zkoss.jspdemo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

/**
 * @author ian
 *
 */
public class MyInit implements Initiator {

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.Initiator#doAfterCompose(org.zkoss.zk.ui.Page)
	 */
	public void doAfterCompose(Page page) throws Exception {
		Object a = page.getVariable("current_date");
		System.out.println("MyInit::doAfterCompose(): current_date= "+a);
	}

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.Initiator#doCatch(java.lang.Throwable)
	 */
	public boolean doCatch(Throwable ex) {
		// TODO Auto-generated method stub
		return false;

	}

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.Initiator#doFinally()
	 */
	public void doFinally() {
		// TODO Auto-generated method stub

	}
	private SimpleDateFormat form = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.Initiator#doInit(org.zkoss.zk.ui.Page, java.lang.Object[])
	 */
	public void doInit(Page page, Object[] args) throws Exception {
		ArrayList list = new ArrayList();
		page.setVariable("current_date", new Date());
		for(int i=0;i<10;i++)
		{
			final String ref = "index: "+i;
			list.add(new MyValue(){
				public String getDate() {
					return form.format(new Date());
				}
				public String getValue() {
					return ref;
				}
			});
		}
		page.setVariable("my_list", list);
	}
	


}
