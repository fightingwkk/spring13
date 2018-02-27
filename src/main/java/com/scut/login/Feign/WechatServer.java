package com.scut.login.Feign;

import com.scut.login.util.JsonResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Ai on 2017/11/8.
 */
@FeignClient("microservice-jiayibilin-weChat")
public interface WechatServer {
    @RequestMapping(value="/aliyun/qrcode/create")
    JsonResult qrCodeCreateLogo(@RequestParam("phone")String phone);

    @RequestMapping(value="/aliyun/qrcode/get",method = RequestMethod.GET)
    JsonResult getdoctorqrcodeurl(@RequestParam("phone") String phone);

}
