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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zkmax.stateless.*;

/**
 * Used with z5.html to demostrate the client-centric programming model.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class BookSuggest implements Statelesslet {
	private Map _bookInfos;

	public void service(HttpServletRequest hreq, HttpServletResponse hres)
	throws javax.servlet.ServletException, java.io.IOException {
		AuRequest areq = AuRequest.getInstance(hreq);
		if ("onSelect".equals(areq.getCommand())) {
			List items = (List)areq.getData().get("items");
			List bookInfo = (List)_bookInfos.get(items.get(0));
			AuResponse.output(hreq, hres, "invoke",
				new Object[] {
					"main"/*widget ID*/, "suggest"/*method name*/,
					bookInfo/*suggest's 1st argument*/
				});
		}
	}

	public void destroy() {
	}
	public void init(StatelessletConfig config) {
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
