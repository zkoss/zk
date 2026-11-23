/* B60_ZK_919_ViewModel.java

	Purpose:
		
	Description:
		
	History:
		Mar 14, 2012 3:53:11 PM, Created by henrichen

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.test2;

/**
 * @author henrichen
 *
 */
public class B60_ZK_919_ViewModel {
	private java.util.Date utildate;
	private java.sql.Date sqldate;
	private java.sql.Time sqltime;
	private java.sql.Timestamp sqltimestamp;
	
	public B60_ZK_919_ViewModel() {
		utildate = new java.util.Date();
		sqldate = new java.sql.Date(utildate.getTime()+1000*60*60*24);
		sqltime = new java.sql.Time(utildate.getTime()+1000*60*60*24*2);
		sqltimestamp = new java.sql.Timestamp(utildate.getTime()+1000*60*60*24*3);
	}
	public java.util.Date getUtildate() {
		return utildate;
	}
	public void setUtildate(java.util.Date utildate) {
		this.utildate = utildate;
	}
	public java.sql.Date getSqldate() {
		return sqldate;
	}
	public void setSqldate(java.sql.Date sqldate) {
		this.sqldate = sqldate;
	}
	public java.sql.Time getSqltime() {
		return sqltime;
	}
	public void setSqltime(java.sql.Time sqltime) {
		this.sqltime = sqltime;
	}
	public java.sql.Timestamp getSqltimestamp() {
		return sqltimestamp;
	}
	public void setSqltimestamp(java.sql.Timestamp sqltimestamp) {
		this.sqltimestamp = sqltimestamp;
	}
}
