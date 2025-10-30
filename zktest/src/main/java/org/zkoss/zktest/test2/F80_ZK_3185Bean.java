package org.zkoss.zktest.test2;

import java.io.Serializable;
import java.util.*;

public class F80_ZK_3185Bean implements Serializable {

	private String name;

	private Date date;

	private Map<String, Date> remark;

	private F80_ZK_3185SubBean mainSubBean;

	private List<F80_ZK_3185SubBean> subBeanList = new ArrayList<F80_ZK_3185SubBean>();
	private Set<F80_ZK_3185SubBean> subBeanSet = new HashSet<F80_ZK_3185SubBean>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public F80_ZK_3185SubBean getMainSubBean() {
		return mainSubBean;
	}

	public void setMainSubBean(F80_ZK_3185SubBean mainSubBean) {
		this.mainSubBean = mainSubBean;
	}


	public List<F80_ZK_3185SubBean> getSubBeanList() {
		return subBeanList;
	}

	public void setSubBeanList(List<F80_ZK_3185SubBean> subBeanList) {
		this.subBeanList = subBeanList;
	}

	public Set<F80_ZK_3185SubBean> getSubBeanSet() {
		return subBeanSet;
	}

	public void setSubBeanSet(Set<F80_ZK_3185SubBean> subBeanSet) {
		this.subBeanSet = subBeanSet;
	}

	public Map<String, Date> getRemark() {
		return remark;
	}

	public void setRemark(Map<String, Date> remark) {
		this.remark = remark;
	}
}
