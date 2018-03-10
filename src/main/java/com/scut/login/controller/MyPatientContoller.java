package com.scut.login.controller;

import com.scut.login.service.DoctorInfo;
import com.scut.login.service.MyPatient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Ai on 2017/9/11.
 */
@RestController
public class MyPatientContoller {
    @Autowired
    private MyPatient myPatient;

    /**
     * 患者列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "patientlist",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> patientlist(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String,String> map){
        return myPatient.patientlist(request,response,map);
    }

    /**
     * 通过标签获取患者列表
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "getPatientsBylabel",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getPatientsBylabel(HttpServletRequest request,@RequestBody Map<String,String> map){
        return myPatient.getPatientsBylabel(request,map);
    }

    /**
     * 添加分组
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "addGroup",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> addGroup(HttpServletRequest request,@RequestBody Map<String,String>map){
        return myPatient.addGroup(request,map);
    }

    /**
     * 删除标签
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "deleteGroup",method = RequestMethod.POST,produces ="application/json;charset=UTF-8")
    public ResponseEntity<String> deleteGroup(HttpServletRequest request,@RequestBody Map<String,String> map){
        return myPatient.deleteGroup(request,map);
    }

    /**
     * 将患者加入分组
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "joinGroup",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> joinGroup(HttpServletRequest request,@RequestBody Map<String,String> map){
        return myPatient.joinGroup(request,map);
    }

    /**
     * 将患者移出分组
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "removeFromGroup",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> removeFromGroup(HttpServletRequest request,@RequestBody Map<String,String>map){
        return myPatient.removeFromGroup(request,map);
    }

    /**
     * 获取患者健康信息
     * @param request
     * @param response
     * @param map
     * @return
     */
    @RequestMapping(value = "getHealthInfo",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getHealthInfo(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,String> map){
        return myPatient.getHealthInfo(request,response,map);
    }

    /**
     * 获取患者生化检查信息
     * @param request
     * @param response
     * @param map
     * @return
     */
    @RequestMapping(value = "getBiologyInfo",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getBiologyInfo(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,String> map){
        return myPatient.getBiologyInfo(request,response,map);
    }

    /**
     * 获取血压记录
     * @param request
     * @param response
     * @param map
     * @return
     */
    @RequestMapping(value = "getBloodPressureInfo",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getBloodPressureInfo(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,Object> map){
        return  myPatient.getBloodPressureInfo(request,response,map);
    }

    /**
     * 获取心电图
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "getCardiogram",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getCardiogram(HttpServletRequest request,@RequestBody Map<String,Object> map){
        return myPatient.getCardiogram(request,map);
    }

    /**
     * 获取风险评估报告
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "getRiskReport",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getRiskReport(HttpServletRequest request,@RequestBody Map<String,String> map){
        return myPatient.getRiskReport(request,map);
    }

    /**
     * 自定义健康信息
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "defineMessage",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> defineMessage(HttpServletRequest request,@RequestBody Map<String,String>map){
        return myPatient.defineMessage(request, map);
    }

    /**
     * 医生发送模板消息
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "messageRemind",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> messageRemind(HttpServletRequest request,@RequestBody Map<String,String>map){
        return myPatient.messageRemind(request, map);
    }
}
