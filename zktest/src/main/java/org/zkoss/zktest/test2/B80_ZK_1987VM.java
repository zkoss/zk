/** B80_ZK_1987VM.java.

 Purpose:

 Description:

 History:
 16:00:00 PM Jul 15, 2015, Created by jameschu

 Copyright (C) 2015 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jameschu
 *
 */
public class B80_ZK_1987VM {
    private List<Element> elements;
    private Element selectedElement;
    private ComboitemRenderer renderer;

    @Init
    public void init() {
        renderer = new Renderer();
        selectedElement = new Element(2, "foo");
        elements = new ArrayList<Element>();
        elements.add(new Element(1, "foo"));
        elements.add(selectedElement);
        elements.add(new Element(3, "bar"));
    }

    public Element getSelectedElement() {
        return selectedElement;
    }

    public void setSelectedElement(Element selectedElement) {
        this.selectedElement = selectedElement;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public ComboitemRenderer getRenderer() {
        return renderer;
    }

    public class Renderer implements ComboitemRenderer<Element> {

        private static final int PER_ANCESTOR_INDENT_IN_PX = 10;

        public void render(final Comboitem comboitem, final Element element, final int index) throws Exception {
            comboitem.setLeft((element.getId() * PER_ANCESTOR_INDENT_IN_PX) + "px"); // indentation
            comboitem.setLabel(element.getLabel());
            comboitem.setTooltiptext(element.getLabel());
        }
    }

    public class Element {

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
}