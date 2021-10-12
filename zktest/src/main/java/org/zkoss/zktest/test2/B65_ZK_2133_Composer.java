package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ext.Selectable;

public class B65_ZK_2133_Composer extends GenericForwardComposer<Component> {

    private static final long serialVersionUID = -1857944359935194138L;
    Listbox lb1;
    Listbox lb2;
    ListModelList<DataHolder> s1;
    ListModelList<DataHolder> s2;

    private class DataHolder {
        String data;

        public DataHolder(String data) {
            this.data = data;
        }

        public String toString() {
            return data;
        }

        @Override
        public boolean equals(Object obj) {
            boolean ret = false;
            if (obj instanceof DataHolder) {
                ret = data.equals(((DataHolder) obj).data);
            }
            return ret;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((data == null) ? 0 : data.hashCode());
            return result;
        }

    }

    private List<DataHolder> createList() {
        List<DataHolder> l1 = new ArrayList<DataHolder>();
        for (int i = 0; i < 10; i++) {
            l1.add(new DataHolder("d" + i));
        }
        return l1;
    }

    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        s1 = new ListModelList<DataHolder>(createList());
        s2 = new ListModelList<DataHolder>(createList());
        s1.setMultiple(true);
        s2.setMultiple(true);
        lb1.setModel(s1);
        lb2.setModel(s2);
    }

    public void onClick$btn(Event e) throws InterruptedException {
        Set<DataHolder> l1 = ((Selectable) lb1.getModel()).getSelection();
        s2.setSelection(l1);
        
    }
}
