package org.jeecg.modules.system.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xkcoding.justauth.AuthRequestFactory;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.enums.MessageTypeEnum;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.*;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.system.entity.SysThirdAccount;
import org.jeecg.modules.system.entity.SysThirdAppConfig;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.model.ThirdLoginModel;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.service.ISysThirdAccountService;
import org.jeecg.modules.system.service.ISysThirdAppConfigService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.service.impl.ThirdAppDingtalkServiceImpl;
import org.jeecg.modules.system.service.impl.ThirdAppWechatEnterpriseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @Author scott
 * @since 2018-12-17
 */
@Controller
@RequestMapping("/sys/thirdLogin")
@Slf4j
public class ThirdLoginController {
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private ISysThirdAccountService sysThirdAccountService;
	@Autowired
	private ISysDictService sysDictService;
	@Autowired
	private BaseCommonService baseCommonService;
	@Autowired
    private RedisUtil redisUtil;
	@Autowired
	private AuthRequestFactory factory;

	@Autowired
	private ThirdAppWechatEnterpriseServiceImpl thirdAppWechatEnterpriseService;
	@Autowired
	private ThirdAppDingtalkServiceImpl thirdAppDingtalkService;

	@Autowired
	private ISysThirdAppConfigService appConfigService;

	@RequestMapping("/render/{source}")
    public void render(@PathVariable("source") String source, HttpServletResponse response) throws IOException {
        log.info("Third-party login to render:" + source);
        AuthRequest authRequest = factory.get(source);
        String authorizeUrl = authRequest.authorize(AuthStateUtils.createState());
        log.info("Third-party login authentication address:" + authorizeUrl);
        response.sendRedirect(authorizeUrl);
    }

	@RequestMapping("/{source}/callback")
    public String loginThird(@PathVariable("source") String source, AuthCallback callback,ModelMap modelMap) {
		log.info("Third-party login to enter the callback:" + source + " params：" + JSONObject.toJSONString(callback));
        AuthRequest authRequest = factory.get(source);
        AuthResponse response = authRequest.login(callback);
        log.info(JSONObject.toJSONString(response));
        Result<JSONObject> result = new Result<JSONObject>();
        if(response.getCode()==2000) {

        	JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(response.getData()));
        	String username = data.getString("username");
        	String avatar = data.getString("avatar");
        	String uuid = data.getString("uuid");
        	//构造第三方登录信息存储对象
			ThirdLoginModel tlm = new ThirdLoginModel(source, uuid, username, avatar);
        	//Judge if there is such a person
			//update-begin-author:wangshuai date:20201118 for:修改成查询第三方账户表
        	LambdaQueryWrapper<SysThirdAccount> query = new LambdaQueryWrapper<SysThirdAccount>();
        	query.eq(SysThirdAccount::getThirdType, source);
			//update-begin---author:wangshuai---date:2023-10-07---for:【QQYUN-6667】敲敲云，线上解绑重新绑定一直提示这个---
        	query.eq(SysThirdAccount::getTenantId, CommonConstant.TENANT_ID_DEFAULT_VALUE);
			//update-end---author:wangshuai---date:2023-10-07---for:【QQYUN-6667】敲敲云，线上解绑重新绑定一直提示这个---
			query.and(q -> q.eq(SysThirdAccount::getThirdUserUuid, uuid).or().eq(SysThirdAccount::getThirdUserId, uuid));
        	List<SysThirdAccount> thridList = sysThirdAccountService.list(query);
			SysThirdAccount user = null;
        	if(thridList==null || thridList.size()==0) {
				//否则直接创建新账号
				user = sysThirdAccountService.saveThirdUser(tlm,CommonConstant.TENANT_ID_DEFAULT_VALUE);
        	}else {
        		//已存在 只设置用户名 不设置头像
        		user = thridList.get(0);
        	}
        	// 生成token
			//update-begin-author:wangshuai date:20201118 for:从第三方登录查询是否存在用户id，不存在绑定手机号
			if(oConvertUtils.isNotEmpty(user.getSysUserId())) {
				String sysUserId = user.getSysUserId();
				SysUser sysUser = sysUserService.getById(sysUserId);
				String token = saveToken(sysUser);
    			modelMap.addAttribute("token", token);
			}else{
				modelMap.addAttribute("token", "bind a mobile phone number,"+""+uuid);
			}
			//update-end-author:wangshuai date:20201118 for:从第三方登录查询是否存在用户id，不存在绑定手机号
		//update-begin--Author:wangshuai  Date:20200729 for：接口在签名校验失败时返回失败的标识码 issues#1441--------------------
        }else{
			modelMap.addAttribute("token", "Login failed");
		}
		//update-end--Author:wangshuai  Date:20200729 for：接口在签名校验失败时返回失败的标识码 issues#1441--------------------
        result.setSuccess(false);
        result.setMessage("If the third-party login is abnormal, contact the administrator");
        return "thirdLogin";
    }

	/**
	 * Create a new account
	 * @param model
	 * @return
	 */
	@PostMapping("/user/create")
	@ResponseBody
	public Result<String> thirdUserCreate(@RequestBody ThirdLoginModel model) {
		log.info("Third-party login to create a new account:" );
		Result<String> res = new Result<>();
		Object operateCode = redisUtil.get(CommonConstant.THIRD_LOGIN_CODE);
		if(operateCode==null || !operateCode.toString().equals(model.getOperateCode())){
			res.setSuccess(false);
			res.setMessage("The verification failed");
			return res;
		}
		//创建新账号
		//update-begin-author:wangshuai date:20201118 for:修改成从第三方登录查出来的user_id，在查询用户表尽行token
		SysThirdAccount user = sysThirdAccountService.saveThirdUser(model,CommonConstant.TENANT_ID_DEFAULT_VALUE);
		if(oConvertUtils.isNotEmpty(user.getSysUserId())){
			String sysUserId = user.getSysUserId();
			SysUser sysUser = sysUserService.getById(sysUserId);
			// 生成token
			String token = saveToken(sysUser);
			//update-end-author:wangshuai date:20201118 for:修改成从第三方登录查出来的user_id，在查询用户表尽行token
			res.setResult(token);
			res.setSuccess(true);
		}
		return res;
	}

	/**
	 * To bind an account, you need to set a password, and you need to go through the verification
	 * @param json
	 * @return
	 */
	@PostMapping("/user/checkPassword")
	@ResponseBody
	public Result<String> checkPassword(@RequestBody JSONObject json) {
		Result<String> result = new Result<>();
		Object operateCode = redisUtil.get(CommonConstant.THIRD_LOGIN_CODE);
		if(operateCode==null || !operateCode.toString().equals(json.getString("operateCode"))){
			result.setSuccess(false);
			result.setMessage("The verification failed");
			return result;
		}
		String username = json.getString("uuid");
		SysUser user = this.sysUserService.getUserByName(username);
		if(user==null){
			result.setMessage("The user was not found");
			result.setSuccess(false);
			return result;
		}
		String password = json.getString("password");
		String salt = user.getSalt();
		String passwordEncode = PasswordUtil.encrypt(user.getUsername(), password, salt);
		if(!passwordEncode.equals(user.getPassword())){
			result.setMessage("The password is incorrect");
			result.setSuccess(false);
			return result;
		}

		sysUserService.updateById(user);
		result.setSuccess(true);
		// 生成token
		String token = saveToken(user);
		result.setResult(token);
		return result;
	}

	private String saveToken(SysUser user) {
		// 生成token
		String token = JwtUtil.sign(user.getUsername(), user.getPassword());
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		// 设置超时时间
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);
		return token;
	}

	/**
	 * The third-party login callback API
	 * @param token
	 * @param thirdType
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getLoginUser/{token}/{thirdType}/{tenantId}", method = RequestMethod.GET)
	@ResponseBody
	public Result<JSONObject> getThirdLoginUser(@PathVariable("token") String token,@PathVariable("thirdType") String thirdType,@PathVariable("tenantId") String tenantId) throws Exception {
		Result<JSONObject> result = new Result<JSONObject>();
		String username = JwtUtil.getUsername(token);

		//1. 校验用户是否有效
		SysUser sysUser = sysUserService.getUserByName(username);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}
		//update-begin-author:wangshuai date:20201118 for:如果真实姓名和头像不存在就取第三方登录的
		LambdaQueryWrapper<SysThirdAccount> query = new LambdaQueryWrapper<>();
		query.eq(SysThirdAccount::getSysUserId,sysUser.getId());
		query.eq(SysThirdAccount::getThirdType,thirdType);
		query.eq(SysThirdAccount::getTenantId,oConvertUtils.getInt(tenantId,CommonConstant.TENANT_ID_DEFAULT_VALUE));
		//update-begin---author:wangshuai ---date:20230328  for：[QQYUN-4883]钉钉auth登录同一个租户下有同一个用户id------------
		List<SysThirdAccount> accountList = sysThirdAccountService.list(query);
		SysThirdAccount account = new SysThirdAccount();
		if(CollectionUtil.isNotEmpty(accountList)){
			account = accountList.get(0);
		}
		//update-end---author:wangshuai ---date:20230328  for：[QQYUN-4883]钉钉auth登录同一个租户下有同一个用户id------------
		if(oConvertUtils.isEmpty(sysUser.getRealname())){
			sysUser.setRealname(account.getRealname());
		}
		if(oConvertUtils.isEmpty(sysUser.getAvatar())){
			sysUser.setAvatar(account.getAvatar());
		}
		//update-end-author:wangshuai date:20201118 for:如果真实姓名和头像不存在就取第三方登录的
		JSONObject obj = new JSONObject();
		//TODO Third-party login determines the login tenant and department logic

		//用户登录信息
		obj.put("userInfo", sysUser);
		//获取字典缓存【解决 #jeecg-boot/issues/3998】
		obj.put("sysAllDictItems", sysDictService.queryAllDictItems());
		//token 信息
		obj.put("token", token);
		result.setResult(obj);
		result.setSuccess(true);
		result.setCode(200);
		baseCommonService.addLog("Username: " + username + ", login successfully [third-party user]!", CommonConstant.LOG_TYPE_1, null);
		return result;
	}
	/**
	 * The third-party bound mobile phone number returns the token
	 *
	 * @param jsonObject
	 * @return
	 */
	@ApiOperation("Mobile phone number login interface")
	@PostMapping("/bindingThirdPhone")
	@ResponseBody
	public Result<String> bindingThirdPhone(@RequestBody JSONObject jsonObject) {
		Result<String> result = new Result<String>();
		String phone = jsonObject.getString("mobile");
		String thirdUserUuid = jsonObject.getString("thirdUserUuid");
		// 校验验证码
		String captcha = jsonObject.getString("captcha");
		//update-begin-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
		String redisKey = CommonConstant.PHONE_REDIS_KEY_PRE+phone;
		Object captchaCache = redisUtil.get(redisKey);
		//update-end-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
		if (oConvertUtils.isEmpty(captcha) || !captcha.equals(captchaCache)) {
			result.setMessage("The verification code is incorrect");
			result.setSuccess(false);
			return result;
		}
		//校验用户有效性
		SysUser sysUser = sysUserService.getUserByPhone(phone);
		if(sysUser != null){
			// 存在用户，直接绑定
			sysThirdAccountService.updateThirdUserId(sysUser,thirdUserUuid);
		}else{
			// 不存在手机号，创建用户
			sysUser = sysThirdAccountService.createUser(phone,thirdUserUuid,CommonConstant.TENANT_ID_DEFAULT_VALUE);
		}
		String token = saveToken(sysUser);
		result.setSuccess(true);
		result.setResult(token);
		return result;
	}

	/**
	 * Wechat/DingTalk O Auth 2 login
	 *
	 * @param source
	 * @param state
	 * @return
	 */
	@ResponseBody
	@GetMapping("/oauth2/{source}/login")
	public String oauth2LoginCallback(@PathVariable("source") String source, @RequestParam("state") String state, HttpServletRequest request, HttpServletResponse response,
									  @RequestParam(value = "tenantId",required = false,defaultValue = "0") String tenantId) throws Exception {
		String url;
		//应用id为空，说明没有配置lowAppId
		if(oConvertUtils.isEmpty(tenantId)){
			return "The tenant code is not configured";
		}
		if (CommonConstant.WECHAT_ENTERPRISE.equalsIgnoreCase(source)) {
			//换成第三方app配置表
			SysThirdAppConfig config = appConfigService.getThirdConfigByThirdType(Integer.valueOf(tenantId), MessageTypeEnum.QYWX.getType());
			if(null == config){
				return "If you have not configured the WeCom app, please configure the WeCom app";
			}
			StringBuilder builder = new StringBuilder();
			// 构造企业微信OAuth2登录授权地址
			builder.append("https://open.weixin.qq.com/connect/oauth2/authorize");
			// 企业的CorpID
			builder.append("?appid=").append(config.getClientId());
			// 授权后重定向的回调链接地址，请使用urlencode对链接进行处理
			String redirectUri = CommonUtils.getBaseUrl(request)  + "/sys/thirdLogin/oauth2/wechat_enterprise/callback?tenantId="+tenantId;;
			builder.append("&redirect_uri=").append(URLEncoder.encode(redirectUri, "UTF-8"));
			// 返回类型，此时固定为：code
			builder.append("&response_type=code");
			// 应用授权作用域。
			// snsapi_base：静默授权，可获取成员的的基础信息（UserId与DeviceId）；
			builder.append("&scope=snsapi_base");
			// 重定向后会带上state参数，长度不可超过128个字节
			builder.append("&state=").append(state);
			// 终端使用此参数判断是否需要带上身份信息
			builder.append("#wechat_redirect");
			url = builder.toString();
		} else if (CommonConstant.DINGTALK.equalsIgnoreCase(source)) {
			//update-begin---author:wangshuai ---date:20230224  for：[QQYUN-3440]新建企业微信和钉钉配置表，通过租户模式隔离------------
			//换成第三方app配置表
			SysThirdAppConfig appConfig = appConfigService.getThirdConfigByThirdType(Integer.valueOf(tenantId), MessageTypeEnum.DD.getType());
			if(null == appConfig){
				return "If you have not configured a DingTalk application, please configure a DingTalk application";
			}
			//update-end---author:wangshuai ---date:20230224  for：[QQYUN-3440]新建企业微信和钉钉配置表，通过租户模式隔离------------
			StringBuilder builder = new StringBuilder();
			// 构造钉钉OAuth2登录授权地址
			builder.append("https://login.dingtalk.com/oauth2/auth");
			// 授权通过/拒绝后回调地址。
			// 注意 需要与注册应用时登记的域名保持一致。
			String redirectUri = CommonUtils.getBaseUrl(request) + "/sys/thirdLogin/oauth2/dingtalk/callback?tenantId="+tenantId;
			builder.append("?redirect_uri=").append(URLEncoder.encode(redirectUri, "UTF-8"));
			// 固定值为code。
			// 授权通过后返回authCode。
			builder.append("&response_type=code");
			// 步骤一中创建的应用详情中获取。
			// 企业内部应用：client_id为应用的AppKey。
			builder.append("&client_id=").append(appConfig.getClientId());
			// 授权范围，授权页面显示的授权信息以应用注册时配置的为准。
			// openid：授权后可获得用户userid
			builder.append("&scope=openid");
			// 跟随authCode原样返回。
			builder.append("&state=").append(state);
            //update-begin---author:wangshuai ---date:20220613  for：[issues/I5BOUF]oauth2 钉钉无法登录------------
            builder.append("&prompt=").append("consent");
            //update-end---author:wangshuai ---date:20220613  for：[issues/I5BOUF]oauth2 钉钉无法登录--------------
            url = builder.toString();
		} else {
			return "Unsupported sources";
		}
		log.info("oauth2 login url:" + url);
		response.sendRedirect(url);
		return "login…";
	}

    /**
     * Wechat/DingTalk O Auth 2 login callback
     *
     * @param code
     * @param state
     * @param response
     * @return
     */
	@ResponseBody
	@GetMapping("/oauth2/{source}/callback")
	public String oauth2LoginCallback(
			@PathVariable("source") String source,
			// 企业微信返回的code
			@RequestParam(value = "code", required = false) String code,
			// 钉钉返回的code
			@RequestParam(value = "authCode", required = false) String authCode,
			@RequestParam("state") String state,
			@RequestParam(name = "tenantId",defaultValue = "0") String tenantId,
			HttpServletResponse response) {
        SysUser loginUser;
        if (CommonConstant.WECHAT_ENTERPRISE.equalsIgnoreCase(source)) {
            log.info("【WeCom】O Auth 2 Login to callback:code=" + code + ", state=" + state);
            loginUser = thirdAppWechatEnterpriseService.oauth2Login(code,Integer.valueOf(tenantId));
            if (loginUser == null) {
                return "Login failed";
            }
        } else if (CommonConstant.DINGTALK.equalsIgnoreCase(source)) {
			log.info("【DingTalk】O Auth 2 login to callback:auth Code=" + authCode + ", state=" + state);
			loginUser = thirdAppDingtalkService.oauth2Login(authCode,Integer.valueOf(tenantId));
			if (loginUser == null) {
				return "Login failed";
			}
        } else {
            return "Unsupported sources";
        }
        try {

			//update-begin-author:taoyan date:2022-6-30 for: 工作流发送消息 点击消息链接跳转办理页面
			String redirect = "";
			if (state.indexOf("?") > 0) {
				String[] arr = state.split("\\?");
				state = arr[0];
				if(arr.length>1){
					redirect = arr[1];
				}
			}

			String token = saveToken(loginUser);
			state += "/oauth2-app/login?oauth2LoginToken=" + URLEncoder.encode(token, "UTF-8") + "&tenantId=" + URLEncoder.encode(tenantId, "UTF-8");
			//update-begin---author:wangshuai ---date:20220613  for：[issues/I5BOUF]oauth2 钉钉无法登录------------
			state += "&thirdType=" + source;
			//state += "&thirdType=" + "wechat_enterprise";
			if (redirect != null && redirect.length() > 0) {
				state += "&" + redirect;
			}
			//update-end-author:taoyan date:2022-6-30 for: 工作流发送消息 点击消息链接跳转办理页面

            //update-end---author:wangshuai ---date:20220613  for：[issues/I5BOUF]oauth2 钉钉无法登录------------
			log.info("O Auth 2 login redirect address: " + state);
            try {
                response.sendRedirect(state);
                return "ok";
            } catch (IOException e) {
                e.printStackTrace();
                return "Redirect failed";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "Decoding failed";
        }
    }

	/**
	 * Register an account and bind a third-party account [Low-code application dedicated interface]
	 * @param jsonObject
	 * @param user
	 * @return
	 */
	@ResponseBody
	@PutMapping("/registerBindThirdAccount")
	public Result<String> registerBindThirdAccount(@RequestBody JSONObject jsonObject, SysUser user) {
		//手机号
		String phone = jsonObject.getString("phone");
		//验证码
		String smscode = jsonObject.getString("smscode");
		String redisKey = CommonConstant.PHONE_REDIS_KEY_PRE + phone;
		Object code = redisUtil.get(redisKey);
		//第三方uuid
		String thirdUserUuid = jsonObject.getString("thirdUserUuid");
		String username = jsonObject.getString("username");
		//未设置用户名，则用手机号作为用户名
		if (oConvertUtils.isEmpty(username)) {
			username = phone;
		}
		//未设置密码，则随机生成一个密码
		String password = jsonObject.getString("password");
		if (oConvertUtils.isEmpty(password)) {
			password = RandomUtil.randomString(8);
		}
		String email = jsonObject.getString("email");
		SysUser sysUser1 = sysUserService.getUserByName(username);
		if (sysUser1 != null) {
			return Result.error("The username is registered");
		}
		SysUser sysUser2 = sysUserService.getUserByPhone(phone);
		if (sysUser2 != null) {
			return Result.error("The mobile phone number is registered");
		}
		if (oConvertUtils.isNotEmpty(email)) {
			SysUser sysUser3 = sysUserService.getUserByEmail(email);
			if (sysUser3 != null) {
				return Result.error("The email address has been registered");
			}
		}
		if (null == code) {
			return Result.error("The verification code of your mobile phone is invalid, please obtain it again");
		}
		if (!smscode.equals(code.toString())) {
			return Result.error("The verification code on your phone is incorrect");
		}
		String realname = jsonObject.getString("realname");
		if (oConvertUtils.isEmpty(realname)) {
			realname = username;
		}
		try {
			//保存用户表
			user.setCreateTime(new Date());
			String salt = oConvertUtils.randomGen(8);
			String passwordEncode = PasswordUtil.encrypt(username, password, salt);
			user.setSalt(salt);
			user.setUsername(username);
			user.setRealname(realname);
			user.setPassword(passwordEncode);
			user.setEmail(email);
			user.setPhone(phone);
			user.setStatus(CommonConstant.USER_UNFREEZE);
			user.setDelFlag(CommonConstant.DEL_FLAG_0);
			user.setActivitiSync(CommonConstant.ACT_SYNC_1);
			sysUserService.addUserWithRole(user, "");
			//保存第三方用户表
			sysThirdAccountService.updateThirdUserId(user, thirdUserUuid);
			String token = saveToken(user);
			return Result.ok(token);
		} catch (Exception e) {
			return Result.error("Registration failed");
		}
	}
}