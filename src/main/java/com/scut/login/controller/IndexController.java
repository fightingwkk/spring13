package com.scut.login.controller;

import com.scut.login.service.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Ai on 2017/9/21.
 */
@Controller
public class IndexController {
    @Autowired
    private Index index;

    /**
     * 获取服务包名称
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getservice", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> getService(HttpServletRequest request, HttpServletResponse response){
        return index.getService(request,response);
    }

    /**
     * 获取患者的新留言
     * @param request
     * @return
     */
    @RequestMapping(value = "/getdoctornewestmessage",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> getdoctornewestmessage(HttpServletRequest request){
        return index.getdoctorNewestMessageBoards(request);
    }

    /**
     * 进入一个留言
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "/getonemessage",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> getOneMessageBoardAndReply(HttpServletRequest request, @RequestBody Map<String,String> map){
        return index.getOneMessageBoardAndReply(request,map);
    }

    /**
     * 回复信息
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "/replyMessage",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> setMessageBoard(HttpServletRequest request,@RequestBody Map<String,String> map){
        return index.setMessageBoard(request,map);
    }

    /**
     * 群发消息
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "/groupSending",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> groupSending(HttpServletRequest request,@RequestBody Map<String,String> map){
        return index.groupSending(request,map);
    }

    /**
     * 群发历史
     * @param request
     * @return
     */
    @RequestMapping(value = "/groupSendingHistory",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> groupSendingHistory(HttpServletRequest request){
        return index.groupSengdingHistory(request);
    }

    /**
     * 删除群发记录
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "/deleteSendingHistory",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> deleteSendingHistory(HttpServletRequest request,@RequestBody Map<String,String> map){
        return index.groupSengdingDelete(request,map);
    }

    /**
     * 删除留言板
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "/deleteMessageBoard",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> deleteMessageBoard(HttpServletRequest request,@RequestBody Map<String,String> map){
        return index.deleteMessageBoard(request,map);
    }

    /**
     * 获取事项提醒
     * @param request
     * @return
     */
    @RequestMapping(value = "/getEventRemind",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> getEventRemind(HttpServletRequest request){
        return index.getEventRemind(request);
    }

    /**
     * 进入查看一个事项提醒
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "/getEventRemindDetail",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> getEventRemindDetail(HttpServletRequest request,@RequestBody Map<String,String>map){
        return index.getEventRemindDetail(request,map);
    }

    /**
     * 删除事项提醒
     * @param request
     * @param map
     * @return
     */
    @RequestMapping(value = "/eventRemindDelete",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> eventRemindDelete(HttpServletRequest request,@RequestBody Map<String,String>map){
        return index.eventRemindDelete(request,map);
    }

    /**
     * 获取未读事项提醒
     * @param request
     * @return
     */
    @RequestMapping(value = "/getEventRemindUnread",method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    public ResponseEntity<String> getEventRemindUnread(HttpServletRequest request){
        return index.getEventRemindUnread(request);
    }
}
