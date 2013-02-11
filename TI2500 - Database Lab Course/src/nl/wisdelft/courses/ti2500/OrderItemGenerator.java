package nl.wisdelft.courses.ti2500;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OrderItemGenerator {
	private static OrderItemGenerator generator;
	private Random random;
	private Map<Long,List<PriceHistory>> pricesGrouped = null;

	private OrderItemGenerator() {
		random = new Random();
	}
	
	private BigDecimal GetPriceForProduct(Product p, Order o) {
		BigDecimal big = null;
		Date lastdate = null;
		List<PriceHistory> prices = pricesGrouped.get(p.id);
		for (int i = 0; big == null && i < prices.size(); i++) {
			PriceHistory ph = prices.get(i);
			if (ph.productId == p.id
					&& (lastdate == null || (lastdate.before(ph.priceInEffect) && ph.priceInEffect
							.before(o.orderPlaced)))) {
				big = prices.get(i).price;
			}
		}
		return big;
	}

	
	public List<OrderItem> GenerateOrders(int nrOfItems, int maxAmount, Order order,List<Product> products,List<PriceHistory> prices, int idStartNumber) {
		if(order==null || products==null || products.size()==0)
			return null;
		//preprocess the prices
		if(pricesGrouped==null){
			pricesGrouped = new HashMap<Long, List<PriceHistory>>();
			for(PriceHistory p:prices){
				if(!pricesGrouped.containsKey(p.productId))
					pricesGrouped.put(p.productId, new ArrayList<PriceHistory>());
				pricesGrouped.get(p.productId).add(p);
			}
			
		}
		
		List<OrderItem> items = new ArrayList<OrderItem>();
		for(int i=0;i<nrOfItems;i++){
			//randomly choose product
			int index = random.nextInt(products.size());
			//randomly choose amount
			int amount = random.nextInt(maxAmount);
			OrderItem item = new OrderItem();
			item.amount =amount;
			item.id  =idStartNumber+items.size();
			item.orderId = order.id;
			item.pricePerUnit = GetPriceForProduct(products.get(index), order);
			item.productId = products.get(index).id;
			items.add(item);		
		}		
		return items;
	}
	
	

	public static OrderItemGenerator GetOrderItemGenerator() {
		if (generator == null)
			generator = new OrderItemGenerator();
		return generator;
	}
}
