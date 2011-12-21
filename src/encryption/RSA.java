package encryption;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {
	private final static BigInteger one      = new BigInteger("1");
	private final static BigInteger zero     = new BigInteger("0");
	private final static SecureRandom random = new SecureRandom();
	
	//public key is the pair (e,n)
	//private key is the pair (d,n)
	private BigInteger n;
	private BigInteger e;
	private BigInteger d;
	
	public RSA(){
		BigInteger p = BigInteger.probablePrime(128, random);
		BigInteger q = BigInteger.probablePrime(128, random);
		
		n= p.multiply(q);
		BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));
		
		e=new BigInteger("65537");
		while(phi.mod(e).equals(zero)){
			e=BigInteger.probablePrime(20, random);
		}
		d=e.modInverse(phi);
	}
	
	public KeyPair getPublicKey(){
		return new KeyPair(e,n);
	}
	
	public KeyPair getPrivateKey(){
		return new KeyPair(d,n);
	}
	
	//encrypts this message using this key
	public static BigInteger encrypt(BigInteger msg, KeyPair key){
		return msg.modPow(key.first(),key.second());
	}
	
	//decrypts this message using this key
	public static BigInteger decrypt(BigInteger msg,KeyPair key){
		return msg.modPow(key.first(),key.second());
	}
	
	//decrypts this message using this RSA instance's private key
	public BigInteger decrypt(BigInteger msg){
		return RSA.decrypt(msg,getPrivateKey());
	}
	
	/*
	 * adds 256 to a number, then encrypts it. That way negative bytes can be transmitted (rsa usually doesn't
	 * work on negative numbers)
	 */
	public static BigInteger[] encrypt(byte[] bytes,KeyPair key){
		BigInteger[] arr = new BigInteger[bytes.length];
		
		for(int i=0; i<bytes.length; i++){
			arr[i]=RSA.encrypt(new BigInteger(bytes[i]+256+""),key);
		}
		return arr;
	}
	
	public BigInteger[] decrypt(BigInteger[] arr){
		BigInteger[] ans = new BigInteger[arr.length];
		for(int i=0; i<arr.length; i++){
			ans[i]=decrypt(arr[i]);
		}
		return ans;
	}
	
	/*
	 * decrypts a message that was encrypted using the encrypt(byte[] bytes,KeyPair key) method
	 */
	public byte[] decryptToBytes(BigInteger[] arr){
		byte[] ans = new byte[arr.length];
		BigInteger _256 = new BigInteger("256");
		for(int i=0; i<arr.length; i++){
			ans[i]=decrypt(arr[i]).subtract(_256).byteValue();
		}
		return ans;
	}
	
	public static void main(String[]theory){
		RSA rsa = new RSA();
		
		BigInteger m = new BigInteger("54321");
		BigInteger mstar = RSA.encrypt(m,rsa.getPublicKey());
		System.out.println(mstar);
		BigInteger mdecoded = rsa.decrypt(mstar);
		System.out.println(mdecoded);
	}
}
