/* B86_ZK_4146Bean.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov 28 09:55:53 CST 2018, Created by jameschu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

/**
 * @author jameschu
 */
public class B86_ZK_4146BeanHolder {
	private B86_ZK_4146Bean dataBean;

	public B86_ZK_4146BeanHolder() {
		super();
	}

	public B86_ZK_4146BeanHolder(B86_ZK_4146Bean databean) {
		super();
		setDataBean(databean);
	}

	public B86_ZK_4146Bean getDataBean() {
		return dataBean;
	}

	public void setDataBean(B86_ZK_4146Bean dataBean) {
		this.dataBean = dataBean;
	}

}
