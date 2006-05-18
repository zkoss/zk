Component Template Guide
------------------------

1. If a component accepts children, it must be one of the following forms:

	a)	<tag>children</tag>
	b)	<tag>
			<tag></tag>
			<tag id="${self.uuid}!real">children</tag>
		</tag>
	c)	<tag>
			<tag></tag>
			<tag>
				children
				<span id="${self.uuid}!child" style="display:none"></span>
			</tag>
		</tag>
