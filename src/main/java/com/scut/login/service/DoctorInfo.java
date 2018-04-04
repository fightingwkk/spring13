package com.scut.login.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scut.login.Feign.UserDao;
import com.scut.login.Feign.WechatServer;
import com.scut.login.config.Constant;
import com.scut.login.entity.DoctorEntity;
import com.scut.login.entity.EvaluationEntity;
import com.scut.login.entity.SoftwareEntity;
import com.scut.login.entity.SuggestionEntity;
import com.scut.login.util.JsonResult;
import com.scut.login.util.JwtUtil;
import com.scut.login.util.ResponseUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Ai on 2017/8/16.
 */
@Component
public class DoctorInfo {

    private static final Logger logger = LogManager.getLogger(DoctorInfo.class);

    @Autowired
    UserDao userDao;
    @Autowired
    WechatServer wechatServer;
    @Autowired
    private JwtUtil jwt;
    @Autowired
    AliyunStorageService aliyunStorageService;

    /**
     * 医生信息查询并返回
     *
     * @param request
     * @param response
     * @return
     */
    public ResponseEntity<String> doctorInfo(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Authorization"); //获取头部token
        try {

            //判断token是否存在
            //
            if (!jwt.isExist(token)) {
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
            }

            //验证token
            Claims claims = jwt.parseJWT(token);
            //获取医生实体信息
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            DoctorEntity doctorEntity = userDao.info(phone);
            if (doctorEntity == null) {
                logger.error(phone + "-doctorInfo: 未取到医生实体");
                return ResponseUtil.exception("未取到医生实体");
            }

            int evaluationUnread = userDao.getEvaluationUnread(phone);


            //刷新token
            String newtoken = jwt.createJWT(Constant.JWT_ID, doctor, Constant.JWT_TTL);
/*            userDao.delete(token);
            userDao.savephonetoken(newtoken,phone);*/
            String state = userDao.updatetoken(newtoken, token);
            // System.out.println(state);
            if (state.equals("success")) {
                //返回相关值，判断是否已经填写个人信息
                JSONObject jo = new JSONObject();
                jo.put("name", doctorEntity.getName());
                jo.put("hospital", doctorEntity.getHospital());
                jo.put("department", doctorEntity.getDepartment());
                jo.put("title", doctorEntity.getTitle());
                jo.put("verify", doctorEntity.getVerify());
                jo.put("head_pic", doctorEntity.getHead_pic());
                jo.put("evaluationUnread", evaluationUnread);
                jo.put("adept", doctorEntity.getAdept());
                jo.put("experience", doctorEntity.getExperience());
                jo.put("QRcode_pic", doctorEntity.getQRcode_pic());
                jo.put("token", newtoken);
                jo.put("phone", doctorEntity.getPhone());
                logger.info(phone + "-doctorInfo: 获取医生信息，刷新token");
                return ResponseUtil.success(jo);
            } else {
                logger.error(phone + "-doctorInfo: 未获取到刷新的token: " + state);
                return ResponseUtil.exception("未获取到刷新的token");
            }
            //System.out.println(newtoken);


        } catch (ExpiredJwtException | SignatureException e) {
            logger.error("doctorInfo: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        } catch (Exception e) {
            logger.error("doctorInfo: " + e);
            return ResponseUtil.unKonwException();
        }
    }

    /**
     * 上传更新医生头像
     *
     * @param request
     * @param response
     * @param file
     * @return
     */
    public ResponseEntity<String> headimageUpload(HttpServletRequest request, HttpServletResponse response, MultipartFile file) {
        String token = request.getHeader("Authorization");
        try {
            if (!jwt.isExist(token)) {
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
            }
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();
            DoctorEntity doctorEntity = userDao.info(phone);
            if (doctorEntity == null) {
                logger.error(phone + "-headimageUpload: 未获取到医生实体");
                return ResponseUtil.exception("未获取到医生实体");
            }

            //接收文件
            if (!file.isEmpty()) {
                if (file.getContentType().contains("image")) {
                    try {
                        //新图片名
                        String newFileName = "head_pic/doctor/" + phone + "/" + System.currentTimeMillis() + ".jpg";

                        //将file转为byte[]数组
                        byte[] byteArr = file.getBytes();

                        // 判断是否有旧头像，如果有就先删除旧头像，再上传
                        String old_pic = doctorEntity.getHead_pic();
                        if (StringUtils.isNotBlank(old_pic)) {
                            old_pic = old_pic.replace("http://jybl-photo.oss-cn-shenzhen.aliyuncs.com/", "");
                            if (aliyunStorageService.existfile(old_pic)) {
                                aliyunStorageService.deletefile(old_pic);
                            }

                        }
                        if (!aliyunStorageService.fileupload(newFileName, byteArr)) {
                            logger.error(phone + "-headimageUpload: 阿里云上传失败请重试");
                            return ResponseUtil.exception("阿里云上传失败请重试");
                        }
                        String webimage = "http://jybl-photo.oss-cn-shenzhen.aliyuncs.com/" + newFileName;

                        //将链接存入医生表
                        doctorEntity.setHead_pic(webimage);
                        doctorEntity.setPhone(phone);
                        String state = userDao.updatedoctorInfoDetails(doctorEntity);
                        if (state.equals("success")) {
                            JSONObject jo = new JSONObject();
                            jo.put("msg", "上传成功!");
                            jo.put("head_pic_url", webimage);
                            logger.info(phone + "-headimageUpload: 医生头像上传成功");
                            return ResponseUtil.success(jo);
                        } else {
                            logger.error(phone + "-headimageUpload: 医生头像未更新成功");
                            return ResponseUtil.exception("医生头像未更新成功");
                        }

                        // 将反斜杠转换为正斜杠
//                        String data = datdDirectory.replaceAll("\\\\", "/") + newFileName;
//                        Map<String, Object> resultMap = new HashMap<>();
//                        resultMap.put("file", data);
//                        resultVo.setData(resultMap);
//                        resultVo.setError(1, "上传成功!");
                    } catch (IOException e) {
                        logger.error(phone + "-headimageUpload: 头像上传失败");
                        return ResponseUtil.exception("上传失败请重试");
                    }
                } else {
                    logger.error(phone + "-headimageUpload: 上传的文件不是图片类型");
                    return ResponseUtil.exception("上传的文件不是图片类型，请重新上传!");
                }
            } else {
                logger.error(phone + "-headimageUpload: 上传失败");
                return ResponseUtil.exception("上传失败，请选择要上传的图片!");
            }

        } catch (ExpiredJwtException e) {
            logger.error("headimageUpload: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        } catch (Exception e) {
            logger.error("headimageUpload: " + e);
            return ResponseUtil.exception("上传失败");
        }
    }

    /**
     * 填写医生擅长与职业经验
     *
     * @param request
     * @param response
     * @param map
     * @return
     */
    public ResponseEntity<String> doctorIntroduction(HttpServletRequest request, HttpServletResponse response, Map<String, String> map) {
        //获取token，验证token
        String token = request.getHeader("Authorization");
        String adept = map.get("adept");
        String experience = map.get("experience");
        try {
            if (!jwt.isExist(token)) {
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            //更新个人简介
            String state = userDao.updateintroduction(phone, adept, experience);
            if (state.equals("success")) {
                logger.info(phone + "-doctorIntroduction: 医生介绍获取成功");
                return ResponseUtil.success();
            } else {
                logger.error(phone + "-doctorIntroduction: 医生介绍获取失败");
                return ResponseUtil.exception("医生介绍获取失败");
            }
//            //获取个人简介
//            DoctorEntity doctorEntity = userDao.info(phone);
//            JSONObject jo = new JSONObject();
//            jo.put("adept",doctorEntity.getAdept());
//            jo.put("experience",doctorEntity.getExperience());


        } catch (ExpiredJwtException e) {
            logger.error("doctorIntroduction: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        } catch (Exception e) {
            logger.error("doctorIntroduction: " + e);
            return ResponseUtil.unKonwException();
        }
    }

    /**
     * 医生填写个人信息
     *
     * @param request
     * @param response
     * @param map
     * @return
     */
    public ResponseEntity<String> doctorInfoDetails(HttpServletRequest request, HttpServletResponse response, Map<String, String> map) {
        //获取token，验证token
        String token = request.getHeader("Authorization");
        String name = map.get("name");
        String sex = map.get("sex");
        String practice_code = map.get("practice_code");
        String hospital = map.get("hospital");
        String department = map.get("department");
        String title = map.get("title");
        String practice_pic = map.get("practice_pic");
        try {
            if (!jwt.isExist(token)) {
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            //更新医生的详细资料

            DoctorEntity doctorEntity = new DoctorEntity();
            doctorEntity.setPhone(phone);
            doctorEntity.setName(name);
            doctorEntity.setSex(sex);
            doctorEntity.setPractice_code(practice_code);
            doctorEntity.setHospital(hospital);
            doctorEntity.setDepartment(department);
            doctorEntity.setTitle(title);
            doctorEntity.setVerify("认证中");
            doctorEntity.setPractice_pic(practice_pic);

            String state = userDao.updatedoctorInfoDetails(doctorEntity);
            if (state.equals("success")) {
                logger.info(phone + "-doctorInfoDetails: 医生详细信息修改成功");
                return ResponseUtil.success();
            } else {
                logger.error(phone + "-doctorInfoDetails: 医生详细信息修改失败");
                return ResponseUtil.exception("医生详细信息修改失败");
            }


        } catch (ExpiredJwtException e) {
            logger.error("doctorInfoDetails: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        } catch (Exception e) {
            logger.error("doctorInfoDetails: " + e);
            return ResponseUtil.unKonwException();
        }
    }

    /**
     * 医师执业证上传阿里云服务器
     *
     * @param request
     * @param response
     * @param
     * @return
     */
    public ResponseEntity<String> practiceimageUploadali(HttpServletRequest request, HttpServletResponse response, MultipartFile file) {
        String token = request.getHeader("Authorization");
        try {
            String webimage;
            if (!jwt.isExist(token)) {
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
            }
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();
            DoctorEntity doctorEntity = userDao.info(phone);
            if (doctorEntity == null) {
                logger.error(phone + "-practiceimagUpload: 未获取到医师实体");
                return ResponseUtil.exception("未获取到医生实体");
            }

            //接收文件
            if (!file.isEmpty()) {
                if (file.getContentType().contains("image")) {
                    try {
                        //新图片名
                        String newFileName = "practice_pic/doctor/" + phone + "/" + System.currentTimeMillis() + ".jpg";
                        //将file转为byte[]数组
                        byte[] byteArr = file.getBytes();

                        // 判断是否有旧职业资格证，如果有就先删除旧的，再上传
                        String old_pic = doctorEntity.getPractice_pic();
                        if (StringUtils.isNotBlank(old_pic)) {
                            old_pic = old_pic.replace("http://jybl-photo.oss-cn-shenzhen.aliyuncs.com/", "");
                            if (aliyunStorageService.existfile(old_pic)) {
                                aliyunStorageService.deletefile(old_pic);
                            }

                        }
                        if (!aliyunStorageService.fileupload(newFileName, byteArr)) {
                            logger.error(phone + "-practiceimagUpload: 阿里云上传失败");
                            return ResponseUtil.exception("阿里云上传失败请重试");
                        }
                        webimage = "http://jybl-photo.oss-cn-shenzhen.aliyuncs.com/" + newFileName;

//                        //将链接存入医生表
//        doctorEntity.setPhone(phone);
//        doctorEntity.setPractice_pic(webimage);
//        String state = userDao.updatedoctorInfoDetails(doctorEntity);
//        if (state.equals("success")){
//            JSONObject jo = new JSONObject();
//            jo.put("msg", "上传成功!");
//            jo.put("practice_pic_url", webimage);
//            logger.info(phone + "-practiceimagUpload: 医生职业证明上传成功");
//            return ResponseUtil.success(jo);
//        }else {
//            logger.error(phone + "-practiceimagUpload: 医生职业证明上传失败");
//            return ResponseUtil.exception("医生执业证明上传失败");
//        }
//
//        // 将反斜杠转换为正斜杠
////                        String data = datdDirectory.replaceAll("\\\\", "/") + newFileName;
////                        Map<String, Object> resultMap = new HashMap<>();
////                        resultMap.put("file", data);
////                        resultVo.setData(resultMap);
////                        resultVo.setError(1, "上传成功!");
                    } catch (IOException e) {
                        logger.error(phone + "-practiceimagUpload: 上传失败");
                        return ResponseUtil.exception("上传失败请重试");
                    }
                } else {
                    logger.error(phone + "-practiceimagUpload: 上传的不是图片类型");
                    return ResponseUtil.exception("上传的文件不是图片类型，请重新上传!");
                }
            } else {
                logger.error(phone + "-practiceimagUpload: 上传失败");
                return ResponseUtil.exception("上传失败，请选择要上传的图片!");
            }
            JSONObject jo = new JSONObject();
            jo.put("msg", "上传成功!");
            jo.put("practice_pic_url", webimage);
            logger.info(phone + "-practiceimagUpload: 医生职业证明上传阿里云成功");
            return ResponseUtil.success(jo);
        } catch (ExpiredJwtException e) {
            logger.error("practiceimagUpload: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        } catch (Exception e) {
            logger.error("practiceimagUpload: " + e);
            return ResponseUtil.exception("上传失败");
        }
    }

    /**
     * 医师执业证上传到数据库
     *
     * @param request
     * @param response
     * @param
     * @return
     */
    public ResponseEntity<String> practiceimagUploadDB(HttpServletRequest request, HttpServletResponse response, Map<String, String> map) {

        String token = request.getHeader("Authorization");
        try {
            if (!jwt.isExist(token)) {
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
            }
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();
            DoctorEntity doctorEntity = userDao.info(phone);
            if (doctorEntity == null) {
                logger.error(phone + "-practiceimagUpload: 未获取到医师实体");
                return ResponseUtil.exception("未获取到医生实体");
            }

            //将链接存入医生表
            String webimage = map.get("practice_pic_url");
            doctorEntity.setPhone(phone);
            doctorEntity.setPractice_pic(webimage);
            String state = userDao.updatedoctorInfoDetails(doctorEntity);
            if (state.equals("success")) {
                JSONObject jo = new JSONObject();
                jo.put("msg", "上传成功!");
                jo.put("practice_pic_url", webimage);
                logger.info(phone + "-practiceimagUpload: 医生职业证明上传成功");
                return ResponseUtil.success(jo);
            } else {
                logger.error(phone + "-practiceimagUpload: 医生职业证明上传失败");
                return ResponseUtil.exception("医生执业证明上传失败");
            }

            // 将反斜杠转换为正斜杠
//                        String data = datdDirectory.replaceAll("\\\\", "/") + newFileName;
//                        Map<String, Object> resultMap = new HashMap<>();
//                        resultMap.put("file", data);
//                        resultVo.setData(resultMap);
//                        resultVo.setError(1, "上传成功!");

        } catch (ExpiredJwtException e) {
            logger.error("practiceimagUpload: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        } catch (Exception e) {
            logger.error("practiceimagUpload: " + e);
            return ResponseUtil.exception("上传失败");
        }
    }

    /**
     * 获取医生的详细信息
     *
     * @param request
     * @param response
     * @return
     */
    public ResponseEntity<String> getDoctorInfoDetails(HttpServletRequest request, HttpServletResponse response) {
        //获取token，验证token
        String token = request.getHeader("Authorization");
        try {
            if (!jwt.isExist(token)) {
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            //获取医生详细信息
            DoctorEntity doctorEntity = userDao.info(phone);

            if (doctorEntity != null) {
                JSONObject jo = new JSONObject();
                jo.put("name", doctorEntity.getName());
                jo.put("sex", doctorEntity.getSex());
                jo.put("practice_code", doctorEntity.getPractice_code());
                jo.put("hospital", doctorEntity.getHospital());
                jo.put("department", doctorEntity.getDepartment());
                jo.put("title", doctorEntity.getTitle());
                jo.put("practice_pic", doctorEntity.getPractice_pic());
                logger.info(phone + "-getDoctorInfoDetails: 医生详细信息获取成功");
                return ResponseUtil.success(jo);
            } else {
                logger.error(phone + "-getDoctorInfoDetails: 医生详细信息获取失败");
                return ResponseUtil.exception("获取失败");
            }


        } catch (ExpiredJwtException e) {
            logger.error("getDoctorInfoDetails: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        } catch (Exception e) {
            logger.error("getDoctorInfoDetails: " + e);
            return ResponseUtil.unKonwException();
        }
    }

    /**
     * 获取评价列表
     *
     * @param request
     * @return
     */
    public ResponseEntity<String> getEvaluationlist(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        try {
            if (!jwt.isExist(token)) {
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            //获取评价列表
            List<EvaluationEntity> list = userDao.getEvaluation(phone);

            if (list != null && !list.isEmpty()) {
                JSONObject jo = new JSONObject();
                jo.put("list", list);
                logger.info(phone + "getEvaluationlist: 获取评价列表成功");
                return ResponseUtil.success(jo);
            } else {
                logger.warn(phone + "-getEvaluationlist: 获取评价列表为空");
                return ResponseUtil.success(null);
            }
        } catch (ExpiredJwtException e) {
            logger.error("getEvaluationlist: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        } catch (Exception e) {
            logger.error("getEvaluationlist: " + e);
            return ResponseUtil.unKonwException();
        }
    }

    /**
     * 获取评价详细信息
     *
     * @param request
     * @param map
     * @return
     */
    public ResponseEntity<String> getEvaluationDetail(HttpServletRequest request, Map<String, String> map) {
        String token = request.getHeader("Authorization");
        int id = Integer.parseInt(map.get("id"));
        try {
            if (!jwt.isExist(token)) {
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            //获取评价实体
            EvaluationEntity evaluationEntity = userDao.getEvaluationDetail(id);

            if (evaluationEntity != null) {
                JSONObject jo = new JSONObject();
                jo.put("evaluation", evaluationEntity);
                logger.info(phone + "-getEvaluationDetail: 获取评价详细信息成功");
                return ResponseUtil.success(jo);
            } else {
                logger.warn(phone + "-getEvaluationDetail: 获取评价详细信息为空");
                return ResponseUtil.success(null);
            }
        } catch (ExpiredJwtException e) {
            logger.error(e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        } catch (Exception e) {
            logger.error(e);
            return ResponseUtil.unKonwException();
        }
    }

    /**
     * 删除评价
     *
     * @param request
     * @param map
     * @return
     */
    public ResponseEntity<String> deleteEvaluation(HttpServletRequest request, Map<String, String> map) {
        String token = request.getHeader("Authorization");
        int id = Integer.parseInt(map.get("id"));
        try {
            if (!jwt.isExist(token)) {
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            //删除评价
            String state = userDao.deleteevaluation(id);
            if (state.equals("success")) {
                logger.info(phone + "-deleteEvaluation: 删除评价成功");
                return ResponseUtil.success("删除成功");
            } else {
                logger.error(phone + "-deleteEvaluation: 删除评价失败");
                return ResponseUtil.exception("删除失败");
            }


        } catch (ExpiredJwtException e) {
            logger.error("deleteEvaluation: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        } catch (Exception e) {
            logger.error("deleteEvaluation: " + e);
            return ResponseUtil.unKonwException();
        }
    }

    /**
     * 获取医生二维码
     *
     * @param request
     * @return
     */
    public ResponseEntity<String> qrCodeGet(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        try {
            if (!jwt.isExist(token)) {
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            JsonResult result = wechatServer.qrCodeCreateLogo(phone);
            if (result.getErrorcode().equals("0")) {
                JsonResult getQrcode = wechatServer.getdoctorqrcodeurl(phone);
                DoctorEntity doctorEntity = new DoctorEntity();
                doctorEntity.setPhone(phone);
                String qrCode = String.valueOf(getQrcode.getData());
                doctorEntity.setQRcode_pic(qrCode);
                userDao.updatedoctorInfoDetails(doctorEntity);
                JSONObject jo = new JSONObject();
                jo.put("qrcode", getQrcode.getData());
                logger.info(phone + "-qrCodeGet: 成功获取二维码");
                return ResponseUtil.success(jo);
            } else {
                logger.error(phone + "-qrCodeGet: 二维码获取失败");
                return ResponseUtil.exception(result.getMessage());
            }
        } catch (ExpiredJwtException e) {
            logger.error("qrCodeGet: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        } catch (Exception e) {
            logger.error("qrCodeGet: " + e);
            return ResponseUtil.exception(e.getMessage());
        }
    }

    /**
     * 医生填写个人信息
     *
     * @param request
     * @param response
     * @param map
     * @return
     */
    public ResponseEntity<String> doctorFeedback(HttpServletRequest request, HttpServletResponse response, Map<String, String> map) {
        //获取token，验证token
        String token = request.getHeader("Authorization");
        String content = map.get("content");
        try {
            if (!jwt.isExist(token)) {
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            DoctorEntity doctorEntity = userDao.info(phone);
            String name = doctorEntity.getName();
            SuggestionEntity suggestionEntity = new SuggestionEntity();
            suggestionEntity.setContent(content);
            suggestionEntity.setName(name);
            suggestionEntity.setPhone(phone);

            String state = userDao.addSuggestion(suggestionEntity);
            if (state.equals("success")) {
                logger.info(phone + "-doctorFeedback: 医生提交建议成功");
                return ResponseUtil.success();
            } else {
                logger.error(phone + "-doctorFeedback: 医生提交建议失败");
                return ResponseUtil.exception("医生提交建议失败");
            }
        } catch (ExpiredJwtException e) {
            logger.error("doctorFeedback: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        } catch (Exception e) {
            logger.error("doctorFeedback: " + e);
            return ResponseUtil.unKonwException();
        }
    }

    /**
     * 医生获取软件
     *
     * @param request
     * @return
     */
    public ResponseEntity<String> doctorSoftware(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        try {
            if (!jwt.isExist(token)) {
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            SoftwareEntity softwareEntity = userDao.getSoftware();
            if (softwareEntity != null) {
                JSONObject jo = new JSONObject();
                jo.put("software", softwareEntity);
                logger.info( "doctorSoftware: 成功获取软件信息");
                return ResponseUtil.success(jo);
            } else {
                logger.error("doctorSoftware: 获取软件信息失败");
                return ResponseUtil.exception("获取软件信息失败");
            }
        } catch (ExpiredJwtException e) {
            logger.error("doctorSoftware: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        } catch (Exception e) {
            logger.error("doctorSoftware: " + e);
            return ResponseUtil.exception(e.getMessage());
        }
    }

}
   /* 代码模板
   //获取token，验证token
    String token = request.getHeader("Authorization");
        try{
                
                if(!jwt.isExist(token)){
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
                }
                //获取token中的信息
                Claims claims = jwt.parseJWT(token);
                String doctor = claims.getSubject();
                JSONObject jobj= JSON.parseObject(doctor);
                String phone=jobj.get("phone").toString();


                JSONObject jo = new JSONObject();
                jo.put();
                return ResponseUtil.success(jo);

                }catch (ExpiredJwtException e){
                logger.error(e);
                userDao.delete(token);
                return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
                }catch (Exception e){
                logger.error(e);
                return ResponseUtil.unKonwException();
                }*/