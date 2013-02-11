package nl.wisdelft.courses.ti2500;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CompanyGenerator {
	private List<String> companyNames;
	private static CompanyGenerator generator;
	private Random random;
	private int companiesCreated;

	private final String companyNamesFilePath = "./data/ListOfCompanyNames_EN.txt";

	private CompanyGenerator() {
		random = new Random();
		try {
			companyNames = ReadNameFile(companyNamesFilePath);
		} catch (IOException ex) {
			System.err.println("Cannot initialize CompanyGenerator.");
			ex.printStackTrace();
			System.exit(1);
		}

	}	

	/***
	 * Generates a random company
	 * 
	 * @return Company with only a name
	 */
	public Company GenerateCompany() {
		if (companiesCreated == companyNames.size()) {
			System.out
					.println("Mo more unique company names. Returning null instead.");
			return null;
		} else {
			Company c = new Company();
			c.companyName = companyNames.get(companiesCreated++);
			return c;
		}
	}

	/***
	 * 
	 * @param path
	 *            The file to read
	 * @return List of Strings (in random order) that resemble company names
	 * @throws IOException
	 */
	private List<String> ReadNameFile(String path) throws IOException {
		File f = new File(path);
		List<String> names = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(f));
		while (reader.ready()) {
			if (names.size() == 0)
				names.add(reader.readLine().trim());
			else
				names.add(random.nextInt(names.size()), reader.readLine()
						.trim());
		}
		reader.close();
		return names;
	}

	public int maxNrOfCompaniesAvailable() {
		return companyNames.size();
	}

	public static CompanyGenerator GetCompanyGenerator() {
		if (generator == null)
			generator = new CompanyGenerator();
		return generator;
	}
}
