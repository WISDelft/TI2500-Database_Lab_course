package nl.wisdelft.courses.ti2500;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Database {
	private Random random;

	public Database() {
		random = new Random();
	}

	/***
	 * 
	 * @param nrProducts
	 *            Amount of products to return
	 * @param lowerBound
	 *            Minimum number of words in description
	 * @param upperBound
	 *            Maximum number of words in description
	 * @param idStartNumber
	 * @return
	 */
	public List<Product> GetProducts(int nrProducts, int lowerBound, int upperBound, int idStartNumber) {
		ProductGenerator gen = ProductGenerator.GetProductGenerator();
		List<Product> products = new ArrayList<Product>();
		for (int i = 0; i < nrProducts; i++) {
			Product p = gen.GenerateProduct(lowerBound, upperBound);
			p.id = idStartNumber + products.size();
			products.add(p);
		}
		return products;
	}

	public List<OrderStatus> GetOrderStatussus(List<Order> orders, int maxNrDaysBeforeAccepted, int chanceNotPaid,
			int maxNrDaysBeforePaid, int maxDaysBeforeShipping, int maxDaysBeforeArrival, int maxDaysBeforeCompleted,
			int idStartNumber) {
		OrderStatusGenerator gen = OrderStatusGenerator.GetOrderStatusGenerator();
		List<OrderStatus> statusses = new ArrayList<OrderStatus>();
		for (Order order : orders) {
			int goodnessOfCompany = (int) order.companyId % 100;
			statusses.addAll(gen.GenerateOrderStatussus(order, goodnessOfCompany, maxNrDaysBeforeAccepted,
					chanceNotPaid, maxNrDaysBeforePaid, maxDaysBeforeShipping, maxDaysBeforeArrival,
					maxDaysBeforeCompleted, idStartNumber));
		}

		return statusses;
	}

	public void WriteOrderStatussus(List<Order> orders, int maxNrDaysBeforeAccepted, int chanceNotPaid,
			int maxNrDaysBeforePaid, int maxDaysBeforeShipping, int maxDaysBeforeArrival, int maxDaysBeforeCompleted,
			int idStartNumber,String path) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path+"OrderStatus.csv")));
		OrderStatusGenerator gen = OrderStatusGenerator.GetOrderStatusGenerator();
		List<OrderStatus> statusses;
		for (Order order : orders) {
			int goodnessOfCompany = (int) order.companyId % 100;
			statusses = gen.GenerateOrderStatussus(order, goodnessOfCompany, maxNrDaysBeforeAccepted, chanceNotPaid,
					maxNrDaysBeforePaid, maxDaysBeforeShipping, maxDaysBeforeArrival, maxDaysBeforeCompleted,
					idStartNumber);
			idStartNumber += statusses.size();

			for (OrderStatus o : statusses) {
				writer.write(o.id + "," + o.orderId + "," + o.orderStatusDescriptionId + "," + o.orderStatusChange
						+ "\n");
			}
			writer.flush();
		}
		writer.close();
	}

	/***
	 * 
	 * @param lowerBound
	 *            Minimum number of items in an order
	 * @param upperBound
	 *            Maximum number of items in an order
	 * @param maxAmount
	 *            Maximum amount per item
	 * @param orders
	 *            List of orders to generate orderitems for
	 * @param products
	 *            List of products to choose from
	 * @param prices
	 *            List of prices for the products
	 * @param idStartNumber
	 * @return
	 */
	public List<OrderItem> GetOrderItems(int lowerBound, int upperBound, int maxAmount, List<Order> orders,
			List<Product> products, List<PriceHistory> prices, int idStartNumber) {
		List<OrderItem> items = new ArrayList<OrderItem>();
		OrderItemGenerator gen = OrderItemGenerator.GetOrderItemGenerator();
		for (Order order : orders) {
			int nrOfOrderItems = random.nextInt(upperBound - lowerBound) + lowerBound;
			items.addAll(gen.GenerateOrders(nrOfOrderItems, maxAmount, order, products, prices,
					idStartNumber + items.size()));
		}
		return items;
	}

	/***
	 * 
	 * @param lowerBound minimum number of orderitems in order
	 * @param upperBound maximum number of orderitems in order
	 * @param maxAmount maximum amount per item
	 * @param orders List of orders
	 * @param products List of products
	 * @param prices List of prices of products
	 * @param idStartNumber
	 * @throws IOException
	 */
	public void WriteOrderItems(int lowerBound, int upperBound, int maxAmount, List<Order> orders,
			List<Product> products, List<PriceHistory> prices, int idStartNumber, String path) throws IOException {
		List<OrderItem> items;
		OrderItemGenerator gen = OrderItemGenerator.GetOrderItemGenerator();
		Writer writer = new BufferedWriter(new FileWriter(new File(path+"OrderItem.csv")));
		for (Order order : orders) {
			int nrOfOrderItems = random.nextInt(upperBound - lowerBound) + lowerBound;
			items = gen.GenerateOrders(nrOfOrderItems, maxAmount, order, products, prices, idStartNumber);
			idStartNumber += items.size();
			for (OrderItem o : items) {
				writer.write(o.id + "," + o.productId + "," + o.orderId + "," + o.amount + "," + o.pricePerUnit + "\n");
			}
			writer.flush();
		}
		writer.close();
	}

	/***
	 * 
	 * @param companies
	 *            List of companies to get order for
	 * @param persons
	 *            List of persons that can order
	 * @param lowerBound
	 *            Minimum number of orders per company per year
	 * @param upperBound
	 *            Maximum numbers of orders per company per year
	 * @param durationInYears
	 *            Number of years to get orders for
	 * @param idStartNumber
	 * @return
	 */
	public List<Order> GetOrders(List<Company> companies, List<Person> persons, int lowerBound, int upperBound,
			int durationInYears, int idStartNumber) {
		List<Order> orders = new ArrayList<Order>();
		OrderGenerator gen = OrderGenerator.GetOrderGenerator();
		// preprocess persons, group by company using hashtable
		HashMap<Long, List<Person>> hash = new HashMap<Long, List<Person>>();

		for (Person p : persons) {
			if (!hash.containsKey(p.companyId)) {
				hash.put(p.companyId, new ArrayList<Person>());
			}
			hash.get(p.companyId).add(p);
		}
		for (Company c : companies) {
			int nrOrders = random.nextInt(upperBound - lowerBound) + lowerBound;
			List<Order> o = gen.GenerateOrders(nrOrders, c, hash.get(c.id), durationInYears,
					idStartNumber + orders.size());
			orders.addAll(o);
		}
		return orders;

	}
	public List<ProductDomainLink> GetProductDomainLinks(List<Domain> domains, List<Product> products, int lowerBound,
			int upperBound, int idStartNumber) {
		List<ProductDomainLink> links = new ArrayList<ProductDomainLink>();
		for (Product p : products) {
			int nrDomains = random.nextInt(upperBound - lowerBound) + lowerBound;
			if (nrDomains > domains.size()) {
				System.out.println("Not enough distinct domains available for product. Using " + domains.size()
						+ " instead of " + nrDomains);
				nrDomains = domains.size();
			}
			// add X random domains
			Collections.shuffle(domains);
			for (int i = 0; i < nrDomains; i++) {
				ProductDomainLink link = new ProductDomainLink();
				link.id = idStartNumber + links.size();
				link.productId = p.id;
				link.domainId = domains.get(i).id;
				links.add(link);
			}
		}

		return links;

	}

	/***
	 * 
	 * @param persons
	 * @param lowerBound
	 *            If a company has at least one person, it also has at least one
	 *            contactperson.
	 * @param upperBound
	 * @param idStartNumber
	 * @return
	 */
	public List<ContactPerson> GetContactPersons(List<Person> persons, int lowerBound, int upperBound, int idStartNumber) {
		// correct lowerbound
		lowerBound = Math.max(lowerBound, 1);

		List<ContactPerson> contactPersons = new ArrayList<ContactPerson>();
		long companyId = -1;
		int cpCount = 0;
		int cpCompanyCount = 0;
		for (Person p : persons) {
			if (p.companyId != companyId) {
				cpCompanyCount = 0;
				cpCount = random.nextInt(upperBound - lowerBound) + lowerBound;
				companyId = p.companyId;
			}
			if (cpCompanyCount < cpCount) {
				ContactPerson cp = new ContactPerson();
				cp.companyId = companyId;
				cp.personId = p.id;
				cp.id = idStartNumber + contactPersons.size();
				contactPersons.add(cp);
				cpCompanyCount++;
			}
		}
		return contactPersons;

	}

	private List<Person> GetPersons(int nrOfPersons, int idStartNumber) {
		PersonGenerator gen = PersonGenerator.GetPersonGenerator();
		List<Person> persons = new ArrayList<Person>();
		for (int i = 0; i < nrOfPersons; i++) {
			Person p = gen.GeneratePerson();
			p.id = idStartNumber + i;
			persons.add(p);
		}
		return persons;
	}

	public List<Person> GetPersons(List<Company> companies, int lowerBound, int upperBound) {
		List<Person> persons = new ArrayList<Person>();
		for (Company c : companies) {
			int nrPersons = random.nextInt(upperBound - lowerBound) + lowerBound;
			List<Person> ps = GetPersons(nrPersons, persons.size());
			for (Person p : ps) {
				p.companyId = c.id;
				persons.add(p);
			}
		}
		return persons;
	}

	public List<Domain> GetDomains(int nrOfDomains, int idStartNumber) {
		DomainGenerator gen = DomainGenerator.GetDomainGenerator();
		int count = Math.min(nrOfDomains, gen.maxNrOfDomainsAvailable());
		if (count < nrOfDomains)
			System.out.println("Not enough domains available. Using " + count + " instead of " + nrOfDomains);
		List<Domain> domains = new ArrayList<Domain>(count);
		for (int i = 0; i < count; i++) {
			Domain d = gen.GenerateDomain();
			d.id = idStartNumber + i;
			domains.add(d);
		}
		return domains;

	}

	public List<Company> GetCompanies(int nrOfCompanies, int idStartNumber) {
		CompanyGenerator gen = CompanyGenerator.GetCompanyGenerator();
		int count = Math.min(nrOfCompanies, gen.maxNrOfCompaniesAvailable());
		if (count < nrOfCompanies)
			System.out.println("Not enough companies available. Using " + count + " instead of " + nrOfCompanies);
		List<Company> companies = new ArrayList<Company>(count);
		for (int i = 0; i < count; i++) {
			Company c = gen.GenerateCompany();
			c.id = idStartNumber + i;
			companies.add(c);
		}
		return companies;
	}

	/***
	 * 
	 * @param products
	 *            The products for which the Prices should be determined
	 * @param lowerBound
	 *            The minimum starting price for products (>0)
	 * @param upperBound
	 *            The maximum starting price for products
	 * @param percentIncreaseUpperBound
	 *            The maximum percent increase a product can have in 1 month
	 *            (0-100)
	 * @param increaseChance
	 *            The percent chance that the price of the product will increase
	 *            (0-100)
	 * @param durationInYears
	 *            The number of years in the history.
	 * @return
	 */
	public List<PriceHistory> GetPriceHistories(List<Product> products, int lowerBound, int upperBound,
			int percentIncreaseUpperBound, int increaseChance, int durationInYears, int idStartNumber) {
		List<PriceHistory> histories = new ArrayList<PriceHistory>();
		PriceHistoryGenerator gen = PriceHistoryGenerator.GetProductGenerator();
		for (Product p : products) {
			List<PriceHistory> ph = gen.GeneratePriceHistory(p, lowerBound, upperBound, percentIncreaseUpperBound,
					increaseChance, durationInYears, idStartNumber + histories.size());
			histories.addAll(ph);
		}
		return histories;
	}

	public void WritePriceHistories(List<Product> products, int lowerBound, int upperBound,
			int percentIncreaseUpperBound, int increaseChance, int durationInYears, int idStartNumber, String path) throws IOException {
		List<PriceHistory> histories;
		PriceHistoryGenerator gen = PriceHistoryGenerator.GetProductGenerator();
		Writer writer = new BufferedWriter(new FileWriter(new File(path+"PriceHistory.csv")));
		for (Product p : products) {
			histories = gen.GeneratePriceHistory(p, lowerBound, upperBound, percentIncreaseUpperBound, increaseChance,
					durationInYears, idStartNumber);
			idStartNumber += histories.size();
			for (PriceHistory ph : histories) {
				writer.write(ph.id + "," + ph.productId + "," + ph.priceInEffect + "," + ph.price + "\n");
			}
			writer.flush();
		}
		writer.close();
	}

	public List<CompanyDomainLink> GetCompanyDomainLinks(List<Company> companies, List<Domain> domains, int lowerBound,
			int upperBound, int idStartNumber) {
		List<CompanyDomainLink> links = new ArrayList<CompanyDomainLink>(companies.size() * (lowerBound + upperBound)
				/ 2);
		for (Company c : companies) {
			int linkCount = random.nextInt(upperBound - lowerBound) + lowerBound;
			List<Domain> ds = new ArrayList<Domain>(upperBound);
			// select random domains
			while (ds.size() < linkCount) {
				Domain d = domains.get(random.nextInt(domains.size()));
				if (!ds.contains(d))
					ds.add(d);
			}
			// add the links
			for (Domain d : ds) {
				CompanyDomainLink link = new CompanyDomainLink();
				link.companyId = c.id;
				link.domainId = d.id;
				link.id = idStartNumber + links.size();
				links.add(link);
			}
		}
		return links;
	}
}

class PriceHistory {
	long id;
	long productId;
	Date priceInEffect;
	BigDecimal price;

	public static String toDB(String schema) {
		String sql = "CREATE TABLE "+schema +".pricehistory (" + " id bigint," + " productId bigint,"
				+ " priceInEffect date," + " price numeric(10,2));";

		return sql;
	}
}

class Product {
	long id;
	String description;

	public static String toDB(String schema) {
		String sql = "CREATE TABLE "+schema +".product (" + " id bigint," + " description text);";

		return sql;
	}
}

class ProductDomainLink {
	long id;
	long productId;
	int domainId;

	public static String toDB(String schema) {
		String sql = "CREATE TABLE "+schema +".productdomainlink (" + " id bigint," + " productId bigint,"
				+ " domainId integer);";

		return sql;
	}
}

class OrderItem {
	long id;
	long productId;
	long orderId;
	int amount;
	BigDecimal pricePerUnit;

	public static String toDB(String schema) {
		String sql = "CREATE TABLE "+schema +".orderitem (" + " id bigint," + " productId bigint," + " orderId bigint,"
				+ " amount integer," + " pricePerUnit numeric(10,2));";

		return sql;
	}
}

class Order {
	long id;
	long companyId;
	long personId;
	Date orderPlaced;

	public String toString(String schema) {
		return "ID: " + id + "\nOrder placed: " + orderPlaced.toString() + "\ncompanyId: " + companyId + "\npersonId: "
				+ personId;
	}

	public static String toDB(String schema) {
		String sql = "CREATE TABLE "+schema +".order (" + " id bigint," + " companyId bigint," + "personId bigint,"
				+ " orderPlaced date);";

		return sql;
	}
}

class OrderStatus {
	long id;
	long orderId;
	int orderStatusDescriptionId;
	Date orderStatusChange;

	public static String toDB(String schema) {
		String sql = "CREATE TABLE "+schema +".orderstatus (" + " id bigint," + " orderId bigint,"
				+ " orderStatusDescriptionId integer," + " orderStatusChange date);";

		return sql;
	}
}

class OrderStatusDescription {
	int id;
	Status status;
	String description;

	public static String toDB(String schema) {
		String sql = "CREATE TABLE "+schema +".orderstatusdescription (" + " id integer,"
				+ " description character varying(100));";

		return sql;
	}

	public enum Status {
		Placed, Accepted, Paid, Shipped, Arrived, Completed, Cancelled;
	}

	public static List<OrderStatusDescription> GetOrderStatusDescriptions() {
		List<OrderStatusDescription> list = new ArrayList<OrderStatusDescription>();
		for (Status s : Status.values()) {
			OrderStatusDescription osd = new OrderStatusDescription();
			osd.id = s.ordinal();
			osd.description = s.toString();
			osd.status = s;
			list.add(osd);
		}
		return list;
	}
}

class Person {
	long id;
	long companyId;
	String firstName;
	String lastName;

	public static String toDB(String schema) {
		String sql = "CREATE TABLE "+schema +".person (" + " id bigint," + " companyId bigint,"
				+ " firstName character varying(100)," + " lastName character varying(100));";

		return sql;
	}
}

class ContactPerson {
	long id;
	long personId;
	long companyId;

	public static String toDB(String schema) {
		String sql = "CREATE TABLE "+schema +".contactperson (" + " id bigint," + " personId bigint,"
				+ " companyId bigint);";

		return sql;
	}
}

class Company {
	long id;
	String companyName;

	public static String toDB(String schema) {
		String sql = "CREATE TABLE "+schema +".company (" + " id bigint," + " companyName character varying(200));";

		return sql;
	}
}

class CompanyDomainLink {
	long id;
	long companyId;
	int domainId;

	public static String toDB(String schema) {
		String sql = "CREATE TABLE "+schema +".companydomainlink (" + " id integer," + " companyId bigint,"
				+ " domainId integer);";

		return sql;
	}
}

class Domain {
	int id;
	String description;

	public static String toDB(String schema) {
		String sql = "CREATE TABLE "+schema +".domain (" + " id integer," + " description character varying(100));";
		return sql;
	}

	public boolean equals(Object o) {
		if (o instanceof Domain && this.id == ((Domain) o).id)
			return true;
		else
			return false;
	}
}
