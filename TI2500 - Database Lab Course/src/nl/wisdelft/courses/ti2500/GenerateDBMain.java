package nl.wisdelft.courses.ti2500;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GenerateDBMain {

	public static void main(String[] args) {
		System.out.println("Starting...");
		long now = System.nanoTime();
		int i_companies = 1000;
		int i_products = 500;
		int i_durationInYears = 2;
		String schemaName = String.format("c%sp%sy%s",i_companies,i_products,i_durationInYears);
		String dir = "/Users/oosterman/Documents/Datasets/TI2500 DB/Lab course 2012-2013/"+schemaName+"/";
		//check if folder exists, else create
		File f_dir = new File(dir);
		if(f_dir.exists())
			System.out.println("Directory exists. Writing output to '"+dir+"'.");
		if(!f_dir.exists()){
			f_dir.mkdirs();
			System.out.println("Directory created. Writing output to '"+dir+"'.");
		}
		else if(f_dir.exists() && f_dir.isFile()){
			System.err.println("Specify a directory.");
			System.exit(1);
		}

		// Get the Data
		try {
			Database db = new Database();
			List<Company> companies = db.GetCompanies(i_companies , 0);
			List<Domain> domains = db.GetDomains(10, 0);
			List<CompanyDomainLink> cdlinks = db.GetCompanyDomainLinks(companies, domains, 1, 3, 0);
			List<Person> persons = db.GetPersons(companies, 1, 20);
			List<ContactPerson> contactPersons = db.GetContactPersons(persons, 1, 4, 0);
			List<Product> products = db.GetProducts(i_products, 0, 100, 0);
			List<PriceHistory> prices = db.GetPriceHistories(products, 20, 2000, 5, 10,i_durationInYears, 0);
			List<ProductDomainLink> pdlinks = db.GetProductDomainLinks(domains, products, 0, 3, 0);
			List<Order> orders = db.GetOrders(companies, persons, 50, 300, i_durationInYears, 0);
			System.out.println("Starting writing orderstatus...");
			db.WriteOrderStatussus(orders, 10, 10, 10, 10, 10, 10, 0,dir);
			System.out.println("Starting writing orderitems...");
			db.WriteOrderItems(1, 10, 1000, orders, products, prices, 0,dir);
			System.out.println("Done creating...");

			BufferedWriter writer = null;

			writer = new BufferedWriter(new FileWriter(new File(dir+"create.sql")));

			// Create DB
			// writer.write("CREATE DATABASE " + dbName + ";\n");
			// Create Schema
			writer.write("CREATE SCHEMA " + schemaName + ";\n");
			// Create Tables
			writer.write(PriceHistory.toDB(schemaName) + "\n");
			writer.write(Product.toDB(schemaName) + "\n");
			writer.write(ProductDomainLink.toDB(schemaName) + "\n");
			writer.write(Domain.toDB(schemaName) + "\n");
			writer.write(CompanyDomainLink.toDB(schemaName) + "\n");
			writer.write(Company.toDB(schemaName) + "\n");
			writer.write(ContactPerson.toDB(schemaName) + "\n");
			writer.write(Person.toDB(schemaName) + "\n");
			writer.write(Order.toDB(schemaName) + "\n");
			writer.write(OrderStatus.toDB(schemaName) + "\n");
			writer.write(OrderItem.toDB(schemaName) + "\n");
			writer.write(OrderStatusDescription.toDB(schemaName) + "\n");

			writer.flush();
			writer.close();

			// Insert Data
			writer = new BufferedWriter(new FileWriter(new File(dir+"Company.csv")));
			for (Company c : companies) {
				writer.write(c.id + "," + c.companyName + "\n");
			}
			writer.flush();
			writer.close();
			
			writer = new BufferedWriter(new FileWriter(new File(dir+"PriceHistory.csv")));
			for (PriceHistory ph : prices) {
				writer.write(ph.id + "," + ph.productId + ","+ph.priceInEffect+","+ph.price + "\n");
			}
			writer.flush();
			writer.close();

			writer = new BufferedWriter(new FileWriter(new File(dir+"CompanyDomainLink.csv")));
			for (CompanyDomainLink c : cdlinks) {
				writer.write(c.id + "," + c.companyId + "," + c.domainId + "\n");
			}
			writer.flush();
			writer.close();

			writer = new BufferedWriter(new FileWriter(new File(dir+"ContactPerson.csv")));
			for (ContactPerson c : contactPersons) {
				writer.write(c.id + "," + c.personId + "," + c.companyId + "\n");
			}
			writer.flush();
			writer.close();

			writer = new BufferedWriter(new FileWriter(new File(dir+"Domain.csv")));
			for (Domain d : domains) {
				writer.write(d.id + "," + d.description + "\n");
			}
			writer.flush();
			writer.close();

			writer = new BufferedWriter(new FileWriter(new File(dir+"Order.csv")));
			for (Order o : orders) {
				writer.write(o.id + "," + o.companyId + "," + o.personId + "," + o.orderPlaced + "\n");
			}
			writer.flush();
			writer.close();

			writer = new BufferedWriter(new FileWriter(new File(dir+"OrderStatusDescription.csv")));
			for (OrderStatusDescription o : OrderStatusDescription.GetOrderStatusDescriptions()) {
				writer.write(o.id + "," + o.description + "\n");
			}
			writer.flush();
			writer.close();

			writer = new BufferedWriter(new FileWriter(new File(dir+"Person.csv")));
			for (Person p : persons) {
				writer.write(p.id + "," + p.companyId + "," + p.firstName + "," + p.lastName + "\n");
			}
			writer.flush();
			writer.close();

			writer = new BufferedWriter(new FileWriter(new File(dir+"Product.csv")));
			for (Product p : products) {
				writer.write(p.id + "," + p.description + "\n");
			}
			writer.flush();
			writer.close();

			writer = new BufferedWriter(new FileWriter(new File(dir+"ProductDomainLink.csv")));
			for (ProductDomainLink p : pdlinks) {
				writer.write(p.id + "," + p.productId + "," + p.domainId + "\n");
			}
			writer.flush();
			writer.close();

			// load insert Data
			String command = "\\copy "+schemaName+".%s FROM '%s' DELIMITER AS ',';\n";
			writer = new BufferedWriter(new FileWriter(new File(dir+"load.sql")));
			String[] tableNames = { "PriceHistory", "Order", "OrderItem", "OrderStatus", "OrderStatusDescription",
					"Person", "ContactPerson", "Domain", "CompanyDomainLink", "ProductDomainLink", "Product", "Company" };
			for (String name : tableNames) {
				writer.write(String.format(command, name, name + ".csv") + "\n");
			}
			writer.flush();
			writer.close();

			System.out.println("Done writing...");
			long duration = System.nanoTime() - now;
			System.out.println("Seconds elapsed: " + duration / 1000000000);

		}
		catch (IOException ex) {
			ex.printStackTrace();
		}

	}
}
