//package com.scut.login.security;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import javax.inject.Inject;
//import javax.inject.Named;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.virt.mon.common.ProfilePreperties;
//import com.virt.mon.persistent.dao.OauthAccessTokenDAO;
//import com.virt.mon.persistent.entity.Account;
//import com.virt.mon.persistent.entity.OauthAccessToken;
//
///**
// *
// * @author wisteria
// * cache manager as a map
// */
//@Named
//public class TokenCacheManager {
//
//	private static Logger logger = LoggerFactory.getLogger(TokenCacheManager.class);
//
//	/** a map bulk contains token, simulate a cache manager**/
//	private static Map<String, TokenExpire> tokenMap = new ConcurrentHashMap<>();
//
//	/** Default time of token expiring is 30 minutes **/
//	private static int timeLongDefault = 30;
//
//	private static int mapMaxSize = 1000;
//	@Inject
//	private OauthAccessTokenDAO accessTokenDAO;
//
//	public TokenCacheManager() {
//		// TODO Auto-generated constructor stub
//	}
//
//	public void pushToken(String token, int expireTimeLong, OauthAccessToken accessToken, Account account) {
//		Long expireTimeDiff = (long) (expireTimeLong * 60 * 1000);
//		Long expireTime = System.currentTimeMillis() + expireTimeDiff;
//		TokenExpire tokenExpire = new TokenExpire(expireTime, accessToken, account);
//		this.clearToken();
//		tokenMap.put(token, tokenExpire);
//	}
//
//	public void pushToken(String token, OauthAccessToken accessToken, Account account) {
//		int expireTimeLong = loadExpireTime();
//		pushToken(token, expireTimeLong, accessToken, account);
//	}
//
//	public boolean isExpired(String token) {
//		if (!tokenMap.containsKey(token)) {
//			return false;
//		}
//		TokenExpire tokenExpire = tokenMap.get(token);
//		Long timeDiff = System.currentTimeMillis() - tokenExpire.getExpireTimestamp();
//		if (timeDiff >= 0) {
//			return true;
//		}
//		return false;
//	}
//
//	public boolean hasToken(String token) {
//		if (tokenMap.containsKey(token)) {
//			return true;
//		}
//		return false;
//	}
//
//	public boolean matchToken(String token) {
//		if (!tokenMap.containsKey(token)) return false;
//		TokenExpire tokenExpire = tokenMap.get(token);
//		Long timeDiff = System.currentTimeMillis() - tokenExpire.getExpireTimestamp();
//		if (timeDiff < 0) {
//			return true;
//		}
//		return false;
//	}
//
//	public boolean isManager(String token) {
//		if (tokenMap.containsKey(token)) {
//			TokenExpire expire = tokenMap.get(token);
//			if (expire.getAccessToken().getRole().contains("ADMIN")) return true;
//		}
//		OauthAccessToken accessToken = accessTokenDAO.findByToken(token.getBytes());
//		if (accessToken != null && accessToken.getRole().contains("ADMIN")) return true;
//		return false;
//	}
//
//	private int loadExpireTime() {
//		try {
//			timeLongDefault = ProfilePreperties.getInstance().getCacheTokenExpireTime();
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.debug("the configuration dose not contains cache.token.expire");
//			return timeLongDefault;
//		}
//		return timeLongDefault;
//	}
//
//	public void clearToken() {
//		if (tokenMap.size() > mapMaxSize) {
//			tokenMap.clear();
//		}
//	}
//
//	public void removeToken(String token) {
//		if (!tokenMap.containsKey(token)) {
//			return;
//		}
//		tokenMap.remove(token);
//	}
//}
