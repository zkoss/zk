/**
 * A simple double spinner constraint.
 * @disable(zkgwt)
 */
zul.inp.SimpleDoubleSpinnerConstraint = zk.$extends(zul.inp.SimpleConstraint, {
	$define: {
		/** Returns the minimum value.
		 * @return double
		 */
		/** Set the minimum value.
		 * @param double min
		 */
		min: _zkf = function(){},
		/** Returns the maximum value.
		 * @return double
		 */
		/** Set the maximum value.
		 * @param double max
		 */
		max: _zkf
	},
    parseConstraint_: function(cst){
    	var cstList = cst.replace(/ +/g,' ').split(/[, ]/),
    		len = cstList.length,
    		isSpinner;
    	for(var i=0; i<len+1; i++){
    		if (cstList[i] == 'min') {
    			this._min = cstList[++i] * 1;
    			isSpinner = true;
    		} else if (cstList[i] == 'max') {
    			this._max = cstList[++i] * 1;
    			isSpinner = true;
    		}
    	}
    	if (isSpinner) return;
    	else
    		return this.$supers('parseConstraint_', arguments);
    },
    validate: function (wgt, val) {
    	switch (typeof val) {
    		case 'number':
    			if ((this._max && val > this._max) || (this._min && val < this._min)) {
    				var msg = msgzul.OUT_OF_RANGE + ': ';
    				msg += "(" + this._min != null ? this._max != null ?
    						this._min + " ~ " + this._max: ">= " + this._min: "<= " + this._max + ")";
    			}	
    	}
    	if(msg)
    		return msg;
    	else
    		return this.$supers('validate',arguments);
    }
});