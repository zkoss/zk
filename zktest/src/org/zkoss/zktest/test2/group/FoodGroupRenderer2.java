package org.zkoss.zktest.test2.group;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * For F60-ZK-701 test case
 * @author jumperchen
 *
 */
public class FoodGroupRenderer2 implements ListitemRenderer, java.io.Serializable {
    public void render(Listitem row, java.lang.Object obj) {
        if (row instanceof Listgroup) {
            row.setLabel(obj.toString());
        } else {
            Object[] data = (Object[]) obj;
            row.appendChild(new Listcell(data[0].toString()));
            row.appendChild(new Listcell(data[1].toString()));
            row.appendChild(new Listcell(data[2].toString()));
            row.appendChild(new Listcell(data[3].toString()));
            row.appendChild(new Listcell(data[4].toString()));
        }
    }
};
