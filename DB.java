package uk.ac.mmu.advprog.hackathon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import javax.lang.model.element.Element;
//import javax.swing.text.Document;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Handles database access from within your web service
 * @author You, Mainly!
 */
public class DB implements AutoCloseable {
	
	//allows us to easily change the database used
	private static final String JDBC_CONNECTION_STRING = "jdbc:sqlite:./data/NaPTAN.db";
	
	//allows us to re-use the connection between queries if desired
	private Connection connection = null;
	
	/**
	 * Creates an instance of the DB object and connects to the database
	 */
	public DB() {
		try {
			connection = DriverManager.getConnection(JDBC_CONNECTION_STRING);
		}
		catch (SQLException sqle) {
			error(sqle);
		}
	}
	
	/**
	 * Returns the number of entries in the database, by counting rows
	 * @return The number of entries in the database, or -1 if empty
	 */
	public int getNumberOfEntries() {
		int result = -1;
		try {
			Statement s = connection.createStatement();
			ResultSet results = s.executeQuery("SELECT COUNT(*) AS count FROM Stops");
			while(results.next()) { //will only execute once, because SELECT COUNT(*) returns just 1 number
				result = results.getInt(results.findColumn("count"));
			}
		}
		catch (SQLException sqle) {
			error(sqle);
			
		}
		return result;
	}
	/**
	 * Gets the number of stops in the specified locality.
	 *
	 * @param The name of the locality to check.
	 * @return The number of stops in the specified locality, or -1 if an error occurs.
	 */
	public int StopsInLocality(String locality) {
		int result = -1;
		try {
			PreparedStatement s = connection.prepareStatement("SELECT COUNT(*) AS number FROM Stops WHERE localityName = ?");
			// Set the "locality" parameter in the statement
			s.setString(1, locality);
			ResultSet results = s.executeQuery();
			//loops through results 
			while(results.next()) {
				result = results.getInt(results.findColumn("number"));
			}
		}
		catch (SQLException sqle) {
			//log the error
			error(sqle);
			
		}
		//returns the results 
		return result;
	}
	
	/**
	 * Gets the stops in the specified locality and of the specified type.
	 *
	 * @param The name of the locality to check.
	 * @param The type of stop to return.
	 * @return A JSONArray of stops that match the specified locality and type.
	 */
	
	public  JSONArray StopsByLocalityAndType(String locality, String type) {
		JSONArray jsonArray = new JSONArray();
		try {
			PreparedStatement s = connection.prepareStatement("SELECT * FROM Stops WHERE LocalityName = ? AND StopType = ?");
			// Set the "locality" and "type" parameters in the statement
			s.setString(1, locality);
			s.setString(2, type);
			ResultSet results = s.executeQuery();
			
			//loops through results
			  while (results.next()) {
				  
					// Set all the fields to the column from the results
				    JSONObject jsonObject = new JSONObject();
				    jsonObject.put("name", results.getString("CommonName"));
				    jsonObject.put("locality", results.getString("LocalityName"));
				    JSONObject locationObject = new JSONObject();
				 // Create a new JSONObject for the "location" field
				    jsonObject.put("location", locationObject);
				 // Set all the fields of the "location" object to the corresponding columns from the results
				    locationObject.put("indicator", results.getString("Indicator"));
				    locationObject.put("bearing", results.getString("Bearing"));
				    locationObject.put("street", results.getString("Street"));
				    locationObject.put("landmark", results.getString("Landmark"));
				    jsonObject.put("type", results.getString("StopType"));
				    jsonArray.put(jsonObject);
				  }
			
	
		}
		catch (SQLException sqle) {
			error(sqle);
			
		}
		return jsonArray;
		}
	
	// Attempted the final task however was unsuccessful
	
	
//	public void StopsByLocation(String latitude, String longitude, String type) {
//		
//		try {
//			PreparedStatement s = connection.prepareStatement("SELECT * FROM Stops WHERE StopType=? AND Latitude IS NOT NULL AND Longitude IS NOT NULL "
//					+ "ORDER BY (((53.472 - Latitude) * (53.472 - Latitude)) + (0.595 * ((-2.244 - Longitude) * (-2.244 - Longitude)))) ASC LIMIT 5");
//			s.setString(1, latitude);
//			s.setString(2, longitude);
//			s.setString(3, type);
//			ResultSet results = s.executeQuery();
//			
//		      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//		      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//		      Document doc = (Document) ((DocumentBuilderFactory) dBuilder.newDocument()).newDocumentBuilder();
//		      
//		      Element rootElement = doc.createElement("NearestStops")
//		    		  doc.appendChild(rootElement);
//		      
//		      Element stop = doc.createElement("Stop");
//		      rootElement.appendChild(stop);
//		}
//		return ;
//		
//	}
	
	
	/**
	 * Closes the connection to the database, required by AutoCloseable interface.
	 */
	@Override
	public void close() {
		try {
			if ( !connection.isClosed() ) {
				connection.close();
			}
		}
		catch(SQLException sqle) {
			error(sqle);
		}
	}

	/**
	 * Prints out the details of the SQL error that has occurred, and exits the programme
	 * @param sqle Exception representing the error that occurred
	 */
	private void error(SQLException sqle) {
		System.err.println("Problem Opening Database! " + sqle.getClass().getName());
		sqle.printStackTrace();
		System.exit(1);
	}
}
