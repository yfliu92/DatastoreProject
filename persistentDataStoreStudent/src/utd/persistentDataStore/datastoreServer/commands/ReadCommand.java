package utd.persistentDataStore.datastoreServer.commands;

import java.io.IOException;

import org.apache.log4j.Logger;

import utd.persistentDataStore.utils.FileUtil;
import utd.persistentDataStore.utils.ServerException;
import utd.persistentDataStore.utils.StreamUtil;

public class ReadCommand extends ServerCommand {

	private Logger logger = Logger.getLogger(ReadCommand.class);

	@Override
	public void run() throws IOException, ServerException {
		String name = StreamUtil.readLine(inputStream);
		if (name == null || "".equals(name)) {
			sendError("Cannot Read Name");
		}

		byte[] data = FileUtil.readData(name);
		sendOK();
		StreamUtil.writeLine(data.length + "\n", outputStream);
		StreamUtil.writeData(data, outputStream);
	}

}
