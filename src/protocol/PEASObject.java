package protocol;

public abstract class PEASObject {
	
	protected PEASHeader header;
	protected PEASBody body;
	
	public abstract PEASHeader getHeader();
	public abstract void setHeader(PEASHeader header);
	
	public abstract PEASBody getBody();
	public abstract void setBody(PEASBody body);
	
	public abstract String toString();
	public abstract String toJSONString();
}
