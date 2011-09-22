<%@ taglib prefix="zk" uri="http://www.zkoss.org/jsp/zul"%>
<zk:page zscriptLanguage="java">

<zk:window>
<zk:tree id="tree" width="400px" rows="8">
<zk:treecols sizable="true">
<zk:treecol label="Name" />
<zk:treecol label="Description" />
</zk:treecols>
<zk:treechildren>
<zk:treeitem>
<zk:treerow>
<zk:treecell label="Item 1" />
<zk:treecell label="Item 1 description" />
</zk:treerow>
</zk:treeitem>
<zk:treeitem>
<zk:treerow>
<zk:treecell label="Item 2" />
<zk:treecell label="Item 2 description" />
</zk:treerow>
<zk:treechildren>
<zk:treeitem>
<zk:treerow>
<zk:treecell label="Item 2.1" />
</zk:treerow>
<zk:treechildren>
<zk:treeitem>
<zk:treerow>
<zk:treecell label="Item 2.1.1" />
</zk:treerow>
</zk:treeitem>
<zk:treeitem>
<zk:treerow>
<zk:treecell label="Item 2.1.2" />
</zk:treerow>
</zk:treeitem>
</zk:treechildren>
</zk:treeitem>
<zk:treeitem>
<zk:treerow>
<zk:treecell label="Item 2.2" />
</zk:treerow>
<zk:treechildren>
<zk:treeitem>
<zk:treerow>
<zk:treecell label="Item 2.2.1" />
</zk:treerow>
</zk:treeitem>
</zk:treechildren>
</zk:treeitem>
</zk:treechildren>
</zk:treeitem>
<zk:treeitem label="Item 3" />
</zk:treechildren>
</zk:tree>

</zk:window>

</zk:page>