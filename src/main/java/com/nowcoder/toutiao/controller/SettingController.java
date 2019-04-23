package com.nowcoder.toutiao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @program: toutiao
 * @description: SettingController
 * @author: Cheng Qun
 * @create: 2019-04-19 15:54
 */
@Controller
public class SettingController {
    @RequestMapping("/setting")
    @ResponseBody
    public String setting(){
        return "Setting : OK";
    }
}
