package utd.persistentDataStore.datastoreServer.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import utd.persistentDataStore.utils.FileUtil;
import utd.persistentDataStore.utils.ServerException;
import utd.persistentDataStore.utils.StreamUtil;

public class DirectoryCommand extends ServerCommand {
	
	private Logger logger = Logger.getLogger(DirectoryCommand.class);

	@Override
	public void run() throws IOException, ServerException {
		List<String> list = new ArrayList<>();
		list = FileUtil.directory();
		int length = list.size();
		
		sendOK();
		StreamUtil.writeLine(length+"\n", outputStream);
		for(int i = 0; i < length; i++) {
			StreamUtil.writeLine(list.get(i), outputStream);
		}
	}

}
