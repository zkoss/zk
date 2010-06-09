<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<html xmlns:zk="http://www.zkoss.org/2005/zk" xmlns:z="http://www.zkoss.org/2005/zul">
<head>
<title>DSP Thru zkFilter</title>
</head>
<body>
	<h1>DSP Thru zkFilter</h1>
	<h2>Bug 1702216: with DTD HTML</h2>
	<p>To test it, you have to map this file to zkFilter in web.zml</p>
	<ul id="ul">
		<li>The first item.</li>
		<li>The second item.</li>
	</ul>
	<input type="button" value="Add Item" zk:onClick="addItem(null)"/>
	<input id="mm" type="button" value="Add but cause failure" zk:onClick="addItem(&quot;mm&quot;)"/>
	<br/>
	<input id="inp0" type="text" zk:onChange="plus()"/> +
	<input id="inp1" type="text" zk:onChange="plus()"/> =
	<text id="out"/>
	<zscript>
	void addItem(String id) {
		Component li = new Raw("li");
		if (id != null) li.setId(id);
		li.setParent(ul);
		new Text("Item "+ul.getChildren().size()).setParent(li);
	}
	void plus() {
		out.setValue(inp0.getValue() + inp1.getValue());
	}
	</zscript>
	<br/>
	<input type="button" value="Say Hi" zk:onClick="alert(&quot;Hi&quot;)"/>

	<form action="/zkdemo/hello.zul">
	Replace with another URL
		<input type="submit" value="Submit" zk:onClick="System.out.println(&quot;Submitted&quot;)"/>
	</form>
	<form action="/zkdemo/hello.zul" target="inner">
	Go to iframe
		<input type="submit" value="Submit"/>
	</form>
	<input type="submit" value="Submit no in form" zk:onClick="System.out.println(&quot;Submitted 2&quot;)"/>
	<iframe name="inner">
	</iframe>

	<input type="image" src="/zkdemo/img/coffee.gif"/> <br/>
	<z:datebox/>
</body>
</html>
