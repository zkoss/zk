/* IImageRichlet.java

		Purpose:
		
		Description:
		
		History:
				Tue Jan 11 11:49:21 CST 2022, Created by leon

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.essential_components;

import static org.zkoss.stateless.action.ActionTarget.SELF;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.zkoss.web.fn.ServletFns;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.ActionVariable;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IImage;
import org.zkoss.stateless.sul.ILabel;
import org.zkoss.stateless.sul.IVlayout;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

/**
 * A set of example for {@link IImage} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Image">IImage</a>,
 * if any.
 * @author leon
 * @see IImage
 */
@RichletMapping("/essential_components/iimage")
public class IImageRichlet implements StatelessRichlet {

	@RichletMapping("/src")
	public List<IComponent> src() throws IOException {
		return Arrays.asList(
				IButton.of("change src").withAction(this::changeSrc),
				IImage.of("/stateless/ZK-Logo.gif").withId("img")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSrc() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("img"),
				new IImage.Updater().src("/zephyr-test/stateless/ZK-Logo-old.gif"));
	}

	@RichletMapping("/content")
	public List<IComponent> content() throws IOException {
		java.io.FileInputStream fis = new java.io.FileInputStream(ServletFns.getCurrentServletContext().getRealPath("/zephyr") + "/ZK-Logo.gif");
		org.zkoss.image.AImage img = new org.zkoss.image.AImage("ZK-Logo-dynamic", fis);
		return Arrays.asList(
				IButton.of("change content").withAction(this::changeContent),
				IImage.DEFAULT.withContent(img).withId("img")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeContent() throws IOException {
		java.io.FileInputStream fis = new java.io.FileInputStream(ServletFns.getCurrentServletContext().getRealPath("/zephyr") + "/ZK-Logo-old.gif");
		org.zkoss.image.AImage img = new org.zkoss.image.AImage("ZK-Logo-dynamic2", fis);
		UiAgent.getCurrent().smartUpdate(Locator.ofId("img"), new IImage.Updater().content(img));
	}

	@RichletMapping("/renderedimage")
	public IComponent renderedImage() {
		BufferedImage bi = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();
		Line2D line = new Line2D.Double(10, 10, 390, 290);
		g2d.setColor(Color.blue);
		g2d.setStroke(new BasicStroke(3));
		g2d.draw(line);
		return IImage.DEFAULT.withContent(bi);
	}

	@RichletMapping("/hover")
	public IComponent hover() {
		return IVlayout.of(
				IButton.of("change hover").withAction(this::changeHover),
				IImage.of("/stateless/ZK-Logo.gif").withHover("/stateless/ZK-Logo_en_US.gif").withId("img"),
				ILabel.of("hover out target")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeHover() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("img"),
				new IImage.Updater().hover("/zephyr-test/stateless/ZK-Logo-old.gif"));
	}

	@RichletMapping("/locale")
	public IComponent locale() {
		return IImage.of("/stateless/ZK-Logo*.gif");
	}

	@RichletMapping("/preload")
	public List<IComponent> preload() {
		return Arrays.asList(
				IButton.of("change preload").withAction(this::changePreloadImage),
				IImage.of("/stateless/ZK-Logo.gif").withPreloadImage(true).withAction(this::doClick)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changePreloadImage() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("img"), new IImage.Updater().preloadImage(false));
	}

	@Action(type = Events.ON_CLICK)
	public void doClick(@ActionVariable(targetId = SELF, field = "_preloadImage") boolean preloadImage) {
		Clients.log(preloadImage);
	}

	@RichletMapping("/size")
	public IComponent size() {
		return IImage.of("/stateless/ZK-Logo.gif").withWidth("72px").withHeight("64px");
	}
}
