<HTML>
<HEAD>
<title>Hello</title>
</HEAD>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://www.zkoss.org/jsf/zul" prefix="z"%>

<body bgcolor="white">
<f:view>
	<h:form id="helloForm">
		<z:page>
			<z:window z:title="Input Components" width="500px" border="normal">
				Textbox:<z:textbox id="t1" f:value="#{testInputboxBean.textValue}" >
				</z:textbox>
				<h:message
				style="color: red; font-family: 'New Century Schoolbook', serif; font-style: oblique; text-decoration: overline"
					for="i1" />
				<br/>
				Intbox:<z:intbox id="i1" f:value="#{testInputboxBean.intValue}" format="#,#0" >
				</z:intbox>
				<h:message
				style="color: red; font-family: 'New Century Schoolbook', serif; font-style: oblique; text-decoration: overline"
					for="i1" />
				<br/>
				
				Doublebox:<z:doublebox id="d1" f:value="#{testInputboxBean.doubleValue}" format="#,#0.#" f:required="true">
				</z:doublebox>
				<h:message
				style="color: red; font-family: 'New Century Schoolbook', serif; font-style: oblique; text-decoration: overline"
					for="d1" />
				<br/>
				
				Decimalbox:<z:decimalbox id="bd1" f:value="#{testInputboxBean.decimalValue}" >
				</z:decimalbox>
				<h:message
				style="color: red; font-family: 'New Century Schoolbook', serif; font-style: oblique; text-decoration: overline"
					for="bd1" />
				<br/>
				
				
				Combobox:<z:combobox id="combo1" f:value="#{testInputboxBean.comboboxValue}" autodrop="true">
					<z:comboitem label="Simple and Rich"/>
					<z:comboitem label="Cool!"/>
					<z:comboitem label="Thumbs Up!"/>
				</z:combobox>
				
				<br/>
				
				Checkbox:<z:checkbox id="c1" f:value="#{testInputboxBean.booleanValue}" >
				</z:checkbox>
				<h:message
				style="color: red; font-family: 'New Century Schoolbook', serif; font-style: oblique; text-decoration: overline"
					for="c1" />
				<br/>
				Radiogroup:
					<z:radiogroup id="r2" f:value="#{testInputboxBean.radioGroupValue}" >
						<z:radio value="A" />
						<z:radio value="B" />
						<z:radio value="C" />
					</z:radiogroup><br/>
				
				
				
				Bandbox:<z:bandbox id="bd" f:value="#{testInputboxBean.bandboxValue}">
						<z:bandpopup>
						<z:vbox>		
						<z:hbox>Search <z:textbox/></z:hbox>
						<z:listbox width="200px"
						onSelect="bd.value=self.selectedItem.label; bd.closeDropdown();">
							<z:listhead>
								<z:listheader label="Name"/>
								<z:listheader label="Description"/>
							</z:listhead>
							<z:listitem>
								<z:listcell label="John"/>
								<z:listcell label="CEO"/>
							</z:listitem>
							<z:listitem>
								<z:listcell label="Joe"/>
								<z:listcell label="Engineer"/>
							</z:listitem>
							<z:listitem>
								<z:listcell label="Mary"/>
								<z:listcell label="Supervisor"/>
							</z:listitem>
						</z:listbox>
						</z:vbox>
						</z:bandpopup>
					</z:bandbox>
					<br/>
					
					Slider:
					<z:slider id="slider" name="slider1" f:value="#{testInputboxBean.sliderValue}"/><br/>
					Timebox:
					<z:timebox id="time1" f:value="#{testInputboxBean.timeValue}"/><br/>
					Datebox:
					<z:datebox id="date1"  f:value="#{testInputboxBean.dateValue}"/><br/>
					Calendar:
					<z:calendar id="cal1"  name="cal1" f:value="#{testInputboxBean.calendarValue}"/><br/>
					
				
				<h:commandButton id="submit" action="#{testInputboxBean.doSubmit}" value="Submit" />
				<h:messages/>
			</z:window>
		</z:page>
	</h:form>
</f:view>
</body>
</HTML>
