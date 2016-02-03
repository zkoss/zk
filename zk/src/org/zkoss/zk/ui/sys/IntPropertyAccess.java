/* IntPropertyAccess.java

    Purpose:
        
    Description:
        
    History:
        Tue, Feb 2, 2016 3:13:59 PM, Created by Sefi

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

/**
 * An int property class.
 * @author Sefi
 * @since 8.0.1.1
 */
public abstract class IntPropertyAccess implements PropertyAccess<Integer> {
    public Class<Integer> getType() {
        return Integer.TYPE;
    }
}
