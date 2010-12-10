/* BookSuggest.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul  6 15:44:37     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zkdemo.test;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.LinkedHashMap;

import javax.servlet.*;
import javax.servlet.http.*;

import org.zkoss.json.JSONArray;

/**
 * Used with booksuggest.html to demostrate the client-centric programming model.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class BookSuggest extends HttpServlet {
	private Map _bookInfos;

	public void service(HttpServletRequest request, HttpServletResponse response)
	throws javax.servlet.ServletException, java.io.IOException {
		final String bookId = request.getParameter("bookId");
		if (bookId != null) {
			List bookInfo = (List)_bookInfos.get(bookId);
			response.getWriter().append(JSONArray.toJSONString(bookInfo));
		}
	}

	public void destroy() {
	}
	public void init(ServletConfig config) {
		_bookInfos = new LinkedHashMap();

		String[] cats = {"bm", "ci", "lf"};
		String[][][] all = {
			{ //bm: Biographies and Memoirs
				{"The Last Lecture", "Randy Pausch", "Jeffrey Zaslow"},
				{"Always Looking Up: The Adventures of an Incurable Optimist ","Michael F. Fox", ""}
			},
			{ //ci: Computer and Internet
				{"ZK: Ajax without the Javascript Framework", "HenriChen", "Robbie Cheng"},
				{"Beautiful Teams: Inspiring and Cautionary Tales", "Andrew Stellman", "Jennifer Greene"}
			},
			{ //lf: Literature and Fiction
				{"The Shack", "William P. Young", ""},
				{"Watchmen", "Alan Moore", "Dave Gibbons"},
				{"Long Lost", "Harlan Coben", ""}
			}
		};
		for (int j = 0; j < cats.length; ++j) {
			String[][] infos = all[j];
			List list = new LinkedList();
			_bookInfos.put(cats[j], list);
			for (int k = 0; k < infos.length; ++k)
				list.add(createBookInfo(infos[k][0], infos[k][1], infos[k][2]));
		}
	}
	private Map createBookInfo(String title, String author1, String author2) {
		Map info = new LinkedHashMap();
		info.put("title", title);
		info.put("author1", author1);
		info.put("author2", author2);
		return info;
	}
}
