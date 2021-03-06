package pimpmykerberos.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;

import com.bitplan.mjpegstreamer.MJpegRenderer;
import com.bitplan.mjpegstreamer.MJpegRunnerBase;
import com.bitplan.mjpegstreamer.ViewerSetting;
import com.bitplan.mjpegstreamer.ViewerSetting.DebugMode;

/**
 * Alternative MJpegRunner implementation
 * http://code.google.com/p/ipcapture/source/browse/IPCapture.java?r=0d
 * f4452208266f77fdc09b427682eaee09054fcb for an alternative implementation
 * Copyright (c) 2014 Wolfgang Fahl
 * 
 */
public class MJpegReaderRunner extends MJpegRunnerBase {

	private ByteArrayOutputStream jpgOut;

	public final static String VERSION = "0.1.1";

	/**
	 * no args default constructor
	 */
	public MJpegReaderRunner() {

	}

	@Override
	public void init(InputStream inputStream) throws IOException {
		this.curFrame = new byte[0];
		this.frameAvailable = false;
		if (inputStream != null)
			this.inputStream = new BufferedInputStream(inputStream);
		// if (debug)
		// debugTrace("init called");
	}

	/**
	 * stop reading
	 */
	public synchronized void stop(String msg) {
		try {
			if (jpgOut != null)
				jpgOut.close();
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			handle("Error closing streams: ", e);
		}
		DebugMode debugMode = DebugMode.None;

		if ((debugMode == DebugMode.Verbose) && (conn != null))
			LOGGER.log(Level.INFO, "stopping connection " + conn.getClass().getName());
		if (conn instanceof HttpURLConnection) {
			HttpURLConnection httpcon = (HttpURLConnection) conn;
			if (debugMode == DebugMode.Verbose)
				LOGGER.log(Level.INFO, "disconnecting " + this.getUrlString());
			httpcon.disconnect();
		}
		if (debugMode == DebugMode.Verbose)
			debugTrace("stop with msg: " + msg, this);
		super.stop(msg);
	}

	/**
	 * run me
	 */
	public void run() {
		connect();
		if (!connected)
			throw new IllegalStateException("connection lost immediately after connect");
		int prev = 0;
		int cur = 0;

		try {
			// EOF is -1
			readloop: while (connected && (inputStream != null) && ((cur = inputStream.read()) >= 0)) {
				if (prev == 0xFF && cur == 0xD8) {
					jpgOut = new ByteArrayOutputStream(INPUT_BUFFER_SIZE);
					jpgOut.write((byte) prev);
				}
				if (jpgOut != null) {
					jpgOut.write((byte) cur);
					if (prev == 0xFF && cur == 0xD9) {
						synchronized (curFrame) {
							curFrame = jpgOut.toByteArray();
						}
						frameAvailable = true;
						jpgOut.close();
						// the image is now available - read it and check if we reached the
						// limit
						// e.g. maxFrameCount
						connected = read();
						// LOGGER.log(Level.INFO,this.getTimeMsg());
						if (!connected) {
							break readloop;
						}
					}
				}
				prev = cur;
			}
			// end of input stream reached
			String msg = "end of inputstream " + this.getTimeMsg();

			stop(msg);
		} catch (IOException e) {
			handle("I/O Error " + this.getTimeMsg() + ":", e);
		}
	}

	@Override
	public void init(String urlString, String user, String pass) throws IOException {
		// TODO Auto-generated method stub
		setViewer(new MJpegRenderer() {
			
			@Override
			public void stop(String msg) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void showMessage(String msg) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setViewerSetting(ViewerSetting viewerSetting) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void renderNextImage(BufferedImage jpegImg) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void init() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public ViewerSetting getViewerSetting() {
				// TODO Auto-generated method stub
				return new ViewerSetting();
			}
		});

	}

	@Override
	public synchronized void start() {
		
			framesReadCount=0;
			framesRenderedCount=0;
			setViewer(new MJpegRenderer() {
				
				@Override
				public void stop(String msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void showMessage(String msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void setViewerSetting(ViewerSetting viewerSetting) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void renderNextImage(BufferedImage jpegImg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void init() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public ViewerSetting getViewerSetting() {
					// TODO Auto-generated method stub
					return new ViewerSetting();
				}
			});
			new Thread(this, "Stream reader").start();
			
		
		
	}

	public ByteArrayOutputStream getJpgOut() {
		return jpgOut;
	}

	public void setJpgOut(ByteArrayOutputStream jpgOut) {
		this.jpgOut = jpgOut;
	}
	
	
}