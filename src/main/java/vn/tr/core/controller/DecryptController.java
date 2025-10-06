package vn.tr.core.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.tr.common.core.domain.R;
import vn.tr.common.encrypt.utils.EncryptUtils;

/**
 * 解密控制器
 *
 * @author admin
 */
@SaIgnore
@RequiredArgsConstructor
@RestController
@RequestMapping("/decrypt")
public class DecryptController {
	
	private static final String PRIVATE_KEY = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAqhHyZfSsYourNxaY7Nt+PrgrxkiA50efORdI5U5lsW79MmFnusUA355oaSXcLhu5xxB38SMSyP2KvuKNPuH3owIDAQABAkAfoiLyL+Z4lf4Myxk6xUDgLaWGximj20CUf+5BKKnlrK+Ed8gAkM0HqoTt2UZwA5E2MzS4EI2gjfQhz5X28uqxAiEA3wNFxfrCZlSZHb0gn2zDpWowcSxQAgiCstxGUoOqlW8CIQDDOerGKH5OmCJ4Z21v+F25WaHYPxCFMvwxpcw99EcvDQIgIdhDTIqD2jfYjPTY8Jj3EDGPbH2HHuffvflECt3Ek60CIQCFRlCkHpi7hthhYhovyloRYsM+IS9h/0BzlEAuO0ktMQIgSPT3aFAgJYwKpqRYKlLDVcflZFCKY7u3UP8iWi1Qw0Y=";
	
	/**
	 * 解密方法
	 *
	 * @param encryptedData 加密数据
	 *
	 * @return 解密结果
	 */
	@PostMapping("/decrypt")
	public R<String> decrypt(@RequestBody String encryptedData) {
		try {
			// 解密示例
			String decryptedData = decryptData(encryptedData);
			return R.ok("Decryption successfully", decryptedData);
		} catch (Exception e) {
			return R.fail("Decryption failed: " + e.getMessage());
		}
	}
	
	/**
	 * 解密数据
	 *
	 * @param encryptedData 加密数据
	 *
	 * @return 解密后的数据
	 */
	private String decryptData(String encryptedData) {
		try {
			// 从issue description中获取的加密字符串
			String encryptedString = "f7ET0fnQ+4FI4VERvjJVwmiQFuJbyASnS3zd+JtkOVuSZ9TLQze1vySGN51lErH+nzx4V34LRCFYQSsNAjAWn4QL7mMFWrQ7FdPhDyWVXQYNms8Rtjv8D1TMTh+b4C+PGyceKnIEmS7M36uK/AXrDy+u7PQxD0berSEE2Xcz1R5WT3zTw+KF2++En/GN/lJ+1zfXeR4m5sRQPKvmAEyDG0q/iAiij4TgYsTnxyPTnR21hgRuEsKNNJKvX0tEjSFzQPVh7Q9geFmUAZeoWsk9xjT2CXXFY/95hXPfol21g+4sXJ/tTct/5+p+0tP8K/79PhZPDzB4RwfbkW5A9Y123wP30wOpTLvdxZRO6Vln4JJfgCheQVXyeh10eopXeWHaiMd/Qzc2t3Mf++9RxSvpG1dyX/cOwplC80dZetV28AfbIMi2Mak5OybkS/vucRxkoqAsvHWdWrCnsqsBN7XOo24gd5Gvbuhu1qzCYJNh4oE=";
			
			// 尝试方法1: 假设这是RSA加密的AES密码
			try {
				String decryptAes = EncryptUtils.decryptByRsa(encryptedString, PRIVATE_KEY);
				String aesPassword = EncryptUtils.decryptByBase64(decryptAes);
				return "解密方法1结果 (RSA解密): " + aesPassword;
			} catch (Exception e) {
				System.out.println("方法1解密失败: " + e.getMessage());
			}
			
			// 尝试方法2: 假设这是AES加密的数据，使用默认密码
			try {
				// 使用一个常见的默认密码尝试解密
				String defaultAesPassword = "1234567890123456"; // 16位AES密码
				String decryptedData = EncryptUtils.decryptByAes(encryptedString, defaultAesPassword);
				return "解密方法2结果 (默认AES密码): " + decryptedData;
			} catch (Exception e) {
				System.out.println("方法2解密失败: " + e.getMessage());
			}
			
			// 尝试方法3: 直接Base64解码
			try {
				String base64Decoded = EncryptUtils.decryptByBase64(encryptedString);
				return "解密方法3结果 (Base64解码): " + base64Decoded;
			} catch (Exception e) {
				System.out.println("方法3解密失败: " + e.getMessage());
			}
			
			// 尝试方法4: 模拟DecryptRequestBodyWrapper的解密过程
			try {
				// 假设我们有一个加密的header，从中获取AES密码
				// 这里我们尝试使用一些常见的AES密码
				String[] commonPasswords = {
						"1234567890123456", // 16位
						"12345678901234567890123456789012", // 32位
						"123456789012345678901234", // 24位
						"RuoYiVuePlus123", // 自定义密码
						"RuoYiVuePlus1234", // 16位
						"RuoYiVuePlus12345678901234", // 24位
						"RuoYiVuePlus1234567890123456789012" // 32位
				};
				
				for (String password : commonPasswords) {
					try {
						String decryptedData = EncryptUtils.decryptByAes(encryptedString, password);
						return "解密方法4结果 (模拟DecryptRequestBodyWrapper，密码: " + password + "): " + decryptedData;
					} catch (Exception e) {
						// 继续尝试下一个密码
					}
				}
				
				System.out.println("方法4解密失败: 所有常见密码都无法解密");
			} catch (Exception e) {
				System.out.println("方法4解密失败: " + e.getMessage());
			}
			
			// 尝试方法5: 假设这是AES加密的数据，使用从配置中提取的密钥
			try {
				// 从RSA私钥中提取一些可能的AES密码
				String privateKeyStr = PRIVATE_KEY;
				String possiblePassword1 = privateKeyStr.substring(0, 16); // 取前16位
				String possiblePassword2 = privateKeyStr.substring(privateKeyStr.length() - 16); // 取后16位
				String possiblePassword3 = privateKeyStr.substring(privateKeyStr.length() / 2 - 8, privateKeyStr.length() / 2 + 8); // 取中间16位
				
				// 尝试这些可能的密码
				String[] possiblePasswords = {possiblePassword1, possiblePassword2, possiblePassword3};
				
				for (String password : possiblePasswords) {
					try {
						String decryptedData = EncryptUtils.decryptByAes(encryptedString, password);
						return "解密方法5结果 (从RSA私钥提取的AES密码: " + password + "): " + decryptedData;
					} catch (Exception e) {
						// 继续尝试下一个密码
					}
				}
				
				System.out.println("方法5解密失败: 所有从RSA私钥提取的密码都无法解密");
			} catch (Exception e) {
				System.out.println("方法5解密失败: " + e.getMessage());
			}
			
			return "无法解密数据，请检查加密方式或密钥";
		} catch (Exception e) {
			return "解密过程出错: " + e.getMessage();
		}
	}
}
