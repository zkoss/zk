/* ContainerWidget.js

	Purpose:

	Description:

	History:
		Thu Feb 13 12:31:44     2014, Created by NoahHuang

Copyright (C) 2014 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The container related widgets, such as window, groupbox, tabpanel, panelchildren.
 */
//zk.$package('zul');

/**
 * 	A ContainerWidget.
 * 	
 * <p>If container widget has client attribute 'scrollable', it will listen <code>onScroll</code> event. 
 * 
 *  @see zul.wnd.Window
 *  @see zul.wnd.Panelchildren
 *  @see zul.wgt.Groupbox
 *  @see zul.tab.Tabpanel
 *
 */
zul.ContainerWidget = zk.$extends(zul.Widget, {
	bind_ : function() {
		this.$supers(zul.ContainerWidget, 'bind_', arguments);

		// B70-ZK-2069: some widget need fire onScroll event, which has
		// characteristic of container
		if (jq(this).data('scrollable')) {
			this.domListen_(this.getCaveNode(), 'onScroll');
		}
	},

	_doScroll : function() {
		if (jq(this).data('scrollable'))
			zWatch.fireDown('onScroll', this);
	},

	unbind_ : function() {
		if (jq(this).data('scrollable')) {
			this.domUnlisten_(this.getCaveNode(), 'onScroll');
		}
		this.$supers(zul.ContainerWidget, 'unbind_', arguments);
	}
});
