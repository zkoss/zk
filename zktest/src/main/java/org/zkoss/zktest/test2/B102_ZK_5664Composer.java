package org.zkoss.zktest.test2;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Image;

public class B102_ZK_5664Composer extends SelectorComposer<Component> {
	@Wire
	private Image badNameImg;
	@Wire
	private Image goodNameImg;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		//initialize a byte array of 1x1 png image
		byte[] png = createPng();

		badNameImg.setContent(new AImage("myimage-\uD83C\uDF89.png", png));
		goodNameImg.setContent(new AImage("myimage.png", png));
	}
	@Listen("onClick=#btn1")
	public void onFileDownload1() throws Exception {
		InputStream in = B102_ZK_5664Composer.class.getClassLoader().getResourceAsStream("/test2/B102-ZK-5664-테스트 파일.docx");
		Filedownload.save(in, "docx", "테스트 파일.docx");
	}

	@Listen("onClick=#btn2")
	public void onFileDownload2() throws Exception {
		InputStream in = B102_ZK_5664Composer.class.getClassLoader().getResourceAsStream("/test2/B102-ZK-5664中文");
		Filedownload.save(in, "", "B102-ZK-5664中文");
	}

	public static byte[] createPng() {
		byte[] imageBytes = null;
		try {
			// Create a 1x1 pixel image
			BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

			// Set the color of the single pixel (e.g., white)
			int color = 0xFFFFFF; // RGB color (white)
			bufferedImage.setRGB(0, 0, color);

			// Write the image to a ByteArrayOutputStream
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

			// Convert the ByteArrayOutputStream to a byte array
			imageBytes = byteArrayOutputStream.toByteArray();


		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageBytes;
	}
}
