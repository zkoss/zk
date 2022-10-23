/* B80_ZK_2983.java

	Purpose:
		
	Description:
		
	History:
		Tue, Jan  5, 2016 10:20:55 AM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zul.*;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
/**
 * 
 * @author Sefi
 */
public class B80_ZK_2983 {

    private String simpleText;

    private List<String> persons;
    private Set<String> selectedPersons;

    public B80_ZK_2983() {
        simpleText = "Value 0";

        persons = new ArrayList<String>();
        persons.add("Alice");
        persons.add("Bob");

        selectedPersons = new LinkedHashSet<String>();
        selectedPersons.add("Alice");
    }

    @Command("select")
    @NotifyChange({"simpleText","selectedPersons"})
    public void select(@BindingParam("divId") Div divId) {

        simpleText = "Value 1";

        selectedPersons = new LinkedHashSet<String>();
        selectedPersons.add("Bob");

        divId.invalidate(); // corrupt the selection in the chosenBox. The value is correct but not visible anymore.
     }

    // does nothing but the selection in the chosenbox reappears
    @Command("dummy")
    @NotifyChange({"simpleText","selectedPersons"})
    public void dummy(@BindingParam("divId") Div divId) {
            // does nothing except notifications
    }

    public String getSimpleText() {
        return simpleText;
    }

    public void setSimpleText(String simpleText) {
        this.simpleText = simpleText;
    }

    public List<String> getPersons() {
        return persons;
    }

    public void setPersons(List<String> persons) {
        this.persons = persons;
    }

    public Set<String> getSelectedPersons() {
        return selectedPersons;
    }

    public void setSelectedPersons(Set<String> selectedPersons) {
        this.selectedPersons = selectedPersons;
    }
}
