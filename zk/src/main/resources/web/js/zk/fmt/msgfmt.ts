/* msgfmt.ts

	Purpose:
		
	Description:
		
	History:
		Sat Jan 17 12:35:34     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
export let Text = {
	format(msg: string): string {
		var i = 0, sb = '';
		for (var j = 0, len = msg.length, cc, k; j < len; ++j) {
			cc = msg.charAt(j);
			if (cc == '\\') {
				if (++j >= len) break;
				sb += msg.substring(i, j);
				cc = msg.charAt(j);
				switch (cc) {
				case 'n': cc = '\n'; break;
				case 't': cc = '\t'; break;
				case 'r': cc = '\r'; break;
				}
				sb += cc;
				i = j + 1;
			} else if (cc == '{') {
				k = msg.indexOf('}', j + 1);
				if (k < 0) break;
				sb += msg.substring(i, j)
					+ arguments[zk.parseInt(msg.substring(j + 1, k)) + 1];
				i = j = k + 1;
			}
		}
		if (sb) sb += msg.substring(i);
		return sb || msg;
	},
	/**
	 * Formatting a filesize depending on its value:
	 * >= 1024 GB: 1.0 TB,
	 * >= 1024 MB: 1.0 GB,
	 * >= 1024 KB: 1.0 MB,
	 * >= 1024 B : 1.0 KB,
	 * else: bytes. Decimal point is determined by System language.
	 */
	formatFileSize(bytes: number): string {
		var divider = 1024;

		if (Math.abs(bytes) < divider) {
			return bytes + msgzk.BYTES;
		}

		var units = [msgzk.KBYTES, msgzk.MBYTES, msgzk.GBYTES, msgzk.TBYTES],
			unit = -1;

		do {
			bytes /= divider;
			++unit;
		} while (Math.abs(bytes) >= divider && unit < units.length - 1);

		return bytes.toFixed(1) + ' ' + units[unit];
	}
};
zk.fmt.Text = Text;