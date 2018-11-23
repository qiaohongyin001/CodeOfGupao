package javax.core.common.encrypt;

import javax.core.common.utils.StringUtils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5MessageEncrypt extends MessageEncrypt {

	@Override
	public byte[] decode(byte[] input) {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] encode(byte[] input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(input);
			return  StringUtils.bytes2Hex(md.digest()).getBytes();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
