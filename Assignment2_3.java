package cs561;
/**
 * Author: Jiahui Bi
 * CWID: 10436836
 * Assignment 2 
 * Query 3
 * For customer and product, find the month by which time, a half of the sales quantities have been purchased. 
 * Again for this query, the “YEAR” attribute is not considered. 
 * Another way to view this problem (problem #2 above) is to pretend all 500 rows of sales data are from the same year.
 */
import java.sql.*;

public class Assignment2_3 {

	public static void main(String[] args)
	{
		String usr ="postgres";
		String pwd ="postgres";
		String url ="jdbc:postgresql://localhost:5432/";
		
		int size = 500;
		
		String[] SalesRow=new String[10];			
		String[][] ResultRow=new String[size][17];  
		int SalesRows=0;
		int ResultRows=1;
		
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
			ResultSet rs = stmt.executeQuery("SELECT * FROM sales");
			
			while (rs.next())
			{

					SalesRow[0]=rs.getString("cust").toString();
					SalesRow[1]=rs.getString("prod").toString();
					SalesRow[2]=rs.getString("day").toString();
					SalesRow[3]=rs.getString("month").toString();
					SalesRow[4]=rs.getString("year").toString();
					SalesRow[5]=rs.getString("state").toString();
					SalesRow[6]=rs.getString("quant").toString();
					SalesRow[7]="0";
					SalesRow[8]="0";
					SalesRow[9]="0";

					if(SalesRows==0){
						ResultRow[1][0]=SalesRow[0];
						ResultRow[1][1]=SalesRow[1];
						for(int i = 2; i < 17; i++) {
							ResultRow[1][i]="0";
						}
						
					
					}
					for(int j=1;j<=ResultRows;j++){
						if(ResultRow[j][0].equals(SalesRow[0])&&ResultRow[j][1].equals(SalesRow[1])){
							ResultRow[j][15]=String.valueOf(Integer.parseInt(ResultRow[j][15])+
									   Integer.parseInt(SalesRow[6]));
							ResultRow[j][Integer.parseInt(SalesRow[3])+2]=String.valueOf(Integer.parseInt(ResultRow[j][Integer.parseInt(SalesRow[3])+2])+
									   Integer.parseInt(SalesRow[6]));	
							SalesRow[9]="1";
						}
						
						if(SalesRow[9]=="0"&&j==ResultRows){
							ResultRows=ResultRows+1;
							ResultRow[ResultRows][0]=SalesRow[0];
							ResultRow[ResultRows][1]=SalesRow[1];
							for(int i = 2; i < 17; i++) {
								ResultRow[ResultRows][i]="0";
							}
							
						}
					}
					
				SalesRows=SalesRows+1;
			}
			
			for(int j=1;j<=ResultRows;j++){
				for(int k=0;k<12;k++){
					if(Integer.parseInt(ResultRow[j][16])>(Integer.parseInt(ResultRow[j][15])/2)){
							ResultRow[j][2]=String.valueOf(k);
							break;
					}else{
						ResultRow[j][16]=String.valueOf(Integer.parseInt(ResultRow[j][16])+Integer.parseInt(ResultRow[j][k+3]));
					}				
				}
			}
			
			System.out.printf("%-7s %-7s %13s%n", "CUSTOMER", "PRODUCT", "1/2 PURCHASED BY MONTH");
			System.out.printf("%-7s %-7s %13s %n", "========", "=======", "======================");
			
			for (int k = 1; k <= ResultRows; k++) {
				for (int j = 0; j < 3; j++) {
					System.out.printf("%8s",ResultRow[k][j]);
					}
					System.out.println(" ");
				}
		}

		catch(SQLException e)
		{
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}

	}
}
