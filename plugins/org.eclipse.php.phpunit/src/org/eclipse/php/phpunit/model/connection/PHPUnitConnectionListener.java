/*******************************************************************************
 * Copyright (c) 2017 Rogue Wave Software Inc. and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Rogue Wave Software Inc. - initial implementation
 *******************************************************************************/
package org.eclipse.php.phpunit.model.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.osgi.util.NLS;
import org.eclipse.php.phpunit.PHPUnitMessages;
import org.eclipse.php.phpunit.PHPUnitPlugin;
import org.eclipse.php.phpunit.launch.PHPUnitLaunchUtils;
import org.eclipse.php.phpunit.model.elements.PHPUnitElementManager;
import org.eclipse.php.phpunit.ui.view.PHPUnitView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class PHPUnitConnectionListener implements Runnable {
	protected ILaunch launch;
	protected int port = 0;
	protected ServerSocket serverSocket;

	protected Socket socket;
	private Thread listenThread;
	private Gson gson = new GsonBuilder().create();

	public PHPUnitConnectionListener(final int port, final ILaunch launch) {
		this.port = port;
		this.launch = launch;
	}

	protected void handleReport(final Socket socket) {
		if (!PHPUnitLaunchUtils.isPHPUnitRunning()) {
			return;
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			final PHPUnitMessageParser parser = PHPUnitMessageParser.getInstance();
			parser.clean();
			parser.setDebugTarget(PHPUnitView.getDefault().getLaunch().getDebugTarget());
			parser.setInProgress(true);
			String line;
			while ((line = reader.readLine()) != null && parser.isInProgress()) {
				try {
					processLine(line, parser);
				} catch (JsonSyntaxException e) {
					PHPUnitPlugin.log(e);
				}
			}
		} catch (final IOException e) {
			PHPUnitPlugin.log(e);
		}
	}

	private void processLine(String line, PHPUnitMessageParser parser) throws JsonSyntaxException {
		if (StringUtils.isEmpty(line) || line.charAt(0) != '{') {
			return;
		}
		Message message = gson.fromJson(line, Message.class);
		parser.parseMessage(message, PHPUnitView.getDefault().getViewer());
	}

	@Override
	public void run() {
		final ILaunch launch = PHPUnitView.getDefault().getLaunch();
		int tries = 3;
		String message = PHPUnitMessages.PHPUnitView_Run_Error;
		do {
			try {
				serverSocket = new ServerSocket(port, 1);
				serverSocket.setSoTimeout(5000);
				serverSocket.setReuseAddress(true);
				socket = serverSocket.accept();
				handleReport(socket);
			} catch (final IOException e) {
				tries--;
				if (tries == 0) {
					PHPUnitPlugin.log(e);
					message = NLS.bind(PHPUnitMessages.PHPUnitView_Run_SocketError, port, e.getMessage());
				}
			} finally {
				shutdown(false);
			}
		} while ((tries > 0 && socket == null) || (!launch.isTerminated() && "debug".equals(launch.getLaunchMode()))); //$NON-NLS-1$
		PHPUnitView.getDefault().stop(PHPUnitElementManager.getInstance().getRoot(), message);
	}

	public void shutdown(boolean terminateLaunch) {
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (final Exception e) {
		}
		try {
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
		} catch (final IOException e) {
		}
		if (terminateLaunch && launch != null && !launch.isTerminated()) {
			try {
				launch.terminate();
			} catch (final DebugException e) {
			}
		}

		PHPUnitMessageParser.getInstance().setInProgress(false);
	}

	public void start() {
		listenThread = new Thread(this, "PHPUnit PHPUnitConnectionListener"); //$NON-NLS-1$
		listenThread.start();
	}

}