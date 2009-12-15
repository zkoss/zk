/* Class.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 15 16:59:23 TST 2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package zk;

/**
Represents a class. It is the metainfo about a class. The class of each object can be accessed by zk.Object#$class.

All static members can be retrieve from this class.

For example,

<pre><code>
MyClass = zk.$extends{zk.Object, {
},{
 find: function (name) {
 }
});
foo = new MyClass();
</code></pre>

where the following two statement are equivalent.

<pre><code>
foo.$class.find('abc');
MyClass.find('abc');
</code></pre>

    <h3>Like and Unlike Java</h3>

    Like Java, all classes objects have #isAssignableFrom and #isInstance. Unlike Java, all static methods are available as a method of the class object, including the superclass's static methods.

<pre><code>
MyClass = zk.$extends(zk.Object, {}, {
 static0: function () {}
});
MyDerive = zk.$extends(zk.Object, {}, {
 static1: function () {}
});
MyDerive.static0(); //OK
MyDerive.static1(); //OK
</code></pre>

    Unlike Java, you cannot access the static methods by an object. Rather, you have to go thru the class object

<pre><code>
var md = new MyDerive();
md.static0(); //Fail
md.static1(); //Fail
md.$class.static0(); //OK
MyDerive.static0(); //OK
</code></pre>

    Unlike Java, the class can by accessed directly, such as o.$instanceof(MyClass). In addition, the class objects are not instances of a particular class (Class in Java).

<pre><code>
    o.$class.$instanceof(zk.Class); //wrong! no zk.Class
</code></pre>

    Unlike Java, if a static method of the class has the same name of a static method of the superclass, it 'overrides' it.

<pre><code>
    MyClass = zk.$extends(zk.Object, {}, {
     static0: function () {}
    });
    MyDerive = zk.$extends(zk.Object, {}, {
     static0: function () {}
    });
    var mc = new MyClass(), md = new MyDerive();
    mc.static0(); //invoke MyClass.static0
    md.static0(); //invoke MyDerive.static0
</code></pre>

    In additions, the static members are placed in different scope from the non-static members. Thus, it is OK that a static member has the same name as a non-static member, though it is not a good practice (due to confusion to users).
 * 
 * @author tomyeh
 */
public class Class extends zk.Object {
	/** Determines if the class by this Class object is either the same as, or is a superclass of, the class represented by the specified Class parameter.
	 * Example: 
<pre><code>
if (klass1.isAssignableFrom(klass2)) {
}
</code></pre>
	 * @param klass the Class object to be checked
	 */
	public boolean isAssignableFrom(Class klass);
	/** Determines if the specified Object is assignment-compatible with this Class. This method is equivalent to [[zk.Object#$instanceof].
	 * Example: 
<pre><code>
if (klass.isInstance(obj)) {
}
</code></pre>
	 * @param obj the object to be checked
	 * @see #isAssignableFrom
	 */
	public boolean isInstance(Object o);
}
