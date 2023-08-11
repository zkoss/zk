/* F100_ZK_5213Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Jul 19 16:56:07 CST 2023, Created by jamsonchan

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.mesg.Messages;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.za11y.mesg.MZa11y;

public class F100_ZK_5213Test extends WebDriverTestCase {
    @Test
    public void bandbox_test() {
        connect();
        String slt = "@bandbox > .z-bandbox-icon.z-icon-search";
        test_(slt, MZa11y.BANDBOX_SEARCH);
    }
    @Test
    public void borderlayout_test() {
        connect();
        String slt = ".z-north-header .z-borderlayout-icon.z-icon-angle-double-up";
        test_(slt, MZa11y.LAYOUTREGION_NORTH, MZa11y.LAYOUTREGION_COLLAPSE);
        click_(slt);

        slt = ".z-north-collapsed .z-borderlayout-icon.z-icon-angle-double-down";
        test_(slt, MZa11y.LAYOUTREGION_NORTH, MZa11y.LAYOUTREGION_EXPAND);
        click_(slt);

        slt = ".z-west-header .z-borderlayout-icon.z-icon-angle-double-left";
        test_(slt, MZa11y.LAYOUTREGION_WEST, MZa11y.LAYOUTREGION_COLLAPSE);
        click_(slt);

        slt = ".z-west-collapsed .z-borderlayout-icon.z-icon-angle-double-right";
        test_(slt, MZa11y.LAYOUTREGION_WEST, MZa11y.LAYOUTREGION_EXPAND);
        click_(slt);

        slt = ".z-east-header .z-borderlayout-icon.z-icon-angle-double-right";
        test_(slt, MZa11y.LAYOUTREGION_EAST, MZa11y.LAYOUTREGION_COLLAPSE);
        click_(slt);

        slt = ".z-east-collapsed .z-borderlayout-icon.z-icon-angle-double-left";
        test_(slt, MZa11y.LAYOUTREGION_EAST, MZa11y.LAYOUTREGION_EXPAND);
        click_(slt);

        slt = ".z-south-header .z-borderlayout-icon.z-icon-angle-double-down";
        test_(slt, MZa11y.LAYOUTREGION_SOUTH, MZa11y.LAYOUTREGION_COLLAPSE);
        click_(slt);

        slt = ".z-south-collapsed .z-borderlayout-icon.z-icon-angle-double-up";
        test_(slt, MZa11y.LAYOUTREGION_SOUTH, MZa11y.LAYOUTREGION_EXPAND);
        click_(slt);
    }
    @Test
    public void calendar_test() {
        connect();
        String slt = ".z-calendar .z-icon-angle-left";
        test_(slt, MZa11y.CALENDAR_ANGLE_LEFT);

        slt = ".z-calendar .z-icon-angle-right";
        test_(slt, MZa11y.CALENDAR_ANGLE_RIGHT);
    }
    @Test
    public void cascader_test() {
        connect();
        String slt = ".z-cascader .z-cascader-icon.z-icon-caret-down";
        test_(slt, MZa11y.CASCADER_CARET_DOWN);
    }
    @Test
    public void chosenBox_test() {
        connect();
        String slt = ".z-chosenbox .z-chosenbox-icon.z-icon-times";
        test_(slt, MZa11y.CHOSENBOX_TIMES);
    }
    @Test
    public void coachmark_test() {
        connect();
        click_("@button:contains(Coachmark Test)");
        String slt = ".z-coachmark-icon.z-icon-times";
        test_(slt, MZa11y.COACHMARK_TIMES);
    }
    @Test
    public void colorbox_test() {
        connect();
        String slt = ".z-colorbox .z-colorbox-icon.z-icon-caret-down";
        test_(slt, MZa11y.COLORBOX_CARET_DOWN);

        click_(".z-colorbox .z-colorbox-button");
        click_(".z-colorbox-popup.z-colorpalette-popup.z-colorbox-shadow .z-colorbox-pickericon");
        slt = ".z-colorbox-popup.z-colorbox-shadow.z-colorpicker-popup .z-colorpicker .z-colorpicker-icon.z-icon-check";
        test_(slt, MZa11y.COLORPICKER_CHECK);
        click_(".z-colorbox-popup.z-colorbox-shadow.z-colorpicker-popup .z-colorpicker .z-button.z-colorpicker-button");
    }
    @Test
    public void combobox_test() {
        connect();
        String slt = ".z-combobox .z-combobox-icon.z-icon-caret-down";
        test_(slt, MZa11y.COMBOBOX_CARET_DOWN);
    }
    @Test
    public void combobutton_test() {
        connect();
        String slt = ".z-combobutton .z-combobutton-icon.z-icon-caret-down";
        test_(slt, MZa11y.COMBOBUTTON_CARET_DOWN);
    }
    @Test
    public void datebox_test() {
        connect();
        String slt = ".z-datebox .z-datebox-icon.z-icon-calendar";
        test_(slt, MZa11y.DATEBOX_CALENDAR);
    }
    @Test
    public void drawer_test() {
        connect();
        click_("@button:contains(Drawer Button)");
        String slt = ".z-drawer.z-drawer-bottom.z-drawer-open .z-icon-times";
        test_(slt, MZa11y.DRAWER_TIMES);
    }
    @Test
    public void grid_test() {
        connect();
        // Detail
        String slt = ".z-grid .z-detail .z-detail-icon.z-icon-angle-down";
        test_(slt, MZa11y.DETAIL_ANGLE_DOWN);
        click_(slt);

        slt = ".z-grid .z-detail .z-detail-icon.z-icon-angle-right";
        test_(slt, MZa11y.DETAIL_ANGLE_RIGHT);
        click_(slt);

        String par = ".z-grid .z-column.z-column-sort";
        click_(par);
        slt = par + " .z-icon-caret-up";
        test_(slt, MZa11y.COLUMN_SORT_CARET_UP);

        click_(par);
        slt = par + " .z-icon-caret-down";
        test_(slt, MZa11y.COLUMN_SORT_CARET_DOWN);

        // Group
        slt = ".z-grid .z-group .z-icon-angle-right.z-group-icon-close";
        test_(slt, MZa11y.GROUP_ANGLE_RIGHT_GROUP_CLOSE);
        click_(slt);

        slt = ".z-grid .z-group .z-icon-angle-down.z-group-icon-open";
        test_(slt, MZa11y.GROUP_ANGLE_DOWN_GROUP_OPEN);
        click_(slt);
    }
    @Test
    public void listbox_test() {
        connect();
        String slt = ".z-listbox .z-listheader-checkable.z-listheader-checked .z-listheader-icon.z-icon-check";
        test_(slt, MZa11y.LISTHEADER_CHECK);

        slt = ".z-listbox .z-listbox-body .z-listitem.z-listitem-selected .z-listcell .z-listitem-checkable.z-listitem-checkbox .z-listitem-icon.z-icon-check";
        test_(slt, MZa11y.LISTITEM_CHECK);

        slt = ".z-listbox .z-listbox-body .z-listgroup.z-listgroup-open .z-icon-angle-down.z-listgroup-icon-open";
        test_(slt, MZa11y.LISTGROUP_ANGLE_DOWN_LISTGROUP_OPEN);
        click_(slt);

        slt = ".z-listbox .z-listbox-body .z-listgroup .z-icon-angle-right.z-listgroup-icon-close";
        test_(slt, MZa11y.LISTGROUP_ANGLE_RIGHT_LISTGROUP_CLOSE);
        click_(slt);

        slt = ".z-listbox .z-listbox-body .z-listitem-icon.z-icon-radio";
        test_(slt, MZa11y.LISTITEM_RADIO);
    }
    @Test
    public void menubar_test() {
        connect();
        String slt = ".z-menubar.z-menubar-horizontal.z-menubar-scroll .z-menubar-icon.z-icon-chevron-left";
        test_(slt, MZa11y.MENUBAR_CHEVRON_LEFT);

        slt = ".z-menubar.z-menubar-horizontal.z-menubar-scroll .z-menubar-icon.z-icon-chevron-right";
        test_(slt, MZa11y.MENUBAR_CHEVRON_RIGHT);

        slt = ".z-menubar.z-menubar-vertical .z-menu-icon.z-icon-caret-right";
        test_(slt, MZa11y.MENU_CARET_RIGHT);
        click_(slt);

        slt = ".z-menupopup.z-menupopup-shadow.z-menupopup-open .z-menu-content .z-menu-icon.z-icon-caret-right";
        test_(slt, MZa11y.MENU_CARET_RIGHT);
        mouseOver(jq(slt));
        slt = ".z-menupopup.z-menupopup-shadow.z-menupopup-open .z-menuitem-content .z-menuitem-icon.z-icon-check";
        test_(slt, MZa11y.MENUITEM_CHECK);
    }
    @Test
    public void organigram_test() {
        connect();
        String slt = ".z-organigram .z-orgnode-icon.z-icon-minus";
        test_(slt, MZa11y.ORGNODE_MINUS);
        click_(slt);

        slt = ".z-organigram .z-orgnode-icon.z-icon-plus";
        test_(slt, MZa11y.ORGNODE_PLUS);
    }
    @Test
    public void paging_test() {
        connect();
        String slt = ".z-paging .z-paging-icon.z-icon-angle-double-left";
        test_(slt, MZa11y.PAGING_ANGLE_DOUBLE_LEFT);

        slt = ".z-paging .z-paging-icon.z-icon-angle-double-right";
        test_(slt, MZa11y.PAGING_ANGLE_DOUBLE_RIGHT);

        slt = ".z-paging .z-paging-icon.z-icon-angle-left";
        test_(slt, MZa11y.PAGING_ANGLE_LEFT);

        slt = ".z-paging .z-paging-icon.z-icon-angle-right";
        test_(slt, MZa11y.PAGING_ANGLE_RIGHT);
    }
    @Test
    public void panel_test() {
        connect();
        String par = ".z-panel.z-panel-noborder.z-panel-noframe .z-icon-";
        test_(par + "times", MZa11y.PANEL_TIMES);

        test_(par + "minus", MZa11y.PANEL_MINUS);

        test_(par + "expand", MZa11y.PANEL_EXPAND);
        click_(par + "expand");

        test_(par + "compress", MZa11y.PANEL_COMPRESS);
        click_(par + "compress");

        test_(par + "angle-up", MZa11y.PANEL_ANGLE_UP);
        click_(par + "angle-up");

        test_(par + "angle-down", MZa11y.PANEL_ANGLE_DOWN);
        click_(par + "angle-down");
    }
    @Test
    public void pdfviewer_test() {
        connect();
        String par = ".z-pdfviewer .z-pdfviewer-toolbar [id$=toolbar-";
        test_(par + "first]", MZa11y.PDFVIEWER_FW_ANGLE_DOUBLE_LEFT);
        test_(par + "last]", MZa11y.PDFVIEWER_FW_ANGLE_DOUBLE_RIGHT);
        test_(par + "prev]", MZa11y.PDFVIEWER_FW_ANGLE_LEFT);
        test_(par + "next]", MZa11y.PDFVIEWER_FW_ANGLE_RIGHT);
        test_(par + "zoom-out]", MZa11y.PDFVIEWER_FW_MINUS);
        test_(par + "zoom-in]", MZa11y.PDFVIEWER_FW_PLUS);
        test_(par + "fullscreen]", MZa11y.PDFVIEWER_FW_EXPAND);
        test_(par + "rotate-left]", MZa11y.PDFVIEWER_FW_ROTATE_LEFT);
        test_(par + "rotate-right]", MZa11y.PDFVIEWER_FW_ROTATE_RIGHT);
    }
    @Test
    public void searchBox_test() {
        connect();
        String slt = ".z-searchbox .z-searchbox-icon.z-icon-caret-down";
        test_(slt, MZa11y.SEARCHBOX_CARET_DOWN);
    }
    @Test
    public void spinner_test() {
        connect();
        String par = ".z-spinner .z-icon-";
        test_(par + "angle-up", MZa11y.SPINNER_ANGLE_UP);
        test_(par + "angle-down", MZa11y.SPINNER_ANGLE_DOWN);
    }
    @Test
    public void doublespinner_test() {
        connect();
        String par = ".z-doublespinner .z-icon-";
        test_(par + "angle-up", MZa11y.DOUBLESPINNER_ANGLE_UP);
        test_(par + "angle-down", MZa11y.DOUBLESPINNER_ANGLE_DOWN);
    }
    @Test
    public void splitLayout_test() {
        connect();
        String par = ".z-splitlayout .z-splitlayout-splitter.z-splitlayout-splitter-";
        String slt = par + "horizontal.z-splitlayout-splitter-draggable .z-splitlayout-splitter-icon.z-icon-caret-right";
        test_(slt, MZa11y.SPLITLAYOUT_CARET_RIGHT);
        slt = par + "vertical.z-splitlayout-splitter-draggable .z-splitlayout-splitter-icon.z-icon-caret-up";
        test_(slt, MZa11y.SPLITLAYOUT_CARET_UP);
    }
    @Test
    public void splitter_test() {
        connect();
        String slt = ".z-hbox .z-splitter-icon.z-icon-caret-left";
        test_(slt, MZa11y.SPLITTER_CARET_LEFT);
        click_(slt);

        slt = ".z-hbox .z-splitter-icon.z-icon-caret-right";
        test_(slt, MZa11y.SPLITTER_CARET_RIGHT);

        slt = ".z-vbox .z-splitter-icon.z-icon-caret-up";
        test_(slt, MZa11y.SPLITTER_CARET_UP);
        click_(slt);

        slt = ".z-vbox .z-splitter-icon.z-icon-caret-down";
        test_(slt, MZa11y.SPLITTER_CARET_DOWN);
    }
    @Test
    public void tabbox_test() {
        connect();
        String slt = ".z-tabbox.z-tabbox-top .z-icon-times.z-tab-icon";
        test_(slt, MZa11y.TAB_TIMES_TAB);
    }
    @Test
    public void timebox_test() {
        connect();
        String slt = ".z-timebox .z-icon-angle-up";
        test_(slt, MZa11y.TIMEBOX_ANGLE_UP);

        slt = ".z-timebox .z-icon-angle-down";
        test_(slt, MZa11y.TIMEBOX_ANGLE_DOWN);
    }
    @Test
    public void timepicker_test() {
        connect();
        String slt = ".z-timepicker.z-timepicker-readonly .z-timepicker-icon.z-icon-clock-o";
        test_(slt, MZa11y.TIMEPICKER_CLOCK_O);
    }
    @Test
    public void toast_test() {
        connect();
        click_("@button:contains(Toast Test)");
        String slt = ".z-messagebox-window.z-window.z-window-modal.z-window-shadow .z-icon-times";
        test_(slt, MZa11y.TOAST_TIMES);
    }
    @Test
    public void toolbar_test() {
        connect();
        String slt = ".z-toolbar.z-toolbar-overflowpopup.z-toolbar-overflowpopup-on .z-toolbar-overflowpopup-button.z-icon-ellipsis-h.z-icon-fw";
        test_(slt, MZa11y.TOOLBAR_ELLIPSIS_H_FW);
    }
    @Test
    public void tree_test() {
        connect();
        String slt = ".z-tree .z-tree-header .z-treecol .z-treecol-icon.z-icon-check";
        test_(slt, MZa11y.TREECOL_CHECK);

        slt = ".z-tree .z-tree-body .z-treerow-icon.z-icon-check";
        test_(slt, MZa11y.TREECELL_CHECK);

        slt = ".z-tree .z-tree-body .z-icon-caret-down.z-tree-open";
        test_(slt, MZa11y.TREECELL_CARET_DOWN_TREE_OPEN);
        click_(slt);

        slt = ".z-tree .z-tree-body .z-icon-caret-right.z-tree-close";
        test_(slt, MZa11y.TREECELL_CARET_RIGHT_TREE_CLOSE);
        click_(slt);

        slt = ".z-tree .z-tree-body .z-treerow-icon.z-icon-radio";
        test_(slt, MZa11y.TREECELL_RADIO);
    }
    @Test
    public void window_test() {
        connect();
        String par = ".z-window.z-window-noborder.z-window-embedded .z-icon-";
        test_(par + "times", MZa11y.WINDOW_TIMES);
        test_(par + "minus", MZa11y.WINDOW_MINUS);
        test_(par + "expand", MZa11y.WINDOW_EXPAND);
        click_(par + "expand");
        test_(par + "compress", MZa11y.WINDOW_COMPRESS);
    }
    public String getTtp(String query){
        return jq(query).attr("title");
    }
    public String getMsg(int msgId){
        return Messages.get(msgId);
    }
    public String getMsg(int msgId1, int msgId2){
        return Messages.get(msgId1) + " " + Messages.get(msgId2);
    }
    public void test_(String selector, int messageId){
        String tooltip = getTtp(selector), message = getMsg(messageId);
        assertEquals(tooltip, message);
    }
    public void test_(String selector, int messageId1, int messageId2){
        String tooltip = getTtp(selector), message = getMsg(messageId1, messageId2);
        assertEquals(tooltip, message);
    }
    public void click_(String selector){
        click(jq(selector));
        waitResponse(true);
    }
}
