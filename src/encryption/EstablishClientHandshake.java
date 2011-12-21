package encryption;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class EstablishClientHandshake {
	private final InputStream inStream;
	private final OutputStream outStream;
	
	private CipherInputStream cis;
	private CipherOutputStream cos;
	
	private RSA rsa;
	public EstablishClientHandshake(InputStream inStream, OutputStream outStream){
		this.inStream=inStream;
		this.outStream=outStream;
	}
	
	/**
	 * establishes a secure connection to the server using DES encryption. Passes the DES symmetric key
	 * securley via RSA. Requires that an EstablishServerHandshake object is listening on the other side of
	 * inStream and outStream
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws ClassNotFoundException
	 * @throws InvalidKeySpecException
	 * @throws InvalidAlgorithmParameterException
	 */
	public void init() throws IOException, NoSuchAlgorithmException,NoSuchProviderException,
		NoSuchPaddingException,InvalidKeyException,ClassNotFoundException,InvalidKeySpecException,
		InvalidAlgorithmParameterException{
	    
		establishRSA();
		
		//listen for the secret key on the input stream
	    ObjectInputStream ois = new ObjectInputStream(inStream);
	    BigInteger[][] keyVectorPair = (BigInteger[][])ois.readObject();
	    byte[] bytekey = rsa.decryptToBytes(keyVectorPair[0]);
	    DESKeySpec ks = new DESKeySpec(bytekey);
	    SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
	    SecretKey key = skf.generateSecret(ks);
	    
	    //make the ciphers
	    byte[] iv = rsa.decryptToBytes(keyVectorPair[1]);;
	    IvParameterSpec spec= new IvParameterSpec(iv);
	    
	    Cipher decode = Cipher.getInstance("DES/CFB8/NoPadding");
	    decode.init(Cipher.DECRYPT_MODE, key, spec);
	    
	    Cipher encode = Cipher.getInstance("DES/CFB8/NoPadding");
	    encode.init(Cipher.ENCRYPT_MODE, key);
		
		//make the encrypted streams
		cos = new CipherOutputStream(outStream, encode);
		cis = new CipherInputStream(inStream,decode);
		
		cos.write(new byte[256]);
		cis.read(new byte[256]);
	}
	
	private void establishRSA()throws IOException{
		rsa = new RSA();
		PrintWriter pw = new PrintWriter(outStream,true);
		pw.print("publicKey "+rsa.getPublicKey()+"\n");
		pw.flush();
	}
	
	/**
	 * requires that the init method has already run successfully
	 * @return a pair of outputStream,inputStream that encrypt messages before sent and decrypt messages
	 * when recived respectivley
	 * @throws IOException
	 */
	public StreamPair getStreamPair()throws IOException{
		return new StreamPair(cis,cos);
	}
}
