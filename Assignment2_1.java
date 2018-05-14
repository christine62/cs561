package cs561;
/**
 * Author: Jiahui Bi
 * CWID: 10436836
 * Assignment 2 
 * Query 1
 * For each customer, product and state combination, compute (1) the customer's average sale of this product for the state, 
 * (2) the average sale of the product and the customer but for the other states and 
 * (3) the customer’s average sale for the given state, but for the other products.
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Assignment2_1 {
	
	private static ArrayList<ArrayList> MFtable = new ArrayList<>();
	private static ArrayList<String> GroupingAttributes = new ArrayList<>();
	
	public static void main(String[] args)
	{
		String usr ="postgres";
		String pwd ="postgres";
		String url ="jdbc:postgresql://localhost:5432/";

		try
		{
			Class.forName("org.postgresql.Driver");
			System.out.println("Success loading Driver!");
		}

		catch(Exception e)
		{
			System.out.println("Fail loading Driver!");
			e.printStackTrace();
		}

		try
		{
			Connection conn = DriverManager.getConnection(url, usr, pwd);
			System.out.println("Success connecting server!");

			Statement stmt = conn.createStatement();
			ResultSet rs1 = stmt.executeQuery("SELECT * FROM Sales");
			while (rs1.next())
			{
	            String str = rs1.getString("cust") + ", " + rs1.getString("prod") + ", " + rs1.getString("state");
	            if (!GroupingAttributes.contains(str)) {
	                GroupingAttributes.add(str);
	            }
			}
			Collections.sort(GroupingAttributes);
			for (int i = 0; i < GroupingAttributes.size(); i++) {
				ArrayList arrayList = new ArrayList();
                arrayList.add(GroupingAttributes.get(i));
                arrayList.addAll(Arrays.asList(0, 0, 0, 0, 0, 0));
                
                MFtable.add(arrayList);
			}
			ResultSet rs = stmt.executeQuery("SELECT * FROM Sales");
			while (rs.next())
			{//first scan
				String str = rs.getString("cust") + ", " + rs.getString("prod") + ", " + rs.getString("state");
	            for (int i = 0; i < MFtable.size(); i++) {
	            	
	            	if (((String) MFtable.get(i).get(0)).equals(str)) {
	            		//update cust_sum, cust_count
	            		MFtable.get(i).set(1, (Integer) MFtable.get(i).get(1) + Integer.valueOf(rs.getString("quant")));
	            		MFtable.get(i).set(2, (Integer) MFtable.get(i).get(2) + 1);
	            	}
	            	else if (rs.getString("cust").equals(((String) MFtable.get(i).get(0)).split(", ")[0]) && rs.getString("prod").equals(((String) MFtable.get(i).get(0)).split(", ")[1])) {
	            		//update other_state_sum, other_state_count
	            		MFtable.get(i).set(3, (Integer) MFtable.get(i).get(3) + Integer.valueOf(rs.getString("quant")));
	            		MFtable.get(i).set(4, (Integer) MFtable.get(i).get(4) + 1);
	            	}
	            	else if (rs.getString("cust").equals(((String) MFtable.get(i).get(0)).split(", ")[0]) && rs.getString("state").equals(((String) MFtable.get(i).get(0)).split(", ")[2])) {
	            		//update other_prod_sum, other_prod_count
	            		MFtable.get(i).set(5, (Integer) MFtable.get(i).get(5) + Integer.valueOf(rs.getString("quant")));
	            		MFtable.get(i).set(6, (Integer) MFtable.get(i).get(6) + 1);
	            	}
	            }
			}
			System.out.printf("%-10s %-9s %-7s %-10s %-17s %-16s%n", "CUSTOMER", "PRODUCT", "STATE", "CUST_AVG", "OTHER_STATE_AVG","OTHER_PROD_AVG");
			System.out.printf("%-10s %-9s %-7s %-10s %-17s %-16s%n", "========", "=======", "=====", "========", "===============", "=============");
			
	        for (ArrayList a : MFtable) {
	            System.out.printf("%-10s",((String) a.get(0)).split(", ")[0]);
	            System.out.printf("%-11s",((String) a.get(0)).split(", ")[1]);
	            System.out.printf("%-7s",((String) a.get(0)).split(", ")[2]);
            	    System.out.printf("%-10s",(Integer) a.get(1)/ (Integer) a.get(2));
                System.out.printf("%-17s",(Integer) a.get(3)/ (Integer) a.get(4));
                System.out.printf("%-16s",(Integer) a.get(5)/ (Integer) a.get(6));
	            System.out.println();        
	        }
		}

		catch(SQLException e)
		{
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}

	}
}