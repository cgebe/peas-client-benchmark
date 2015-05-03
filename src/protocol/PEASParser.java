package protocol;

import java.util.Map;
import java.util.regex.Pattern;


public class PEASParser {
	
	/**
	 * Parses a String or a Map and creates the corresponding PEASObject, either PEASRequest or PEASResponse
	 * 
	 * @param obj String or Map
	 * @return A PEASObject.
	 * @throws PEASException
	 */
	public static PEASObject parse(Object obj) throws PEASException {
		if (obj instanceof String) {
			String s = (String) obj;
			Pattern p1 = Pattern.compile(System.getProperty("line.separator"));
			String[] lines = p1.split(s);

			// command line
			Pattern p2 = Pattern.compile("\\s+");
			String[] commands = p2.split(lines[0]);
			
			if (lines.length > 0 && commands[0].equals("KEY")) {
				PEASHeader header = new PEASHeader();
				
				// command KEY has 2 fields
				if (commands.length > 1) {
					header.setCommand(commands[0]);
					header.setIssuer(commands[1]);
				}
				
				return new PEASRequest(header, new PEASBody());
			} else if (lines.length > 2 && commands[0].equals("QUERY")) {
				PEASHeader header = new PEASHeader();

				// command QUERY has 3 fields
				if (commands.length > 2) {
					header.setCommand(commands[0]);
					header.setIssuer(commands[1]);
					header.setProtocol(commands[2]);
					header.setQuery(lines[1]);
				}
				
				// create new PEADBody for PEAS request
				PEASBody body = new PEASBody();
				
				// set the body
				body.setBody(lines[2]);
				
				return new PEASRequest(header, body);
			} else if (lines.length > 1 && commands[0].equals("RESPONSE")) {
				PEASHeader header = new PEASHeader();
			
				// command RESPONSE has 2 fields
				if (commands.length > 1) {
					header.setCommand(commands[0]);
					header.setStatus(commands[1]);
				}
				
				// create new PEADBody for PEAS request
				PEASBody body = new PEASBody();
				
				// set the body
				body.setBody(lines[1]);
				
				return new PEASResponse(header, body);
			} else {
				// todo throw exception here
				return new PEASRequest(new PEASHeader(), new PEASBody());
			}
		} else if (obj instanceof Map<?, ?>) {
			Map<?, ?> o = (Map<?, ?>) obj;
			if (o.get("command") instanceof String) {
				String c = (String) o.get("command");
				if (c.equals("KEY")) {
					// KEY is a request command
					// create new header for this PEAS request
					PEASHeader header = new PEASHeader();
					
					// set the command
					header.setCommand(c);
					
					// set the issuer address
					if (o.get("issuer") instanceof String) {
						header.setIssuer((String) o.get("issuer"));
					}
					
					// create new body for this PEAS request
					PEASBody body = new PEASBody();
					
					if (o.get("body") instanceof String) {
						body.setBody((String) o.get("body"));
					}
					
					// return the newly created request
					return new PEASRequest(header, body);
				} else if (c.equals("QUERY")) {
					// QUERY is a request command
					// create new header for this PEAS request
					PEASHeader header = new PEASHeader();
					// set the command
					header.setCommand(c);
					
					if (o.get("issuer") instanceof String && o.get("protocol") instanceof String && o.get("query") instanceof String) {
						header.setIssuer((String) o.get("issuer"));
						header.setProtocol((String) o.get("protocol"));
						header.setQuery((String) o.get("query"));
					}
					
					// create new body for this PEAS request
					PEASBody body = new PEASBody();
					
					if (o.get("body") instanceof String) {
						body.setBody((String) o.get("body"));
					}
					
					// return the newly created request
					return new PEASRequest(header, body);
				} else if (c.equals("RESPONSE")) {
					// RESPONSE is a response command
					// create new header for this PEAS response
					PEASHeader header = new PEASHeader();
					
					// set the command
					header.setCommand(c);
					
					// set the status
					if (o.get("status") instanceof String) {
						header.setStatus((String) o.get("status"));
					}
					
					// create new body for this PEAS response
					PEASBody body = new PEASBody();
					
					if (o.get("body") instanceof String) {
						body.setBody((String) o.get("body"));
					}
					
					// return the newly created response
					return new PEASResponse(header, body);
				} else {
					// todo throw exception here
					return new PEASRequest(new PEASHeader(), new PEASBody());
				}
			} else {
				// todo throw exception here
				return new PEASRequest(new PEASHeader(), new PEASBody());
			}
		} else {
			// todo throw exception here
			return new PEASRequest(new PEASHeader(), new PEASBody());
		}
		
	}
}
