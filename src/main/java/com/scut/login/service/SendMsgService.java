package com.scut.login.service;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.scut.login.util.AliyunMessageUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ai on 2017/8/22.
 */
@Component
public class SendMsgService {
    /**
     * 发送短信
     * @return
     */
    public String sendMsg(String phone,String randomNum){
        try {
            String phoneNumber = phone;
            String jsonContent = "{\"code\":\"" + randomNum + "\"}";
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("phoneNumber", phoneNumber);
            paramMap.put("msgSign", "佳医比邻");
            paramMap.put("templateCode", "SMS_110845006");
            paramMap.put("jsonContent", jsonContent);
            SendSmsResponse sendSmsResponse = AliyunMessageUtil.sendSms(paramMap);
            if(sendSmsResponse.getCode() == null) {
                //这里可以抛出自定义异常
                return "0";
            }
            if(!sendSmsResponse.getCode().equals("OK")) {
                //这里可以抛出自定义异常
                return "0";
            }
            return "1";
        }catch (Exception e){
            return "0";
        }
    }
    /**
     * 生成随机数
     * @param num 位数
     * @return
     */
    public static String createRandomNum(int num){
        String randomNumStr = "";
        for(int i = 0; i < num;i ++){
            int randomNum = (int)(Math.random() * 10);
            randomNumStr += randomNum;
        }
        return randomNumStr;
    }
}
