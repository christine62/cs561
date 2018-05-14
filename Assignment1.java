package cs561;
/**
 * Author: Jiahui Bi
 * CWID: 10436836
 * Assignment 1 
 * 
 * Query 1
 * For each customer compute the minimum and maximum sales quantities along with the corresponding products (purchased), 
 * dates (i.e., dates of those minimum and maximum sales quantities) and the state in which the sale transaction took place. 
 * If there are >1 occurrences of the min or max, choose one – do not display all.
 * For the same customer, compute the average sales quantity.
 * 
 * Query2
 * For each combination of customer and product, output the maximum sales quantities for NJ,
 *  minimum sales quantities for NY and CT in 3 separate columns. 
 *  Like the first report, display the corresponding dates. 
 *  Furthermore, for NY and NJ, include only the sales that occurred earlier than 2009; for CT, include all sales.
 *  a.Instruction:
    1.Run Eclipse.
    2. Create new project named "cs561"
    Import the Assignment1.java file
    3. Add external JAR "postgresql-9.4-1203.jdbc4.jar" into Eclipse:
    click "Project" and go to "Properties"
    select "Java Build Path"
    select "Libraries"
    click "Add External JARs"
    select the location of JDBC
    click "OK"
    4.Change the database password to your database password at line87
    5. Run Program:
    Right Click --> Choose Run As --> 1. Java Application
    
 *  b. Data Structure
 *  HashMap
 *  It stores the data in form of key-value pair where key must be unique. So it's easier to get the data.
 *  No order of data is maintained. It provides the constant time performance for the basic operations.
 *  Report1:
 *  		key: cust
 *  		value: cust, min_q, min_cust, min_date, min_state, max_q, max_cust, max_date, mqx_state, average
 *  Report2:
 *  		key: cust+prod
 *  		value: cust, prod, nj_max, nj_date, ny_min, ny_date, ct_min, ct_date
 *  c. Algorithm
 *  Report1:
 *  MAX
 *  	   if (key(new data) = key(existing data) and quant(new data) > max(existing data)) 
 *     		max(existing data) = quant(new data)
 *  MIN
 *     if (key(new data) = key(existing data) and quant(new data) < min(existing data)) 
 *       	min(existing data) = quant(new data)
 *  AVG
 *     if (key(new data) == key(existing data)) 
 *       	sum += quant(new data)
 *       	count += 1
 *       	avg = sum / count
 * Report2:
 * NJ_MAX
 * 		if (key(new data) = key(existing data), state = 'NJ', quant(new data) > nj_max(existing data), year < 2009) 
 *       	nj_max(existing data) = quant(new data)
 * NY_MIN
 * 		if (key(new data) = key(existing data), state = 'NY', quant(new data) < ny_min(existing data), year < 2009) 
 *       	ny_min(existing data) = quant(new data)
 * CT_MIN
 * 		if (key(new data) = key(existing data), state = 'CT', quant(new data) < ct_min(existing data)) 
 *       	ct_min(existing data) = quant(new data)

 */
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;


public class Assignment1 {
	// transform (year, month, day) into date(mm/dd/yyyy)
	public static String date(String year, String month, String day) {
		if (Integer.parseInt(month) < 10) {
			month = "0" + month;
		}
		if (Integer.parseInt(day) < 10) {
			day = "0" + day;
		}
		return month + "/" + day + "/" + year;
	}	
	
	public static void main(String[] args) {
		String usr = "postgres";
		String pwd = "postgres";
		String url = "jdbc:postgresql://localhost:5432/";
		
		HashMap<String, Result1> query1 = new HashMap<String, Result1> ();
		// key = cust, value = result1(cust, min_q, min_cust, min_date, min_state, max_q, max_cust, max_date, mqx_state, count, sum)
		
		HashMap<String, Result2> query2 = new HashMap<String, Result2> ();
		// key = comb(prod + cust), value = result2(cust, prod, nj_max, nj_date, ny_min, ny_date, ct_min, ct_date)
		
		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("Success loading Driver!");
		}
		
		catch(Exception e) {
			System.out.println("Fail loading Driver!");
			e.printStackTrace();
		}
		
		try {
			Connection conn = DriverManager.getConnection(url, usr, pwd);
			System.out.println("Success connecting server!");
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Sales");
			
			while (rs.next()) {
				String cust = rs.getString("cust");
				String prod = rs.getString("prod");
				String state = rs.getString("state");
				int quant = Integer.parseInt(rs.getString("quant"));
				String year = rs.getString("year");
				String month = rs.getString("month");
				String day = rs.getString("day");
				String date = date(year, month, day);//mm/dd/yyyy
				String comb = cust + prod;
				
				Result1 result1 = new Result1(cust, Integer.MAX_VALUE, null, null, null, -1, null, null, null, 0, 0);
				Result2 result2 = new Result2(cust, prod, -1, null, Integer.MAX_VALUE, null, Integer.MAX_VALUE, null);
				// result1(cust, min_q, min_cust, min_date, min_state, max_q, max_cust, max_date, mqx_state, count, sum)
				// result2(cust, prod, nj_max, nj_date, ny_min, ny_date, ct_min, ct_date)
				
				// add data if there is not the same key in HashMap
				if (!query1.containsKey(cust)) {
					query1.put(cust, result1);
				}
				if (!query2.containsKey(comb)) {
					query2.put(comb, result2);
				}
				
				// for each data existing in the HashMap(report1), check
				for (HashMap.Entry <String, Result1> entry : query1.entrySet()) {
					
					// if the new one have the same key and quant is smaller, change the value(quant, prod, date, state) of minimum
					if (entry.getKey().equals(cust) && entry.getValue().getMin() > quant) {
						entry.getValue().setMin(quant, prod, date, state);
					}
					// if the new one have the same key and quant is bigger, change the value(quant, prod, date, state) of maximum
					if (entry.getKey().equals(cust) && entry.getValue().getMax() < quant) {
						entry.getValue().setMax(quant, prod, date, state);
					}
					// if the new one have the same key, change the value of sum and count
					if (entry.getKey().equals(cust)) {
						entry.getValue().setSumCount(quant);
						
					}
				}
				
				// for each data existing in the HashMap(report2), check
				for (HashMap.Entry <String, Result2> entry : query2.entrySet()) {
					// if the new one have the same key, state is NJ, quant is biggrt, year earlier than 2009, get the value(quant, date) of NJ
					if (entry.getKey().equals(comb) && state.equals("NJ") && entry.getValue().getNJmax() < quant && Integer.parseInt(year) < 2009) {
						entry.getValue().setNJmax(quant, date);
					}
					
					// if the new one have the same key, state is NY, quant is smaller, year earlier than 2009, get the value(quant, date) of NY
					if (entry.getKey().equals(comb) && state.equals("NY") && entry.getValue().getNYmin() > quant && Integer.parseInt(year) < 2009) {
						entry.getValue().setNYmin(quant, date);
					}
					
					// if the new one have the same key, state is CT, quant is smaller, include all dates, get the value(quant, date) of CT
					if (entry.getKey().equals(comb) && state.equals("CT") && entry.getValue().getCTmin() > quant) {
						entry.getValue().setCTmin(quant, date);
					}
					
				}
			}
		}
		
		catch(SQLException e) {
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
		
		// sort by key(cust)
		List<HashMap.Entry<String, Result1>> report1_order = new ArrayList<HashMap.Entry<String, Result1>>(query1.entrySet());
		Collections.sort(report1_order, new Comparator<HashMap.Entry<String, Result1>>() {
			@Override
			public int compare(Entry<String, Result1> arg0, Entry<String, Result1> arg1) {
				return arg0.getKey().compareTo(arg1.getKey());
			}
		});
		
		// print output
		System.out.println("OUTPUT1");
		System.out.println();
		System.out.println("CUSTOMER   MIN_Q  MIN_PROD  MIN_DATE    ST  MAX_Q  MAX_PROD  MAX_DATE    ST  AVG_Q");
		System.out.println("=========  =====  ========  ==========  ==  =====  ========  ==========  ==  =====");
		for (HashMap.Entry<String, Result1> entry : report1_order) {
			entry.getValue().print();
		}	
		
		// sort by key(prod_cust)
		List<HashMap.Entry<String, Result2>> report2_order = new ArrayList<HashMap.Entry<String, Result2>>(query2.entrySet());
		Collections.sort(report2_order, new Comparator<HashMap.Entry<String, Result2>>() {
			@Override
			public int compare(Entry<String, Result2> arg0, Entry<String, Result2> arg1) {
				return arg0.getKey().compareTo(arg1.getKey());
			}
		});
		
		// print output
		System.out.println();
		System.out.println("OUTPUT2");
		System.out.println();
		System.out.println("CUSTOMER  PRODUCT   NJ_MAX  DATE        NY_MIN  DATE        CT_MIN  DATE");
		System.out.println("========  ========  ======  ==========  ======  ==========  ======  ==========");
		for (HashMap.Entry<String, Result2> entry : report2_order) {
			entry.getValue().print();
		}
	}
}

class Result1 {
	private String cust, min_prod, min_date, min_state, max_prod, max_date, max_state;
	private int min_q, max_q, count, sum;
	
	public Result1() {
	}
	public Result1(String cust, int min_q, String min_prod, String min_date, String min_state, int max_q, String max_prod, String max_date, String max_state, int count, int sum) {
		this.cust = cust;
		this.min_q = min_q;
		this.min_prod = min_prod;
		this.min_date = min_date;
		this.min_state = min_state;
		this.max_q = max_q;
		this.max_prod = max_prod;
		this.max_date = max_date;
		this.max_state = max_state;
		this.count = count;
		this.sum = sum;
	}
	public int getMin() {
		return min_q;
	}
	public int getMax() {
		return max_q;
	}
	
	
	public void setMin(int min_q, String min_prod, String min_date, String min_state) {
		this.min_q = min_q;
		this.min_prod = min_prod;
		this.min_date = min_date;
		this.min_state = min_state;
	}
	public void setMax(int max_q, String max_prod, String max_date, String max_state) {
		this.max_q = max_q;
		this.max_prod = max_prod;
		this.max_date = max_date;
		this.max_state = max_state;
	}
	public void setSumCount(int q) {
		sum += q;
		count++;
	}
	public void print() {
		System.out.printf("%-8s  ", cust);
		System.out.printf("%6s  ", min_q);
		System.out.printf("%-8s  ", min_prod);
		System.out.printf("%10s  ", min_date);
		System.out.printf("%-2s  ", min_state);
		System.out.printf("%5s  ", max_q);
		System.out.printf("%-8s  ", max_prod);
		System.out.printf("%10s  ", max_date);
		System.out.printf("%-2s  ", max_state);
		System.out.printf("%5s", sum / count);
		System.out.println("");
	}
}

class Result2 {
	private String cust, prod, nj_date, ny_date, ct_date;
	private int nj_max, ny_min, ct_min;
	
	public Result2() {
	}
	public Result2(String cust, String prod, int nj_max, String nj_date, int ny_min, String ny_date, int ct_min, String ct_date) {
		this.cust = cust;
		this.prod = prod;
		this.nj_max = nj_max;
		this.nj_date = nj_date;
		this.ny_min = ny_min;
		this.ny_date = ny_date;
		this.ct_min = ct_min;
		this.ct_date = ct_date;
	}
	public int getNJmax() {
		return nj_max;
	}
	public int getNYmin() {
		return ny_min;
	}
	public int getCTmin() {
		return ct_min;
	}
	public void setNJmax(int nj_max, String nj_date) {
		this.nj_max = nj_max;
		this.nj_date = nj_date;
	}
	public void setNYmin(int ny_min, String ny_date) {
		this.ny_min = ny_min;
		this.ny_date = ny_date;
	}
	public void setCTmin(int ct_min, String ct_date) {
		this.ct_min = ct_min;
		this.ct_date = ct_date;
	}
	public void print() {
		System.out.printf("%-8s  ", cust);
		System.out.printf("%-8s  ", prod);
		if (nj_max == -1) {
			System.out.printf("%6s  ", "null");
		}
		else {
			System.out.printf("%6s  ", nj_max);
		}
		System.out.printf("%10s  ", nj_date);
		if (ny_min == Integer.MAX_VALUE) {
			System.out.printf("%6s  ", "null");
		}
		else {
			System.out.printf("%6s  ", ny_min);
		}
		System.out.printf("%10s  ", ny_date);
		if (ct_min == Integer.MAX_VALUE) {
			System.out.printf("%6s  ", "null");
		}
		else {
			System.out.printf("%6s  ", ct_min);
		}
		System.out.printf("%10s", ct_date);
		System.out.println("");
	}
}