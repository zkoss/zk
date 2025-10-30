/* F90_ZK_4380VM.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 23 09:47:36 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.SimpleListModel;

/**
 * @author rudyhuang
 */
public class F90_ZK_4380VM {
	private SimpleListModel<String> continent;
	private List<String> proglangs;

	private String resultContinent = null;
	private List<String> resultLangs = new ArrayList<>();

	@Init
	public void init() {
		continent = new SimpleListModel<>(new String[] {
				"North America", "South America", "Europe", "Asia", "Africa",
				"Oceania", "Antarctica"
		});
		proglangs = Arrays.asList(
				"Java", "C", "Python", "C++", "C#", "Visual Basic .NET",
				"JavaScript", "SQL", "PHP", "Objective-C", "Groovy",
				"Assembly language", "Delphi/Object Pascal", "Go", "Ruby",
				"Swift", "Visual Basic", "MATLAB", "R", "Perl", "SAS", "D",
				"PL/SQL", "Dart", "F#", "Transact-SQL", "ABSP", "Scratch",
				"TypeScript", "Scala", "COBOL", "Lisp", "Rust", "Fortran",
				"Ada", "Julia", "Kotlin", "ActionScript", "RPG", "Logo",
				"Lua", "Prolog", "Scheme", "PostScript", "LabVIEW",
				"VBScript", "Bash", "PL/I", "MS-DOS batch", "Haskell"
		);
	}

	public ListModel<String> getContinent() {
		return continent;
	}

	public List<String> getProglangs() {
		return proglangs;
	}

	public String getResultContinent() {
		return resultContinent;
	}

	public void setResultContinent(String resultContinent) {
		this.resultContinent = resultContinent;
	}

	public List<String> getResultLangs() {
		return resultLangs;
	}

	public void setResultLangs(List<String> resultLangs) {
		this.resultLangs = resultLangs;
	}

	@Command
	public void show() {
		Clients.log(resultContinent + " / " + String.join(", ", resultLangs));
	}
}
