package pimpmykerberos.server.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pimpmykerberos.utils.Fonctions;

public class StreamServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 474719652648270932L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		ServletOutputStream out;
		String url = request.getParameter("url");
		String camName = request.getParameter("camName");
		// Try to look if it's a local file
		File localFile = new File(url);
		if (localFile.exists()) {
			Fonctions.trace("DBG", "It's a local file " + url + " trying to diplay", "StreamServlet");
			try {
				out = response.getOutputStream();
				InputStream fin = new FileInputStream(url);
				out = response.getOutputStream();
				int ch = 0;
				;
				while ((ch = fin.read()) != -1) {
					out.write(ch);
				}

				fin.close();

				out.close();
			} catch (Exception e) {
				Fonctions.trace("ERR", "Can't display local file " + url, "StreamServlet");
			}

		} else {

			try {
				out = response.getOutputStream();

				Fonctions.trace("DBG", "Start streaming to " + url, "StreamServlet");
				InputStream fin = new URL(url).openStream();
				BufferedInputStream bin = new BufferedInputStream(fin);
				BufferedOutputStream bout = new BufferedOutputStream(out);
				response.setContentType("multipart/x-mixed-replace; boundary=mjpegstream");
				int ch = 0;
				;
				while ((ch = bin.read()) != -1) {
					bout.write(ch);
				}

				bin.close();
				fin.close();
				bout.close();
				out.close();
			} catch (Exception e) {
				try {
					out = response.getOutputStream();

					Fonctions.trace("ERR",
							"Too bad streaming to " + camName + ":8889 because " + url + " error was " + e.getMessage(),
							"StreamServlet");
					url = "http://" + camName + ":8889";
					InputStream fin = new URL(url).openStream();
					BufferedInputStream bin = new BufferedInputStream(fin);
					BufferedOutputStream bout = new BufferedOutputStream(out);
					response.setContentType("multipart/x-mixed-replace; boundary=mjpegstream");
					int ch = 0;
					while ((ch = bin.read()) != -1) {
						bout.write(ch);
					}

					bin.close();
					fin.close();
					bout.close();
					out.close();
				} catch (Exception e2) {

					Fonctions.trace("ERR", "Couldn't stream to " + url
							+ " generally because ip is different from kerberos.io ip, if pimpMyKerberos is on same server, you can force ip to aCore.xml <webServerIp>same_as_kerberos.io</webServerIp>, or either set <ip>kerberos.io ip</ip> under each camera in aCore.xml or route issue "
							+ e2.getMessage(), "StreamServlet");
					try {
						InputStream fin = StreamServlet.class.getResourceAsStream("/webInterface/images/wolf.png");
						out = response.getOutputStream();
						int ch = 0;
						;
						while ((ch = fin.read()) != -1) {
							out.write(ch);
						}

						fin.close();

						out.close();

					} catch (Exception e1) {
						Fonctions.trace("ERR", "Couldn't send default image " + e1.getMessage(), "StreamServlet");

					}
				}
			}
		}
	}

}
