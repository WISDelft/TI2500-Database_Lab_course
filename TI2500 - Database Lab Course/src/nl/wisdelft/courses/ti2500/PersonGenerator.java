package nl.wisdelft.courses.ti2500;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PersonGenerator {
	private List<String> firstNames;
	private List<String> lastNames;
	private static PersonGenerator generator;
	private Random random;

	private final String firstNamesFilePath = "./data/ListOfFirstNames_EN.txt";
	private final String lastNamesFilePath = "./data/ListOfLastNames_EN.txt";

	private PersonGenerator() {
		try {
			firstNames = ReadNameFile(firstNamesFilePath);
			lastNames = ReadNameFile(lastNamesFilePath);
		} catch (IOException ex) {
			System.err.println("Cannot initialize PersonGenerator.");
			ex.printStackTrace();
			System.exit(1);
		}
		random = new Random();
	}
	
	/***
	 * Generates a random person
	 * @return Person with first and last name, but no id
	 */
	public Person GeneratePerson(){
		Person p = new Person();
		//get random first name
		p.firstName = firstNames.get(random.nextInt(firstNames.size()));
		//get random last name
		p.lastName = lastNames.get(random.nextInt(lastNames.size()));
		return p;
	}

	private List<String> ReadNameFile(String path) throws IOException {
		File f = new File(path);
		List<String> names = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(f));
		while (reader.ready()) {
			names.add(reader.readLine().trim());
		}
		reader.close();
		return names;
	}

	public static PersonGenerator GetPersonGenerator() {
		if (generator == null)
			generator = new PersonGenerator();
		return generator;
	}
}
