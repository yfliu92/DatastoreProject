package utd.persistentDataStore.datastoreServer.commands;

import java.io.IOException;

import org.apache.log4j.Logger;

import utd.persistentDataStore.utils.FileUtil;
import utd.persistentDataStore.utils.ServerException;
import utd.persistentDataStore.utils.StreamUtil;

public class WriteCommand extends ServerCommand {
	
	private static Logger logger = Logger.getLogger(WriteCommand.class); 
	
	@Override
	public void run() throws IOException, ServerException {
		String name = StreamUtil.readLine(inputStream);
		if(name == null || "".equals(name)) {
			sendError("Cannot Read name");
		}
		
		String dataSize = StreamUtil.readLine(inputStream);
		if(dataSize == null || "".equals(dataSize)) {
			sendError("Cannot find size of input data");
		}
		int dataLen = Integer.parseInt(dataSize);
		byte[] data = StreamUtil.readData(dataLen, inputStream);
		FileUtil.writeData(name, data);
		sendOK();
	}

}
