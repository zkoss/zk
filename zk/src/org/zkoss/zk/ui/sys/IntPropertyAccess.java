/* IntPropertyAccess.java

    Purpose:
        
    Description:
        
    History:
        Tue, Feb  2, 2016  3:13:59 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.sys;

/**
 * 
 * @author Sefi
 */
public abstract class IntPropertyAccess implements PropertyAccess<Integer> {
    public Class<Integer> getType() {
        return Integer.TYPE;
    }
}
