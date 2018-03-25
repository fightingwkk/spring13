package com.scut.login.util;

import com.alibaba.fastjson.JSONObject;
import com.scut.login.Feign.UserDao;
import com.scut.login.entity.DoctorEntity;
import com.scut.login.config.Constant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Component
public class JwtUtil {
	
	@Value("${jwt.key}")
    private String key;

	@Autowired
	UserDao userDao;
	
	/**
	 * 由字符串生成加密key
	 * @return
	 */
	public SecretKey generalKey(){
		String stringKey = key + Constant.JWT_SECRET;
		byte[] encodedKey = Base64.decodeBase64(stringKey);
	    SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
	    return key;
	}

	/**
	 * 创建jwt
	 * @param id
	 * @param subject
	 * @param ttlMillis
	 * @return
	 * @throws Exception
	 */
	public String createJWT(String id, String subject, long ttlMillis) throws Exception {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		SecretKey key = generalKey();
		JwtBuilder builder = Jwts.builder()
			.setId(id)
			.setIssuedAt(now)
			.setSubject(subject)	//放入用户信息
		    .signWith(signatureAlgorithm, key);
		if (ttlMillis >= 0) {
		    long expMillis = nowMillis + ttlMillis;
		    Date exp = new Date(expMillis);
		    builder.setExpiration(exp);
		}
		return builder.compact();
	}
	
	/**
	 * 解密jwt
	 * @param jwt
	 * @return
	 * @throws Exception
	 */
	public Claims parseJWT(String jwt) throws Exception{
		SecretKey key = generalKey();
		Claims claims = Jwts.parser()         
		   .setSigningKey(key)
		   .parseClaimsJws(jwt).getBody();
		return claims;
	}

	public Claims parseJWTnoException(String jwt){
		SecretKey key = generalKey();
		Claims claims = Jwts.parser()
				.setSigningKey(key)
				.parseClaimsJws(jwt).getBody();
		return claims;
	}
	/**
	 * 验证jwt
	 *
	 */
	public boolean isExist(String token) {
		if(token == null){
			return false;
		}
		String isExist = userDao.isExist(token);		//先判断是否存在
		if(isExist == null){
			return false;
		}
		return true;
	}


	/**
	 * 生成subject信息
	 * @param doctorEntity
	 * @return
	 */
	public static String generalSubject(DoctorEntity doctorEntity){
		JSONObject jo = new JSONObject();
		jo.put("phone", doctorEntity.getPhone());
//		jo.put("roleId", doctorEntity.getRoleId());
		return jo.toJSONString();
	}
}
