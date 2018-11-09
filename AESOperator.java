import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/**
 *
 * @author lhl AES128 �㷨
 *
 *         CBC ģʽ
 *
 *         PKCS7Padding ���ģʽ
 *
 *         CBCģʽ��Ҫ���һ������iv
 *
 *         ����java ��֧��PKCS7Padding��ֻ֧��PKCS5Padding ����PKCS7Padding �� PKCS5Padding
 *         û��ʲô���� Ҫʵ����java����PKCS7Padding��䣬��Ҫ�õ�bouncycastle�����ʵ��
 */
public class AESOperator {
	// �㷨����
	final String KEY_ALGORITHM = "AES";
	// �ӽ����㷨/ģʽ/��䷽ʽ
	final String algorithmStr = "AES/CBC/PKCS7Padding";
	//
	private Key key;
	private Cipher cipher;
	boolean isInited = false;

	//ƫ��iv 16λ��16���Ƶ�ASCII�룬������ʾ����iv��ABCDEF1234123412����Կ�ڴ���ʵ��ʱ����
	byte[] iv = { 0x41, 0x42, 0x43, 0x44, 0x45, 0x46,0x31, 0x32, 0x33, 0x34, 0x31, 0x32, 0x33, 0x34, 0x31, 0x32 };
	
	public void init(byte[] keyBytes) {

		// �����Կ����16λ����ô�Ͳ���. ���if �е����ݺ���Ҫ
		int base = 16;
		if (keyBytes.length % base != 0) {
			int groups = keyBytes.length / base + (keyBytes.length % base != 0 ? 1 : 0);
			byte[] temp = new byte[groups * base];
			Arrays.fill(temp, (byte) 0);
			System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
			keyBytes = temp;
		}
		// ��ʼ��
		Security.addProvider(new BouncyCastleProvider());
		// ת����JAVA����Կ��ʽ
		key = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
		try {
			// ��ʼ��cipher
			cipher = Cipher.getInstance(algorithmStr, "BC");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
	}


	/**
	 * ���ܷ���
	 *
	 * @param encryptedData
	 *            Ҫ���ܵ��ַ���
	 * @param keyBytes
	 *            ������Կ
	 * @return
	 */
	public byte[] decrypt(String encryptedData, byte[] keyBytes) {
		byte[] encryptedText = null;
		init(keyBytes);
		try {
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
			//��ǰ�˴����ļ�������ת���ɴ˷�����Ҫ������
			byte[] DataByte = Hex.decode(encryptedData);
			//����
			encryptedText = cipher.doFinal(DataByte);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedText;
	}


	//����һ����̨javaʹ�õ�ʾ��
	public static void main(String[] args) {
		//����AES���ܵ���Կ 16λ��16���Ƶ�ASCII�룬������ʾ����1234123412ABCDEF
		byte[] keybytes = { 0x31, 0x32, 0x33, 0x34, 0x31, 0x32, 0x33, 0x34, 0x31, 0x32, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46 };
		//AES����ʵ��
		AESOperator aes = new AESOperator();

		//����data��ǰ�˴������ܹ������ݣ����ִ�Сд
		String data = "b59227d86200d7fedfb8418a59a8eea9";

		//���ܲ����
		System.Out.println(new String( aes.decrypt(data,keybytes)));

	}
}