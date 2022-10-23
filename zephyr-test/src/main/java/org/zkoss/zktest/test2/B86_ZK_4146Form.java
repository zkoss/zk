/* B86_ZK_4146Form.java

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
public class B86_ZK_4146Form {
	private B86_ZK_4146Bean dataBean;
	private B86_ZK_4146BeanHolder dataBeanHolder;

	public B86_ZK_4146Form() {
		dataBean = new B86_ZK_4146Bean(true);
		dataBeanHolder = new B86_ZK_4146BeanHolder(getDataBean());
	}

	public B86_ZK_4146Bean getDataBean() {
		return dataBean;
	}

	public void setDataBean(B86_ZK_4146Bean dataBean) {
		this.dataBean = dataBean;
	}

	public B86_ZK_4146BeanHolder getDataBeanHolder() {
		return dataBeanHolder;
	}

	public void setDataBeanHolder(B86_ZK_4146BeanHolder dataBeanHolder) {
		this.dataBeanHolder = dataBeanHolder;
	}

}
