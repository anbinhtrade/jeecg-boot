package org.jeecg.modules.demo.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.demo.test.entity.JeecgDemo;
import org.jeecg.modules.demo.test.service.IJeecgDemoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: Preview the entrance on the large screen
 * @Author: scott
 * @Date:2019-12-12
 * @Version:V1.0
 */
@Slf4j
@Controller
@RequestMapping("/test/bigScreen/templat")
public class BigScreenTemplatController extends JeecgController<JeecgDemo, IJeecgDemoService> {

    /**
     * @param modelAndView
     * @return
     */
    @RequestMapping("/html")
    public ModelAndView ftl(ModelAndView modelAndView) {
        modelAndView.setViewName("demo3");
        List<String> userList = new ArrayList<String>();
        userList.add("admin");
        userList.add("user1");
        userList.add("user2");
        log.info("--------------test--------------");
        modelAndView.addObject("userList", userList);
        return modelAndView;
    }

    /**
     * Production and sales monitoring templates
     * @param modelAndView
     * @return
     */
    @RequestMapping("/index1")
    public ModelAndView index1(ModelAndView modelAndView) {
        modelAndView.setViewName("/bigscreen/template1/index");
        return modelAndView;
    }

    /**
     * Smart logistics monitoring template
     * @param modelAndView
     * @return
     */
    @RequestMapping("/index2")
    public ModelAndView index2(ModelAndView modelAndView) {
        modelAndView.setViewName("/bigscreen/template2/index");
        return modelAndView;
    }

}
