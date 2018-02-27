package com.scut.login.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.scut.login.Feign.UserDao;
import com.scut.login.Feign.WechatServer;
import com.scut.login.config.Constant;
import com.scut.login.entity.DoctorEntity;
import com.scut.login.util.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);
    @Autowired
    UserDao userDao;
    @Autowired
    private JwtUtil jwt;
    @Autowired
    DoctorEntity doctorEntity;
    @Autowired
    SendMsgService sendMsgService;
    @Autowired
    WechatServer wechatServer;
    @Value("${jwt.key}")
    private String KEY;

    /**
     * 登录操作
     *
     * @param request
     * @param response
     * @param map
     * @return
     */
    public ResponseEntity<String> login(HttpServletRequest request, HttpServletResponse response, Map<String, String> map) {
//		System.out.println(doctorEntity.getPhone());
        String phone = map.get("phone");
        String password = map.get("password");
        //System.out.println(phone);
        try {
            if (!userDao.loginverify(phone, password)) {
                logger.error(phone + "-login: 账号或者密码错误");
                return ResponseUtil.exception("账号或者密码错误");
            }
            String verifytoken = userDao.phonetokenselect(phone);
            if (verifytoken != null) {
                userDao.delete(verifytoken);
                logger.error(phone + "-login: 请勿重复登录");
                return ResponseUtil.TOKENexception("请再试一次");
            }
//			user.setRoleId(1L);
            //生成Token
            doctorEntity.setPhone(phone);
            String subject = JwtUtil.generalSubject(doctorEntity);
            String token = jwt.createJWT(Constant.JWT_ID, subject, Constant.JWT_TTL);
            String state = userDao.savephonetoken(token, phone);
            if (state.equals("error")){
                logger.error(phone + "-login: 号码未保存成功登录失败");
                return ResponseUtil.exception(phone + "-login: 登录失败");
            }
            Claims claims = jwt.parseJWT(token);
           // System.out.println("now：" + System.currentTimeMillis() + "过期时间：" + claims.getExpiration().getTime());
            System.out.println("token:" + token);
            int evaluationUnread = userDao.getEvaluationUnread(phone);
//			String refreshToken = jwt.createJWT(Constant.JWT_ID, subject, Constant.JWT_REFRESH_TTL);
            JSONObject jo = new JSONObject();
            DoctorEntity DoctorEntity = userDao.info(phone);
            if (DoctorEntity == null){
                logger.error(phone + "-login: 医生实体未取到");
                return ResponseUtil.exception("登录失败");
            }
            jo.put("name", DoctorEntity.getName());
            jo.put("hospital", DoctorEntity.getHospital());
            jo.put("department", DoctorEntity.getDepartment());
            jo.put("title", DoctorEntity.getTitle());
            jo.put("verify", DoctorEntity.getVerify());
            jo.put("head_pic", DoctorEntity.getHead_pic());
            jo.put("evaluationUnread", evaluationUnread);
            jo.put("adept",DoctorEntity.getAdept());
            jo.put("experience",DoctorEntity.getExperience());
            jo.put("QRcode_pic",DoctorEntity.getQRcode_pic());
            jo.put("token", token);
//			jo.put("refreshToken", refreshToken);
            logger.info(phone + "-login: 登录成功");
            return ResponseUtil.success(jo);
        } catch (Exception e) {
            logger.error(phone + "-login: " + e);
            return ResponseUtil.unKonwException();
        }
    }

    /**
     * 注销操作
     *
     * @param request
     * @param response
     * @return
     */
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Authorization");
        try{
            if (!jwt.isExist(token)) {
                logger.error("logout: 请勿重复操作");
                return ResponseUtil.TOKENexception("请勿重复操作");
            }
            
            userDao.delete(token);
            logger.info("logout: 注销成功");
            return ResponseUtil.success("注销成功");
        }catch (Exception e){
            logger.error("logout: 注销失败");
            return ResponseUtil.exception("注销失败");
        }

    }



    /**
     * 注册获取验证码
     *
     * @param requestMap
     * @return
     */
    public ResponseEntity<String> sendMsg(Map<String, Object> requestMap) {
        try {
            String phone = requestMap.get("phoneNumber").toString();
            if(userDao.info(phone) != null){
                logger.error(phone + "-sendMsg: 该手机已被注册");
                return ResponseUtil.exception("该手机已注册，请重试？？？");
            }
            String randomNum = sendMsgService.createRandomNum(6);// 生成随机数
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MINUTE, 30);
            String currentTime = sf.format(c.getTime());// 生成30分钟后时间，用户校验是否过期

            if(sendMsgService.sendMsg(phone,randomNum)=="0"){ //此处执行发送短信验证码方法
                logger.error(phone + "-sendMsg: 获取验证码失败");
                return ResponseUtil.exception("验证码获取失败请稍后再试");
            }
            String hash = MD5Utils.getMD5Code(KEY + "@" + currentTime + "@" + randomNum);//生成MD5值
//            System.out.println(randomNum + " " + hash);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("hash", hash);
            resultMap.put("tamp", currentTime);
            logger.info(phone + "-sendMsg: 成功发送短信");
            return ResponseUtil.success(resultMap); //将hash值和tamp时间返回给前端
        } catch (Exception e) {
            logger.error("sendMsg: "+ e);
            return ResponseUtil.exception("验证码获取失败请稍后再试");
        }
    }

    /**
     * 效验验证码并注册新用户
     * @param requestMap
     * @return
     */
    public ResponseEntity<String> addregister(Map<String, Object> requestMap) {
        try {
            String phone = requestMap.get("phone").toString();
            String password = requestMap.get("password").toString();
            String requestHash = requestMap.get("hash").toString();
            String tamp = requestMap.get("tamp").toString();
            String msgNum = requestMap.get("msgNum").toString();
            String hash = MD5Utils.getMD5Code(KEY + "@" + tamp + "@" + msgNum);
//            System.out.println(msgNum + " " + hash);
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar c = Calendar.getInstance();
            String currentTime = sf.format(c.getTime());
            if (tamp.compareTo(currentTime) > 0) {  //判断是否过期
                if (hash.equalsIgnoreCase(requestHash)) {
                    //校验成功

                    JsonResult result = wechatServer.qrCodeCreateLogo(phone);
                    if(result.getErrorcode().equals("0")){
                        JsonResult getQrcode = wechatServer.getdoctorqrcodeurl(phone);
                        String qrCode = String.valueOf(getQrcode.getData());
                        String state = userDao.register(phone,password,qrCode);
                        if (!state.equals("success")){
                            logger.error(phone + "-addregister: 注册失败");
                            return ResponseUtil.exception("注册失败");
                        }
                        JSONObject jo = new JSONObject();
                        jo.put("qrcode",getQrcode.getData());
                        logger.info(phone + "-addregister: 注册成功");
                        return ResponseUtil.success(jo);
                    }else {
                        logger.error(phone + "-addregister: "+ result.getMessage());
                        return ResponseUtil.exception(result.getMessage());
                    }
                } else {
                    //验证码不正确，校验失败
                    logger.error(phone + "-addregister: 验证码不正确");
                    return ResponseUtil.exception("验证码不正确，请确认");
                }
            } else {
                // 超时过期
                logger.error(phone + "-addregister: 验证码已过期");
                return ResponseUtil.exception("验证码已过期，请重新获取");
            }
        } catch (Exception e) {
            logger.error("addregister: "+ e);
            return ResponseUtil.exception("系统操作失败，请重试");
        }
    }

    /**
     * 修改密码
     * @param request
     * @param response
     * @param map
     * @return
     */
    public ResponseEntity<String> changePwd(HttpServletRequest request,HttpServletResponse response,Map<String,String> map){
        String token = request.getHeader("Authorization");
        String oldpwd = map.get("oldpwd");
        String newpwd = map.get("newpwd");
        try{
            
            if(!jwt.isExist(token)){
                logger.error("changePwd: token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj= JSON.parseObject(doctor);
            String phone=jobj.get("phone").toString();

            //检查旧密码是否正确
            if (userDao.loginverify(phone, oldpwd)) {
                String state = userDao.updatepwd(phone,newpwd);
                if (!state.equals("success")){
                    logger.error(phone+"-changePwd: 密码更新失败");
                    return ResponseUtil.exception("修改密码失败");
                }
                userDao.delete(token);
                logger.info(phone + "-changePwd: 修改密码成功");
                return ResponseUtil.success();
            }else {
                logger.error(phone + "-changePwd: 修改失败，请检查密码是否输入正确");
                return ResponseUtil.exception("修改失败，请检查密码是否输入正确");
            }

        }catch (ExpiredJwtException e){
            logger.error("changePwd: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("changePwd: " + e);
            return ResponseUtil.unKonwException();
        }
    }

    /**
     * 找回密码发送验证码
     * @param map
     * @return
     */
    public ResponseEntity<String> sendMsgFindPwd(Map<String,Object> map){
        try {
            String phone = map.get("phoneNumber").toString();

            String randomNum = sendMsgService.createRandomNum(6);// 生成随机数
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MINUTE, 30);
            String currentTime = sf.format(c.getTime());// 生成30分钟后时间，用户校验是否过期

            if(sendMsgService.sendMsg(phone,randomNum)=="0"){ //此处执行发送短信验证码方法
                logger.error(phone + "-sendMsgFindPwd: 获取验证码失败");
                return ResponseUtil.exception("验证码获取失败请稍后再试");
            }
            String hash = MD5Utils.getMD5Code(KEY + "@" + currentTime + "@" + randomNum);//生成MD5值
//            System.out.println(randomNum + " " + hash);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("hash", hash);
            resultMap.put("tamp", currentTime);
            logger.info(phone + "-sendMsgFindPwd: 成功发送短信");
            return ResponseUtil.success(resultMap); //将hash值和tamp时间返回给前端
        } catch (Exception e) {
            logger.error("sendMsgFindPwd: "+ e);
            return ResponseUtil.exception("验证码获取失败请稍后再试");
        }
    }

    /**
     * 找回密码
     * @param map
     * @return
     */
    public ResponseEntity<String> findPwd(Map<String, Object> map) {
        try {
            String phone = map.get("phone").toString();
            String password = map.get("password").toString();
            String requestHash = map.get("hash").toString();
            String tamp = map.get("tamp").toString();
            String msgNum = map.get("msgNum").toString();
            String hash = MD5Utils.getMD5Code(KEY + "@" + tamp + "@" + msgNum);
//            System.out.println(msgNum + " " + hash);
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar c = Calendar.getInstance();
            String currentTime = sf.format(c.getTime());
            if (tamp.compareTo(currentTime) > 0) {  //判断是否过期
                if (hash.equalsIgnoreCase(requestHash)) {
                    //校验成功
                    String state = userDao.updatepwd(phone,password);
                    if(state.equals("success")){
                        logger.info(phone + "-findPwd: 找回密码操作成功");
                        return ResponseUtil.success("操作成功");
                    }else {
                        logger.error(phone + "-findPwd: 找回密码修改失败");
                        return ResponseUtil.exception("操作失败");
                    }

                } else {
                    //验证码不正确，校验失败
                    logger.error(phone + "-findPwd: 验证码不正确");
                    return ResponseUtil.exception("验证码不正确，请确认");
                }
            } else {
                // 超时过期
                logger.error(phone + "-findPwd: 验证码已过期");
                return ResponseUtil.exception("验证码已过期，请重新获取");
            }
        } catch (Exception e) {
            logger.error("findPwd: " + e);
            return ResponseUtil.exception("系统操作失败，请重试");
        }
    }
//    public String register(HttpServletRequest request){
//		String username = request.getParameter("username");
//		String password = request.getParameter("password");
//		String password2 = request.getParameter("password2");
//		User user = new User();
//		if (password.equals(password2)&&(userDao.info(username) == null)){
//			userDao.register(username,password);
//			System.out.println("注册成功");
//			return "login";
//		}else {
//			System.out.println("失败");
//			return "register";
//		}
//	}

}
