package com.example.data;

/**
 * fast hash
 * taken from stackoverflow answer:
 *     http://stackoverflow.com/questions/5446080/java-hash-algorithms-fastest-implementations
 * and website:
 *     http://lemire.me/blog/2015/10/22/faster-hashing-without-effort/
 */

import java.io.UnsupportedEncodingException;

public class HashUtil {
	
	public static int hash8(String val) {
	    try {
			return hash8(val.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Bad Encoding", e);
		}
	}

	public static int hash8(byte[] val) {
	    int h = 1, i = 0;
	    for (; i + 7 < val.length; i += 8) {
	        h = 31 * 31 * 31 * 31 * 31 * 31 * 31 * 31 * h + 31 * 31 * 31 * 31
	                * 31 * 31 * 31 * val[i] + 31 * 31 * 31 * 31 * 31 * 31
	                * val[i + 1] + 31 * 31 * 31 * 31 * 31 * val[i + 2] + 31
	                * 31 * 31 * 31 * val[i + 3] + 31 * 31 * 31 * val[i + 4]
	                + 31 * 31 * val[i + 5] + 31 * val[i + 6] + val[i + 7];
	    }
	    for (; i + 3 < val.length; i += 4) {
	        h = 31 * 31 * 31 * 31 * h + 31 * 31 * 31 * val[i] + 31 * 31
	                * val[i + 1] + 31 * val[i + 2] + val[i + 3];
	    }
	    for (; i < val.length; i++) {
	        h = 31 * h + val[i];
	    }
	    return h;
	}
}
