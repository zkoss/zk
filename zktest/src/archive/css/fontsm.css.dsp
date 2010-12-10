<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-combobox-rounded-inp,
.z-bandbox-rounded-inp,
.z-datebox-rounded-inp,
.z-timebox-rounded-inp,
.z-spinner-rounded-inp,
.z-combobox-inp,
.z-bandbox-inp,
.z-datebox-inp,
.z-timebox-inp,
.z-spinner-inp {
	height: 12px;
}
.z-combobox-rounded-inp {
	background-image: url(${c:encodeURL('/img/component/combobox-rounded-s.gif')});
}
.z-bandbox-rounded-inp {
	background-image: url(${c:encodeURL('/img/component/bandbox-rounded-s.gif')});
}
.z-datebox-rounded-inp {
	background-image: url(${c:encodeURL('/img/component/datebox-rounded-s.gif')});
}
.z-timebox-rounded-inp,
.z-spinner-rounded-inp {
	background-image: url(${c:encodeURL('/img/component/timebox-rounded-s.gif')});
}
.z-combobox-rounded input.z-combobox-rounded-text-invalid,
.z-bandbox-rounded input.z-bandbox-rounded-text-invalid,
.z-datebox-rounded input.z-datebox-rounded-text-invalid,
.z-timebox-rounded input.z-timebox-rounded-text-invalid,
.z-spinner-rounded input.z-spinner-rounded-text-invalid {
	background: #FFF url(${c:encodeURL('/img/component/redcombo-rounded-s.gif')}) repeat-x 0 0;
}
.z-combobox-rounded .z-combobox-rounded-text-invalid + i.z-combobox-rounded-btn-right-edge,
.z-bandbox-rounded .z-bandbox-rounded-text-invalid + i.z-bandbox-rounded-btn-right-edge,
.z-datebox-rounded .z-datebox-rounded-text-invalid + i.z-datebox-rounded-btn-right-edge,
.z-timebox-rounded .z-timebox-rounded-text-invalid + i.z-timebox-rounded-btn-right-edge,
.z-spinner-rounded .z-spinner-rounded-text-invalid + i.z-spinner-rounded-btn-right-edge {
	background-image: url(${c:encodeURL('/img/component/redcombo-rounded-s.gif')});
	background-position: 0 -22px;
}
i.z-combobox-rounded i.z-combobox-rounded-btn-right-edge-invalid,
i.z-bandbox-rounded i.z-bandbox-rounded-btn-right-edge-invalid,
i.z-datebox-rounded i.z-datebox-rounded-btn-right-edge-invalid,
i.z-timebox-rounded i.z-timebox-rounded-btn-right-edge-invalid,
i.z-spinner-rounded i.z-spinner-rounded-btn-right-edge-invalid {
	background-image: url(${c:encodeURL('/img/component/redcombo-rounded-s.gif')});
	background-position: 0 -22px;
}
.z-combobox .z-combobox-btn,
.z-bandbox .z-bandbox-btn,
.z-datebox .z-datebox-btn,
.z-timebox .z-timebox-btn,
.z-spinner .z-spinner-btn {
	height: 17px;
}
.z-combobox-rounded .z-combobox-rounded-btn,
.z-bandbox-rounded .z-bandbox-rounded-btn,
.z-datebox-rounded .z-datebox-rounded-btn,
.z-timebox-rounded .z-timebox-rounded-btn,
.z-spinner-rounded .z-spinner-rounded-btn {
	height: 22px;
	background-position: 0 -110px;	
}
.z-combobox-rounded .z-combobox-rounded-btn{	
	background-image: url(${c:encodeURL('/img/component/combobox-rounded-s.gif')});
}
.z-bandbox-rounded .z-bandbox-rounded-btn {
	background-image: url(${c:encodeURL('/img/component/bandbox-rounded-s.gif')});
}
.z-datebox-rounded .z-datebox-rounded-btn {
	background-image: url(${c:encodeURL('/img/component/datebox-rounded-s.gif')});
}
.z-timebox-rounded .z-timebox-rounded-btn,
.z-spinner-rounded .z-spinner-rounded-btn {
	background-image: url(${c:encodeURL('/img/component/timebox-rounded-s.gif')});
}
.z-combobox-rounded .z-combobox-rounded-btn-right-edge,
.z-bandbox-rounded .z-bandbox-rounded-btn-right-edge,
.z-datebox-rounded .z-datebox-rounded-btn-right-edge,
.z-timebox-rounded .z-timebox-rounded-btn-right-edge,
.z-spinner-rounded .z-spinner-rounded-btn-right-edge {
	background-position: -19px -110px;
}
.z-combobox-rounded-inp-over,
.z-bandbox-rounded-inp-over,
.z-datebox-rounded-inp-over,
.z-timebox-rounded-inp-over,
.z-spinner-rounded-inp-over {
	background-position: 0 -22px;
}
.z-combobox-rounded .z-combobox-rounded-btn-over,
.z-bandbox-rounded .z-bandbox-rounded-btn-over,
.z-datebox-rounded .z-datebox-rounded-btn-over,
.z-timebox-rounded .z-timebox-rounded-btn-over,
.z-spinner-rounded .z-spinner-rounded-btn-over  {
	background-position: 0 -132px;
}
.z-combobox-rounded-focus .z-combobox-rounded-btn,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn,
.z-datebox-rounded-focus .z-datebox-rounded-btn,
.z-timebox-rounded-focus .z-timebox-rounded-btn,
.z-spinner-rounded-focus .z-spinner-rounded-btn {
	background-position: 0 -176px;
}
.z-combobox-rounded-focus .z-combobox-rounded-btn-right-edge,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn-right-edge,
.z-datebox-rounded-focus .z-datebox-rounded-btn-right-edge,
.z-timebox-rounded-focus .z-timebox-rounded-btn-right-edge,
.z-spinner-rounded-focus .z-spinner-rounded-btn-right-edge {
	background-position: -19px -110px;
}
.z-combobox-rounded-focus .z-combobox-rounded-btn-over,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn-over,
.z-datebox-rounded-focus .z-datebox-rounded-btn-over,
.z-timebox-rounded-focus .z-timebox-rounded-btn-over,
.z-spinner-rounded-focus .z-spinner-rounded-btn-over {
	background-position: 0 -198px;
}
.z-combobox-rounded-focus .z-combobox-rounded-inp-clk, .z-combobox-rounded .z-combobox-inp-clk,
.z-bandbox-rounded-focus .z-bandbox-rounded-inp-clk, .z-bandbox-rounded .z-bandbox-inp-clk,
.z-datebox-rounded-focus .z-datebox-rounded-inp-clk, .z-datebox-rounded .z-datebox-inp-clk,
.z-timebox-rounded-focus .z-timebox-rounded-inp-clk, .z-timebox-rounded .z-timebox-inp-clk,
.z-spinner-rounded-focus .z-spinner-rounded-inp-clk, .z-spinner-rounded .z-spinner-inp-clk {
	background-position: 0 -44px;
}
.z-combobox-rounded-focus .z-combobox-rounded-btn-clk, .z-combobox-rounded .z-combobox-rounded-btn-clk,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn-clk, .z-bandbox-rounded .z-bandbox-rounded-btn-clk,
.z-datebox-rounded-focus .z-datebox-rounded-btn-clk, .z-datebox-rounded .z-datebox-rounded-btn-clk,
.z-timebox-rounded-focus .z-timebox-rounded-btn-clk, .z-timebox-rounded .z-timebox-rounded-btn-clk,
.z-spinner-rounded-focus .z-spinner-rounded-btn-clk, .z-spinner-rounded .z-spinner-rounded-btn-clk {
	background-position: 0 -154px !important;
}
.z-combobox-rounded-readonly,
.z-bandbox-rounded-readonly,
.z-datebox-rounded-readonly,
.z-timebox-rounded-readonly,
.z-spinner-rounded-readonly {
	background-position: 0 -66px;
}
.z-combobox-rounded-readonly {
	background-image: url(${c:encodeURL('/img/component/combobox-rounded-s.gif')});
}
.z-bandbox-rounded-readonly {
	background-image: url(${c:encodeURL('/img/component/bandbox-rounded-s.gif')});
}
.z-datebox-rounded-readonly {
	background-image: url(${c:encodeURL('/img/component/datebox-rounded-s.gif')});
}
.z-timebox-rounded-readonly,
.z-spinner-rounded-readonly {
	background-image: url(${c:encodeURL('/img/component/timebox-rounded-s.gif')});
}
.z-combobox-rounded .z-combobox-rounded-btn-right-edge.z-combobox-rounded-btn-readonly,
.z-combobox-rounded i.z-combobox-rounded-btn-right-edge-readonly,
.z-bandbox-rounded .z-bandbox-rounded-btn-right-edge.z-bandbox-rounded-btn-readonly,
.z-bandbox-rounded i.z-bandbox-rounded-btn-right-edge-readonly,
.z-datebox-rounded .z-datebox-rounded-btn-right-edge.z-datebox-rounded-btn-readonly,
.z-datebox-rounded i.z-datebox-rounded-btn-right-edge-readonly,
.z-timebox-rounded .z-timebox-rounded-btn-right-edge.z-timebox-rounded-btn-readonly,
.z-timebox-rounded i.z-timebox-rounded-btn-right-edge-readonly,
.z-spinner-rounded .z-spinner-rounded-btn-right-edge.z-spinner-rounded-btn-readonly,
.z-spinner-rounded i.z-spinner-rounded-btn-right-edge-readonly {
	background-position: -19px -176px;
}
.z-combobox-rounded .z-combobox-rounded-btn-readonly,
.z-bandbox-rounded .z-bandbox-rounded-btn-readonly,
.z-datebox-rounded .z-datebox-rounded-btn-readonly,
.z-timebox-rounded .z-timebox-rounded-btn-readonly,
.z-spinner-rounded .z-spinner-rounded-btn-readonly {
	background-position: 0 -176px;
}
.z-combobox-rounded-focus .z-combobox-rounded-readonly,
.z-bandbox-rounded-focus .z-bandbox-rounded-readonly,
.z-datebox-rounded-focus .z-datebox-rounded-readonly,
.z-timebox-rounded-focus .z-timebox-rounded-readonly,
.z-spinner-rounded-focus .z-spinner-rounded-readonly {
	background-position: 0 -88px;
}
.z-combobox-rounded-focus .z-combobox-rounded-readonly {
	background-image: url(${c:encodeURL('/img/component/combobox-rounded-s.gif')});
}
.z-bandbox-rounded-focus .z-bandbox-rounded-readonly {
	background-image: url(${c:encodeURL('/img/component/bandbox-rounded-s.gif')});
}
.z-datebox-rounded-focus .z-datebox-rounded-readonly {
	background-image: url(${c:encodeURL('/img/component/datebox-rounded-s.gif')});
}
.z-timebox-rounded-focus .z-timebox-rounded-readonly,
.z-spinner-rounded-focus .z-spinner-rounded-readonly {
	background-image: url(${c:encodeURL('/img/component/timebox-rounded-s.gif')});	
}
.z-combobox-rounded-focus .z-combobox-rounded-btn-right-edge.z-combobox-rounded-btn-readonly,
.z-combobox-rounded-focus i.z-combobox-rounded-btn-right-edge-readonly,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn-right-edge.z-bandbox-rounded-btn-readonly,
.z-bandbox-rounded-focus i.z-bandbox-rounded-btn-right-edge-readonly,
.z-datebox-rounded-focus .z-datebox-rounded-btn-right-edge.z-datebox-rounded-btn-readonly,
.z-datebox-rounded-focus i.z-datebox-rounded-btn-right-edge-readonly,
.z-timebox-rounded-focus .z-timebox-rounded-btn-right-edge.z-timebox-rounded-btn-readonly,
.z-timebox-rounded-focus i.z-timebox-rounded-btn-right-edge-readonly,
.z-spinner-rounded-focus .z-spinner-rounded-btn-right-edge.z-spinner-rounded-btn-readonly,
.z-spinner-rounded-focus i.z-spinner-rounded-btn-right-edge-readonly {
	background-position: -19px -198px;
}
.z-combobox-rounded-focus .z-combobox-rounded-btn-readonly,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn-readonly,
.z-datebox-rounded-focus .z-datebox-rounded-btn-readonly,
.z-timebox-rounded-focus .z-timebox-rounded-btn-readonly,
.z-spinner-rounded-focus .z-spinner-rounded-btn-readonly {
	background-position: 0 -198px;
}