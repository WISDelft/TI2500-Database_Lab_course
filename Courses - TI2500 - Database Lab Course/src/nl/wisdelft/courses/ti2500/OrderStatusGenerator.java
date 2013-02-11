package nl.wisdelft.courses.ti2500;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class OrderStatusGenerator {

	private static OrderStatusGenerator generator;
	private Random random;

	private OrderStatusGenerator() {
		random = new Random();
	}

	public List<OrderStatus> GenerateOrderStatussus(Order order,
			int goodnessOfCompany, int maxNrDaysBeforeAccepted,
			int chanceNotPaid, int maxNrDaysBeforePaid,
			int maxDaysBeforeShipping, int maxDaysBeforeArrival,
			int maxDaysBeforeCompleted, int idStartNumber) {
		// Placed, Accepted, Paid, Shipped, Arrived, Completed, Cancelled;
		if (order == null)
			return null;
		List<OrderStatus> statusses = new ArrayList<OrderStatus>();
		// The starting date
		Calendar cal = new GregorianCalendar();
		cal.setTime(order.orderPlaced);

		// The order immediate gets the status Placed
		OrderStatus os = new OrderStatus();
		os.id = idStartNumber + statusses.size();
		os.orderStatusChange = order.orderPlaced;
		os.orderId = order.id;
		os.orderStatusDescriptionId = OrderStatusDescription.Status.Placed
				.ordinal();
		statusses.add(os);

		// All orders are accepted
		int daysToAccept = random.nextInt(maxNrDaysBeforeAccepted);
		cal.add(Calendar.DAY_OF_YEAR, daysToAccept);
		os = new OrderStatus();
		os.id = idStartNumber + statusses.size();
		os.orderId = order.id;
		os.orderStatusChange = cal.getTime();
		os.orderStatusDescriptionId = OrderStatusDescription.Status.Accepted
				.ordinal();
		statusses.add(os);

		// not all orders are paid
		if (random.nextInt(100) < chanceNotPaid*goodnessOfCompany/100.0) {
			// paid
			// determine the amount of days before paying
			int daysToPaid = random.nextInt(maxNrDaysBeforePaid);
			cal.add(Calendar.DAY_OF_YEAR, daysToPaid);
			os = new OrderStatus();
			os.id = idStartNumber + statusses.size();
			os.orderId = order.id;
			os.orderStatusChange = cal.getTime();
			os.orderStatusDescriptionId = OrderStatusDescription.Status.Paid
					.ordinal();
			statusses.add(os);
		} else {
			// not paid
			// the order is cancelled
			int daysToCancel = random.nextInt(60);
			cal.add(Calendar.DAY_OF_YEAR, daysToCancel);
			os = new OrderStatus();
			os.id = idStartNumber + statusses.size();
			os.orderId = order.id;
			os.orderStatusChange = cal.getTime();
			os.orderStatusDescriptionId = OrderStatusDescription.Status.Cancelled
					.ordinal();
			statusses.add(os);
			return statusses;
		}

		// All paid orders are shipped
		int daysToShip = random.nextInt(maxDaysBeforeShipping);
		cal.add(Calendar.DAY_OF_YEAR, daysToShip);
		os = new OrderStatus();
		os.id = idStartNumber + statusses.size();
		os.orderId = order.id;
		os.orderStatusChange = cal.getTime();
		os.orderStatusDescriptionId = OrderStatusDescription.Status.Shipped
				.ordinal();
		statusses.add(os);

		// all shipped orders arrive
		int daysToArrive = random.nextInt(maxDaysBeforeArrival);
		cal.add(Calendar.DAY_OF_YEAR, daysToArrive);
		os = new OrderStatus();
		os.id = idStartNumber + statusses.size();
		os.orderId = order.id;
		os.orderStatusChange = cal.getTime();
		os.orderStatusDescriptionId = OrderStatusDescription.Status.Arrived
				.ordinal();
		statusses.add(os);

		// all arrived orders are completed.
		int daysToCompleted = random.nextInt(maxDaysBeforeCompleted);
		cal.add(Calendar.DAY_OF_YEAR, daysToCompleted);
		os = new OrderStatus();
		os.id = idStartNumber + statusses.size();
		os.orderId = order.id;
		os.orderStatusChange = cal.getTime();
		os.orderStatusDescriptionId = OrderStatusDescription.Status.Completed
				.ordinal();
		statusses.add(os);

		return statusses;
	}

	public static OrderStatusGenerator GetOrderStatusGenerator() {
		if (generator == null)
			generator = new OrderStatusGenerator();
		return generator;
	}
}
