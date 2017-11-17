package utd.persistentDataStore.datastoreServer;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.junit.Test;

import utd.persistentDataStore.datastoreClient.*;

public class DatastoreClientTestCase
{
	int port = 10023;
	String address = "localhost";
//	String address = "";   // remote address
	

	private InetAddress getAddress() throws UnknownHostException
	{
		InetAddress inetAddress = InetAddress.getByName(address);
		return inetAddress;
	}

	@Test
	public void testWrite() throws Exception
	{
		InetAddress address = getAddress();
		DatastoreClient dsClient = new DatastoreClientImpl(address, port);

		byte data[] = generateData(100);
		dsClient.write("testData", data);
	}

	@Test
	public void testWrite2() throws Exception
	{
		InetAddress address = getAddress();
		DatastoreClient dsClient = new DatastoreClientImpl(address, port);

		byte data[] = generateData(100);
		String fname = "testData " + String.valueOf(System.currentTimeMillis());
		dsClient.write(fname, data);
	}

	@Test
	public void testRead() throws Exception
	{
		InetAddress address = getAddress();
		DatastoreClient dsClient = new DatastoreClientImpl(address, port);

		byte dataOut[] = generateData(100);
		dsClient.write("testData", dataOut);

		byte dataIn[] = dsClient.read("testData");
		assertEquals(100, dataIn.length);

		Checksum dataOutChecksum = new CRC32();
		dataOutChecksum.update(dataOut, 0, dataOut.length);
		long checksumOut = dataOutChecksum.getValue();

		Checksum dataInChecksum = new CRC32();
		dataInChecksum.update(dataIn, 0, dataIn.length);
		long checksumIn = dataInChecksum.getValue();

		assertEquals(checksumOut, checksumIn);
	}

	/**
	 * Attempt to read named data that does not exist on the server. Expect a
	 * ClientException
	 */
	@Test(expected = ClientException.class)
	public void testReadBroken() throws Exception
	{
		InetAddress address = getAddress();
		DatastoreClient dsClient = new DatastoreClientImpl(address, port);

		byte dataIn[] = dsClient.read("missingData");
	}

	@Test
	public void testDelete() throws Exception
	{
		InetAddress address = getAddress();
		DatastoreClient dsClient = new DatastoreClientImpl(address, port);

		byte data[] = generateData(10);
		dsClient.write("testData", data);
		dsClient.delete("testData");
	}

	/**
	 * Attempt to delete named data that does not exist on the server. Expect a
	 * ClientException
	 */
	@Test(expected = ClientException.class)
	public void testDeleteBroken() throws Exception
	{
		InetAddress address = getAddress();
		DatastoreClient dsClient = new DatastoreClientImpl(address, port);

		dsClient.delete("missingData");
	}

	@Test
	public void testDirectory() throws Exception
	{
		InetAddress address = getAddress();
		DatastoreClient dsClient = new DatastoreClientImpl(address, port);

		byte data[] = generateData(10);
		dsClient.write("testData", data);

		List<String> names = dsClient.directory();
		assertTrue(names.size() > 0);
		for (String name : names) {
			System.out.println(name);
		}
	}

	private byte[] generateData(int size)
	{
		byte data[] = new byte[size];
		Random random = new Random();
		random.nextBytes(data);
		return data;
	}
}
