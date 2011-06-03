package exemplo.infraestrutura;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import exemplo.dominio.exceptions.DescriptografiaException;

@Name("criptografiaService")
@AutoCreate
public class CriptografiaService {

	private String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public String SHA1(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] sha1hash = new byte[40];
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
			sha1hash = md.digest();
			return convertToHex(sha1hash);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Problema ao gerar o SHA-1 do valor", e);
		}
	}

	public char[] decode(String chave, String secret)
			throws DescriptografiaException {
		byte[] kbytes = chave.getBytes();
		SecretKeySpec key = new SecretKeySpec(kbytes, "Blowfish");

		BigInteger n = new BigInteger(secret, 16);
		byte[] encoding = n.toByteArray();

		try {
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decode = cipher.doFinal(encoding);
			return new String(decode).toCharArray();
		} catch (Exception e) {
			throw new DescriptografiaException(
					"Problema ao descriptografar senha", e);
		}
	}

	public String encode(String chave, String secret) {
		byte[] kbytes = chave.getBytes();
		SecretKeySpec key = new SecretKeySpec(kbytes, "Blowfish");

		try {
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] encoding = cipher.doFinal(secret.getBytes());
			BigInteger n = new BigInteger(encoding);
			return n.toString(16);
		} catch (Exception e) {
			throw new IllegalStateException("Problema ao criptografar senha", e);
		}
	}

	public static void main(String[] args) throws DescriptografiaException,
			NoSuchAlgorithmException, UnsupportedEncodingException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHm");
		CriptografiaService c = new CriptografiaService();
		String chave = "SALrafael";
		String valorCifrado = c.encode(chave,
				"rafael|" + sdf.format(new Date()) + "|123");
		System.out.println(valorCifrado);
		char[] valorDecrifrado = c.decode(chave, valorCifrado);
		System.out.println(valorDecrifrado);
		System.out.println(c.SHA1(new String(valorDecrifrado)));
	}
}
