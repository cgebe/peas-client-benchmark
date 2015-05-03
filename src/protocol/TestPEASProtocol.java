package protocol;

import io.netty.buffer.ByteBuf;

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
		PEASHeader obj = PEASParser.parseHeader(s);
		System.out.print(obj.toString());
		end_time = System.nanoTime();
		System.out.println("lasts in ms: " + (end_time - start_time) / 1e6);
		System.out.println();
		System.out.println();
		
		
		String s2 = "QUERY 127.0.0.1:11777" + System.getProperty("line.separator")
				  + "Query: TESTQUERY" + System.getProperty("line.separator")
				  + "Protocol: HTTP" + System.getProperty("line.separator")
				  + "Content-Length: 5000" + System.getProperty("line.separator");
		
		start_time = System.nanoTime();
		PEASHeader obj2 = PEASParser.parseHeader(s2);
		System.out.print(obj2.toString());
		end_time = System.nanoTime();
		System.out.println("lasts in ms: " + (end_time - start_time) / 1e6);
		System.out.println();
		System.out.println();
		
		String s3 = "RESPONSE 127.0.0.1:11777" + System.getProperty("line.separator")
				  + "Status: 100" + System.getProperty("line.separator")
				  + "Protocol: HTTP" + System.getProperty("line.separator")
				  + "Content-Length: 5000" + System.getProperty("line.separator");
		
		start_time = System.nanoTime();
		PEASHeader obj3 = PEASParser.parseHeader(s3);
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
		String jsonquery = "{\"command\":\"QUERY\", \"issuer\":\"127.0.0.1:11777\", \"protocol\":\"http\", \"bodylength\":\"256\", \"query\":\"ENCRYPTED STRING\", \"body\":\"BODY\"}";
		String jsonresponse = "{\"command\":\"RESPONSE\", \"status\":\"100\", \"protocol\":\"http\", \"bodylength\":\"256\", \"body\":\"BODY\"}";
		
		start_time = System.nanoTime();
		JSONObject jsonobj = (JSONObject) parser.parse(jsonkey);
		PEASHeader pobj = PEASHeaderFromJSONObject(jsonobj);
		System.out.println(pobj.toJSONString());
		end_time = System.nanoTime();
		System.out.println("lasts in ms: " + (end_time - start_time) / 1e6);
		System.out.println();
		System.out.println();
		
		
		start_time = System.nanoTime();
		JSONObject jsonobj2 = (JSONObject) parser.parse(jsonquery);
		PEASHeader pobj2 = PEASHeaderFromJSONObject(jsonobj2);
		System.out.println(pobj2.toJSONString());
		end_time = System.nanoTime();
		System.out.println("lasts in ms: " + (end_time - start_time) / 1e6);
		System.out.println();
		System.out.println();
		
		
		start_time = System.nanoTime();
		JSONObject jsonobj3 = (JSONObject) parser.parse(jsonresponse);
		PEASHeader pobj3 = PEASHeaderFromJSONObject(jsonobj3);
		System.out.println(pobj3.toJSONString());
		end_time = System.nanoTime();
		System.out.println("lasts in ms: " + (end_time - start_time) / 1e6);
		System.out.println();
		System.out.println();

		
	}
	
	private static PEASHeader PEASHeaderFromJSONObject(JSONObject obj) {
		String c = (String) obj.get("command");
		if (c.equals("KEY")) {
			PEASHeader header = new PEASHeader();
			header.setCommand(c);
			header.setIssuer((String) obj.get("issuer"));
			
			return header;
		} else if (c.equals("QUERY")) {
			PEASHeader header = new PEASHeader();
			header.setCommand(c);
			header.setIssuer((String) obj.get("issuer"));
			header.setProtocol((String) obj.get("protocol"));
			header.setBodyLength(Integer.parseInt((String) obj.get("bodylength")));
			header.setQuery((String) obj.get("query"));

			return header;
		} else if (c.equals("RESPONSE")) {
			PEASHeader header = new PEASHeader();
			header.setCommand(c);
			header.setStatus((String) obj.get("status"));
			header.setProtocol((String) obj.get("protocol"));
			header.setBodyLength(Integer.parseInt((String) obj.get("bodylength")));
			
			
			return header;
		} else {
			return new PEASHeader();
		}
	}

}
