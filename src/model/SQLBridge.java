package model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;

import controller.Controller;

public class SQLBridge {
	
	public static Controller controller;
	
	public static Boolean isDatabaseOnline() {
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "")) {
			if(!isTableCreated("redditgrabber_meta")) {
				LogUtility.newLineToLog("Warning: Could not find meta info table. Trying to create it...");
				createMetaTable();
			}
			
            return true;
        }catch (CommunicationsException e) {
        	e.printStackTrace();
        	controller.status = "ERROR: Could not connect to SQL Database...";
        	//System.out.println(controller.status);
        	LogUtility.newLineToLog("ERROR: Could not connect to SQL Database. Is a localhost mySQL Server started?");
        	return false;
		}catch (Exception e) {
        	e.printStackTrace();
        	return false;
		}
	} 
	
	
    
	static Boolean createMetaTable() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", ""); Statement stmt = conn.createStatement();) {
        	
            stmt.executeUpdate("use reddit;");
            
            String sql = "CREATE TABLE redditgrabber_meta"
            		+ " (keyID INT  NOT NULL,"
            		+ " first VARCHAR(255),"
            		+ " last VARCHAR(255),"
            		+ " size VARCHAR(255),"
            		+ " files INT,"
            		+ " pulls INT,"
            		+ " path VARCHAR(255),"
            		+ " PRIMARY KEY (keyID))"; 
            stmt.executeUpdate(sql);
            
            ArrayList<String> stats = Stats.getStatArray();
            sql = "INSERT INTO `redditgrabber_meta` (`keyID`, `first`, `last`, `size`, `files`, `pulls`, `path`) VALUES "
            		+ "('0',"
            		+ " '"+stats.get(0)+"',"
            		+ " '"+stats.get(1)+"',"
            		+ " '"+stats.get(2)+"',"
            		+ " '"+stats.get(3)+"',"
            		+ " '"+stats.get(4)+"',"
            		+ " '"+stats.get(5).replace("\\", "\\\\")+"')";
            stmt.executeUpdate(sql);
            
            
            LogUtility.newLineToLog("Info: Created meta info table.");
            return true;
        }catch (CommunicationsException e) {
        	e.printStackTrace();
        	controller.status = "ERROR: Could not connect to SQL Database...";
        	LogUtility.newLineToLog("ERROR: Could not connect to SQL Database. Meta info table not created...");
		}catch (Exception e) {
        	e.printStackTrace();
		}
        return false;
	}
	
	public static void updateMetaInfoTable() {
		
		Boolean flag_continue = false;
		
		if(!isTableCreated("redditgrabber_meta")) {
			LogUtility.newLineToLog("Warning: Could not find meta info table. Trying to create it...");
			if(createMetaTable()) {
				flag_continue = true;
			}
		}else {
			flag_continue = true;
		}
		
		if(flag_continue) {
	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", ""); Statement stmt = conn.createStatement();) {
	        	
	            stmt.executeUpdate("use reddit;");
	            
	            ArrayList<String> stats = Stats.getStatArray();
	            String sql = " UPDATE `redditgrabber_meta` SET"
	            		+ " `first` = '"+stats.get(0)+"',"
	            		+ " `last` = '"+stats.get(1)+"',"
	            		+ " `size` = '"+stats.get(2)+"',"
	            		+ " `files` = '"+stats.get(3)+"',"
	            		+ " `pulls` = '"+stats.get(4)+"',"
	            		+ " `path` = '"+stats.get(5).replace("\\", "\\\\")+"'"
	            		+ " WHERE `redditgrabber_meta`.`keyID` = 0";
	            
	            
	            stmt.executeUpdate(sql);
	        }catch (CommunicationsException e) {
	        	e.printStackTrace();
	        	controller.status = "ERROR: Could not connect to SQL Database...";
	        	LogUtility.newLineToLog("ERROR: Could not connect to SQL Database. Meta info table not created...");
			}catch (Exception e) {
	        	e.printStackTrace();
			}
		}
	}
	
    static Boolean isDBCreated() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", ""); Statement stmt = conn.createStatement();) {
        	
            ResultSet resultSet = conn.getMetaData().getCatalogs();

            while (resultSet.next()) {
                String databaseName = resultSet.getString(1);
                if (databaseName.equals("reddit")) {
                	resultSet.close();
                	return true;
                }
            }
            resultSet.close();
            return false;
            
        }catch (CommunicationsException e) {
        	e.printStackTrace();
        	controller.status = "ERROR: Could not connect to SQL Database...";
        	LogUtility.newLineToLog("ERROR: Could not connect to SQL Database. Is the a local SQL Server started?");
        	return false;
		}catch (Exception e) {
        	e.printStackTrace();
        	return false;
		}
    	
    }
    
    static Boolean createDB() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", ""); Statement stmt = conn.createStatement();) {
        	
            String sql = "CREATE DATABASE reddit";
            stmt.executeUpdate(sql);
            //System.out.println("Database reddit created successfully...");
            return true;
            
        }catch (CommunicationsException e) {
        	e.printStackTrace();
        	controller.status = "ERROR: Could not connect to SQL Database...";
        	LogUtility.newLineToLog("ERROR: Could not connect to SQL Database. Is the a local SQL Server started?");
        	return false;
		}catch (Exception e) {
        	e.printStackTrace();
        	return false;
		}
    }
    
    static Boolean isTableCreated(String table) {
    	checkDBCreated();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", ""); Statement stmt = conn.createStatement();) {
        	
        	table = table.replace('/', '_');
        	
        	DatabaseMetaData databaseMetaData = conn.getMetaData();
        	ResultSet resultSet = databaseMetaData.getTables(null, null, table, new String[] {"TABLE"});
        	
        	if (resultSet.next()) {
        		//String name = resultSet.getString("TABLE_NAME");
        		//System.out.println("Table "+name+" exists");
        		resultSet.close();
        		return true;
    		}
        	
        	//System.out.println("Table "+table+" not found");
        	resultSet.close();
            return false;
            
        }catch (CommunicationsException e) {
        	e.printStackTrace();
        	controller.status = "ERROR: Could not connect to SQL Database...";
        	LogUtility.newLineToLog("ERROR: Could not connect to SQL Database. Is the a local SQL Server started?");
        	return false;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    
    static void checkDBCreated() {
    	
    	Boolean flag = false;
    	
    	try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", ""); Statement stmt = conn.createStatement();) {

			ResultSet rs = conn.getMetaData().getCatalogs();

			while(rs.next()){
				String catalogs = rs.getString(1);
				
				if("reddit".equals(catalogs)) {
					flag = true;
				}
			}
			
			if(!flag) {
		         String sql = "CREATE DATABASE reddit";
		         stmt.executeUpdate(sql);
			}
    		
    	}catch (CommunicationsException e) {
    		e.printStackTrace();
    		controller.status = "ERROR: Could not connect to SQL Database...";
    		LogUtility.newLineToLog("ERROR: Could not connect to SQL Database. Is the a local SQL Server started?");
    	}catch (Exception e) {
    		e.printStackTrace();
    		LogUtility.newLineToLog("ERROR: Unknown"+e.toString());
    	}
    }
    
    public static Boolean deleteTable(String table) {
    	checkDBCreated();
    	
    	table = table.replace('/', '_');
    	
    	if(isTableCreated(table)) {
    		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/reddit", "root", ""); Statement stmt = conn.createStatement();) {
            	
                stmt.executeUpdate("DROP TABLE `reddit`.`"+table+"`");
                LogUtility.newLineToLog("Info: Deleted the SQL table "+table);
    		}catch (CommunicationsException e) {
            	e.printStackTrace();
            	controller.status = "ERROR: Could not connect to SQL Database...";
            	LogUtility.newLineToLog("ERROR: Could not connect to SQL Database. Failed to delete table: "+table);
            	return false;
    		}catch (Exception e) {
            	e.printStackTrace();
            	return false;
    		}
    	}
    	
    	return false;
    }
    
    static Boolean createTable(String table) {
    	
    	table = table.replace('/', '_');
    	
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", ""); Statement stmt = conn.createStatement();) {
        	
            stmt.executeUpdate("use reddit;");
            
            String sql = "CREATE TABLE `"+table+"` " +
                    "(keyId int NOT NULL AUTO_INCREMENT, " +
                    " user VARCHAR(255), " + 
                    " userUri VARCHAR(255), " + 
                    " id VARCHAR(255), " + 
                    " uri VARCHAR(255), " + 
                    " date VARCHAR(255), " + 
                    " title VARCHAR(255), " + 
                    " media VARCHAR(1023), " + 
                    " PRIMARY KEY ( keyId ))"; 
            
            //System.out.println("sql: "+sql);
            stmt.executeUpdate(sql);
            return true;
            
        }catch (CommunicationsException e) {
        	e.printStackTrace();
        	controller.status = "ERROR: Could not connect to SQL Database...";
        	LogUtility.newLineToLog("ERROR: Could not connect to SQL Database. Is the a local SQL Server started?");
        	return false;
		}catch (Exception e) {
        	e.printStackTrace();
        	return false;
		}
    }
    
    static ArrayList<Entry> getEntrysBySubreddit(String subreddit, int limiter){  
    	
    	subreddit = subreddit.replace('/', '_');
    	if(!isTableCreated(subreddit)) {
    		createTable(subreddit);
    	}
    	
    	ArrayList<Entry> output = new ArrayList<Entry>();
    	
    	try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/reddit", "root", ""); Statement stmt = conn.createStatement();) {
    		String SQL = "SELECT * from "+subreddit+" LIMIT "+limiter;
    		
    		if(limiter==0) {
    			SQL = "SELECT * from "+subreddit;
    		}
    		
    		PreparedStatement ps;
    		ps = conn.prepareStatement(SQL);
    		ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            	Entry entry = new Entry(rs.getString("user"), rs.getString("userUri"), rs.getString("id"), rs.getString("uri"), rs.getString("date"), rs.getString("title"));
            	
            	
            	output.add(entry);
            }
    	}catch (CommunicationsException e) {
        	e.printStackTrace();
        	controller.status = "ERROR: Could not connect to SQL Database...";
        	LogUtility.newLineToLog("ERROR: Could not connect to SQL Database. Is the a local SQL Server started?");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return output;
    }
    
    public static Boolean writeEntrysToSubreddit(String subreddit, ArrayList<Entry> entrys){
    	
    	subreddit = subreddit.replace('/', '_');
    	
    	if(entrys.size()!=0) {
        	if(!isTableCreated(subreddit)) {
        		createTable(subreddit);
        	}
        	
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/reddit", "root", ""); Statement stmt = conn.createStatement();) {
            	
            	String sql = "INSERT INTO `"+subreddit+"` (`keyId`, `user`, `userUri`, `id`, `uri`, `date`, `title`, `media`) VALUES ";
        		for (Iterator<Entry> it = entrys.iterator() ; it.hasNext() ; ){
        			sql += it.next().toSQL();
        			if(it.hasNext()) {
        				sql += ", ";
        			}
        			
        			
        		}
        		////System.out.println(sql);
        		stmt.executeUpdate(sql);
                return true;
                
            }catch (CommunicationsException e) {
            	e.printStackTrace();
            	controller.status = "ERROR: Could not connect to SQL Database...";
            	LogUtility.newLineToLog("ERROR: Could not connect to SQL Database. Is the a local SQL Server started?");
            	return false;
    		}catch (Exception e) {
            	e.printStackTrace();
            	return false;
    		}
    	}
		return true;
    }
    
}