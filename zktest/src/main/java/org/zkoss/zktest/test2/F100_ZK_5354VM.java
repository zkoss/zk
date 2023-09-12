/* F100_ZK_5354VM.java

        Purpose:
                
        Description:
                
        History:
                Wed Sep 06 15:27:17 CST 2023, Created by rebeccalai

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Init;

public class F100_ZK_5354VM {
    private static int initCount = 0;

    public int getInitCount() {
        return initCount;
    }

    @Init
    public void init() {
        initCount++;
    }
}
