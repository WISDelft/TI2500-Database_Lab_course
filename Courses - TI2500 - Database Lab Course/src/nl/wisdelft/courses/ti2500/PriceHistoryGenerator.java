package nl.wisdelft.courses.ti2500;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class PriceHistoryGenerator {
	private static PriceHistoryGenerator generator;
	private Random random;

	private PriceHistoryGenerator() {
		random = new Random();
	}

	/***
	 * 
	 * @param product The product for which the Prices should be determined
	 * @param lowerBound The minimum starting price for products (>0)
	 * @param upperBound The maximum starting price for products
	 * @param percentIncreaseUpperBound The maximum percent increase a product can have in 1 month (0-100)
	 * @param increaseChance The percent chance that the price of the product will increase (0-100)
	 * @param durationInYears The number of years in the history.
	 * @return
	 */
	public List<PriceHistory> GeneratePriceHistory(Product product,int lowerBound, int upperBound,int percentIncreaseUpperBound,int increaseChance, int durationInYears, int idStartNumber) {
		if(product==null)
			return null;
		List<PriceHistory> histories = new ArrayList<PriceHistory>();
		//create initial price;
		BigDecimal lastPrice = new BigDecimal(random.nextInt(upperBound-lowerBound)+lowerBound+"."+random.nextInt(100));
		//create the initial date;
		Calendar cal = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR)-durationInYears-1,Calendar.JANUARY,1);
		
		PriceHistory ph = new PriceHistory();
		ph.productId = product.id;
		ph.price = lastPrice;	
		ph.priceInEffect = cal.getTime();
		ph.id = idStartNumber;
		histories.add(ph);
		
		
		//determine price increases
		for(int i=0;i<12*durationInYears;i++){
			cal.add(Calendar.MONTH, 1);
			if(random.nextInt(100)<increaseChance-1){
				PriceHistory ph2 = new PriceHistory();
				ph2.productId = product.id;
				BigDecimal increase = BigDecimal.valueOf((random.nextInt(percentIncreaseUpperBound)+1)/100.0+1);
				lastPrice = lastPrice.multiply(increase);
				ph2.price = lastPrice.setScale(2, RoundingMode.HALF_EVEN);
				ph2.priceInEffect = cal.getTime();
				ph2.id = idStartNumber+histories.size();
				histories.add(ph2);
			}
		}
		return histories;
	}

	public static PriceHistoryGenerator GetProductGenerator() {
		if (generator == null)
			generator = new PriceHistoryGenerator();
		return generator;
	}
}
