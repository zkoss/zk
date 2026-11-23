/** B80_ZK_1987.java.

 Purpose:

 Description:

 History:
 16:00:00 PM Jul 15, 2015, Created by jameschu

 Copyright (C) 2015 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jameschu
 *
 */
public class B80_ZK_1987 extends SelectorComposer<Component> {
        private final List<Element> elements;
        private Element selectedElement;

        public B80_ZK_1987() {
            selectedElement = new Element(2, "foo");
            elements = new ArrayList<Element>();
            elements.add(new Element(1, "foo"));
            elements.add(selectedElement);
            elements.add(new Element(3, "bar"));
        }

        @Override
        public ComponentInfo doBeforeCompose(final Page page, final Component parent, final ComponentInfo compInfo) {
            page.setAttribute("renderer", new ElementComboitemRenderer());
            return super.doBeforeCompose(page, parent, compInfo);
        }

        @Override
        public void doAfterCompose(final Component comp) throws Exception {
            // nothing
        }

        public List<Element> getElements() {
            return elements;
        }

        public Element getSelectedElement() {
            return selectedElement;
        }

        public void setSelectedElement(final Element element) {
            this.selectedElement = element;
        }

        public static class Element {
            private final int id;
            private final String label;

            public Element(final int id, final String label) {
                this.id = id;
                this.label = label;
            }

            public int getId() {
                return this.id;
            }

            public String getLabel() {
                return this.label;
            }

            @Override
            public String toString() {
                return "Element(id=" + this.id + ", label=" + this.label + ")";
            }
        }

        public static class ElementComboitemRenderer implements ComboitemRenderer<Element> {

            private static final int PER_ANCESTOR_INDENT_IN_PX = 10;

            public void render(final Comboitem comboitem, final Element element, final int index) throws Exception {
                comboitem.setLeft((element.getId() * PER_ANCESTOR_INDENT_IN_PX) + "px"); // indentation
                comboitem.setLabel(element.getLabel());
                comboitem.setTooltiptext(element.getLabel());
            }
        }
    }