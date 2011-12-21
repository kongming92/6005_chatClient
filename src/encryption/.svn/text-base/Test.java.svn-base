package encryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Test {
    
	public static void main(String[]dsaf)throws NoSuchAlgorithmException,NoSuchPaddingException,InvalidKeyException,IOException,InterruptedException,
	BadPaddingException,IllegalBlockSizeException,NoSuchProviderException,InvalidAlgorithmParameterException,
	InvalidKeySpecException,ClassNotFoundException{

		establishHandshakeTest();
	}
	
	public static void establishHandshakeTest()throws IOException,NoSuchAlgorithmException,NoSuchPaddingException,InvalidKeyException,IOException,InterruptedException,
		BadPaddingException,IllegalBlockSizeException,NoSuchProviderException,InvalidAlgorithmParameterException,
		InvalidKeySpecException,ClassNotFoundException{
		
		Server s = new Server();
		Thread server = new Thread(s);
		server.start();
		Socket socket = new Socket("localhost",4444);
		
		
		EstablishClientHandshake clientHandshake = new EstablishClientHandshake(socket.getInputStream(),socket.getOutputStream());
		
		clientHandshake.init();
		System.out.println("cient handshake");
		StreamPair pair = clientHandshake.getStreamPair();
		
		PrintWriter toServer = new PrintWriter(pair.outputStream(),true);
		
		BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
		
		String input;
		while( (input = userInput.readLine())!=null){
			System.out.println("looping");
			toServer.print(input+"\n");
			toServer.flush();
		}
	}

	private static class Server implements Runnable{
		public Server(){
			
		}
		public void run(){
			
			try{
				ServerSocket ss=new ServerSocket(4444);
				Socket socket =ss.accept();
				
				EstablishServerHandshake s = new EstablishServerHandshake(socket.getInputStream(),socket.getOutputStream());
				s.init();
				System.out.println("server handshake");
				StreamPair pair = s.getStreamPair();
				
				BufferedReader in = new BufferedReader(new InputStreamReader(pair.inputStream()));
				String input;
			
				while((input=in.readLine())!=null){
					System.out.println(">>>"+input);					
				}
			}catch(Exception e){
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			
		}
	}

	
}


class ForkedOutputStream extends OutputStream {
	OutputStream tee = null, out = null;
	
	public ForkedOutputStream(OutputStream chainedStream,  OutputStream teeStream){
	  out = chainedStream;
	  if (teeStream == null)
	      tee = System.out;
	  else
	      tee = teeStream;
	}
	
	/**
	* Implementation for parent's abstract write method.  
	* This writes out the passed in character to the both,
	* the chained stream and "tee" stream.
	*/
	public void write(int c) throws IOException{
	  out.write(c);
	  tee.write(c);
	  tee.flush();
	}
	
	/**
	* Closes both, chained and tee, streams.
	*/
	public void close() throws IOException{
	  flush();
	
	  out.close();
	  tee.close();
	}
	
	/**
	* Flushes chained stream; the tee stream is flushed 
	* each time a character is written to it.
	*/
	public void flush() throws IOException{
	  out.flush();
	}
}
