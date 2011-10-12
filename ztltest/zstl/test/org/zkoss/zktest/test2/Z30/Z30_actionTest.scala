/* Z30-actionTest.scala

	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 16:23:10 TST 2011, Created by TonyQ

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2.Z30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 *
 * @author jumperchen
 */
@Tags(tags = "Z30-action.zul,C,E,Window,Textbox")
class Z30_actionTest extends ZTL4ScalaTestCase {
	def testCase() = {
		val zscript = {
<window title="Test of JavaScript Utilities">
<script><![CDATA[
var comm = {
	getData: function (args, start) {
		var len = args.length, data;
		if (len > start) {
			data = [];
			for (var j = start; j < len; ++j)
				data[j - start] = args[j];
		}
		return data;
	},
	sendUser: function (element) {
		zAu.send(new zk.Event(zk.Widget.$(element), 'onUser', comm.getData(arguments, 1)));
	},
	sendEvent: function (element, evtnm) {
		zAu.send(new zk.Event(zk.Widget.$(element), evtnm, comm.getData(arguments, 2)));
	}
};

]]></script>
	<vbox>
You can click each link, then you should see that the label is the same as that link.
		<html onUser='l.value ="onUser "+org.zkoss.lang.Objects.toString(event.data)'><![CDATA[
			<a id="a1" href="javascript:;" onclick="comm.sendUser(this);">onUser with null</a>
			<a id="a2"  href="javascript:;" onclick="comm.sendUser(this, 'One');">onUser with One</a>
			<a id="a3" href="javascript:;" onclick="comm.sendUser(this, 'One', 'Two');">onUser with [One, Two]</a>
			<a id="a4" href="javascript:;" onclick="comm.sendEvent(this, 'onUser', '1', '2');">onUser with [1, 2]</a>
		]]></html>
		<separator/>
		<label id="l"/>
	</vbox>
</window>
		}
		runZTL(zscript, () => {
		  	click(jq("#a1"));
  		  	waitResponse
  		  	verifyEquals(widget(jq("$l")).get("value"),"onUser null");
  		  	
  		  	click(jq("#a2"));
  		  	waitResponse
  		  	verifyEquals(widget(jq("$l")).get("value"),"onUser [One]");
  		  	
  		  	click(jq("#a3"));
  		  	waitResponse
			verifyEquals(widget(jq("$l")).get("value"),"onUser [One, Two]");
  		  	
  		  	click(jq("#a4"));
  		  	waitResponse
  		  	verifyEquals(widget(jq("$l")).get("value"),"onUser [1, 2]");
		})
	}
}
