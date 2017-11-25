package com.ezweb.engine;

import com.ezweb.engine.util.RsaKeyHelper;
import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

/**
 * <一句话说明功能>
 * <功能详细描述>
 *
 * @author zuodengpeng
 * @version 1.0.0
 * @date 2017/11/23
 */
public class RsaKeyTest {
	@Test
	public void testKey() {
		String pemData = "" +
				"-----BEGIN RSA PRIVATE KEY-----\n" +
				"MIIEogIBAAKCAQEAx7GzoGxs78tow0LIqvJ881EPc0YRycetDZvKR00sGOKLSp/u\n" +
				"TJhh7+QOpHrRm/jD5xXivJQbIx03jacqCly7v8IaI+FLaoZ56MIDfT5UNXDX4Uns\n" +
				"9cYFWLi1uQfjGGF492mond/oo/ZUKNpHxgtnID9Clviug+Lge7zEZqwE3tTRaPxt\n" +
				"FV0abZqEdf9Z6kiqNq3xhBnltk/lwUek+XSj006WtgVs5q5M64YxuH4+ihEky95j\n" +
				"D/AEFAI9BUwPWpaRQvTcU0Bohu9x+GGLy6XBUvBaMY4TqF5ZQpe8VUSh0IpnWwaZ\n" +
				"dIYmMRjSzFQMJZDkp/EFfpb81WsrJI14zdRZzwIDAQABAoIBAHvIe0AF9cDupZHj\n" +
				"49+TUEDDDoC1/nDFhU/aMhjgjWUfiJhJar2v94VsKGdsemHkYgrsVg/u/qNViQoV\n" +
				"hMnHkdnbx7hV/xmZIhhVqzQHXsuBl0/fMzNl/Apy9LItYWfLg+BZblvuX5bDKofo\n" +
				"RnVMvDARBoXuwL6f8+a/rD64goxc5V/wFw2uKvXOM7oOipTQnjnN0Osra/zKUuzY\n" +
				"6/jFRhlmfV2zq4bqY6HxFxQw0/khVEIjNAy/yI1nQYEzqwe76r36ZIy0jSjzjKrx\n" +
				"ktMfIJ7CfqaDRmIiZTMzdRQ5EmweMq4Bm3vCzJ5oh1Rp89COSqlTMCgRmq/bLscK\n" +
				"MZVSeIECgYEA+tZjYwPThDpoDvmFQSK26Pr7SKqUSVdqCajGY4sXMASE0Ua2AEMW\n" +
				"OIaID3oDGA2g+04N/Bvn3yzb6//G277jgvQoXLuB91jXoTdvN1pIL0KpZEV2cT6M\n" +
				"GeIySyIImYArbWkvpb3NP0fSTvw8cqpmqJJ6Ooc+6Pe+X4KCGTq6hz0CgYEAy83Z\n" +
				"hk0xtjzraqVMMPT4x+xFZXDJHrA5v+t2G+mrMnrr6LKn/kR6EUq/dppisuAfaoDK\n" +
				"t4Ovr3UDvVt+RK1M5wYR8805ytTBeNeK/wNAATA4QZYs7bJ2WQ1capkU3qPASDXe\n" +
				"9Vf0o8C15IZ69pRB9Y7FyBJ0mCRRWpeAeubc1fsCgYB1RKt8S/qkE+Y4MgFRXVhj\n" +
				"/YpjA5SF4T8quuPc/9bcVRB/Am7uGm8WCBcuHR27Lgv30RsKpUU2+jLq105xx4F3\n" +
				"5IvGJsdxCvDsZw0wtJ2QsrqcrTjdYp9/LlfS9ltM4DXtVcK32s9M6VfyI0xjUzTh\n" +
				"VHGeUUJhCgnPfyUxhFpWBQKBgBDzhzM3enoI6R/Ord6okYS3bzI3xlKqH7OJ1yIN\n" +
				"NnMVbEfHjFeGxIxppjEsOCdeot5N5g6LgnJajjwSvxbhPzM7+wRHPpZTSeBONT5u\n" +
				"u8UdiKBT6FrA7D0N8nDfWLAH+LdI28mWTj/MiJSAZvvZav8MqACzIEWDR8z8KvCO\n" +
				"ATI/AoGAHDvDI3/4gcbHGjYcTXuemJc9RHFN9jO9EY7vm/B4hS1NqG8qoNf62yH1\n" +
				"MIfqcNbvsU8bEKQJk6WffGgK9DFKZIJvQX+Z/SJ1nCLDr+tbwfWI5IqZ/wXBhjCy\n" +
				"tE/Qiu0WSA7KX/eTSUb2vGtqPDh3L8Vyz8dtvCJP1V15c9f2QP4=\n" +
				"-----END RSA PRIVATE KEY-----\n";
		String public_ssh = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCsKWzKo8aEhIkXlSAG2b0S3DgM2ARtxd5P4cHxcxUISqpmoal1Z09+Ys6TdsMEHIcSaGH4gx+R8O/eQHLu42q2ecMRIamSNCQyVF778ARs6die3CBn/bTIwumobtoLksEL2v6H6SrpDQaB4MPpPjNdfZMVaicPZ0bE8c5TlbAeV5j1eexgNalogaKOPHG28G2BGWc86R9dXR0a1n7cda3Yrmy9hHA7gAZ7tC41GZERObtDlBUgAPQq0NcHNPYaNpsbPJEvkCG4BKJo3Dv6CxkrD6tXmoRCEEMkxXQ58ac+PE47i6yycHR8KNahaWUHp/hwArH75jwIrBu3+vZjFcTt cqzuodp@163.com";
		String public_pem = "" +
				"-----BEGIN PUBLIC KEY-----\n" +
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArClsyqPGhISJF5UgBtm9Etw4DNgEbcXe\n" +
				"T+HB8XMVCEqqZqGpdWdPfmLOk3bDBByHEmhh+IMfkfDv3kBy7uNqtnnDESGpkjQkMlRe+/AEbOnY\n" +
				"ntwgZ/20yMLpqG7aC5LBC9r+h+kq6Q0GgeDD6T4zXX2TFWonD2dGxPHOU5WwHleY9XnsYDWpaIGi\n" +
				"jjxxtvBtgRlnPOkfXV0dGtZ+3HWt2K5svYRwO4AGe7QuNRmRETm7Q5QVIAD0KtDXBzT2GjabGzyR\n" +
				"L5AhuASiaNw7+gsZKw+rV5qEQhBDJMV0OfGnPjxOO4ussnB0fCjWoWllB6f4cAKx++Y8CKwbt/r2\n" +
				"YxXE7QIDAQAB\n" +
				"-----END PUBLIC KEY-----\n";
		{

			KeyPair kp = RsaKeyHelper.parseKeyPair(pemData);
			System.out.println(kp.getPublic());
			System.out.println(kp.getPrivate());
		}
		{
			RSAPublicKey pub = RsaKeyHelper.parsePublicKey(public_ssh);
			String encodePublicKey = RsaKeyHelper.fmtSSHPublicKey(pub, "cqzuodp@163.com");
			Assert.assertEquals(public_ssh, encodePublicKey);

			RSAPublicKey pub2 = RsaKeyHelper.parsePublicKey(public_pem);
			Assert.assertEquals(pub2, pub);

			String encodeX509 = RsaKeyHelper.fmtX509PublicKey(pub2);
			Assert.assertEquals(encodeX509, public_pem); // openssl 的 bases64 是64个字符就换行，不是标准的Base64的76个字符+换行，就算找到原因了。
		}
	}
}
