package protocol;

public abstract class PEASObject {
	
	protected PEASHeader header;
	protected PEASBody body;
	
	public abstract String toString();
	public abstract String toJSONString();
}
