/** B80_ZK_3143.java.

 Purpose:

 Description:

 History:
 		Tue Mar 15 17:12:46 CST 2016, Created by jameschu

 Copyright (C) 2015 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jameschu
 *
 */
public class B80_ZK_3143 extends SelectorComposer<Window> {
	@Wire
	Listbox lb;

	public void doAfterCompose(final Window win) throws Exception {
		super.doAfterCompose(win);
		Map<Integer, B80_ZK_3143_Object> DUMMY_PERSON_TABLE = new LinkedHashMap<Integer, B80_ZK_3143_Object>();
		int uuid = 0;
		B80_ZK_3143_Object person = null;
		person = new B80_ZK_3143_Object(++uuid, "Jabir", "Uilleag", false);
		DUMMY_PERSON_TABLE.put(person.getId(), person);

		person = new B80_ZK_3143_Object(++uuid, "Finbar", "Eetu", true);
		DUMMY_PERSON_TABLE.put(person.getId(), person);

		person = new B80_ZK_3143_Object(++uuid, "Brice", "Cainan", true);
		DUMMY_PERSON_TABLE.put(person.getId(), person);

		person = new B80_ZK_3143_Object(++uuid, "Issy", "Willy", true);
		DUMMY_PERSON_TABLE.put(person.getId(), person);

		person = new B80_ZK_3143_Object(++uuid, "Hugo ", "Wayne", false);
		DUMMY_PERSON_TABLE.put(person.getId(), person);

		person = new B80_ZK_3143_Object(++uuid, "Chelle", "Dinah", true);
		DUMMY_PERSON_TABLE.put(person.getId(), person);

		person = new B80_ZK_3143_Object(++uuid, "Lonnie", "Tarbox", true);
		DUMMY_PERSON_TABLE.put(person.getId(), person);

		person = new B80_ZK_3143_Object(++uuid, "Loraine", "Vassel", false);
		DUMMY_PERSON_TABLE.put(person.getId(), person);
		ListModelList model = new ListModelList<B80_ZK_3143_Object>(DUMMY_PERSON_TABLE.values());
		model.setMultiple(true);
		lb.setModel(model);
	}
}
