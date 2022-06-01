declare namespace zul {
	namespace wgt {
		class Image {} // ./widget
		class Caption {} // ./flex
		// ./au
		class Popup extends zk.Widget {
			public reposition(): void
		}
		class Notification extends Popup {
			public static show(msg: string, pid: string, opts: Record<string, unknown>): void
		}
		class Errorbox extends Notification {
			public _fixarrow(): void
		}
	}
	namespace mesh {
		class HeaderWidget {} // ./flex
		class Auxheader {} // ./flex
	}
	namespace wnd {
		class Panelchildren {} // ./flex
	}
	namespace sel {
		class ItemWidget {} // ./dom
		// ./domtouch
		class Listitem {}
		class Treerow {}
	}
	// ./domtouch
	namespace grid {
		class Row {}
	}
}