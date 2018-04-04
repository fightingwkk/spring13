package com.scut.login.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scut.login.Feign.UserDao;
import com.scut.login.entity.*;
import com.scut.login.util.JwtUtil;
import com.scut.login.util.ResponseUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by Ai on 2017/9/21.
 */
@Component
public class Index {
    private static final Logger logger = LogManager.getLogger(Index.class);
    @Autowired
    private UserDao userDao;
    @Autowired
    private JwtUtil jwt;

    /**
     * 获取服务包
     * @param request
     * @param response
     * @return
     */
    public ResponseEntity<String> getService(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Authorization");
        try{
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            List<DoctorServiceEntity> getSerice = userDao.getService(phone);
            if (getSerice !=null && !getSerice.isEmpty()){
                JSONObject jo = new JSONObject();
                jo.put("data",getSerice);
                logger.info(phone + "-getService: 获取服务成功");
                return ResponseUtil.success(jo);
            }else{
                logger.warn(phone + "-getService: 未获取到服务");
                return ResponseUtil.exception("未获取到服务");
            }


        }catch (ExpiredJwtException | SignatureException e){
            logger.error("getService: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("getService: " + e);
            return ResponseUtil.unKonwException();
        }
    }

    /**
     * 获取患者留言的最新回复
     * @param request
     * @return
     */
    public ResponseEntity<String> getdoctorNewestMessageBoards(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        try{
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            List<MessageBoardEntity> newMessageList = userDao.getdoctorNewestMessageBoards(phone);
            int EvenRemindUnread = userDao.getEvenRemindUnread(phone);
            String eventRemindUnread = String.valueOf(EvenRemindUnread);
            System.out.println(eventRemindUnread);

            if (newMessageList != null && !newMessageList.isEmpty()){
                JSONObject jo = new JSONObject();
                jo.put("newMessageList",newMessageList);
                jo.put("EventRemindUnread",eventRemindUnread);
                logger.info(phone + "-getdoctorNewestMessageBoards: 获取患者留言的最新回复");
                return ResponseUtil.success(jo);
            }else{
                JSONObject jo = new JSONObject();
                jo.put("newMessageList","");
                jo.put("EventRemindUnread",eventRemindUnread);
                logger.warn(phone + "-getdoctorNewestMessageBoards: 获取患者留言的最新回复为空");
                return ResponseUtil.success(jo);
            }

        }catch (ExpiredJwtException e){
            logger.error("getdoctorNewestMessageBoards: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("getdoctorNewestMessageBoards: " + e);
            return ResponseUtil.unKonwException();
        }
    }

    public ResponseEntity<String> getOneMessageBoardAndReply(HttpServletRequest request, Map<String,String> map){
        String token = request.getHeader("Authorization");
        int id = Integer.parseInt(map.get("id"));
        try{
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            List<MessageBoardEntity> replyList = userDao.getOneMessageBoardAndReply(id,1);

            if (replyList != null && !replyList.isEmpty()){
                JSONObject jo = new JSONObject();
                jo.put("replyList",replyList);
                logger.info(phone + "-getOneMessageBoardAndReply: 查看回复");
                return ResponseUtil.success(jo);
            }else{
                logger.warn(phone + "-getOneMessageBoardAndReply: 没有信息");
                return ResponseUtil.success(null);
            }

        }catch (ExpiredJwtException e){
            logger.error("getOneMessageBoardAndReply: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("getOneMessageBoardAndReply: " + e);
            return ResponseUtil.unKonwException();
        }
    }

    public ResponseEntity<String> setMessageBoard(HttpServletRequest request,Map<String,String> map){
        String token = request.getHeader("Authorization");
        String wechat_id = map.get("wechat_id");
        String content = map.get("content");
        String id = map.get("reply_id");
        int reply_id = Integer.parseInt(id);
        try{
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            MessageBoardEntity messageBoardEntity = new MessageBoardEntity();
            messageBoardEntity.setPhone(phone);
            messageBoardEntity.setReply_id(reply_id);
            messageBoardEntity.setContent(content);
            messageBoardEntity.setWechat_id(wechat_id);
            messageBoardEntity.setSender(1);

            String state = userDao.setMessageBoard(messageBoardEntity);


            if (state.equals("success")){
                logger.info(phone + "-setMessageBoard: 回复成功");
                return ResponseUtil.success("回复成功");
            }else{
                logger.warn(phone + "-setMessageBoard: 回复失败");
                return ResponseUtil.exception("回复失败");
            }

        }catch (ExpiredJwtException e){
            logger.error("setMessageBoard: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("setMessageBoard: "+ e);
            return ResponseUtil.unKonwException();
        }
    }
    public ResponseEntity<String> deleteMessageBoard(HttpServletRequest request,Map<String,String> map){
        String token = request.getHeader("Authorization");
        int id = Integer.parseInt(map.get("id"));
        try{
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();


            String state = userDao.deletePatientMessageBoard(id,1);


            if (state.equals("success")){
                logger.info(phone + "-deleteMessageBoard: 删除成功");
                return ResponseUtil.success("留言删除成功");
            }else{
                logger.warn(phone + "-deleteMessageBoard: 留言删除失败");
                return ResponseUtil.exception("留言删除失败");
            }

        }catch (ExpiredJwtException e){
            logger.error("deleteMessageBoard: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("deleteMessageBoard"+ e);
            return ResponseUtil.unKonwException();
        }
    }

    public ResponseEntity<String> groupSending(HttpServletRequest request,Map<String,String> map){
        String token = request.getHeader("Authorization");
        String content =map.get("content");
        String group_names = map.get("group_names");
        String kind_names = map.get("kinds_names");
        String patient_names = map.get("patient_names");
        String all_patients = map.get("all_patients");


        try{
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            DoctorGroupSendingEntity doctorGroupSendingEntity = new DoctorGroupSendingEntity();
            doctorGroupSendingEntity.setPhone(phone);
            doctorGroupSendingEntity.setContent(content);
            doctorGroupSendingEntity.setGroup_names(group_names);
            doctorGroupSendingEntity.setKind_names(kind_names);
            doctorGroupSendingEntity.setPatient_names(patient_names);

            String state = "";
            if(all_patients!= null && all_patients.equals("1")){
                state = userDao.groupsendingAll(doctorGroupSendingEntity);
            }else{
                state = userDao.groupsending(doctorGroupSendingEntity);
            }

            if (state.equals("success")){
                logger.info(phone + "-groupSending: 群发成功");
                return ResponseUtil.success("群发成功");
            }else{
                logger.warn(phone + "-groupSending: 群发失败");
                return ResponseUtil.exception("群发失败");
            }

        }catch (ExpiredJwtException e){
            logger.error("groupSending: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("groupSending: "+ e);
            return ResponseUtil.unKonwException();
        }
    }

    public ResponseEntity<String> groupSengdingHistory(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        try{
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();



            List<DoctorGroupSendingEntity> historylist = userDao.groupsendinghistory(phone);

            if (historylist != null && !historylist.isEmpty()){
                JSONObject vo = new JSONObject();
                vo.put("historylist",historylist);
                logger.info(phone + "-groupSengdingHistory: 查看群发历史记录");
                return ResponseUtil.success(vo);
            }else{
                logger.warn(phone + "-groupSengdingHistory: 历史记录为空");
                return ResponseUtil.success(null);
            }

        }catch (ExpiredJwtException e){
            logger.error("groupSengdingHistory: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("groupSengdingHistory: "+ e);
            return ResponseUtil.unKonwException();
        }
    }

    public ResponseEntity<String> groupSengdingDelete(HttpServletRequest request ,Map<String,String> map){
        String token = request.getHeader("Authorization");
        int id = Integer.parseInt(map.get("id"));
        try{
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            String historylist = userDao.groupsendingdelete(id);

            if (historylist != null && !historylist.equals("")){
                logger.info(phone + "-groupSengdingDelete: 删除成功");
                return ResponseUtil.success("删除成功");
            }else{
                logger.warn(phone + "-groupSengdingDelete: 删除失败");
                return ResponseUtil.exception("删除失败");
            }

        }catch (ExpiredJwtException e){
            logger.error("groupSengdingDelete: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("groupSengdingDelete: "+ e);
            return ResponseUtil.unKonwException();
        }
    }

    public ResponseEntity<String> getEventRemind(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        try{
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            List<RemindersEntity> list = userDao.geteventremind(phone);

            if (list != null && !list.isEmpty()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("eventlist",list);
                logger.info(phone + "-getEventRemind: 获取事项提醒列表");
                return ResponseUtil.success(jsonObject);
            }else{
                logger.warn(phone + "-getEventRemind: 未发现事项提醒");
                return ResponseUtil.exception("未发现事项提醒");
            }

        }catch (ExpiredJwtException e){
            logger.error("getEventRemind"+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("getEventRemind"+ e);
            return ResponseUtil.unKonwException();
        }
    }

    public ResponseEntity<String> getEventRemindDetail(HttpServletRequest request,Map<String,String> map){
        String token = request.getHeader("Authorization");
        int id = Integer.parseInt(map.get("id"));
        try{
            if(!jwt.isExist(token)){
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            RemindersEntity remindersEntity = userDao.geteventreminddetail(id);

            if (remindersEntity != null){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("remindersEntity",remindersEntity);
                logger.info(phone + "-getEventRemindDetail: 获取详细事项提醒");
                return ResponseUtil.success(jsonObject);
            }else{
                logger.warn(phone + "-getEventRemindDetail: 未发现事项提醒的详细信息");
                return ResponseUtil.exception("未发现事项提醒，请稍后再试");
            }

        }catch (ExpiredJwtException e){
            logger.error("getEventRemindDetail: " + e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        }catch (Exception e){
            logger.error("getEventRemindDetail: "+ e);
            return ResponseUtil.unKonwException();
        }
    }

    public ResponseEntity<String> eventRemindDelete(HttpServletRequest request,Map<String,String>map){
        String token = request.getHeader("Authorization");
        int id = Integer.parseInt(map.get("id"));
        try {
            
            if (!jwt.isExist(token)) {
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            //删除评价
            String state = userDao.eventreminddelete(id);
            if(state.equals("success")){
                logger.info(phone + "-eventRemindDelete: 删除成功");
                return ResponseUtil.success("删除成功");
            }else{
                logger.warn(phone + "-eventRemindDelete: 删除失败");
                return ResponseUtil.exception("删除失败");
            }


        } catch (ExpiredJwtException e) {
            logger.error("eventRemindDelete: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        } catch (Exception e) {
            logger.error("eventRemindDelete"+ e);
            return ResponseUtil.unKonwException();
        }
    }

    public ResponseEntity<String> getEventRemindUnread(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        try {
            
            if (!jwt.isExist(token)) {
                logger.error("token不存在");
                return ResponseUtil.TOKENexception("您的登录权限已失效，请重新登录");            }
            //获取token中的信息
            Claims claims = jwt.parseJWT(token);
            String doctor = claims.getSubject();
            JSONObject jobj = JSON.parseObject(doctor);
            String phone = jobj.get("phone").toString();

            int EvenRemindUnread = userDao.getEvenRemindUnread(phone);
            JSONObject jo = new JSONObject();
            jo.put("EventRemindUnread",EvenRemindUnread);
            logger.info(phone + "-getEventRemindUnread: 获取未读的事项提醒");
            return ResponseUtil.success(jo);

        } catch (ExpiredJwtException e) {
            logger.error("getEventRemindUnread: "+ e);
            userDao.delete(token);
            return ResponseUtil.TOKENexception("您的登录权限已过期，请重新登录");
        } catch (Exception e) {
            logger.error("getEventRemindUnread: "+ e);
            return ResponseUtil.unKonwException();
        }
    }
}
