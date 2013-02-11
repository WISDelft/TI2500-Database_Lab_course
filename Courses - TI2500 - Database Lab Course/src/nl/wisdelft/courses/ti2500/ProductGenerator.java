package nl.wisdelft.courses.ti2500;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProductGenerator {
	private List<String> words;
	private static ProductGenerator generator;
	private Random random;

	private final String wordsFilePath = "./data/ListOfWords_EN.txt";

	private ProductGenerator() {
		random = new Random();
		try {
			words = ReadNameFile(wordsFilePath);
		} catch (IOException ex) {
			System.err.println("Cannot initialize ProductGenerator.");
			ex.printStackTrace();
			System.exit(1);
		}
	}

	/***
	 * 
	 * @param lowerBound minimum words in description
	 * @param upperBound maximum words in description
	 * @return A product with random description
	 */
	public Product GenerateProduct(int lowerBound, int upperBound) {
		Product p = new Product();
		StringBuilder builder = new StringBuilder();
		int nrWords = random.nextInt(upperBound-lowerBound)+lowerBound;
		for(int i=0;i<nrWords;i++){
			builder.append(words.get(random.nextInt(words.size())));
			builder.append(" ");
		}
		p.description = builder.toString();
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

	public static ProductGenerator GetProductGenerator() {
		if (generator == null)
			generator = new ProductGenerator();
		return generator;
	}
}
