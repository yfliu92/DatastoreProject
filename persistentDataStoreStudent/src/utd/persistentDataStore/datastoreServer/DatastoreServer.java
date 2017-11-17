package utd.persistentDataStore.datastoreServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import utd.persistentDataStore.datastoreServer.commands.DeleteCommand;
import utd.persistentDataStore.datastoreServer.commands.DirectoryCommand;
import utd.persistentDataStore.datastoreServer.commands.ReadCommand;
import utd.persistentDataStore.datastoreServer.commands.ServerCommand;
import utd.persistentDataStore.datastoreServer.commands.WriteCommand;
import utd.persistentDataStore.utils.ServerException;
import utd.persistentDataStore.utils.StreamUtil;

public class DatastoreServer {
	private static Logger logger = Logger.getLogger(DatastoreServer.class);

	static public final int port = 10023;

	public void startup() throws IOException {
		logger.debug("Starting Service at port " + port);

		ServerSocket serverSocket = new ServerSocket(port);

		InputStream inputStream = null;
		OutputStream outputStream = null;
		while (true) {
			try {
				logger.debug("Waiting for request");
				// The following accept() will block until a client connection
				// request is received at the configured port number
				Socket clientSocket = serverSocket.accept();
				logger.debug("Request received");

				inputStream = clientSocket.getInputStream();
				outputStream = clientSocket.getOutputStream();

				ServerCommand command = dispatchCommand(inputStream);
				logger.debug("Processing Request: " + command);
				command.setInputStream(inputStream);
				command.setOutputStream(outputStream);
				command.run();

				StreamUtil.closeSocket(inputStream);
			} catch (ServerException ex) {
				String msg = ex.getMessage();
				logger.error("Exception while processing request. " + msg);
				StreamUtil.sendError(msg, outputStream);
				StreamUtil.closeSocket(inputStream);
			} catch (Exception ex) {
				logger.error("Exception while processing request. " + ex.getMessage());
				ex.printStackTrace();
				StreamUtil.closeSocket(inputStream);
			}
		}
	}

	private ServerCommand dispatchCommand(InputStream inputStream) throws ServerException, IOException {
		String commandString = StreamUtil.readLine(inputStream);
		ServerCommand serverCommand;
		switch (commandString.toLowerCase()) {
		case "write":
			serverCommand = new WriteCommand();
			break;
		case "read":
			serverCommand = new ReadCommand();
			break;
		case "delete":
			serverCommand = new DeleteCommand();
			break;
		case "directory":
			serverCommand = new DirectoryCommand();
			break;
		default:
			throw new ServerException("Unknown Request: " + commandString);
		}
		return serverCommand;
	}

	public static void main(String args[]) {
		DatastoreServer server = new DatastoreServer();
		try {
			server.startup();
		} catch (IOException ex) {
			logger.error("Unable to start server. " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
