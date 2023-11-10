package org.jeecg.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.service.impl.SysBaseApiImpl;
import org.jeecg.modules.system.vo.SysUserOnlineVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @Description: Online users
 * @Author: chenli
 * @Date: 2020-06-07
 * @Version: V1.0
 */
@RestController
@RequestMapping("/sys/online")
@Slf4j
public class SysUserOnlineController {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    public RedisTemplate redisTemplate;
    @Autowired
    public ISysUserService userService;
    @Autowired
    private SysBaseApiImpl sysBaseApi;

    @Resource
    private BaseCommonService baseCommonService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<Page<SysUserOnlineVO>> list(@RequestParam(name="username", required=false) String username,
                                              @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,@RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        Collection<String> keys = redisTemplate.keys(CommonConstant.PREFIX_USER_TOKEN + "*");
        List<SysUserOnlineVO> onlineList = new ArrayList<SysUserOnlineVO>();
        for (String key : keys) {
            String token = (String)redisUtil.get(key);
            if (StringUtils.isNotEmpty(token)) {
                SysUserOnlineVO online = new SysUserOnlineVO();
                online.setToken(token);
                //TODO Change to a one-time query
                LoginUser loginUser = sysBaseApi.getUserByName(JwtUtil.getUsername(token));
                if (loginUser != null) {
                    //update-begin---author:wangshuai ---date:20220104  for：[JTC-382]在线用户查询无效------------
                    //验证用户名是否与传过来的用户名相同
                    boolean isMatchUsername=true;
                    //判断用户名是否为空，并且当前循环的用户不包含传过来的用户名，那么就设成false
                    if(oConvertUtils.isNotEmpty(username) && !loginUser.getUsername().contains(username)){
                        isMatchUsername = false;
                    }
                    if(isMatchUsername){
                        BeanUtils.copyProperties(loginUser, online);
                        onlineList.add(online);
                    }
                    //update-end---author:wangshuai ---date:20220104  for：[JTC-382]在线用户查询无效------------
                }
            }
        }
        Collections.reverse(onlineList);

        Page<SysUserOnlineVO> page = new Page<SysUserOnlineVO>(pageNo, pageSize);
        int count = onlineList.size();
        List<SysUserOnlineVO> pages = new ArrayList<>();
        // 计算当前页第一条数据的下标
        int currId = pageNo > 1 ? (pageNo - 1) * pageSize : 0;
        for (int i = 0; i < pageSize && i < count - currId; i++) {
            pages.add(onlineList.get(currId + i));
        }
        page.setSize(pageSize);
        page.setCurrent(pageNo);
        page.setTotal(count);
        // 计算分页总页数
        page.setPages(count % 10 == 0 ? count / 10 : count / 10 + 1);
        page.setRecords(pages);

        Result<Page<SysUserOnlineVO>> result = new Result<Page<SysUserOnlineVO>>();
        result.setSuccess(true);
        result.setResult(page);
        return result;
    }

    /**
     * Force out of users
     */
    @RequestMapping(value = "/forceLogout",method = RequestMethod.POST)
    public Result<Object> forceLogout(@RequestBody SysUserOnlineVO online) {
        //User exit logic
        if(oConvertUtils.isEmpty(online.getToken())) {
            return Result.error("Failed to log out!");
        }
        String username = JwtUtil.getUsername(online.getToken());
        LoginUser sysUser = sysBaseApi.getUserByName(username);
        if(sysUser!=null) {
            baseCommonService.addLog("Compulsion: "+sysUser.getRealname()+"Exit Successful!", CommonConstant.LOG_TYPE_1, null,sysUser);
            log.info(" COMPULSION  "+sysUser.getRealname()+"Exit Successful! ");
            //Clear the login token cache of the user
            redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + online.getToken());
            //Clear the user login permission cache for Shiro
            redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
            //Clear the user's cache information（Include departmental information），例如sys:cache:user::<username>
            redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
            //Call shiro's logout
            SecurityUtils.getSubject().logout();
            return Result.ok("Logout successful!");
        }else {
            return Result.error("The token is invalid!");
        }
    }
}
