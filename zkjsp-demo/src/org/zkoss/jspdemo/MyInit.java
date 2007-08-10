/**
 * 
 */
package org.zkoss.jspdemo;

import java.util.ArrayList;
import java.util.Date;

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
	public void doCatch(Throwable ex) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.Initiator#doFinally()
	 */
	public void doFinally() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.Initiator#doInit(org.zkoss.zk.ui.Page, java.lang.Object[])
	 */
	public void doInit(Page page, Object[] args) throws Exception {

		final Date now = new Date();
		class Ref{
			public Object ref; 
		}
		final Ref value = new Ref();
		ArrayList list = new ArrayList();
		page.setVariable("current_date", now);
		for(int i=0;i<10;i++)
		{
			value.ref = "index: "+i;
			list.add(new MyValue(){
				String val = (String) value.ref;
				public Date getDate() {
					return now;
				}
				public String getValue() {
					return val;
				}
			});
		}
		page.setVariable("my_list", list);
	}
	


}
