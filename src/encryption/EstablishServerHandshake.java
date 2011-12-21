package encryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class EstablishServerHandshake{
	private final InputStream inStream;
	private final OutputStream outStream;
	
	private CipherInputStream cis;
	private CipherOutputStream cos;
	
	private KeyPair clientRSAKey;
	public EstablishServerHandshake(InputStream inStream, OutputStream outStream){
		this.inStream=inStream;
		this.outStream=outStream;
	}
	
	/**
	 * establishes a handshake using DES encryption over the inStream and OutStream given in the 
	 * constructor. Uses RSA to securley pass the DES symmetric key over the stream.Requires that 
	 * there be an EstablishClientHandshake object currently in its init method
	 * on the other end of the streams
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws ClassNotFoundException
	 * @throws InvalidKeySpecException
	 * @throws InvalidAlgorithmParameterException
	 */
	public void init()throws IOException, NoSuchAlgorithmException,NoSuchProviderException,
		NoSuchPaddingException,InvalidKeyException,ClassNotFoundException,InvalidKeySpecException,
		InvalidAlgorithmParameterException{
		
		establishRSA();
	
		//initialize the secret key
		KeyGenerator generator = KeyGenerator.getInstance("DES");
		generator.init(new SecureRandom());
		SecretKey key = generator.generateKey();
		SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
		Class spec = Class.forName("javax.crypto.spec.DESKeySpec");
		DESKeySpec ks = (DESKeySpec) skf.getKeySpec(key, spec);
		
		//make the ciphers
		Cipher encode = Cipher.getInstance("DES/CFB8/NoPadding");
		encode.init(Cipher.ENCRYPT_MODE, key);
		Cipher decode = Cipher.getInstance("DES/CFB8/NoPadding");
		IvParameterSpec spek= new IvParameterSpec( encode.getIV());
		decode.init(Cipher.DECRYPT_MODE, key,spek);
		
		//write the secret key and intialization vector to the output stream
		//make sure there is nothing on the stream
		ObjectOutputStream oos = new ObjectOutputStream(outStream);
		BigInteger[] ksKey = RSA.encrypt(ks.getKey(), clientRSAKey);
		BigInteger[] iv		= RSA.encrypt(encode.getIV(),clientRSAKey);
		oos.writeObject(new BigInteger[][]{ksKey,iv});
		oos.flush();
		
		//make the encrypted streams
		cos = new CipherOutputStream(outStream, encode);
		cis = new CipherInputStream(inStream,decode);
		
		//the first couple of bytes over the stream are not encrypted correctly, so get them out of the way
		cos.write(new byte[256]);
		cis.read(new byte[256]);
	}
	
	private void establishRSA()throws IOException{		
		PrintWriter pw = new PrintWriter(outStream,true);
		BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
		String input;
		while( (input = br.readLine())!=null){
			if(!input.matches("\\ApublicKey \\d+ \\d+")){
				pw.print("error malformed command\n");
				pw.flush();
				continue;
			}
			break;
		}
		
		BigInteger first = new BigInteger(input.split(" ")[1]);
		BigInteger second = new BigInteger(input.split(" ")[2]);
		clientRSAKey = new KeyPair(first,second);
	}
	
	/**
	 * requires that the init method has already run successfully
	 * @return a pair of outputStream,inputStream that encrypt messages before sent and decrypt messages
	 * when recived respectivley
	 */
	public StreamPair getStreamPair(){
		return new StreamPair(cis,cos);
	}
}
