package protocol;

import io.netty.buffer.ByteBuf;

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
	public static PEASHeader parseHeader(Object obj) throws PEASException {
		if (obj instanceof String) {
			String s = (String) obj;
			Pattern p1 = Pattern.compile(System.getProperty("line.separator"));
			Pattern p2 = Pattern.compile("\\s+");
			
			String[] lines = p1.split(s);
			// command line
			PEASHeader header = new PEASHeader();
			
			for (int i = 0; i < lines.length; i++) {
				if (i <= 0) {
					String[] commands = p2.split(lines[i]);
					
					header.setCommand(commands[0]);
					header.setIssuer(commands[1]);

				} else {
					String[] values = p2.split(lines[i]);
					
					if (values[0].equals("Status:")) {
						header.setStatus(values[1]);
					}
					
					if (values[0].equals("Query:")) {
						header.setQuery(values[1]);
					}
					
					if (values[0].equals("Protocol:")) {
						header.setProtocol(values[1]);
					}
					
					if (values[0].equals("Content-Length:")) {
						header.setBodyLength(Integer.parseInt(values[1]));
					}
				}
			}

			return header;

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
				
					// return the newly created request
					return header;
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
					
					// return the newly created request
					return header;
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
					
					// return the newly created response
					return header;
				} else {
					// todo throw exception here
					return new PEASHeader();
				}
			} else {
				// todo throw exception here
				return new PEASHeader();
			}
		} else {
			// todo throw exception here
			return new PEASHeader();
		}
		
	}
}
