package pimpmykerberos.server.servlets;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import pimpmykerberos.beans.Camera;
import pimpmykerberos.core.Core;
import pimpmykerberos.utils.Fonctions;

public class PictureServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 474719652648270932L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		String url = request.getParameter("url");
		String camName = request.getParameter("camName");

		File aFile = new File(Core.getInstance().getKerberosioPath() + "/" + camName + "/capture");
		if (aFile.exists()) {
			Camera aCamera = Core.getInstance().getUsers().get("admin").getCameras().get(camName);
			if (aCamera != null) {
				if (aCamera.getTimeToFile().size() > 0) {
					File lastFile = new File(aCamera.getTimeToFile().lastEntry().getValue());
					Fonctions.trace("DBG", "Last File is " + lastFile.getAbsolutePath(), "PictureServlet");
					if (lastFile.exists()) {
						if (lastFile.getAbsolutePath().endsWith(".jpg")) {
							performJpg(lastFile, request, response);
						} else {
							performMp4(lastFile, request, response);
						}

					} else {
						Fonctions.trace("ERR", "Last File " + lastFile.getAbsolutePath() + " is missing",
								"PictureServlet");
					}
				} else {
					Fonctions.trace("ERR", "Camera " + camName + " not yet populated", "PictureServlet");
				}

			} else {
				Fonctions.trace("ERR", "Camera " + camName + " not exist", "PictureServlet");
			}

		} else {
			Fonctions.trace("ERR", "File " + aFile.getAbsolutePath() + " exists but contain no file", "PictureServlet");
		}

//		// Try to look if it's a local file
//		File localFile = new File(url);
//		if (localFile.exists()) {
//			Fonctions.trace("DBG", "It's a local file " + url + " trying to diplay", "PictureServlet");
//			try {
//				out = response.getOutputStream();
//				InputStream fin = new FileInputStream(url);
//				out = response.getOutputStream();
//				int ch = 0;
//				;
//				while ((ch = fin.read()) != -1) {
//					out.write(ch);
//				}
//
//				fin.close();
//
//				out.close();
//			} catch (Exception e) {
//				Fonctions.trace("ERR", "Can't display local file " + url, "PictureServlet");
//			}
//
//		} else {
//
//			try {
//				out = response.getOutputStream();
//
//				Fonctions.trace("DBG", "Start  to " + url, "PictureServlet");
//				InputStream fin = new URL(url).openStream();
//				Random aRandom = new Random();
//				File outputFile = new File(
//						System.getProperty("java.io.tmpdir") + "/pimpMyKerberos" + aRandom.nextInt());
//				OutputStream outStream = new FileOutputStream(outputFile, false);
//
//				int read;
//				int totalRead = 0;
//				int maxRead = 1000000;
//				byte[] bytes = new byte[maxRead];
//				while ((read = fin.read(bytes)) < maxRead) {
//					if (totalRead >= maxRead) {
//						break;
//					}
//					totalRead += read;
//					Fonctions.trace("DBG", "Read " + totalRead + " bytes / " + maxRead, "PictureServlet");
//
//				}
//				Fonctions.trace("DBG", "Writing to temporary file " + outputFile.getAbsolutePath(), "PictureServlet");
//				outStream.write(bytes, 0, read);
//				outStream.close();
//				Picture picture = FrameGrab.getFrameFromFile(outputFile, 1);
//				outputFile.delete();
//				fin.close();
//
//				out.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//
//			}
	}

	private void performJpg(File lastFile, HttpServletRequest request, HttpServletResponse response) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(lastFile);
			ServletOutputStream out = response.getOutputStream();
			response.setContentType("image/jpeg");
			int ch = 0;
			while ((ch = fis.read()) != -1) {
				out.write(ch);
			}
			fis.close();
		} catch (Exception e) {
			Fonctions.trace("ERR", "Can't read " + lastFile.getAbsolutePath() + " error:" + e.getMessage(),
					"PictureServlet");
		}

	}

	private void performMp4(File lastFile, HttpServletRequest request, HttpServletResponse response) {
		try {
			Picture picture = FrameGrab.getFrameFromFile(lastFile, 1);
			BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
			ServletOutputStream out = response.getOutputStream();
			response.setContentType("image/jpeg");
			ImageIO.write(bufferedImage, "jpeg", out);
			out.flush();
		} catch (Exception e) {
			Fonctions.trace("ERR", "Can't read " + lastFile.getAbsolutePath() + " error:" + e.getMessage(),
					"PictureServlet");
		}

	}

}