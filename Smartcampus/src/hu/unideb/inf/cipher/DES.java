package hu.unideb.inf.cipher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DES {

	private static final Charset CHARSET = Charset.forName("UTF-8");
	private static final String KEY;
	
	static {
		Properties p = new Properties();
		try {
			p.load(DES.class.getClassLoader().getResourceAsStream("hu/unideb/inf/cipher/key.properties"));
		} catch (IOException ioe) {
			ioe.printStackTrace(System.err);
		}
		KEY = p.getProperty("security.des.key");
	}
	
	private static byte[] encript(byte[] plain) {
		try {
			DESKeySpec dks = new DESKeySpec(KEY.getBytes());
			SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
			SecretKey desKey = skf.generateSecret(dks);
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); // DES/ECB/PKCS5Padding for SunJCE
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ByteArrayInputStream  bis = new ByteArrayInputStream(plain);
			cipher.init(Cipher.ENCRYPT_MODE, desKey);
			CipherInputStream cis = new CipherInputStream(bis, cipher);
			for (int b; (b = cis.read()) != -1;) {
				bos.write(b);
			}
			bos.close();
			cis.close();
			return bos.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}
	
	private static byte[] decrypt(byte[] secret) {
		try {
			DESKeySpec dks = new DESKeySpec(KEY.getBytes());
			SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
			SecretKey desKey = skf.generateSecret(dks);
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); //  for SunJCE
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ByteArrayInputStream  bis = new ByteArrayInputStream(secret);
			cipher.init(Cipher.DECRYPT_MODE, desKey);
			CipherOutputStream cos = new CipherOutputStream(bos, cipher);
			for (int b; (b = bis.read()) != -1;) {
				cos.write(b);
			}
			cos.close();
			return bos.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static  byte[] encriptStr(String str) {
		return encript( str.getBytes(CHARSET) );
	}	
	
	public static String decryptStr(byte bs[]) {
		return new String( decrypt( bs ), CHARSET);
	}	
	
}