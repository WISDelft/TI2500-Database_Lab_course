package nl.wisdelft.courses.ti2500;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class OrderGenerator {
	private static OrderGenerator generator;
	private Random random;

	private OrderGenerator() {
		random = new Random();
	}

	
	public List<Order> GenerateOrders(int nrOfOrders, Company company,List<Person> persons,int durationInYears, int idStartNumber) {
		if(company==null || persons==null)
			return null;
		List<Order> orders = new ArrayList<Order>();
		//create the initial date;
		Calendar cal = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR)-durationInYears-1,Calendar.JANUARY,1);
		//randomize orders in each year
		for(int i=0;i<durationInYears;i++){
			for(int j=0;j<nrOfOrders;j++){
				int dayOfYear = random.nextInt(355)+1;
				cal.set(Calendar.DAY_OF_YEAR, dayOfYear);
				Order order = new Order();
				order.companyId = company.id;
				order.orderPlaced = cal.getTime();
				if(persons.size()>0)
					order.personId = persons.get(random.nextInt(persons.size())).id;
				else
					order.personId = -1;
				order.id = idStartNumber+orders.size();				
				orders.add(order);
			}
			cal.add(Calendar.YEAR, 1);
		}
		
		return orders;
	}

	public static OrderGenerator GetOrderGenerator() {
		if (generator == null)
			generator = new OrderGenerator();
		return generator;
	}
}
