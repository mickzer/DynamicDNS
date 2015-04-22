package DynamDNS;

public class IPV4 {

	private String ip;
	
	public IPV4(String input) throws InvalidIPV4Exception{
		
		if(input.matches("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"))
			ip = input;
		else
			throw new InvalidIPV4Exception(input + " is an invalid IPV4 address.");
		
	}
	
	public String getIp() {
		return ip;
	}
	
}
