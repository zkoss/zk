/* F100_ZK_5503_Composer.java

        Purpose:
                
        Description:
                
        History:
                Thu Jul 27 14:13:48 CST 2023, Created by jamsonchan

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.zul.Nav;
import org.zkoss.zkmax.zul.Navitem;
import org.zkoss.zkmax.zul.Orgnode;
import org.zkoss.zul.A;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Combobutton;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treecol;
import org.zkoss.zul.Treefooter;
import org.zkoss.zul.impl.LabelImageElement;

public class F100_ZK_5503_Composer extends SelectorComposer {
    @Wire
    A a;
    @Wire
    Auxheader auxheader;
    @Wire
    Column column;
    @Wire
    Footer footer;
    @Wire
    Button button;
    @Wire
    Caption caption;
    @Wire
    Checkbox checkbox;
    @Wire
    Combobutton combobutton;
    @Wire
    Comboitem comboitem;
    @Wire
    Fileupload fileupload;
    @Wire
    Listheader listheader;
    @Wire
    Listcell listcell;
    @Wire
    Listfooter listfooter;
    @Wire
    Menu menu;
    @Wire
    Menuitem menuitem;
    @Wire
    Nav nav;
    @Wire
    Navitem navitem;
    @Wire
    Orgnode orgnode;
    @Wire
    Radio radio;
    @Wire
    Tab tab;
    @Wire
    Toolbarbutton toolbarbutton;
    @Wire
    Treecol treecol;
    @Wire
    Treecell treecell;
    @Wire
    Treefooter treefooter;

    @Listen("#test1")
    public void test1() {
        LabelImageElement[] lies = new LabelImageElement[] {a, auxheader, column, footer, button, caption, checkbox, combobutton, comboitem, fileupload, listheader, listcell, listfooter, menu, menuitem, nav, navitem, orgnode, radio, tab, toolbarbutton, treecol, treecell, treefooter};
        for (LabelImageElement lie : lies) {
            lie.setIconSclasses(new String[]{"z-icon-plus", "z-icon-minus"});
            lie.setIconTooltips(new String[]{"1", "1"});
        }
    }

    @Listen("#test2")
    public void test2() {
        LabelImageElement[] lies = new LabelImageElement[] {a, auxheader, column, footer, button, caption, checkbox, combobutton, comboitem, fileupload, listheader, listcell, listfooter, menu, menuitem, nav, navitem, orgnode, radio, tab, toolbarbutton, treecol, treecell, treefooter};
        for (LabelImageElement lie : lies) {
            lie.setIconSclasses(new String[] {"z-icon-plus", "z-icon-minus"});
            lie.setIconTooltips(new String[] {"2"});
        }
    }

    @Listen("#test3")
    public void test3() {
        LabelImageElement[] lies = new LabelImageElement[] {a, auxheader, column, footer, button, caption, checkbox, combobutton, comboitem, fileupload, listheader, listcell, listfooter, menu, menuitem, nav, navitem, orgnode, radio, tab, toolbarbutton, treecol, treecell, treefooter};
        for (LabelImageElement lie : lies) {
            lie.setIconSclasses(new String[] {"z-icon-plus"});
            lie.setIconTooltips(new String[] {"3", "3"});
        }
    }

    @Listen("#test4")
    public void test4() {
        LabelImageElement[] lies = new LabelImageElement[] {a, auxheader, column, footer, button, caption, checkbox, combobutton, comboitem, fileupload, listheader, listcell, listfooter, menu, menuitem, nav, navitem, orgnode, radio, tab, toolbarbutton, treecol, treecell, treefooter};
        for (LabelImageElement lie : lies) {
            lie.setIconSclasses(new String[] {null, null, null, null, null, null, null});
            lie.setIconTooltips(new String[] {null, null, null, null});
        }
    }

    @Listen("#test5")
    public void test5() {
        LabelImageElement[] lies = new LabelImageElement[] {a, auxheader, column, footer, button, caption, checkbox, combobutton, comboitem, fileupload, listheader, listcell, listfooter, menu, menuitem, nav, navitem, orgnode, radio, tab, toolbarbutton, treecol, treecell, treefooter};
        for (LabelImageElement lie : lies) {
            lie.setIconSclasses(new String[] {"z-icon-plus", null, "z-icon-minus"});
            lie.setIconTooltips(new String[] {null, "123", ""});
        }
    }
}
