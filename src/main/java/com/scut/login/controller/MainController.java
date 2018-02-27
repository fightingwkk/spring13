package com.scut.login.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.scut.login.Feign.UserDao;
import com.scut.login.entity.DoctorEntity;
import com.scut.login.entity.User;
//import com.scut.login.security.WebSecurityConfig;
import com.scut.login.service.UserService;
import com.scut.login.util.AliyunMessageUtil;
import com.scut.login.util.MD5Utils;
import com.scut.login.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 控制器
 *
 */
@Controller
public class MainController {
//	@Autowired
//	private UserRepository userRepository;
	@Autowired
	UserDao userDao;
	@Autowired
	private UserService userService;

	/**
	 * 登录验证
	 * @param request
	 * @param response
	 * @param map
	 * @return
	 */
	@RequestMapping (value = "login", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
	public ResponseEntity<String> login(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String,String> map){
		return userService.login(request, response, map);
	}

	/**
	 * 注销
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "logout",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response){
		return userService.logout(request,response);
	}


	/**
	 * 获取验证码
	 * @param requestMap
	 * @return
	 */
	@RequestMapping(value = "/sendMsg", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
	public ResponseEntity<String> sendMsg(@RequestBody Map<String,Object> requestMap) {
		return userService.sendMsg(requestMap);
	}

	/**
	 * 效验验证码
	 * @param requestMap
	 * @return
	 */
	@RequestMapping(value = "/addregister", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
	public ResponseEntity<String> addregister(@RequestBody Map<String, Object> requestMap){
		return userService.addregister(requestMap);
	}

	/**
	 * 修改密码
	 * @param request
	 * @param response
	 * @param map
	 * @return
	 */
	@RequestMapping (value = "/changePwd", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
	public ResponseEntity<String> changePwd(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String,String> map){
		return userService.changePwd(request, response, map);
	}

	/**
	 * 找回密码发送验证码
	 * @param requestMap
	 * @return
	 */
	@RequestMapping(value = "/sendMsgFindPwd", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
	public ResponseEntity<String> sendMsgFindPwd(@RequestBody Map<String,Object> requestMap) {
		return userService.sendMsgFindPwd(requestMap);
	}

	/**
	 * 找回密码效验验证码
	 * @param requestMap
	 * @return
	 */
	@RequestMapping(value = "/findPwd", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
	public ResponseEntity<String> findPwd(@RequestBody Map<String, Object> requestMap){
		return userService.findPwd(requestMap);
	}


//	@GetMapping("/")
//	public String index(@SessionAttribute(WebSecurityConfig.SESSION_KEY) String account, Model model) {
//		model.addAttribute("name", account);
//		return "index";
//	}
//
//	@GetMapping("/login")
//	public String login() {
//		return "login";
//	}
//
//	@PostMapping("/loginPost")
//	public @ResponseBody Map<String, Object> loginPost(String account, String password, HttpSession session) {
//		Map<String, Object> map = new HashMap<>();
////		User user = userRepository.findByUsernameAndPassword(account,password);
//
//		if (!userDao.loginverify(account,password)) {
//			map.put("success", false);
//			map.put("message", "账号或密码错误");
//			return map;
//		}
//
//		// 设置session
//		session.setAttribute(WebSecurityConfig.SESSION_KEY, account);
//
//		map.put("success", true);
//		map.put("message", "登录成功");
//		return map;
//	}

////	@GetMapping("/doctorInfo")
////	public String doctorInfo(@SessionAttribute(WebSecurityConfig.SESSION_KEY) String account, Model model){
////		DoctorEntity doctorEntity =userDao.info(account);
////		model.addAttribute("name",doctorEntity.getName());
////		model.addAttribute("department",doctorEntity.getDepartment());
////		model.addAttribute("title",doctorEntity.getTitle());
////		model.addAttribute("hospital",doctorEntity.getHospital());
////		return "doctorInfo";
////	}
//	@GetMapping("/logout")
//	public String logout(HttpSession session) {
//		// 移除session
//		session.removeAttribute(WebSecurityConfig.SESSION_KEY);
//		return "redirect:/login";
//	}

}
