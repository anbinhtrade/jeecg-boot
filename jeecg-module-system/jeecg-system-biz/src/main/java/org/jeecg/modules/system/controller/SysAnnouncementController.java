package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jeecg.dingtalk.api.core.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.CommonSendStatus;
import org.jeecg.common.constant.WebsocketConst;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.mybatis.MybatisPlusSaasConfig;
import org.jeecg.modules.message.enums.RangeDateEnum;
import org.jeecg.modules.message.websocket.WebSocket;
import org.jeecg.modules.system.entity.SysAnnouncement;
import org.jeecg.modules.system.entity.SysAnnouncementSend;
import org.jeecg.modules.system.service.ISysAnnouncementSendService;
import org.jeecg.modules.system.service.ISysAnnouncementService;
import org.jeecg.modules.system.service.impl.SysBaseApiImpl;
import org.jeecg.modules.system.service.impl.ThirdAppDingtalkServiceImpl;
import org.jeecg.modules.system.service.impl.ThirdAppWechatEnterpriseServiceImpl;
import org.jeecg.modules.system.util.XssUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.jeecg.common.constant.CommonConstant.ANNOUNCEMENT_SEND_STATUS_1;

/**
 * @Title: Controller
 * @Description: System notification table
 * @Author: jeecg-boot
 * @Date: 2019-01-02
 * @Version: V1.0
 */
@RestController
@RequestMapping("/sys/annountCement")
@Slf4j
public class SysAnnouncementController {
	@Autowired
	private ISysAnnouncementService sysAnnouncementService;
	@Autowired
	private ISysAnnouncementSendService sysAnnouncementSendService;
	@Resource
    private WebSocket webSocket;
	@Autowired
    ThirdAppWechatEnterpriseServiceImpl wechatEnterpriseService;
	@Autowired
    ThirdAppDingtalkServiceImpl dingtalkService;
	@Autowired
	private SysBaseApiImpl sysBaseApi;
	@Autowired
	@Lazy
	private RedisUtil redisUtil;

	/**
	 * QQYUN-5072[Performance Optimization] Online notification messages are a bit slow to open
	 */
	public static ExecutorService cachedThreadPool = new ThreadPoolExecutor(0, 1024,60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	public static ExecutorService completeNoteThreadPool = new ThreadPoolExecutor(0, 1024,60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

	/**
	  * Paginated list query
	 * @param sysAnnouncement
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysAnnouncement>> queryPageList(SysAnnouncement sysAnnouncement,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		//------------------------------------------------------------------------------------------------
		//Whether to enable multi-tenant data isolation of the system management module [SAAS multi-tenant mode]
		if(MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL){
			sysAnnouncement.setTenantId(oConvertUtils.getInt(TenantContext.getTenant(), 0));
		}
		//------------------------------------------------------------------------------------------------
		Result<IPage<SysAnnouncement>> result = new Result<IPage<SysAnnouncement>>();
		sysAnnouncement.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
		QueryWrapper<SysAnnouncement> queryWrapper = QueryGenerator.initQueryWrapper(sysAnnouncement, req.getParameterMap());
		Page<SysAnnouncement> page = new Page<SysAnnouncement>(pageNo,pageSize);
		IPage<SysAnnouncement> pageList = sysAnnouncementService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	  *   Add to
	 * @param sysAnnouncement
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Result<SysAnnouncement> add(@RequestBody SysAnnouncement sysAnnouncement) {
		Result<SysAnnouncement> result = new Result<SysAnnouncement>();
		try {
			// update-begin-author:liusq date:20210804 for:标题处理xss攻击的问题
			String title = XssUtils.scriptXss(sysAnnouncement.getTitile());
			sysAnnouncement.setTitile(title);
			// update-end-author:liusq date:20210804 for:标题处理xss攻击的问题
			sysAnnouncement.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
            //未发布
			sysAnnouncement.setSendStatus(CommonSendStatus.UNPUBLISHED_STATUS_0);
			sysAnnouncementService.saveAnnouncement(sysAnnouncement);
			result.success("Added successfully！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("Operation failed");
		}
		return result;
	}

	/**
	 * Edit
	 * @param sysAnnouncement
	 * @return
	 */
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<SysAnnouncement> eidt(@RequestBody SysAnnouncement sysAnnouncement) {
		Result<SysAnnouncement> result = new Result<SysAnnouncement>();
		SysAnnouncement sysAnnouncementEntity = sysAnnouncementService.getById(sysAnnouncement.getId());
		if(sysAnnouncementEntity==null) {
			result.error500("No corresponding entity found");
		}else {
			// update-begin-author:liusq date:20210804 for:标题处理xss攻击的问题
			String title = XssUtils.scriptXss(sysAnnouncement.getTitile());
			sysAnnouncement.setTitile(title);
			// update-end-author:liusq date:20210804 for:标题处理xss攻击的问题
			boolean ok = sysAnnouncementService.upDateAnnouncement(sysAnnouncement);
			//TODO returns false？
			if(ok) {
				result.success("The modification was successful!");
			}
		}

		return result;
	}

	/**
	  *   Delete by ID
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<SysAnnouncement> delete(@RequestParam(name="id",required=true) String id) {
		Result<SysAnnouncement> result = new Result<SysAnnouncement>();
		SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(id);
		if(sysAnnouncement==null) {
			result.error500("No corresponding entity found");
		}else {
			sysAnnouncement.setDelFlag(CommonConstant.DEL_FLAG_1.toString());
			boolean ok = sysAnnouncementService.updateById(sysAnnouncement);
			if(ok) {
				result.success("The deletion is successful!");
			}
		}

		return result;
	}

	/**
	  *  Delete in bulk
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public Result<SysAnnouncement> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SysAnnouncement> result = new Result<SysAnnouncement>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("The parameter is not recognized！");
		}else {
			String[] id = ids.split(",");
			for(int i=0;i<id.length;i++) {
				SysAnnouncement announcement = sysAnnouncementService.getById(id[i]);
				announcement.setDelFlag(CommonConstant.DEL_FLAG_1.toString());
				sysAnnouncementService.updateById(announcement);
			}
			result.success("The deletion is successful!");
		}
		return result;
	}

	/**
	  * Query by ID
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryById", method = RequestMethod.GET)
	public Result<SysAnnouncement> queryById(@RequestParam(name="id",required=true) String id) {
		Result<SysAnnouncement> result = new Result<SysAnnouncement>();
		SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(id);
		if(sysAnnouncement==null) {
			result.error500("No corresponding entity found");
		}else {
			result.setResult(sysAnnouncement);
			result.setSuccess(true);
		}
		return result;
	}

	/**
	 *	 Update the publish action
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/doReleaseData", method = RequestMethod.GET)
	public Result<SysAnnouncement> doReleaseData(@RequestParam(name="id",required=true) String id, HttpServletRequest request) {
		Result<SysAnnouncement> result = new Result<SysAnnouncement>();
		SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(id);
		if(sysAnnouncement==null) {
			result.error500("No corresponding entity found");
		}else {
            //发布中
			sysAnnouncement.setSendStatus(CommonSendStatus.PUBLISHED_STATUS_1);
			sysAnnouncement.setSendTime(new Date());
			String currentUserName = JwtUtil.getUserNameByToken(request);
			sysAnnouncement.setSender(currentUserName);
			boolean ok = sysAnnouncementService.updateById(sysAnnouncement);
			if(ok) {
				result.success("The system notifies that the push is successful");
				if(sysAnnouncement.getMsgType().equals(CommonConstant.MSG_TYPE_ALL)) {
					// Complete the previous relationship between the announcement and the user
					sysAnnouncementService.batchInsertSysAnnouncementSend(sysAnnouncement.getId(), sysAnnouncement.getTenantId());
					
					// Push websocket notifications
					JSONObject obj = new JSONObject();
			    	obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
					obj.put(WebsocketConst.MSG_ID, sysAnnouncement.getId());
					obj.put(WebsocketConst.MSG_TXT, sysAnnouncement.getTitile());
			    	webSocket.sendMessage(obj.toJSONString());
				}else {
					// 2.Insert a User Advertisement Read Tag table record
					String userId = sysAnnouncement.getUserIds();
					String[] userIds = userId.substring(0, (userId.length()-1)).split(",");
					String anntId = sysAnnouncement.getId();
					Date refDate = new Date();
					JSONObject obj = new JSONObject();
			    	obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
					obj.put(WebsocketConst.MSG_ID, sysAnnouncement.getId());
					obj.put(WebsocketConst.MSG_TXT, sysAnnouncement.getTitile());
			    	webSocket.sendMessage(userIds, obj.toJSONString());
				}
				try {
					// Synchronize WeCom and DingTalk message notifications
					Response<String> dtResponse = dingtalkService.sendActionCardMessage(sysAnnouncement, null, true);
					wechatEnterpriseService.sendTextCardMessage(sysAnnouncement, true);

					if (dtResponse != null && dtResponse.isSuccess()) {
						String taskId = dtResponse.getResult();
						sysAnnouncement.setDtTaskId(taskId);
						sysAnnouncementService.updateById(sysAnnouncement);
					}
				} catch (Exception e) {
					log.error("Failed to send third-party APP messages synchronously：", e);
				}
			}
		}

		return result;
	}

	/**
	 *	 Update the undo action
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/doReovkeData", method = RequestMethod.GET)
	public Result<SysAnnouncement> doReovkeData(@RequestParam(name="id",required=true) String id, HttpServletRequest request) {
		Result<SysAnnouncement> result = new Result<SysAnnouncement>();
		SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(id);
		if(sysAnnouncement==null) {
			result.error500("No corresponding entity found");
		}else {
            //撤销发布
			sysAnnouncement.setSendStatus(CommonSendStatus.REVOKE_STATUS_2);
			sysAnnouncement.setCancelTime(new Date());
			boolean ok = sysAnnouncementService.updateById(sysAnnouncement);
			if(ok) {
				result.success("The system notifies that the revocation is successful");
				if (oConvertUtils.isNotEmpty(sysAnnouncement.getDtTaskId())) {
					try {
						dingtalkService.recallMessage(sysAnnouncement.getDtTaskId());
					} catch (Exception e) {
						log.error("The third-party app failed to withdraw the message：", e);
					}
				}
			}
		}

		return result;
	}

	/**
	 * @Function: Supplement user data and return system messages
	 * @return
	 */
	@RequestMapping(value = "/listByUser", method = RequestMethod.GET)
	public Result<Map<String, Object>> listByUser(@RequestParam(required = false, defaultValue = "5") Integer pageSize) {
		Result<Map<String,Object>> result = new Result<Map<String,Object>>();
		Map<String,Object> sysMsgMap = new HashMap(5);
		LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		String userId = sysUser.getId();
		
//		//Supplementary push data (the relationship table between users and notifications.)）
//		completeNoteThreadPool.execute(()->{
//			sysAnnouncementService.completeAnnouncementSendInfo();
//		});
		
		// 2.Query system messages that are not read by the user
		Page<SysAnnouncement> anntMsgList = new Page<SysAnnouncement>(0, pageSize);
        //通知公告消息
		anntMsgList = sysAnnouncementService.querySysCementPageByUserId(anntMsgList,userId,"1");
		sysMsgMap.put("anntMsgList", anntMsgList.getRecords());
		sysMsgMap.put("anntMsgTotal", anntMsgList.getTotal());
		
        //系统消息
		Page<SysAnnouncement> sysMsgList = new Page<SysAnnouncement>(0, pageSize);
		sysMsgList = sysAnnouncementService.querySysCementPageByUserId(sysMsgList,userId,"2");
		sysMsgMap.put("sysMsgList", sysMsgList.getRecords());
		sysMsgMap.put("sysMsgTotal", sysMsgList.getTotal());
		
		result.setSuccess(true);
		result.setResult(sysMsgMap);
		return result;
	}


    /**
     * Export to Excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysAnnouncement sysAnnouncement,HttpServletRequest request) {
        // Step.1 组装查询条件
        LambdaQueryWrapper<SysAnnouncement> queryWrapper = new LambdaQueryWrapper<SysAnnouncement>(sysAnnouncement);
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		queryWrapper.eq(SysAnnouncement::getDelFlag,CommonConstant.DEL_FLAG_0.toString());
        List<SysAnnouncement> pageList = sysAnnouncementService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "List of system announcements");
        mv.addObject(NormalExcelConstants.CLASS, SysAnnouncement.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("System advertisement list data", "Exporter:"+user.getRealname(), "Export information"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * Import data via Excel
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // Obtain the object to which the file was uploaded
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<SysAnnouncement> listSysAnnouncements = ExcelImportUtil.importExcel(file.getInputStream(), SysAnnouncement.class, params);
                for (SysAnnouncement sysAnnouncementExcel : listSysAnnouncements) {
                	if(sysAnnouncementExcel.getDelFlag()==null){
                		sysAnnouncementExcel.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
					}
                    sysAnnouncementService.save(sysAnnouncementExcel);
                }
                return Result.ok("The file was imported successfully！Number of rows of data：" + listSysAnnouncements.size());
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                return Result.error("File import failed！");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("File import failed！");
    }
	/**
	 * Synchronize messages
	 * @param anntId
	 * @return
	 */
	@RequestMapping(value = "/syncNotic", method = RequestMethod.GET)
	public Result<SysAnnouncement> syncNotic(@RequestParam(name="anntId",required=false) String anntId, HttpServletRequest request) {
		Result<SysAnnouncement> result = new Result<SysAnnouncement>();
		JSONObject obj = new JSONObject();
		if(StringUtils.isNotBlank(anntId)){
			SysAnnouncement sysAnnouncement = sysAnnouncementService.getById(anntId);
			if(sysAnnouncement==null) {
				result.error500("No corresponding entity found");
			}else {
				if(sysAnnouncement.getMsgType().equals(CommonConstant.MSG_TYPE_ALL)) {
					obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
					obj.put(WebsocketConst.MSG_ID, sysAnnouncement.getId());
					obj.put(WebsocketConst.MSG_TXT, sysAnnouncement.getTitile());
					webSocket.sendMessage(obj.toJSONString());
				}else {
					// 2.Insert a User Advertisement Read Tag table record
					String userId = sysAnnouncement.getUserIds();
					if(oConvertUtils.isNotEmpty(userId)){
						String[] userIds = userId.substring(0, (userId.length()-1)).split(",");
						obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
						obj.put(WebsocketConst.MSG_ID, sysAnnouncement.getId());
						obj.put(WebsocketConst.MSG_TXT, sysAnnouncement.getTitile());
						webSocket.sendMessage(userIds, obj.toJSONString());
					}
				}
			}
		}else{
			obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
			obj.put(WebsocketConst.MSG_TXT, "Set read in bulk");
			webSocket.sendMessage(obj.toJSONString());
		}
		return result;
	}

	/**
	 * Notice view detail page (for third-party apps)
	 * @param modelAndView
	 * @param id
	 * @return
	 */
    @GetMapping("/show/{id}")
    public ModelAndView showContent(ModelAndView modelAndView, @PathVariable("id") String id, HttpServletRequest request) {
        SysAnnouncement announcement = sysAnnouncementService.getById(id);
        if (announcement != null) {
            boolean tokenOk = false;
            try {
                // Verify the validity of the token
                tokenOk = TokenUtils.verifyToken(request, sysBaseApi, redisUtil);
            } catch (Exception ignored) {
            }
            // Determine whether the token is passed and the token is valid, if it is, it will not be restricted and will be returned directly
            // If the token is invalid, you can only view the published token
            if (tokenOk || ANNOUNCEMENT_SEND_STATUS_1.equals(announcement.getSendStatus())) {
                modelAndView.addObject("data", announcement);
                modelAndView.setViewName("announcement/showContent");
                return modelAndView;
            }
        }
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

	/**
	 * vue3 uses the message list query
	 * @param fromUser
	 * @param beginDate
	 * @param endDate
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/vue3List", method = RequestMethod.GET)
	public Result<List<SysAnnouncement>> vue3List(@RequestParam(name="fromUser", required = false) String fromUser,
												  @RequestParam(name="starFlag", required = false) String starFlag,
                                                  @RequestParam(name="rangeDateKey", required = false) String rangeDateKey,
                                                  @RequestParam(name="beginDate", required = false) String beginDate, 
												  @RequestParam(name="endDate", required = false) String endDate,
												  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo, 
												  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		long calStartTime = System.currentTimeMillis(); // Record the start time
		
		// 1、Get the date query criteria, start time, and end time
		Date beginTime = null, endTime = null;
		if (RangeDateEnum.ZDY.getKey().equals(rangeDateKey)) {
			// Custom date range queries
			if (oConvertUtils.isNotEmpty(beginDate)) {
				beginTime = DateUtils.parseDatetime(beginDate);
			}
			if (oConvertUtils.isNotEmpty(endDate)) {
				endTime = DateUtils.parseDatetime(endDate);
			}
		} else {
			// Date Paragraph Query
			Date[] arr = RangeDateEnum.getRangeArray(rangeDateKey);
			if (arr != null) {
				beginTime = arr[0];
				endTime = arr[1];	
			}
		}
		
		// 2、Query the notification messages of users based on the conditions
		List<SysAnnouncement> ls = this.sysAnnouncementService.querySysMessageList(pageSize, pageNo, fromUser, starFlag, beginTime, endTime);

		// 3、Set the message on the current page to read
		if (!CollectionUtils.isEmpty(ls)) {
			// 设置已读
			String readed = "1";
			List<String> annoceIdList = ls.stream().filter(item -> !readed.equals(item.getReadFlag())).map(item -> item.getId()).collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(annoceIdList)) {
				cachedThreadPool.execute(() -> {
					sysAnnouncementService.updateReaded(annoceIdList);
				});
			}
		}
		
		JSONObject obj = new JSONObject();
		obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		webSocket.sendMessage(sysUser.getId(), obj.toJSONString());

		// 4、性能统计耗时
		long calEndTime = System.currentTimeMillis(); // 记录结束时间
		long duration = calEndTime - calStartTime; // 计算耗时
		System.out.println("Take：" + duration + " Millisecond");
		
		return Result.ok(ls);
	}


    /**
     * Obtain the latest message sent time based on user ID (creation time)
     * @param userId
     * @return
     */
	@GetMapping("/getLastAnnountTime")
	public Result<Page<SysAnnouncementSend>> getLastAnnountTime(@RequestParam(name = "userId") String userId){
        Result<Page<SysAnnouncementSend>> result = new Result<>();
        Page<SysAnnouncementSend> page = new Page<>(1,1);
        LambdaQueryWrapper<SysAnnouncementSend> query = new LambdaQueryWrapper<>();
        query.eq(SysAnnouncementSend::getUserId,userId);
        query.select(SysAnnouncementSend::getCreateTime);
        query.orderByDesc(SysAnnouncementSend::getCreateTime);
        Page<SysAnnouncementSend> pageList = sysAnnouncementSendService.page(page, query);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

	/**
	 * Clear all unread messages for the current user
	 * @return
	 */
	@PostMapping("/clearAllUnReadMessage")
    public Result<String> clearAllUnReadMessage(){
		sysAnnouncementService.clearAllUnReadMessage();
		return Result.ok("The unread messages were cleared successfully");
	}
}
