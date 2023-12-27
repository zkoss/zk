// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-nocheck
/* re.js

	Purpose:
		
	Description:
		
	History:
		11:16 AM 2023/12/25, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/

'use strict';

module.exports = {

	toRegexp: function( str ) {
		var pair = str.split( '/' );
		return new RegExp( pair[ 0 ], pair[ 1 ] );
	},

	any: function( input, regexps ) {

		for( var i = 0; i < regexps.length; i++ ) {
			if( regexps[ i ].exec( input ) )
				return true;
		}

		return false;
	},
};