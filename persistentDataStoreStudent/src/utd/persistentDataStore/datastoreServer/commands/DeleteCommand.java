package utd.persistentDataStore.datastoreServer.commands;

import java.io.IOException;

import org.apache.log4j.Logger;

import utd.persistentDataStore.utils.FileUtil;
import utd.persistentDataStore.utils.ServerException;
import utd.persistentDataStore.utils.StreamUtil;

public class DeleteCommand extends ServerCommand {

	private Logger logger = Logger.getLogger(DeleteCommand.class);

	@Override
	public void run() throws IOException, ServerException {
		String name = StreamUtil.readLine(inputStream);
		if (name == null || "".equals(name)) {
			sendError("Cannot Read name");
		}
		boolean flag = FileUtil.deleteData(name);
		if (flag == true) {
			sendOK();
		} else {
			sendError("Cannot find file: " + name);
		}
	}

}
