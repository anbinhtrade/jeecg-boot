package org.jeecg.modules.system.controller;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.constant.enums.DySmsEnum;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.*;
import org.jeecg.common.util.encryption.EncryptedString;
import org.jeecg.config.JeecgBaseConfig;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysRoleIndex;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.model.SysLoginModel;
import org.jeecg.modules.system.service.*;
import org.jeecg.modules.system.service.impl.SysBaseApiImpl;
import org.jeecg.modules.system.util.RandImageUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author scott
 * @since 2018-12-17
 */
@RestController
@RequestMapping("/sys")
@Api(tags="User login")
@Slf4j
public class LoginController {
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private ISysPermissionService sysPermissionService;
	@Autowired
	private SysBaseApiImpl sysBaseApi;
	@Autowired
	private ISysLogService logService;
	@Autowired
    private RedisUtil redisUtil;
	@Autowired
    private ISysDepartService sysDepartService;
	@Autowired
    private ISysDictService sysDictService;
	@Resource
	private BaseCommonService baseCommonService;
	@Autowired
	private JeecgBaseConfig jeecgBaseConfig;

	private final String BASE_CHECK_CODES = "qwertyuiplkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";

	@ApiOperation("Login interface")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Result<JSONObject> login(@RequestBody SysLoginModel sysLoginModel){
		Result<JSONObject> result = new Result<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		//update-begin-author:taoyan date:2022-11-7 for: issues/4109 平台用户登录失败锁定用户
		if(isLoginFailOvertimes(username)){
			return result.error500("This user has failed to log in too many times, please log in again in 10 minutes!");		}
		//update-end-author:taoyan date:2022-11-7 for: issues/4109 平台用户登录失败锁定用户
		//update-begin--Author:scott  Date:20190805 for：暂时注释掉密码加密逻辑，有点问题
		//前端密码加密，后端进行密码解密
		//password = AesEncryptUtil.desEncrypt(sysLoginModel.getPassword().replaceAll("%2B", "\\+")).trim();//密码解密
		//update-begin--Author:scott  Date:20190805 for：暂时注释掉密码加密逻辑，有点问题

		//update-begin-author:taoyan date:20190828 for:校验验证码
        String captcha = sysLoginModel.getCaptcha();
        if(captcha==null){
            result.error500("Verification code is invalid");
            return result;
        }
        String lowerCaseCaptcha = captcha.toLowerCase();
        //update-begin-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
		// 加入密钥作为混淆，避免简单的拼接，被外部利用，用户自定义该密钥即可
        String origin = lowerCaseCaptcha+sysLoginModel.getCheckKey()+jeecgBaseConfig.getSignatureSecret();
		String realKey = Md5Util.md5Encode(origin, "utf-8");
		//update-end-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
		Object checkCode = redisUtil.get(realKey);
		//当进入登录页时，有一定几率出现验证码错误 #1714
		if(checkCode==null || !checkCode.toString().equals(lowerCaseCaptcha)) {
            log.warn("Verification code error，key= {} , Ui checkCode= {}, Redis checkCode = {}", sysLoginModel.getCheckKey(), lowerCaseCaptcha, checkCode);
			result.error500("Verification code error");
			// 改成特殊的code 便于前端判断
			result.setCode(HttpStatus.PRECONDITION_FAILED.value());
			return result;
		}
		//update-end-author:taoyan date:20190828 for:校验验证码
		
		//1. 校验用户是否有效
		//update-begin-author:wangshuai date:20200601 for: 登录代码验证用户是否注销bug，if条件永远为false
		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysUser::getUsername,username);
		SysUser sysUser = sysUserService.getOne(queryWrapper);
		//update-end-author:wangshuai date:20200601 for: 登录代码验证用户是否注销bug，if条件永远为false
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}

		//2. 校验用户名或密码是否正确
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		if (!syspassword.equals(userpassword)) {
			//update-begin-author:taoyan date:2022-11-7 for: issues/4109 平台用户登录失败锁定用户
			addLoginFailOvertimes(username);
			//update-end-author:taoyan date:2022-11-7 for: issues/4109 平台用户登录失败锁定用户
			result.error500("WRONG USER NAME OR PASSWORD");
			return result;
		}
				
		//用户登录信息
		userInfo(sysUser, result);
		//update-begin--Author:liusq  Date:20210126  for：登录成功，删除redis中的验证码
		redisUtil.del(realKey);
		//update-begin--Author:liusq  Date:20210126  for：登录成功，删除redis中的验证码
		redisUtil.del(CommonConstant.LOGIN_FAIL + username);
		LoginUser loginUser = new LoginUser();
		BeanUtils.copyProperties(sysUser, loginUser);
		baseCommonService.addLog("Username: " + username + ",Login successful！", CommonConstant.LOG_TYPE_1, null,loginUser);
        //update-end--Author:wangshuai  Date:20200714  for：登录日志没有记录人员
		return result;
	}


	/**
	 * 【Special for vue 3】Get user information
	 */
	@GetMapping("/user/getUserInfo")
	public Result<JSONObject> getUserInfo(HttpServletRequest request){
		Result<JSONObject> result = new Result<JSONObject>();
		String  username = JwtUtil.getUserNameByToken(request);
		if(oConvertUtils.isNotEmpty(username)) {
			// 根据用户名查询用户信息
			SysUser sysUser = sysUserService.getUserByName(username);
			JSONObject obj=new JSONObject();

			//update-begin---author:scott ---date:2022-06-20  for：vue3前端，支持自定义首页-----------
			String version = request.getHeader(CommonConstant.VERSION);
			//update-begin---author:liusq ---date:2022-06-29  for：接口返回值修改，同步修改这里的判断逻辑-----------
			SysRoleIndex roleIndex = sysUserService.getDynamicIndexByUserRole(username, version);
			if (oConvertUtils.isNotEmpty(version) && roleIndex != null && oConvertUtils.isNotEmpty(roleIndex.getUrl())) {
				String homePath = roleIndex.getUrl();
				if (!homePath.startsWith(SymbolConstant.SINGLE_SLASH)) {
					homePath = SymbolConstant.SINGLE_SLASH + homePath;
				}
				sysUser.setHomePath(homePath);
			}
			//update-begin---author:liusq ---date:2022-06-29  for：接口返回值修改，同步修改这里的判断逻辑-----------
			//update-end---author:scott ---date::2022-06-20  for：vue3前端，支持自定义首页--------------
			
			obj.put("userInfo",sysUser);
			obj.put("sysAllDictItems", sysDictService.queryAllDictItems());
			result.setResult(obj);
			result.success("");
		}
		return result;

	}
	
	/**
	 * Sign out
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/logout")
	public Result<Object> logout(HttpServletRequest request,HttpServletResponse response) {
		//用户退出逻辑
	    String token = request.getHeader(CommonConstant.X_ACCESS_TOKEN);
	    if(oConvertUtils.isEmpty(token)) {
	    	return Result.error("Logout failed！");
	    }
	    String username = JwtUtil.getUsername(token);
		LoginUser sysUser = sysBaseApi.getUserByName(username);
	    if(sysUser!=null) {
			//update-begin--Author:wangshuai  Date:20200714  for：登出日志没有记录人员
			baseCommonService.addLog("user name: "+sysUser.getRealname()+",exit successfully!", CommonConstant.LOG_TYPE_1, null,sysUser);
			//update-end--Author:wangshuai  Date:20200714  for：登出日志没有记录人员
	    	log.info(" user name:  "+sysUser.getRealname()+",exit successfully！ ");
	    	//清空用户登录Token缓存
	    	redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
	    	//清空用户登录Shiro权限缓存
			redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
			//清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
			redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
			//调用shiro的logout
			SecurityUtils.getSubject().logout();
	    	return Result.ok("Log out successfully！");
	    }else {
	    	return Result.error("Token is invalid!");
	    }
	}
	
	/**
	 * Get visits
	 * @return
	 */
	@GetMapping("loginfo")
	public Result<JSONObject> loginfo() {
		Result<JSONObject> result = new Result<JSONObject>();
		JSONObject obj = new JSONObject();
		//update-begin--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
		// 获取一天的开始和结束时间
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date dayStart = calendar.getTime();
		calendar.add(Calendar.DATE, 1);
		Date dayEnd = calendar.getTime();
		// Get system access records
		Long totalVisitCount = logService.findTotalVisitCount();
		obj.put("totalVisitCount", totalVisitCount);
		Long todayVisitCount = logService.findTodayVisitCount(dayStart,dayEnd);
		obj.put("todayVisitCount", todayVisitCount);
		Long todayIp = logService.findTodayIp(dayStart,dayEnd);
		//update-end--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
		obj.put("todayIp", todayIp);
		result.setResult(obj);
		result.success("login successful");
		return result;
	}
	
	/**
	 * Get visits
	 * @return
	 */
	@GetMapping("visitInfo")
	public Result<List<Map<String,Object>>> visitInfo() {
		Result<List<Map<String,Object>>> result = new Result<List<Map<String,Object>>>();
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date dayEnd = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date dayStart = calendar.getTime();
        List<Map<String,Object>> list = logService.findVisitCount(dayStart, dayEnd);
		result.setResult(oConvertUtils.toLowerCasePageList(list));
		return result;
	}
	
	
	/**
	 * Log in successfully and select the user’s current department.
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/selectDepart", method = RequestMethod.PUT)
	public Result<JSONObject> selectDepart(@RequestBody SysUser user) {
		Result<JSONObject> result = new Result<JSONObject>();
		String username = user.getUsername();
		if(oConvertUtils.isEmpty(username)) {
			LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
			username = sysUser.getUsername();
		}
		
		//获取登录部门
		String orgCode= user.getOrgCode();
		//获取登录租户
		Integer tenantId = user.getLoginTenantId();
		//设置用户登录部门和登录租户
		this.sysUserService.updateUserDepart(username, orgCode,tenantId);
		SysUser sysUser = sysUserService.getUserByName(username);
		JSONObject obj = new JSONObject();
		obj.put("userInfo", sysUser);
		result.setResult(obj);
		return result;
	}

	/**
	 * SMS login interface
	 * 
	 * @param jsonObject
	 * @return
	 */
	@PostMapping(value = "/sms")
	public Result<String> sms(@RequestBody JSONObject jsonObject) {
		Result<String> result = new Result<String>();
		String mobile = jsonObject.get("mobile").toString();
		//手机号模式 登录模式: "2"  注册模式: "1"
		String smsmode=jsonObject.get("smsmode").toString();
		log.info(mobile);
		if(oConvertUtils.isEmpty(mobile)){
			result.setMessage("Mobile phone number is not allowed to be empty！");
			result.setSuccess(false);
			return result;
		}
		
		//update-begin-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
		String redisKey = CommonConstant.PHONE_REDIS_KEY_PRE+mobile;
		Object object = redisUtil.get(redisKey);
		//update-end-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
		
		if (object != null) {
			result.setMessage("The verification code will still be valid within 10 minutes！");
			result.setSuccess(false);
			return result;
		}

		//随机数
		String captcha = RandomUtil.randomNumbers(6);
		JSONObject obj = new JSONObject();
    	obj.put("code", captcha);
		try {
			boolean b = false;
			//注册模板
			if (CommonConstant.SMS_TPL_TYPE_1.equals(smsmode)) {
				SysUser sysUser = sysUserService.getUserByPhone(mobile);
				if(sysUser!=null) {
					result.error500(" Your mobile phone number has been registered, please log in directly！");
					baseCommonService.addLog("Your mobile phone number has been registered, please log in directly！", CommonConstant.LOG_TYPE_1, null);
					return result;
				}
				b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.REGISTER_TEMPLATE_CODE);
			}else {
				//Login mode, verify user validity
				SysUser sysUser = sysUserService.getUserByPhone(mobile);
				result = sysUserService.checkUserIsEffective(sysUser);
				if(!result.isSuccess()) {
					String message = result.getMessage();
					String userNotExist="This user does not exist, please register";
					if(userNotExist.equals(message)){
						result.error500("This user does not exist or has not bound a mobile phone number");
					}
					return result;
				}
				
				/**
				 * smsmode SMS template method 0. Login template, 1. Registration template, 2. Forgot password template
				 */
				if (CommonConstant.SMS_TPL_TYPE_0.equals(smsmode)) {
					//Login template
					b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.LOGIN_TEMPLATE_CODE);
				} else if(CommonConstant.SMS_TPL_TYPE_2.equals(smsmode)) {
					//Forgot password template
					b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.FORGET_PASSWORD_TEMPLATE_CODE);
				}
			}

			if (b == false) {
				result.setMessage("The SMS verification code failed to be sent, please try again later.");
				result.setSuccess(false);
				return result;
			}
			
			//update-begin-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
			//验证码10分钟内有效
			redisUtil.set(redisKey, captcha, 600);
			//update-end-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
			
			//update-begin--Author:scott  Date:20190812 for：issues#391
			//result.setResult(captcha);
			//update-end--Author:scott  Date:20190812 for：issues#391
			result.setSuccess(true);

		} catch (ClientException e) {
			e.printStackTrace();
			result.error500(" The SMS interface is not configured, please contact the administrator！");
			return result;
		}
		return result;
	}
	

	/**
	 * Mobile phone number login interface
	 * 
	 * @param jsonObject
	 * @return
	 */
	@ApiOperation("Mobile phone number login interface")
	@PostMapping("/phoneLogin")
	public Result<JSONObject> phoneLogin(@RequestBody JSONObject jsonObject) {
		Result<JSONObject> result = new Result<JSONObject>();
		String phone = jsonObject.getString("mobile");
		//update-begin-author:taoyan date:2022-11-7 for: issues/4109 平台用户登录失败锁定用户
		if(isLoginFailOvertimes(phone)){
			return result.error500("This user has failed to log in too many times. Please log in again in 10 minutes.！");
		}
		//update-end-author:taoyan date:2022-11-7 for: issues/4109 平台用户登录失败锁定用户
		//校验用户有效性
		SysUser sysUser = sysUserService.getUserByPhone(phone);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}
		
		String smscode = jsonObject.getString("captcha");

		//update-begin-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
		String redisKey = CommonConstant.PHONE_REDIS_KEY_PRE+phone;
		Object code = redisUtil.get(redisKey);
		//update-end-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906

		if (!smscode.equals(code)) {
			//update-begin-author:taoyan date:2022-11-7 for: issues/4109 平台用户登录失败锁定用户
			addLoginFailOvertimes(phone);
			//update-end-author:taoyan date:2022-11-7 for: issues/4109 平台用户登录失败锁定用户
			result.setMessage("Mobile phone verification code error");
			return result;
		}
		//User Info
		userInfo(sysUser, result);
		//ADD LOG
		baseCommonService.addLog("user name: " + sysUser.getUsername() + ",login successful！", CommonConstant.LOG_TYPE_1, null);

		return result;
	}


	/**
	 * User Info
	 *
	 * @param sysUser
	 * @param result
	 * @return
	 */
	private Result<JSONObject> userInfo(SysUser sysUser, Result<JSONObject> result) {
		String username = sysUser.getUsername();
		String syspassword = sysUser.getPassword();
		// Get user department information
		JSONObject obj = new JSONObject(new LinkedHashMap<>());

		//1.Generate token
		String token = JwtUtil.sign(username, syspassword);
		// Set token cache validity time
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);
		obj.put("token", token);

		//2.Set up login tenant
		Result<JSONObject> loginTenantError = sysUserService.setLoginTenant(sysUser, obj, username,result);
		if (loginTenantError != null) {
			return loginTenantError;
		}

		//3.Set login user information
		obj.put("userInfo", sysUser);
		
		//4.Set up login department
		List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
		obj.put("departs", departs);
		if (departs == null || departs.size() == 0) {
			obj.put("multi_depart", 0);
		} else if (departs.size() == 1) {
			sysUserService.updateUserDepart(username, departs.get(0).getOrgCode(),null);
			obj.put("multi_depart", 1);
		} else {
			//Check whether there is currently a logged-in department
			// update-begin--Author:wangshuai Date:20200805 for：如果用戶为选择部门，数据库为存在上一次登录部门，则取一条存进去
			SysUser sysUserById = sysUserService.getById(sysUser.getId());
			if(oConvertUtils.isEmpty(sysUserById.getOrgCode())){
				sysUserService.updateUserDepart(username, departs.get(0).getOrgCode(),null);
			}
			// update-end--Author:wangshuai Date:20200805 for：If the user selects a department and the last logged-in department exists in the database, then take one and store it in it.
			obj.put("multi_depart", 2);
		}
		obj.put("sysAllDictItems", sysDictService.queryAllDictItems());
		result.setResult(obj);
		result.success("LOGIN SUCCESSFUL");
		return result;
	}

	/**
	 * Get encrypted string
	 * @return
	 */
	@GetMapping(value = "/getEncryptedString")
	public Result<Map<String,String>> getEncryptedString(){
		Result<Map<String,String>> result = new Result<Map<String,String>>();
		Map<String,String> map = new HashMap(5);
		map.put("key", EncryptedString.key);
		map.put("iv",EncryptedString.iv);
		result.setResult(map);
		return result;
	}

	/**
	 * Graphical verification code generated in the background: valid
	 * @param response
	 * @param key
	 */
	@ApiOperation("Get Verification Code")
	@GetMapping(value = "/randomImage/{key}")
	public Result<String> randomImage(HttpServletResponse response,@PathVariable("key") String key){
		Result<String> res = new Result<String>();
		try {
			//Generate verification code
			String code = RandomUtil.randomString(BASE_CHECK_CODES,4);
			//Save to redis
			String lowerCaseCode = code.toLowerCase();
			
			//update-begin-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
			// Add key as obfuscation，Avoid simple splicing，EXPLOITED BY OUTSIDERS，The user can customize the key
			String origin = lowerCaseCode+key+jeecgBaseConfig.getSignatureSecret();
			String realKey = Md5Util.md5Encode(origin, "utf-8");
			//update-end-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
            
			redisUtil.set(realKey, lowerCaseCode, 60);
			log.info("Get verification code，Redis key = {}，checkCode = {}", realKey, code);
			//返回前端
			String base64 = RandImageUtil.generate(code);
			res.setSuccess(true);
			res.setResult(base64);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			res.error500("Failed to obtain verification code, Please check redis configuration!");
			return res;
		}
		return res;
	}

	/**
	 * Switch menu table to vue 3 table
	 */
	@RequiresRoles({"admin"})
	@GetMapping(value = "/switchVue3Menu")
	public Result<String> switchVue3Menu(HttpServletResponse response) {
		Result<String> res = new Result<String>();
		sysPermissionService.switchVue3Menu();
		return res;
	}
	
	/**
	 * APP LOGIN
	 * @param sysLoginModel
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/mLogin", method = RequestMethod.POST)
	public Result<JSONObject> mLogin(@RequestBody SysLoginModel sysLoginModel) throws Exception {
		Result<JSONObject> result = new Result<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		JSONObject obj = new JSONObject();
		
		//update-begin-author:taoyan date:2022-11-7 for: issues/4109 平台用户登录失败锁定用户
		if(isLoginFailOvertimes(username)){
			return result.error500("This user has failed to log in too many times，Please log in again in 10 minutes！");
		}
		//update-end-author:taoyan date:2022-11-7 for: issues/4109 平台用户登录失败锁定用户
		//1. 校验用户是否有效
		SysUser sysUser = sysUserService.getUserByName(username);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}
		
		//2. 校验用户名或密码是否正确
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		if (!syspassword.equals(userpassword)) {
			//update-begin-author:taoyan date:2022-11-7 for: issues/4109 平台用户登录失败锁定用户
			addLoginFailOvertimes(username);
			//update-end-author:taoyan date:2022-11-7 for: issues/4109 平台用户登录失败锁定用户
			result.error500("WRONG USER NAME OR PASSWORD");
			return result;
		}
		
		//3.设置登录部门
		String orgCode = sysUser.getOrgCode();
		if(oConvertUtils.isEmpty(orgCode)) {
			//If the current user has no selected department View department related information
			List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
			//update-begin-author:taoyan date:20220117 for: JTC-1068【app】新建用户，没有设置部门及角色，点击登录提示暂未归属部，一直在登录页面 使用手机号登录 可正常
			if (departs == null || departs.size() == 0) {
				/*result.error500("用户暂未归属部门,不可登录!");
				return result;*/
			}else{
				orgCode = departs.get(0).getOrgCode();
				sysUser.setOrgCode(orgCode);
				this.sysUserService.updateUserDepart(username, orgCode,null);
			}
			//update-end-author:taoyan date:20220117 for: JTC-1068【app】新建用户，没有设置部门及角色，点击登录提示暂未归属部，一直在登录页面 使用手机号登录 可正常
		}

		//4. 设置登录租户
		Result<JSONObject> loginTenantError = sysUserService.setLoginTenant(sysUser, obj, username, result);
		if (loginTenantError != null) {
			return loginTenantError;
		}

		//5. 设置登录用户信息
		obj.put("userInfo", sysUser);
		
		//6. 生成token
		String token = JwtUtil.sign(username, syspassword);
		// 设置超时时间
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME*2 / 1000);

		//token 信息
		obj.put("token", token);
		result.setResult(obj);
		result.setSuccess(true);
		result.setCode(200);
		baseCommonService.addLog("USER NAME: " + username + ",LOGIN SUCCESSFUL[移动端]！", CommonConstant.LOG_TYPE_1, null);
		return result;
	}

	/**
	 * CAPTCHA
	 * @param sysLoginModel
	 * @return
	 */
	@RequestMapping(value = "/checkCaptcha", method = RequestMethod.POST)
	public Result<?> checkCaptcha(@RequestBody SysLoginModel sysLoginModel){
		String captcha = sysLoginModel.getCaptcha();
		String checkKey = sysLoginModel.getCheckKey();
		if(captcha==null){
			return Result.error("Verification code is invalid");
		}
		String lowerCaseCaptcha = captcha.toLowerCase();
		String realKey = Md5Util.md5Encode(lowerCaseCaptcha+checkKey, "utf-8");
		Object checkCode = redisUtil.get(realKey);
		if(checkCode==null || !checkCode.equals(lowerCaseCaptcha)) {
			return Result.error("Verification code error");
		}
		return Result.ok();
	}
	/**
	 * Login QR code
	 */
	@ApiOperation(value = "Login QR code", notes = "Login QR code")
	@GetMapping("/getLoginQrcode")
	public Result<?>  getLoginQrcode() {
		String qrcodeId = CommonConstant.LOGIN_QRCODE_PRE+IdWorker.getIdStr();
		//Define QR code parameters
		Map params = new HashMap(5);
		params.put("qrcodeId", qrcodeId);
		//The unique identification of stored QR code is valid for 30 seconds.
		redisUtil.set(CommonConstant.LOGIN_QRCODE + qrcodeId, qrcodeId, 30);
		return Result.OK(params);
	}
	/**
	 * Scan the QR code
	 */
	@ApiOperation(value = "Scan the QR code to log in to the QR code", notes = "Scan the QR code to log in to the QR code")
	@PostMapping("/scanLoginQrcode")
	public Result<?> scanLoginQrcode(@RequestParam String qrcodeId, @RequestParam String token) {
		Object check = redisUtil.get(CommonConstant.LOGIN_QRCODE + qrcodeId);
		if (oConvertUtils.isNotEmpty(check)) {
			//存放token给前台读取
			redisUtil.set(CommonConstant.LOGIN_QRCODE_TOKEN+qrcodeId, token, 60);
		} else {
			return Result.error("The QR code has expired, please refresh and try again");
		}
		return Result.OK("Scan code successfully");
	}


	/**
	 * Get the token saved after the user scans the code
	 */
	@ApiOperation(value = "Get the token saved after the user scans the code", notes = "Get the token saved after the user scans the code")
	@GetMapping("/getQrcodeToken")
	public Result getQrcodeToken(@RequestParam String qrcodeId) {
		Object token = redisUtil.get(CommonConstant.LOGIN_QRCODE_TOKEN + qrcodeId);
		Map result = new HashMap(5);
		Object qrcodeIdExpire = redisUtil.get(CommonConstant.LOGIN_QRCODE + qrcodeId);
		if (oConvertUtils.isEmpty(qrcodeIdExpire)) {
			//QR code expiration notification front desk refresh
			result.put("token", "-2");
			return Result.OK(result);
		}
		if (oConvertUtils.isNotEmpty(token)) {
			result.put("success", true);
			result.put("token", token);
		} else {
			result.put("token", "-1");
		}
		return Result.OK(result);
	}

	/**
	 * The number of login failures exceeded 5 RETURN TRUE
	 * @param username
	 * @return
	 */
	private boolean isLoginFailOvertimes(String username){
		String key = CommonConstant.LOGIN_FAIL + username;
		Object failTime = redisUtil.get(key);
		if(failTime!=null){
			Integer val = Integer.parseInt(failTime.toString());
			if(val>5){
				return true;
			}
		}
		return false;
	}

	/**
	 * Record the number of failed login attempts
	 * @param username
	 */
	private void addLoginFailOvertimes(String username){
		String key = CommonConstant.LOGIN_FAIL + username;
		Object failTime = redisUtil.get(key);
		Integer val = 0;
		if(failTime!=null){
			val = Integer.parseInt(failTime.toString());
		}
		// 10 minutes
		redisUtil.set(key, ++val, 10);
	}

}