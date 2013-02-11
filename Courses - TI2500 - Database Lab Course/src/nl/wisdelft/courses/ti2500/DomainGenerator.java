package nl.wisdelft.courses.ti2500;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DomainGenerator {
	private List<String> domains;
	private static DomainGenerator generator;
	private Random random;
	private int domainsCreated;

	private final String domainsFilePath = "./data/ListOfDomains_EN.txt";

	private DomainGenerator() {
		random = new Random();
		try {
			domains = ReadNameFile(domainsFilePath);
		} catch (IOException ex) {
			System.err.println("Cannot initialize DomainGenerator.");
			ex.printStackTrace();
			System.exit(1);
		}
		
	}

	/***
	 * Generates a random Domain
	 * 
	 * @return Domain with only a name
	 */
	public Domain GenerateDomain() {
		if (domainsCreated == domains.size()) {
			System.out
					.println("Mo more unique domain names. Returning null instead.");
			return null;
		} else {
			Domain d = new Domain();
			d.description = domains.get(domainsCreated++);
			return d;
		}
	}

	/***
	 * 
	 * @param path
	 *            The file to read
	 * @return List of Strings (in random order) that resemble domain names
	 * @throws IOException
	 */
	private List<String> ReadNameFile(String path) throws IOException {
		File f = new File(path);
		List<String> names = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(f));
		while (reader.ready()) {
			if(names.size()==0)
				names.add(reader.readLine().trim());
			else
				names.add(random.nextInt(names.size()), reader.readLine().trim());
		}
		reader.close();
		return names;
	}

	public int maxNrOfDomainsAvailable() {
		return domains.size();
	}

	public static DomainGenerator GetDomainGenerator() {
		if (generator == null)
			generator = new DomainGenerator();
		return generator;
	}

}
