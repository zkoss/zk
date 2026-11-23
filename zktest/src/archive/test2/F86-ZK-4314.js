zk.afterLoad('zul.wgt', function() {
    var globalDragListener = {
        onStartDrag : function(drag, event) {
            var draggedWgt = drag.origin.control;
            if (draggedWgt._isRodDndEnabled()) {
                draggedWgt.fire("onStartDrag", {}, {toServer: true});
            }
        }
    };
    zWatch.listen({
        onStartDrag: globalDragListener
    });

    var xWidget = {};
    zk.override(zk.Widget.prototype, xWidget, {
        _isRodDndEnabled: function() {
            for(var wgt = this; wgt; wgt = wgt.parent) {
                if(wgt._rodDndEnabled !== undefined) {
                    return wgt._rodDndEnabled;
                }
            }
            return false;
        },
    });//zk.override
});//zk.afterLoad