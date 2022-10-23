/* B96_ZK_4877VM.java

	Purpose:
		
	Description:
		
	History:
		Mon May 17 16:28:05 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

/**
 * @author rudyhuang
 */
public class B96_ZK_4877VM {
	private Course course;

	@Init
	public void init() {
		course = new Course();
	}

	public Course getCourse() {
		return course;
	}

	@Command
	public void createCourse() {
		// do nothing
	}

	public static class Course {
		private String title;

		public Course() {
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
}
