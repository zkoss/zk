/* B103_ZK_5631VM.java

        Purpose:
                
        Description:
                
        History:
                Tue Dec 23 17:17:54 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.math.BigDecimal;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class B103_ZK_5631VM {
    BigDecimal maxpos;
    BigDecimal curpos;

    @Init
    public void init() {
        curpos = new BigDecimal("1000");
        maxpos = new BigDecimal("1000");
    }

    public BigDecimal getMaxpos() {
        return maxpos;
    }

    public void setMaxpos(BigDecimal maxpos) {
        this.maxpos = maxpos;
    }

    public BigDecimal getCurpos() {return curpos;}

    public void setCurpos(BigDecimal curpos) {
        this.curpos = curpos;
    }
}
