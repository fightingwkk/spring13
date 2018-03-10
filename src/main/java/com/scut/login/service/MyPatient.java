package com.scut.login.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scut.login.Feign.UserDao;
import com.scut.login.entity.*;
import com.scut.login.util.JwtUtil;
import com.scut.login.util.ResponseUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Ai on 2017/9/11.
 */
@Component
public class MyPatient {

    private static final Logger logger = LogManager.getLogger(MyPatient.class);

    @Autowired
    UserDao userDao;
    @Autowired
    DoctorEntity doctorEntity;
    @Autowired
    private JwtUtil jwt;

    /**
     * 我的患者列表按照患病几率排序
     * @param request
     * @param response
     * @return
     */
    public ResponseEntity<String> patientlist(HttpServletRequest request, HttpServletResponse response, Map<String,String> map){
       //获取token，验证token
        String token = request.getHeader("Authorization");
        String kind = map.get("kind");
        try{
            
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
            }
            //取出token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj= JSON.parseObject(doctor);
            String phone=jobj.get("phone").toString();

            //获得自定义标签
            String label = userDao.getlabel(phone);
            List<String> Label = null;
            if (label != null && !label.equals("")){
                 Label = Arrays.asList(label.split(","));
            };
            System.out.println(Label);

            if(kind == null || kind.equals("")){
                kind = "";
            }
            //遍历获得患者信息
            List<PatientEntity> list = userDao.getpatientsbykind(phone,kind);
           // System.out.println(list);

            if(list != null && !list.isEmpty()){
                JSONObject jo = new JSONObject();
                jo.put("label",Label);
                jo.put("data",list);
                logger.info(phone + "-patientlist: 通过类型获取患者列表");
                return ResponseUtil.success(jo);
            }else {
                logger.warn(phone + "-patientlist: 患者列表为空");
                JSONObject jo = new JSONObject();
                jo.put("label",Label);
                jo.put("data",null);
                return ResponseUtil.success(jo);
            }


        }catch (ExpiredJwtException e){
            logger.error("patientlist: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("patientlist: "+ e);
            return ResponseUtil.unKonwException();
        }
    }

    /**
     * 获取患者列表，按标签排序
     * @param request
     * @param map
     * @return
     */
    public ResponseEntity<String> getPatientsBylabel(HttpServletRequest request,Map<String,String> map){
        //获取token，验证token
        String token = request.getHeader("Authorization");
        String label = map.get("label");
        try{
            
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");
            }
            //取出token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj= JSON.parseObject(doctor);
            String phone=jobj.get("phone").toString();


            //按标签遍历获得患者信息
            List<PatientEntity> list = userDao.getPatientsBylabel(phone,label);
            System.out.println(list);

            if(list != null && !list.isEmpty()){
                JSONObject jo = new JSONObject();
                jo.put("data",list);
                logger.info(phone + "-getPatientsBylabel: 通过标签获取患者列表");
                return ResponseUtil.success(jo);
            }else{
                logger.warn(phone + "-getPatientsBylabel: 患者列表为空");
                return ResponseUtil.success(null);
            }

        }catch (ExpiredJwtException e){
            logger.error("getPatientsBylabel: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("getPatientsBylabel: "+ e);
            return ResponseUtil.unKonwException();
        }
    }

    /**
     * 添加分组
     * @param request
     * @param map
     * @return
     */
    public ResponseEntity<String> addGroup(HttpServletRequest request,Map<String,String>map){
        //获取token，验证token
        String token = request.getHeader("Authorization");
        String label = map.get("label");
        try{
            
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj= JSON.parseObject(doctor);
            String phone=jobj.get("phone").toString();

            //新增医生标签表
            String Label = userDao.getlabel(phone);
            List<String> list = new ArrayList<String>();
            if(Label!=null && !Label.equals("")){
                for(String t : Label.split(",")){
                    list.add(t);
                }
            }
            list.add(label);
            String LabelStr =  String.join(",",list);
            System.out.println(LabelStr);
            doctorEntity.setPhone(phone);
            doctorEntity.setLabel(LabelStr);
            String state = userDao.updatedoctorInfoDetails(doctorEntity);
            if(state.equals("success")){
                logger.info(phone + "-addGroup: 添加分组成功");
                return ResponseUtil.success("添加成功");
            }else{
                logger.warn(phone + "-addGroup: 添加分组失败");
                return ResponseUtil.exception("添加失败");
            }


        }catch (ExpiredJwtException e){
            logger.error("addGroup: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("addGroup: "+ e);
            return ResponseUtil.unKonwException();
        }
    }

    /**
     * 删除分组
     * @param request
     * @param map
     * @return
     */
    public ResponseEntity<String> deleteGroup(HttpServletRequest request,Map<String,String> map){
        //获取token，验证token
        String token = request.getHeader("Authorization");
        String label = map.get("label");
        System.out.println(label);
        try{
            
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj= JSON.parseObject(doctor);
            String phone=jobj.get("phone").toString();

            //修改医生标签表和患者分组
            String Label = userDao.getlabel(phone);
            List<String> list = new ArrayList<String>();
            for(String t : Label.split(",")){
                list.add(t);
            }
            System.out.println(list);
            Iterator<String> it = list.iterator();
            while(it.hasNext())
            {
                if(it.next().equals(label))
                {
                    it.remove();
                }
            }
            System.out.println(list);
            String LabelStr =  String.join(",",list);
            System.out.println(LabelStr);
            String state = userDao.deletelabel(phone,LabelStr,label);
            if(state.equals("success")){
                logger.info(phone + "-deleteGroup: 删除成功");
                return ResponseUtil.success("删除成功");
            }else {
                logger.warn(phone + "-deleteGroup: 删除失败");
                return ResponseUtil.exception("删除失败");
            }


        }catch (ExpiredJwtException e){
            logger.error("deleteGroup: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("deleteGroup: "+ e);
            return ResponseUtil.unKonwException();
        }
    }

    /**
     * 添加患者进分组
     * @param request
     * @param map
     * @return
     */
    public ResponseEntity<String> joinGroup(HttpServletRequest request,Map<String,String> map){
        //获取token，验证token
        String token = request.getHeader("Authorization");
        String wechat_id = map.get("wechat_id");
        String label = map.get("label");
        System.out.println(label);
        try{
            
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj= JSON.parseObject(doctor);
            String phone=jobj.get("phone").toString();

            //修改医生标签表和患者分组
            String state = userDao.addpatientlabel(phone,wechat_id,label);
            if(state.equals("success")){
                logger.info(phone + "-joinGroup: 添加成功");
                return ResponseUtil.success("添加成功");
            }else {
                logger.warn(phone + "-joinGroup: 添加失败");
                return ResponseUtil.exception("添加失败");
            }


        }catch (ExpiredJwtException e){
            logger.error("joinGroup: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("joinGroup: " + e);
            return ResponseUtil.unKonwException();
        }
    }

    public ResponseEntity<String> removeFromGroup(HttpServletRequest request,Map<String,String> map){
        //获取token，验证token
        String token = request.getHeader("Authorization");
        String wechat_id = map.get("wechat_id");
        String label = map.get("label");
        System.out.println(label);
        try{
            
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj= JSON.parseObject(doctor);
            String phone=jobj.get("phone").toString();

            //修改医生标签表和患者分组
            String state = userDao.deletepatientlabel(phone,wechat_id,label);
            if(state.equals("success")){
                logger.info(phone + "-removeFromGroup: 删除成功");
                return ResponseUtil.success("删除成功");
            }else {
                logger.warn(phone + "-removeFromGroup: 删除失败");
                return ResponseUtil.exception("删除失败");
            }

        }catch (ExpiredJwtException e){
            logger.error("removeFromGroup: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("removeFromGroup:" +e);
            return ResponseUtil.unKonwException();
        }
    }
    /**
     * 获取健康信息
     * @param request
     * @param response
     * @param map
     * @return
     */
    public ResponseEntity<String> getHealthInfo(HttpServletRequest request,HttpServletResponse response,Map<String,String> map){
        //获取token，验证token
        String token = request.getHeader("Authorization");
        String wechat_id = map.get("wechat_id");
        System.out.println(wechat_id);
        try{
            
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj= JSON.parseObject(doctor);
            String phone=jobj.get("phone").toString();

            //获取病人健康信息
            PatientEntity patient= userDao.getPatient(wechat_id);
            if(patient == null){
                return ResponseUtil.success(null);
            }
            HealthCheckEntity healthCheckEntity = userDao.getHealthTable(wechat_id);
            if (healthCheckEntity != null){
                JSONObject jo = new JSONObject();
                jo.put("name",patient.getName());
                jo.put("sex",patient.getSex());
                jo.put("id_card",patient.getId_card());
                jo.put("age",patient.getAge());
                jo.put("phone",patient.getPhone());
                jo.put("address",patient.getAddress());
                jo.put("detailed_address",patient.getDetailed_address());
                float height = healthCheckEntity.getHeight();
                jo.put("height",height);
                float weight = healthCheckEntity.getWeight();
                jo.put("weight",weight);
                DecimalFormat decimalFormat=new DecimalFormat(".00");
                String BMI = decimalFormat.format(weight/height/height*10000);
                jo.put("BMI",BMI);
                jo.put("diabetes",healthCheckEntity.getDiabetes());
                jo.put("chd",healthCheckEntity.getChd());
                jo.put("stroke",healthCheckEntity.getStroke());
                jo.put("hypertension",healthCheckEntity.getHypertension());
                jo.put("other_history",healthCheckEntity.getOther_history());
                jo.put("family_history",healthCheckEntity.getFamily_history());
                jo.put("smoke",healthCheckEntity.getSmoke());
                jo.put("smoking",healthCheckEntity.getSmoking());
                jo.put("drink",healthCheckEntity.getDrink());
                jo.put("drinking",healthCheckEntity.getDrinking());
                logger.info(phone + "-getHealthInfo: 获取健康信息");
                return ResponseUtil.success(jo);
            }else{
                logger.warn(phone + "-getHealthInfo: 健康信息为空");
                return ResponseUtil.success(null);
            }

        }catch (ExpiredJwtException e){
            logger.error("getHealthInfo: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("getHealthInfo: "+ e);
            return ResponseUtil.unKonwException();
        }
    }

    /**
     * 获取生化检查
     * @param request
     * @param response
     * @param map
     * @return
     */
    public ResponseEntity<String> getBiologyInfo(HttpServletRequest request,HttpServletResponse response,Map<String,String> map){
        //获取token，验证token
        String token = request.getHeader("Authorization");
        String wechat_id = map.get("wechat_id");
        System.out.println(wechat_id);
        try{
            
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj= JSON.parseObject(doctor);
            String phone=jobj.get("phone").toString();


            //获取生化检查
            BiologyCheckEntity biologyCheckEntity = userDao.getBiologyInfo(wechat_id);
            if (biologyCheckEntity != null){
                JSONObject jo = new JSONObject();
                jo.put("tch",biologyCheckEntity.getTch());
                jo.put("tch_time",biologyCheckEntity.getTch_time());
                jo.put("fbg",biologyCheckEntity.getFbg());
                jo.put("fbg_time",biologyCheckEntity.getFbg_time());
                jo.put("tg",biologyCheckEntity.getTg());
                jo.put("tg_time",biologyCheckEntity.getTg_time());
                jo.put("hdl",biologyCheckEntity.getHdl());
                jo.put("hdl_time",biologyCheckEntity.getHdl_time());
                jo.put("ldl",biologyCheckEntity.getLdl());
                jo.put("ldl_time",biologyCheckEntity.getLdl_time());
                logger.info(phone + "-getBiologyInfo: 获取生化检查");
                return ResponseUtil.success(jo);
            }else{
                logger.warn(phone + "-getBiologyInfo: 健康检查为空");
                return ResponseUtil.success(null);
            }

        }catch (ExpiredJwtException e){
            logger.error("getBiologyInfo: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("getBiologyInfo: "+ e);
            return ResponseUtil.unKonwException();
        }
    }

    public ResponseEntity<String> getBloodPressureInfo(HttpServletRequest request,HttpServletResponse response,Map<String,Object> map){
        //获取token，验证token
        String token = request.getHeader("Authorization");
        String wechat_id = map.get("wechat_id").toString();
        String timearea = map.get("timearea").toString();
        String time = map.get("time").toString();
        try{
            
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj= JSON.parseObject(doctor);
            String phone=jobj.get("phone").toString();


            //获取血压记录
            int timeareaint = 0;
            if(timearea.equals("week")){
                timeareaint = 7;
            }else{
                timeareaint = 30;
            }
            List<BloodPressureEntity> list = userDao.getBloodPressureTable(wechat_id,timeareaint,time);
            if (list != null && !list.isEmpty()){
                JSONObject jo = new JSONObject();
                jo.put("BloodPressureList",list);
                logger.info(phone + "-getBloodPressureInfo: 获取血压记录");
                return ResponseUtil.success(jo);
            }else{
                logger.warn(phone + "-getBloodPressureInfo: 血压记录为空");
                return ResponseUtil.success(null);
            }

        }catch (ExpiredJwtException e){
            logger.error("getBloodPressureInfo: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("getBloodPressureInfo: "+ e);
            return ResponseUtil.unKonwException();
        }
    }

//获取心电图
    public ResponseEntity<String> getCardiogram(HttpServletRequest request,Map<String,Object> map){
        //获取token，验证token
        String token = request.getHeader("Authorization");
        String wechat_id = map.get("wechat_id").toString();
        try{
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj= JSON.parseObject(doctor);
            String phone=jobj.get("phone").toString();

            //获取心电图
            List<CardiogramEntity> list = userDao.getCardiogram(wechat_id);
            if (list != null && !list.isEmpty()){
                JSONObject jo = new JSONObject();
                jo.put("Cardiogram",list);
                logger.info(phone + "-getCardiogram: 获取心电图记录");
                return ResponseUtil.success(jo);
            }else{
                logger.warn(phone + "-getCardiogram: 心电图记录为空");
                return ResponseUtil.success(null);
            }

        }catch (ExpiredJwtException e){
            logger.error("getCardiogram: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("getCardiogram: "+ e);
            return ResponseUtil.unKonwException();
        }
    }


    public ResponseEntity<String> getRiskReport(HttpServletRequest request,Map<String,String> map){
        //获取token，验证token
        String token = request.getHeader("Authorization");
        String wechat_id = map.get("wechat_id").toString();
        System.out.println(wechat_id);
        try{
            
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj= JSON.parseObject(doctor);
            String phone=jobj.get("phone").toString();


            //获取风险报告记录
            List<RiskReportEntity> list = userDao.getAllReport(wechat_id);
            if (list != null && !list.isEmpty()){
                JSONObject jo = new JSONObject();
                jo.put("RiskReportList",list);
                logger.info(phone + "-getRiskReport: 获取风险评估报告");
                return ResponseUtil.success(jo);
            }else{
                logger.warn(phone + "-getRiskReport: 风险报告为空");
                return ResponseUtil.success(null);
            }

        }catch (ExpiredJwtException e){
            logger.error("getRiskReport: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("getRiskReport: "+ e);
            return ResponseUtil.unKonwException();
        }
    }

    public ResponseEntity<String> defineMessage(HttpServletRequest request,Map<String,String>map){
        //获取token，验证token
        String token = request.getHeader("Authorization");
        String wechat_id = map.get("wechat_id");
        String title = map.get("title");
        String content = map.get("content");
        try{
            
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj= JSON.parseObject(doctor);
            String phone=jobj.get("phone").toString();

            DefinitionMessageEntity definitionMessageEntity = new DefinitionMessageEntity();
            definitionMessageEntity.setPhone(phone);
            definitionMessageEntity.setContent(content);
            definitionMessageEntity.setTitle(title);
            definitionMessageEntity.setWechat_id(wechat_id);
            String state = userDao.definemessage(definitionMessageEntity);
            if(state.equals("success")){
                logger.info(phone + "-defineMessage: 自定义健康计划发送成功");
                return ResponseUtil.success("发送成功");
            }else{
                logger.warn(phone + "-defineMessage: 自定义健康计划发送失败");
                return ResponseUtil.exception("发送失败");
            }


        }catch (ExpiredJwtException e){
            logger.error("defineMessage: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("defineMessage: "+ e);
            return ResponseUtil.exception(e.getMessage());
        }
    }

    /**
     * 医生发送模板消息
     * @param request
     * @param map
     * @return
     */
    public ResponseEntity<String> messageRemind(HttpServletRequest request,Map<String,String>map){
        //获取token，验证token
        String token = request.getHeader("Authorization");
        try{
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj= JSON.parseObject(doctor);
            String phone=jobj.get("phone").toString();

            String wechat_id = map.get("wechat_id");
            String title = map.get("title");
            String target = map.get("target");
            String remark = map.get("remark");
            String period = map.get("period");
            MessageRemindEntity messageRemindEntity = new MessageRemindEntity();
            messageRemindEntity.setPhone(phone);
            messageRemindEntity.setWechat_id(wechat_id);
            messageRemindEntity.setTitle(title);
            messageRemindEntity.setRemark(remark);
            messageRemindEntity.setTarget(target);
            messageRemindEntity.setPeriod(Integer.valueOf(period));

            String state = userDao.saveMessageRemind(messageRemindEntity);
            if(state.equals("success")){
                logger.info(phone + "-messageRemind: 发送模板消息成功");
                return ResponseUtil.success("发送模板消息成功");
            }else{
                logger.warn(phone + "-messageRemind: 发送模板消息失败");
                return ResponseUtil.exception("发送模板消息失败");
            }
        }catch (ExpiredJwtException e){
            logger.error("messageRemind: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("messageRemind: "+ e);
            return ResponseUtil.unKonwException();
        }
    }
}
