/* groupbox.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 12:47:07     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out, skipper) {	
	var	uuid = this.uuid,
		cap = this.caption,
		title = this.getTitle();
		title = title && !cap ? zUtl.encodeXML(title) :  null;
	
	if(this._isDefault()) 
		out.push('<fieldset');
	else
		out.push('<div');
	
	out.push(' ', this.domAttrs_(), '>');
	
	if (title || cap) {
		if(!this._isDefault()) 
			out.push('<div');		
		else 
			out.push('<legend');

		out.push(' id="', uuid, '-header" class="', this.$s('header'), (this._closable? '': ' ' +  this.$s('readonly')),'">');
		if (cap)
			cap.redraw(out);
		else
			out.push('<div id="', uuid,'-title" class="', this.$s('title'), 
					'"><span class="', this.$s('title-content'), '">', title, '</span></div>');
		
		if(!this._isDefault()) 
			out.push('</div>');		
		else 
			out.push('</legend>');
	}
	
	this._redrawCave(out, skipper);
	
	if(this._isDefault())
		out.push('</fieldset>');
	else {
		// classicblue is deprecated and 
		// shadow not used in breeze, sapphire and silvertail,
		out.push('</div>');
	}	
}