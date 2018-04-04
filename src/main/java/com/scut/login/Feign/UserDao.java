package com.scut.login.Feign;

import com.scut.login.entity.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Ai on 2017/8/3.
 */
@FeignClient("microservice-jiayibilin-mySql")
public interface UserDao {
    @RequestMapping(value = "/doctor/loginverify", method = RequestMethod.GET)
    boolean loginverify(@RequestParam("phone") String phone, @RequestParam("password") String password);

    @RequestMapping(value = "/doctor/findbyphone", method = RequestMethod.GET)
    DoctorEntity info(@RequestParam("phone") String phone);

    @RequestMapping(value = "/doctor/register", method = RequestMethod.POST)
    String register(@RequestParam("phone") String phone, @RequestParam("password") String password, @RequestParam("QRcode_pic")String QRcode);

    @RequestMapping(value = "/doctor/updatepwd", method = RequestMethod.POST)
    String updatepwd(@RequestParam("phone") String phone, @RequestParam("password") String password);

    @RequestMapping(value = "/doctor/savephonetoken", method = RequestMethod.POST)
    String savephonetoken(@RequestParam("token") String token, @RequestParam("phone") String phone);

    @RequestMapping(value = "/doctor/selecttoken", method = RequestMethod.GET)
    String isExist(@RequestParam("token") String token);

    @RequestMapping(value = "/doctor/deletetoken", method = RequestMethod.POST)
    void delete(@RequestParam("token") String token);

    @RequestMapping(value = "/doctor/selectphonetoken", method = RequestMethod.POST)
    String phonetokenselect(@RequestParam("phone") String phone);

    @RequestMapping(value = "/doctor/deletephonetoken", method = RequestMethod.POST)
    void deletephone(@RequestParam("phone") String phone);

    @RequestMapping(value = "/doctor/updatehead", method = RequestMethod.POST)
    void update_head_pic(@RequestParam("head_pic") String head_pic, @RequestParam("phone") String phone);

    @RequestMapping(value = "/doctor/updatepracticeimage", method = RequestMethod.POST)
    void update_practice_pic(@RequestParam("practice_pic") String head_pic, @RequestParam("phone") String phone);

    @RequestMapping(value = "/doctor/updatetoken", method = RequestMethod.POST)
    String updatetoken(@RequestParam("newToken") String newToken, @RequestParam("token") String token);

    @RequestMapping(value = "/doctor/updateintroduction", method = RequestMethod.POST)
    String updateintroduction(@RequestParam("phone") String phone, @RequestParam("adept") String adept, @RequestParam("experience") String experience);

    @RequestMapping(value = "/doctor/updatedoctorinfodetails", method = RequestMethod.POST)
//    String updatedoctorInfoDetails(@RequestParam("phone")String phone, @RequestParam("name")String name, @RequestParam("sex")String sex, @RequestParam("practice_code")String practice_code, @RequestParam("hospital")String hospital, @RequestParam("department")String department, @RequestParam("title")String title);
    String updatedoctorInfoDetails(@RequestBody DoctorEntity doctorEntity);

    @RequestMapping(value = "/doctor/getevaluation",method = RequestMethod.GET)
    List<EvaluationEntity> getEvaluation(@RequestParam("phone")String phone);

    @RequestMapping(value = "/doctor/getevaluationdetail",method = RequestMethod.POST)
    EvaluationEntity getEvaluationDetail(@RequestParam("id")int id);

    @RequestMapping(value = "/doctor/deleteevaluation",method = RequestMethod.POST)
    String deleteevaluation(@RequestParam("id")int id);

    //获取医生未读评价的个数
    @RequestMapping(value = "/doctor/getevaluationunread")
    int getEvaluationUnread(@RequestParam("phone") String phone);

    @RequestMapping(value = "/doctor/getlabel", method = RequestMethod.POST)
    String getlabel(@RequestParam("phone") String phone);

    @RequestMapping(value = "/doctor/deletelabel",method = RequestMethod.POST)
    String deletelabel(@RequestParam("phone")String phone,@RequestParam("labelStr")String labelStr,@RequestParam("label")String label);

    @RequestMapping(value = "/doctor/deletepatientlabel",method = RequestMethod.POST)
    String deletepatientlabel(@RequestParam("phone")String phone,@RequestParam("wechat_id")String wechat_id,@RequestParam("label")String label);

    @RequestMapping(value = "/doctor/addpatientlabel",method = RequestMethod.POST)
    String addpatientlabel(@RequestParam("phone")String phone,@RequestParam("wechat_id")String wechat_id,@RequestParam("label")String label);

    @RequestMapping(value = "/doctor/getpatientsbykind", method = RequestMethod.POST)
    List<PatientEntity> getpatientsbykind(@RequestParam("phone") String phone,@RequestParam("kind")String kind);

    //根据医生和标签返回患者的详细数据
    @RequestMapping(value = "/doctor/getpatientbylabel", method = RequestMethod.POST)
    List<PatientEntity> getPatientsBylabel(@RequestParam("phone") String phone, @RequestParam("label") String label);

    @RequestMapping(value = "/doctor/getpatient",method = RequestMethod.GET)
    PatientEntity getPatient(@RequestParam("wechat_id")String wechat_id);

    @RequestMapping(value = "/doctor/service", method = RequestMethod.GET)
    List<DoctorServiceEntity> getService(@RequestParam("phone") String phone);

    @RequestMapping(value = "/healthmanage/gethealthtable",method = RequestMethod.GET)
    HealthCheckEntity getHealthTable(@RequestParam("wechat_id") String wechat_id);

    @RequestMapping(value = "/healthmanage/getbiologyinfo",method = RequestMethod.GET)
    BiologyCheckEntity getBiologyInfo(@RequestParam("wechat_id") String wechat_id);

    @RequestMapping(value = "/healthmanage/getbloodpressuretable")
    List<BloodPressureEntity> getBloodPressureTable(@RequestParam("wechat_id") String wechat_id, @RequestParam("timearea") int timearea, @RequestParam("time") String time);

    //获取心电图
    @RequestMapping(value = "/healthmanage/findcardiogramtable")
    List<CardiogramEntity> getCardiogram(@RequestParam("wechat_id") String wechat_id);

    @RequestMapping(value = "/healthmanage/report/getall")
    List<RiskReportEntity> getAllReport(@RequestBody String wechat_id);

    @RequestMapping(value = "/healthmanage/definemessage",method = RequestMethod.POST)
    String definemessage(@RequestBody DefinitionMessageEntity definitionMessageEntity);

    //医生发送模板消息
    @RequestMapping(value = "/healthmanage/savemessageremind",method = RequestMethod.POST)
    String saveMessageRemind(@RequestBody MessageRemindEntity messageRemindEntity);
    /*
	*发起留言和回复
	 */
    @RequestMapping(value = "/patient/messageboard/set",method = RequestMethod.POST)
    String setMessageBoard(@RequestBody MessageBoardEntity messageBoardEntity);

    //获取患者留言的最新回复
    @RequestMapping(value = "/patient/messageboard/getdoctornewestmessage")
    List<MessageBoardEntity> getdoctorNewestMessageBoards(@RequestParam("phone") String phone);

    //获取患者一个留言板及回复
    @RequestMapping(value = "/patient/messageboard/getonemessage")
    List<MessageBoardEntity> getOneMessageBoardAndReply(@RequestParam("id")int id,@RequestParam("who")int who);

    @RequestMapping(value = "/patient/messageboard/delete", method = RequestMethod.POST)
    String deletePatientMessageBoard(@RequestParam("id") int id, @RequestParam("who") int who);

    @RequestMapping(value = "/doctor/groupsending",method = RequestMethod.POST)
    String groupsending(@RequestBody DoctorGroupSendingEntity doctorGroupSendingEntity);

    @RequestMapping(value = "/doctor/groupsendingall",method = RequestMethod.POST)
    String groupsendingAll(@RequestBody DoctorGroupSendingEntity doctorGroupSendingEntity);

    @RequestMapping(value = "/doctor/groupsendinghistory")
    List<DoctorGroupSendingEntity> groupsendinghistory(@RequestParam("phone")String phone);

    @RequestMapping(value = "/doctor/groupsendingdelete")
    String groupsendingdelete(@RequestParam("id")int id);

    @RequestMapping(value = "/doctor/geteventremind")
    List<RemindersEntity> geteventremind(@RequestParam("phone")String phone);

    @RequestMapping(value = "/doctor/geteventreminddetail")
    RemindersEntity geteventreminddetail(@RequestParam("id")int id);

    @RequestMapping(value = "/doctor/eventreminddelete", method = RequestMethod.POST)
    String eventreminddelete(@RequestParam("id")int id);

    @RequestMapping(value = "/doctor/geteventremindunread")
    int getEvenRemindUnread(@RequestParam("phone") String phone);

    //医生提交建议借口
    @RequestMapping(value = "/doctor/suggestion/add", method = RequestMethod.POST)
    String addSuggestion(@RequestBody SuggestionEntity suggestionEntity);

    //医生获取软件
    @RequestMapping(value = "/doctor/software/get", method = RequestMethod.GET)
    SoftwareEntity getSoftware();
    }