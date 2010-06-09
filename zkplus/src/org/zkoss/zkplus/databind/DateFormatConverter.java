/* DateToStringConverter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jan 15 15:33:43     2009, Created by Henri Chen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModelMap;
import org.zkoss.zul.Listbox;

/**
 * <p>Convert a {@link java.util.Date} or a {@link java.sql.Timestamp} to a 
 * formated date String. You can specify the date format in 'format' annotation, 
 * and the converter will format the given date for you. e.g.
 * <pre>
 * &lt;label value="@{mydate, converter='org.zkoss.zkplus.datebind.DateFormatConverter' self="@{format(yyyy/MM/dd)}"/>
 * </pre> 
 * If format is not given, default to 'MM/dd/yyyy'.
 * 
 * @author Henri Chen
 * @since 3.0.9
 */
public class DateFormatConverter implements TypeConverter {

	public Object coerceToBean(Object val, org.zkoss.zk.ui.Component comp) {
		//do nothing
		return val;
	}
	/**
	 * Depending whether the data is coming from the database or coming from the datebox 
	 * we might be passed either a java.util.Date or a java.sql.Timestamp
	 * 
	 * @see org.zkoss.zkplus.databind.TypeConverter#coerceToUi(java.lang.Object, org.zkoss.zk.ui.Component)
	 */
	public Object coerceToUi(Object val, org.zkoss.zk.ui.Component comp) {
		Date date = null;
		if( val instanceof Timestamp ){
			final Timestamp timestamp = (Timestamp)val;
			date = new Date(timestamp.getTime());
		} else if( val instanceof Date ){
			date = (Date)val;
		}

		final Annotation annot = ((ComponentCtrl)comp).getAnnotation("format");
		String pattern = null;
		if (annot != null) {
			pattern = annot.getAttribute("value");
		}
		
		//prepare dateFormat and convert Date to String
		return date == null ? "" : (new SimpleDateFormat(pattern == null ? "MM/dd/yyyy" : pattern)).format(date);
	}
}