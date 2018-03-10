package com.scut.login.controller;

import com.scut.login.Feign.UserDao;
import com.scut.login.entity.DoctorEntity;
//import com.scut.login.security.WebSecurityConfig;
import com.scut.login.service.DoctorInfo;
import com.scut.login.service.UserService;
import net.sf.json.util.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ai on 2017/8/3.
 */
@CrossOrigin
@RestController
public class DoctorInfoController {
    @Autowired
    private DoctorInfo doctorInfo;
    @Autowired
    UserDao userDao;


//    @CrossOrigin(allowCredentials="true", allowedHeaders="*", methods={RequestMethod.GET,
//            RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS,
//            RequestMethod.HEAD, RequestMethod.PUT, RequestMethod.PATCH}, origins="*")

    /**
     * 医生信息查询并返回
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "doctorInfo",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> doctorInfo(HttpServletRequest request, HttpServletResponse response){
        return doctorInfo.doctorInfo(request,response);
    }

    /**
     * 上传更新医生头像
     * @param request
     * @param response
     * @param file
     * @return
     */
    @RequestMapping(value = "headimageUpload",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> imageUpload(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="file")MultipartFile file){
        return doctorInfo.headimageUpload(request,response,file);
    }

    /**
     * 填写医生擅长与职业经验
     * @param request
     * @param response
     * @param map
     * @return
     */
    @RequestMapping(value = "doctorIntroduction",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> doctorIntroduction(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String,String> map){
        return doctorInfo.doctorIntroduction(request,response,map);
    }

    /**
     * 个人资料填写
     * @param request
     * @param response
     * @param map
     * @return
     */
    @RequestMapping(value = "doctorInfoDetails",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> doctorInfoDetails(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,String> map){
        return doctorInfo.doctorInfoDetails(request,response,map);
    }

    /**
     * 医师执业证上传到阿里云
     * @param request
     * @param response
     * @param
     * @return
     */
    @RequestMapping(value = "practiceimageUploadali",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> practiceimageUploadali(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="file")MultipartFile file){

        return doctorInfo.practiceimageUploadali(request,response,file);
    }
    /**
     * 医师执业证上传数据库
     * @param request
     * @param response
     * @param
     * @return
     */
    @RequestMapping(value = "practiceimagUploaddb",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> practiceimagUploadDB(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String,String> map){

        return doctorInfo.practiceimagUploadDB(request,response,map);
    }
    /**
     * 获取医生个人信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getDoctorInfoDetails",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getDoctorInfoDetails(HttpServletRequest request,HttpServletResponse response){
        return doctorInfo.getDoctorInfoDetails(request,response);
    }

    /**
     * 评价列表
     * @param request
     * @return
     */
    @RequestMapping(value = "getEvaluationlist",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getEvaluationlist(HttpServletRequest request){
        return doctorInfo.getEvaluationlist(request);
    }

    /**
     * 查看一个评价
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "getEvaluationDetail",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getEvaluationDetail(HttpServletRequest request,@RequestBody Map<String,String>map){
        return doctorInfo.getEvaluationDetail(request,map);
    }

    /**
     * 删除评价
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "deleteEvaluation",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> deleteEvaluation(HttpServletRequest request,@RequestBody Map<String,String>map){
        return doctorInfo.deleteEvaluation(request,map);
    }

    /**
     * 生成医生二维码
     * @param request
     * @return
     */
    @RequestMapping(value = "qrCodeGet",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> qrCodeGet(HttpServletRequest request){
        return doctorInfo.qrCodeGet(request);
    }
//    @GetMapping("/doctorInfo")
//    public String doctorInfo(@SessionAttribute(WebSecurityConfig.SESSION_KEY) String account, Model model){
//        DoctorEntity doctorEntity =userDao.info(account);
////        DoctorEntity doctorEntity =userDao.info("137");//调试用户，记得改成需要认证的用户
//        Map returnMap = new HashMap();
//        boolean isFill = (doctorEntity.getName() == null)&&(doctorEntity.getPractice_code() == null);
//        returnMap.put("name",doctorEntity.getName());
//        returnMap.put("hospital",doctorEntity.getHospital());
//        returnMap.put("department",doctorEntity.getDepartment());
//        returnMap.put("title",doctorEntity.getTitle());
//        returnMap.put("verify",doctorEntity.getVerify());
//        returnMap.put("isFill",isFill);
////        model.addAttribute("name",doctorEntity.getName());
////        model.addAttribute("department",doctorEntity.getDepartment());
////        model.addAttribute("title",doctorEntity.getTitle());
////        model.addAttribute("hospital",doctorEntity.getHospital());
////        return "doctorInfo";
//        return JSONUtils.valueToString(returnMap);
//    }

    /**
     * 医生提交反馈
     * @param request
     * @param response
     * @param map
     * @return
     */
    @RequestMapping(value = "doctorFeedback",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> doctorFeedback(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,String> map){
        return doctorInfo.doctorFeedback(request,response,map);
    }

    /**
     * 医生获取软件
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "doctorSoftware",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> doctorSoftware(HttpServletRequest request,HttpServletResponse response){
        return doctorInfo.doctorSoftware(request);
    }
}
