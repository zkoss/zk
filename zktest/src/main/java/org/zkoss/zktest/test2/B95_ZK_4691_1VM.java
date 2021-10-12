package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

public class B95_ZK_4691_1VM {

    static int count;
    @Init
    public void init(){
        Clients.log("init >>> " + (++count));
    }
}
