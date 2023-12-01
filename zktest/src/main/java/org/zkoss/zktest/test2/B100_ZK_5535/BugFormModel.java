package org.zkoss.zktest.test2.B100_ZK_5535;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
public class BugFormModel  {
public LinkedHashMap<String, BugFieldLayout> fields = new LinkedHashMap<String, BugFieldLayout>();
private BugOasiTreeNode<BugFormModel> node=null;

/**
 * <LI>BugFormModel</LI>
 * <PRE>
 * Nel caso di un Tree restituisce il nodo associato
 * </PRE>
 *  
 * @author m.spuri
 */
public BugOasiTreeNode<BugFormModel> getNode() {
	return node;
}
/**
 * <LI>BugFormModel</LI>
 * <PRE>
 * Nel caso di un Tree restituisce il nodo associato
 * </PRE>
 *  
 * @author m.spuri
 */
public void setNode(BugOasiTreeNode<BugFormModel> node) {
	this.node = node;
}
public LinkedHashMap<String, BugFieldLayout> getFields() {
	return fields;
}
public void setFields(LinkedHashMap<String, BugFieldLayout> fields) {
	this.fields = fields;
}
public BugFieldLayout get(String name)
{
	return fields.get(name);
}
public BugFormModel()
{
	fields.put("coarfo",new BugFieldLayout());
	fields.put("descri",new BugFieldLayout());
}
public void detach()
{	
	if ( this.fields!=null )
	{
		for(Entry<String, BugFieldLayout> obj: this.fields.entrySet())
			obj.getValue().detach();
		this.fields.clear();
		this.fields=null;
	}
}
}
