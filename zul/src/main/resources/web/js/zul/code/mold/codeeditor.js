/* codeeditor.js

        Purpose:
                
        Description:
                
        History:
                Thu Jun 18 18:06:28 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
function codeeditor$mold$(out) {
	out.push('<div', this.domAttrs_(), '><div id="', this.uuid,
			'-cave" class="', this.$s('cave'), '"></div>',
			'<div id="', this.uuid, '-status" class="', this.$s('status'),
			'" aria-live="polite">Press Escape to toggle the Tab key between indentation and focus navigation.</div>',
			'</div>');
}
