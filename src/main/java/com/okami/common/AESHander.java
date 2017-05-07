package com.okami.common;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.okami.bean.GlobaVariableBean;
import com.okami.util.DataUtil;


/**
 * AES 加解密模块
 * @author orleven
 * @date 2017年5月4日
 */
@Component
public class AESHander {

    
	private Cipher cipher;
	private SecretKeySpec keySpec;
	private IvParameterSpec ivSpec;
	private boolean flag = false;
	
	public void AESInit(String key,String iv)  throws Exception{
		// AES/CFB/NoPadding         
		// AES/CFB/PKCS5Padding  
		// AES/CFB/ISO10126Padding
        cipher = Cipher.getInstance("AES/CFB/NoPadding");
        keySpec = new SecretKeySpec(key.getBytes(), "AES");
        ivSpec = new IvParameterSpec(iv.getBytes());
        flag = true;
	}
	

	/**
	 * 加密
	 * @data 2017年5月5日
	 * @param content
	 * @return
	 */
	public String encrypt(String content){
		if(flag){
			String result = null;
			try {
				cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
				result = DataUtil.base64Encode(cipher.doFinal(content.getBytes()));
			} catch (IllegalBlockSizeException | BadPaddingException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			}
			return result;
		}
		return content;
	}
	
	/**
	 * 解密
	 * @data 2017年5月5日
	 * @param content
	 * @return
	 */
	public String decrypt(String content){
		if(flag){
			String result = null;
			try {
				cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
				result = new String((cipher.doFinal(DataUtil.base64Decode(content))));
			} catch (IllegalBlockSizeException | BadPaddingException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			}
		    return new String(result);
		}
		return content;
	}
	
    public boolean getFlag(){
    	return this.flag;
    }
}
