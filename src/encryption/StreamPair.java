package encryption;

import java.io.InputStream;
import java.io.OutputStream;

public class StreamPair{
	private final InputStream in;
	private final OutputStream out;
	public StreamPair(InputStream in, OutputStream out){
		this.in=in;
		this.out=out;
	}
	
	public InputStream inputStream(){
		return in;
	}
	public OutputStream outputStream(){
		return out;
	}
}