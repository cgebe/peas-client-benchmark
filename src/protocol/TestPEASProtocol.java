package protocol;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TestPEASProtocol {
	
	private static JSONParser parser = new JSONParser();
	
	public static void main(String [] args) throws PEASException, ParseException
	{
		long start_time;
		long end_time;
		
		
		String s = "KEY 127.0.0.1:11777";
		
		start_time = System.nanoTime();
		PEASObject obj = PEASParser.parse(s);
		System.out.print(obj.toString());
		end_time = System.nanoTime();
		System.out.println("lasts in ms: " + (end_time - start_time) / 1e6);
		System.out.println();
		System.out.println();
		
		
		
		String s2 = "QUERY 127.0.0.1:11777 http" + System.getProperty("line.separator")
				  + "ENCRYPTEDSTRING" + System.getProperty("line.separator")
				  + "BODY";
		
		start_time = System.nanoTime();
		PEASObject obj2 = PEASParser.parse(s2);
		System.out.print(obj2.toString());
		end_time = System.nanoTime();
		System.out.println("lasts in ms: " + (end_time - start_time) / 1e6);
		System.out.println();
		System.out.println();
		
		
		
		String s3 = "RESPONSE 100" + System.getProperty("line.separator")
				  + "BODY";
		
		start_time = System.nanoTime();
		PEASObject obj3 = PEASParser.parse(s3);
		System.out.print(obj3.toString());
		end_time = System.nanoTime();
		System.out.println("lasts in ms: " + (end_time - start_time) / 1e6);
		System.out.println();
		System.out.println();
		
		
		/*
		Map<String, String> map = new HashMap<String, String>();
		map.put("command", "KEY");
		map.put("issuer", "127.0.0.1:11777");
		
		start_time = System.nanoTime();
		PEASObject obj4 = PEASParser.parse(map);
		System.out.print(obj4.toString());
		end_time = System.nanoTime();
		System.out.println("lasts in ms: " + (end_time - start_time) / 1e6);
		System.out.println();
		System.out.println();
		
		
		
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("command", "QUERY");
		map2.put("issuer", "127.0.0.1:11777");
		map2.put("protocol", "http");
		map2.put("query", "ENCRYPTED STRING");
		map2.put("body", "BODY");
		
		start_time = System.nanoTime();
		PEASObject obj5 = PEASParser.parse(map2);
		System.out.print(obj5.toString());
		end_time = System.nanoTime();
		System.out.println("lasts in ms: " + (end_time - start_time) / 1e6);
		System.out.println();
		System.out.println();
		
		
		
		Map<String, String> map3 = new HashMap<String, String>();
		map3.put("command", "RESPONSE");
		map3.put("status", "200");
		map3.put("body", "BODY");
		
		start_time = System.nanoTime();
		PEASObject obj6 = PEASParser.parse(map3);
		System.out.print(obj6.toString());
		end_time = System.nanoTime();
		System.out.println("lasts in ms: " + (end_time - start_time) / 1e6);
		System.out.println();
		System.out.println();
		*/
		
		
		// test json approach against own protocol scheme
		String jsonkey = "{\"command\":\"KEY\", \"issuer\":\"127.0.0.1:11777\"}";
		String jsonquery = "{\"command\":\"QUERY\", \"issuer\":\"127.0.0.1:11777\", \"protocol\":\"http\", \"query\":\"ENCRYPTED STRING\", \"body\":\"BODY\"}";
		String jsonresponse = "{\"command\":\"RESPONSE\", \"status\":\"100\", \"body\":\"BODY\"}";
		
		start_time = System.nanoTime();
		JSONObject jsonobj = (JSONObject) parser.parse(jsonkey);
		PEASObject pobj = PEASObjectFromJSONObject(jsonobj);
		System.out.println(pobj.toJSONString());
		end_time = System.nanoTime();
		System.out.println("lasts in ms: " + (end_time - start_time) / 1e6);
		System.out.println();
		System.out.println();
		
		
		start_time = System.nanoTime();
		JSONObject jsonobj2 = (JSONObject) parser.parse(jsonquery);
		PEASObject pobj2 = PEASObjectFromJSONObject(jsonobj2);
		System.out.println(pobj2.toJSONString());
		end_time = System.nanoTime();
		System.out.println("lasts in ms: " + (end_time - start_time) / 1e6);
		System.out.println();
		System.out.println();
		
		
		start_time = System.nanoTime();
		JSONObject jsonobj3 = (JSONObject) parser.parse(jsonresponse);
		PEASObject pobj3 = PEASObjectFromJSONObject(jsonobj3);
		System.out.println(pobj3.toJSONString());
		end_time = System.nanoTime();
		System.out.println("lasts in ms: " + (end_time - start_time) / 1e6);
		System.out.println();
		System.out.println();

		
	}
	
	private static PEASObject PEASObjectFromJSONObject(JSONObject obj) {
		String c = (String) obj.get("command");
		if (c.equals("KEY")) {
			PEASHeader header = new PEASHeader();
			header.setCommand(c);
			header.setIssuer((String) obj.get("issuer"));
			
			return new PEASRequest(header, new PEASBody());
		} else if (c.equals("QUERY")) {
			PEASHeader header = new PEASHeader();
			header.setCommand(c);
			header.setIssuer((String) obj.get("issuer"));
			header.setProtocol((String) obj.get("protocol"));
			header.setQuery((String) obj.get("query"));
			
			PEASBody body = new PEASBody();
			body.setBody((String) obj.get("body"));
			
			return new PEASRequest(header, body);
		} else if (c.equals("RESPONSE")) {
			PEASHeader header = new PEASHeader();
			header.setCommand(c);
			header.setStatus((String) obj.get("status"));
			
			PEASBody body = new PEASBody();
			body.setBody((String) obj.get("body"));
			
			return new PEASResponse(header, body);
		} else {
			return new PEASResponse(new PEASHeader(), new PEASBody());
		}
	}

}
