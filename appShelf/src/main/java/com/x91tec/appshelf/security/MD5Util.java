package com.x91tec.appshelf.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * @author oeager
 * @version 1.0
 */
public final class MD5Util {
	private static final String ALGORITHM = "MD5";

	private static char sHexDigits[] = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
};
	private static MessageDigest sDigest;

	static {
		try {
			sDigest = MessageDigest.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private MD5Util() {
	}

	
	public static String encode(String source) {
		byte[] bytes = source.getBytes();
		byte[] encodedBytes = sDigest.digest(bytes);

		return hexString(encodedBytes);
	}
	public static String hexString(byte[] source) {
		if (source == null || source.length <= 0) {
			return "";
		}

		final int size = source.length;
		final char str[] = new char[size * 2];
		int index = 0;
		byte b;
		for (int i = 0; i < size; i++) {
			b = source[i];
			str[index++] = sHexDigits[b >>> 4 & 0xf];
			str[index++] = sHexDigits[b & 0xf];
		}
		return new String(str);
	}


}
