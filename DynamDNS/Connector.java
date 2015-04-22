package DynamDNS;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class Connector {

	static String record;
	static String accessKey;
	static String domainName;
	static String logPath; 
	
	public static void main(String args[]) {
		
		//domain record id
		record = "RECORD_KEY";
		//digital ocean api access key
		accessKey = "API_KEY";
		//domain name
		domainName = "DOMAIN_NAME";
		//path to log.txt
		logPath = "path/to/log.txt";
		
		
		//check for internet connectivity
		boolean google = isReachable("www.google.com");
		boolean amazon = isReachable("www.amazon.com");
		
		if(!(google && amazon)) {
			log("No Internet Connectivity", "main");
			return; //no point doing anything else if there's no internet
		}
		
		//get the ip currently recorded on digital ocean
		IPV4 currentRecordIP = null;
		try {
			currentRecordIP = getCurrentRecord();
		} catch (InvalidIPV4Exception e) {
			log("Current Record Invalid IPV4", "main");
		}
		
		//get the machine's current IP (Thanks AWS)
		IPV4 currentPublicIP = null;
		try {
			currentPublicIP = getMyPublicIP();
		} catch (InvalidIPV4Exception e) {
			log("Current Public IP Invalid IPV4", "main");
		}
		
		//log failures
		if(currentRecordIP == null)
			log("Current Record NULL", "main");
		else if(currentPublicIP == null)
			log("Current Public IP NULL", "main");
		else if(!currentRecordIP.getIp().equals(currentPublicIP.getIp())) {
			//update the record to new public IP
			if(updateRecordIP(currentPublicIP))
				log("Current Record changed from " + currentRecordIP.getIp() + " to " + currentPublicIP.getIp(), "main");
			else
				log("Current Record change FAILED from " + currentRecordIP.getIp() + " to " + currentPublicIP.getIp(), "main");
		}
		
	}
	
	 public static boolean isReachable(String host)
     {
         try {

             URL url = new URL("http://"+host);

             HttpURLConnection con = (HttpURLConnection)url.openConnection();

             //will cause an exception if it can't connect
             Object objData = con.getContent();

         } catch (Exception e) {
             return false;
         }
         
         return true;
     }
	
	public static IPV4 getCurrentRecord() throws InvalidIPV4Exception {
		
		URL url;
		try {
			url = new URL("https://api.digitalocean.com/v2/domains/"+domainName+"/records/"+record);
			HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
			
			con.setRequestMethod("GET");
			
			//set required headers
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer "+accessKey);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			StringBuilder str = new StringBuilder();
			String line = "";
			while((line = reader.readLine()) != null)
				str.append(line);
			
			Object obj = JSONValue.parse(str.toString());
			JSONObject response = (JSONObject)obj;
			//record is inside the domain_record object in the json response
			JSONObject record = (JSONObject)response.get("domain_record");
			
			return new IPV4(record.get("data").toString());
			
		} catch (MalformedURLException e) {
			log("Bad Connection URL", "getCurrentRecord");
		} catch (IOException e) {
			log("Connection Error", "updateRecordIP");
		}
		
		return null;
		
	}
	
	public static IPV4 getMyPublicIP() throws InvalidIPV4Exception {
		
		URL pubIP = null;
		try {
			pubIP = new URL("http://checkip.amazonaws.com");
			
			HttpURLConnection con = (HttpURLConnection)pubIP.openConnection();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			StringBuilder str = new StringBuilder();
			String line = "";
			while((line = in.readLine()) != null)
				str.append(line);
			
			//return public IP
			return new IPV4(str.toString());
			
		} catch (IOException e) {
			log("Connection Error", "getMyPublicIP");
		}
		
		return null;
	}
	
	public static boolean updateRecordIP(IPV4 ip) {
		
		URL url;
		try {
			url = new URL("https://api.digitalocean.com/v2/domains/"+domainName+"/records/"+record);
			HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
			
			con.setRequestMethod("PUT");
			
			//set required headers
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer "+accessKey);
			
			//create and write body 
			con.setDoOutput(true);
			JSONObject obj = new JSONObject();
			obj.put("type","A");
			obj.put("name", "beast");
			obj.put("data", ip.getIp());
			
			PrintWriter writer = new PrintWriter(con.getOutputStream());
			writer.write(obj.toJSONString());
			writer.flush();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			StringBuilder str = new StringBuilder();
			String line = "";
			while((line = reader.readLine()) != null)
				str.append(line);
			
			Object ob = JSONValue.parse(str.toString());
			JSONObject response = (JSONObject)ob;
			//domain record is in domain_record fields of response json
			JSONObject record = (JSONObject)response.get("domain_record");
			
			//check if the update worked
			if(record.get("data").equals(ip.getIp()))
				return true;
			else
				return false;
			
			
		} catch (MalformedURLException e) {
			log("Bad Connection URL", "updateRecordIP");
		} catch (IOException e) {
			log("Connection Error", "updateRecordIP");
		}
		
		return false;
		
	}
	
	public static void log(String msg, String methodName ) {
		
		Date now = new Date();
		
		try{
			
			//append to end of file
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logPath+"log.txt", true))); 
		    out.println(now.toString() + " | " + methodName +  " - " + msg);
		    out.flush();
		    
		}catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
}
