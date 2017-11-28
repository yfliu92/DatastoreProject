package utd.persistentDataStore.datastoreClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import utd.persistentDataStore.utils.StreamUtil;

public class DatastoreClientImpl implements DatastoreClient {
	private static Logger logger = Logger.getLogger(DatastoreClientImpl.class);

	private InetAddress address;
	private int port;

	public DatastoreClientImpl(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utd.persistentDataStore.datastoreClient.DatastoreClient#write(java.lang.
	 * String, byte[])
	 */
	@Override
	public void write(String name, byte data[]) throws ClientException, ConnectionException {
		try {
			logger.debug("Executing Write Operation");
			Socket socket = new Socket();
			SocketAddress addr = new InetSocketAddress(address, port);
			socket.connect(addr);

			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();

			StreamUtil.writeLine("write\n", outputStream);
			StreamUtil.writeLine(name.trim() + "\n", outputStream);
			StreamUtil.writeLine(Integer.toString(data.length) + "\n", outputStream);
			StreamUtil.writeData(data, outputStream);

			String resCode = StreamUtil.readLine(inputStream);
			
			if ("ok".equals(resCode.toLowerCase())) {
				logger.debug("Write Response: " + resCode);
			} else {
				logger.debug("Error Write Response: " + resCode);
			}
			
			StreamUtil.closeSocket(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utd.persistentDataStore.datastoreClient.DatastoreClient#read(java.lang.
	 * String)
	 */
	@Override
	public byte[] read(String name) throws ClientException, ConnectionException {
		byte[] data = null;
		try {
			logger.debug("Executing Read Operation");
			Socket socket = new Socket();
			SocketAddress addr = new InetSocketAddress(address, port);
			socket.connect(addr);
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();

			StreamUtil.writeLine("read\n", outputStream);
			StreamUtil.writeLine(name + "\n", outputStream);

			String resCode = StreamUtil.readLine(inputStream);
			
			if ("ok".equals(resCode.toLowerCase())) {
				int length = Integer.parseInt(StreamUtil.readLine(inputStream));
				data = StreamUtil.readData(length, inputStream);
				logger.debug(data);
			} else {
				logger.debug("Error Read Response: " + resCode);
				StreamUtil.closeSocket(inputStream);
				throw new ClientException(resCode);
			}
			
			StreamUtil.closeSocket(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * utd.persistentDataStore.datastoreClient.DatastoreClient#delete(java.lang.
	 * String)
	 */
	@Override
	public void delete(String name) throws ClientException, ConnectionException {

		try {
			logger.debug("Executing Delete Operation");
			Socket socket = new Socket();
			SocketAddress addr = new InetSocketAddress(address, port);
			socket.connect(addr);
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();

			StreamUtil.writeLine("delete\n", outputStream);
			StreamUtil.writeLine(name + "\n", outputStream);

			String resCode = StreamUtil.readLine(inputStream);
			StreamUtil.closeSocket(inputStream);
			if ("ok".equals(resCode.toLowerCase())) {
				logger.debug("Delete Response: " + resCode);
			} else {
				logger.debug("Error Delete Response: " + resCode);
				throw new ClientException(resCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utd.persistentDataStore.datastoreClient.DatastoreClient#directory()
	 */
	@Override
	public List<String> directory() throws ClientException, ConnectionException {
		List<String> result = new ArrayList<>();
		try {
			logger.debug("Executing Directory Operation");
			Socket socket = new Socket();
			SocketAddress addr = new InetSocketAddress(address, port);
			socket.connect(addr);
			
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();

			StreamUtil.writeLine("directory\n", outputStream);

			String resCode = StreamUtil.readLine(inputStream);
			
			
			if ("ok".equals(resCode.toLowerCase())) {
				int length = Integer.parseInt(StreamUtil.readLine(inputStream));
				while (length > 0) {
					result.add(StreamUtil.readLine(inputStream));
					length--;
				}
			} else {
				StreamUtil.closeSocket(inputStream);
				logger.debug("Error Directory Response: " + resCode);
				throw new ClientException(resCode);
			}
			StreamUtil.closeSocket(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
