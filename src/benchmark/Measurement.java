package benchmark;

public class Measurement {
	
	
	private long begin;
	private long end;
	
	public Measurement() {
		begin = 0L;
		end = 0L;
	}

	public long getBegin() {
		return begin;
	}

	public void setBegin(long begin) {
		this.begin = begin;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}
	
	public String getTimeInMs() {
		return String.valueOf((end - begin) / 1e6);
	}
}
