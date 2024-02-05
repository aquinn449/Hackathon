package uk.ac.mmu.advprog.hackathon;
import static spark.Spark.get;
import static spark.Spark.port;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handles the setting up and starting of the web service
 * You will be adding additional routes to this class, and it might get quite large
 * Feel free to distribute some of the work to additional child classes, like I did with DB
 * @author You, Mainly!
 */
public class TransportWebService {

	/**
	 * Main program entry point, starts the web service
	 * @param args not used
	 */
	public static void main(String[] args) {		
		port(8088);
		
		//Simple route so you can check things are working...
		//Accessible via http://localhost:8088/test in your browser
		get("/test", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				try (DB db = new DB()) {
					return "Number of Entries: " + db.getNumberOfEntries();
				}
			}			
		});
		
		/**
		 *  Handles a GET request to the "/stopcount" route.
		 *  It expects a query parameter called "locality", which is a string in the URL
		 *  @param The request object containing information about the HTTP request
		 *  @param response The response object that will be used to send the response back to the client
		 *  @return The result of the number of stops in that locality, or "Invalid Request" if the "locality" parameter is not present
		 *  @throws Exception if an exception is thrown while handling the request
		 */
		get("/stopcount", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				String locality = request.queryParams("locality");
				if(!request.queryParams().contains("locality")) {
					return "Invalid Request";
				}
				try (DB db = new DB()) {
					return db.StopsInLocality(locality);
				}
			}			
		});
		
		/**
		 * Handles a GET request to the "/stops" route.
		 *
		 * @param The request object, containing the request data.
		 * @param The response object, that will be used to send the response back to the client.
		 * @return A JSON object representing the stops that match the specified locality and type.
		 * @throws Exception If an error occurs while handling the request.
		 */
		get("/stops", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				String locality = request.queryParams("locality");
				String type = request.queryParams("type");
				try (DB db = new DB()) {
					response.header("Content-Type", "application/json");
					return db.StopsByLocalityAndType(locality, type);
				}
			}
		});
		
		// Was unable to complete the /nearest task but attempted 
		
//		get("/nearest", new Route() {
//			@Override
//			public Object handle(Request request, Response response) throws Exception {
//				String latitude = request.queryParams("latitude");
//				String longitude = request.queryParams("longitude");
//				String type = request.queryParams("type");
//				try (DB db = new DB()) {
//					return db.StopsByLocation(latitude, longitude, type);
//				}
//			}
//		});
		

		
		System.out.println("Server up! Don't forget to kill the program when done!");
	}

}
