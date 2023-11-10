package org.jeecg.modules.system.controller;

import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.DataBaseConstant;
import org.jeecg.common.constant.WebsocketConst;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SqlInjectionUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.message.websocket.WebSocket;
import org.jeecg.modules.system.entity.SysAnnouncementSend;
import org.jeecg.modules.system.model.AnnouncementSendModel;
import org.jeecg.modules.system.service.ISysAnnouncementSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

 /**
 * @Title: Controller
 * @Description: User Advertisement Read Tag Table
 * @Author: jeecg-boot
 * @Date:  2019-02-21
 * @Version: V1.0
 */
@RestController
@RequestMapping("/sys/sysAnnouncementSend")
@Slf4j
public class SysAnnouncementSendController {
	@Autowired
	private ISysAnnouncementSendService sysAnnouncementSendService;
	@Autowired
	private WebSocket webSocket;

	/**
	  * Paginated list queries
	 * @param sysAnnouncementSend
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public Result<IPage<SysAnnouncementSend>> queryPageList(SysAnnouncementSend sysAnnouncementSend,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<SysAnnouncementSend>> result = new Result<IPage<SysAnnouncementSend>>();
		QueryWrapper<SysAnnouncementSend> queryWrapper = new QueryWrapper<SysAnnouncementSend>(sysAnnouncementSend);
		Page<SysAnnouncementSend> page = new Page<SysAnnouncementSend>(pageNo,pageSize);
		//排序逻辑 处理
		String column = req.getParameter("column");
		String order = req.getParameter("order");

		if(oConvertUtils.isNotEmpty(column) && oConvertUtils.isNotEmpty(order)) {
			if(DataBaseConstant.SQL_ASC.equals(order)) {
				queryWrapper.orderByAsc(SqlInjectionUtil.getSqlInjectSortField(column));
			}else {
				queryWrapper.orderByDesc(SqlInjectionUtil.getSqlInjectSortField(column));
			}
		}
		IPage<SysAnnouncementSend> pageList = sysAnnouncementSendService.page(page, queryWrapper);
		//log.info("Query the current page："+pageList.getCurrent());
		//log.info("Query the number of current pages："+pageList.getSize());
		//log.info("The number of query results："+pageList.getRecords().size());
		//log.info("Total number of data："+pageList.getTotal());
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   Add to
	 * @param sysAnnouncementSend
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<SysAnnouncementSend> add(@RequestBody SysAnnouncementSend sysAnnouncementSend) {
		Result<SysAnnouncementSend> result = new Result<SysAnnouncementSend>();
		try {
			sysAnnouncementSendService.save(sysAnnouncementSend);
			result.success("The addition was successful！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("The operation failed");
		}
		return result;
	}
	
	/**
	  *  Edit
	 * @param sysAnnouncementSend
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<SysAnnouncementSend> eidt(@RequestBody SysAnnouncementSend sysAnnouncementSend) {
		Result<SysAnnouncementSend> result = new Result<SysAnnouncementSend>();
		SysAnnouncementSend sysAnnouncementSendEntity = sysAnnouncementSendService.getById(sysAnnouncementSend.getId());
		if(sysAnnouncementSendEntity==null) {
			result.error500("No corresponding entity found");
		}else {
			boolean ok = sysAnnouncementSendService.updateById(sysAnnouncementSend);
			//TODO What does returning false mean？
			if(ok) {
				result.success("The operation was successful!");
			}
		}
		
		return result;
	}
	
	/**
	  *   Delete by ID
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<SysAnnouncementSend> delete(@RequestParam(name="id",required=true) String id) {
		Result<SysAnnouncementSend> result = new Result<SysAnnouncementSend>();
		SysAnnouncementSend sysAnnouncementSend = sysAnnouncementSendService.getById(id);
		if(sysAnnouncementSend==null) {
			result.error500("No corresponding entity found");
		}else {
			boolean ok = sysAnnouncementSendService.removeById(id);
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
	@DeleteMapping(value = "/deleteBatch")
	public Result<SysAnnouncementSend> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SysAnnouncementSend> result = new Result<SysAnnouncementSend>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("The parameter is not recognized！");
		}else {
			this.sysAnnouncementSendService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("The deletion is successful!");
		}
		return result;
	}
	
	/**
	  * Query by ID
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result<SysAnnouncementSend> queryById(@RequestParam(name="id",required=true) String id) {
		Result<SysAnnouncementSend> result = new Result<SysAnnouncementSend>();
		SysAnnouncementSend sysAnnouncementSend = sysAnnouncementSendService.getById(id);
		if(sysAnnouncementSend==null) {
			result.error500("No corresponding entity found");
		}else {
			result.setResult(sysAnnouncementSend);
			result.setSuccess(true);
		}
		return result;
	}
	
	/**
	 * @Function：Update the reading status of the user's system messages
	 * @param json
	 * @return
	 */
	@PutMapping(value = "/editByAnntIdAndUserId")
	public Result<SysAnnouncementSend> editById(@RequestBody JSONObject json) {
		Result<SysAnnouncementSend> result = new Result<SysAnnouncementSend>();
		String anntId = json.getString("anntId");
		LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		String userId = sysUser.getId();
		LambdaUpdateWrapper<SysAnnouncementSend> updateWrapper = new UpdateWrapper().lambda();
		updateWrapper.set(SysAnnouncementSend::getReadFlag, CommonConstant.HAS_READ_FLAG);
		updateWrapper.set(SysAnnouncementSend::getReadTime, new Date());
		//update-begin-author:liusq date:2023-09-04 for:系统模块存在的sql漏洞写法
		updateWrapper.eq(SysAnnouncementSend::getAnntId,anntId);
		updateWrapper.eq(SysAnnouncementSend::getUserId,userId);
		//update-end-author:liusq date:2023-09-04 for: How to write SQL vulnerabilities in system modules
		//updateWrapper.last("where annt_id ='"+anntId+"' and user_id ='"+userId+"'");
		SysAnnouncementSend announcementSend = new SysAnnouncementSend();
		sysAnnouncementSendService.update(announcementSend, updateWrapper);
		result.setSuccess(true);
		return result;
	}
	
	/**
	 * @Function: Get My Messages
	 * @return
	 */
	@GetMapping(value = "/getMyAnnouncementSend")
	public Result<IPage<AnnouncementSendModel>> getMyAnnouncementSend(AnnouncementSendModel announcementSendModel,
			@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
			  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Result<IPage<AnnouncementSendModel>> result = new Result<IPage<AnnouncementSendModel>>();
		LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		String userId = sysUser.getId();
		announcementSendModel.setUserId(userId);
		announcementSendModel.setPageNo((pageNo-1)*pageSize);
		announcementSendModel.setPageSize(pageSize);
		Page<AnnouncementSendModel> pageList = new Page<AnnouncementSendModel>(pageNo,pageSize);
		pageList = sysAnnouncementSendService.getMyAnnouncementSendPage(pageList, announcementSendModel);
		result.setResult(pageList);
		result.setSuccess(true);
		return result;
	}

	/**
	 * @Function: One-click read
	 * @return
	 */
	@PutMapping(value = "/readAll")
	public Result<SysAnnouncementSend> readAll() {
		Result<SysAnnouncementSend> result = new Result<SysAnnouncementSend>();
		LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		String userId = sysUser.getId();
		LambdaUpdateWrapper<SysAnnouncementSend> updateWrapper = new UpdateWrapper().lambda();
		updateWrapper.set(SysAnnouncementSend::getReadFlag, CommonConstant.HAS_READ_FLAG);
		updateWrapper.set(SysAnnouncementSend::getReadTime, new Date());
		updateWrapper.eq(SysAnnouncementSend::getUserId,userId);
		//updateWrapper.last("where user_id ='"+userId+"'");
		SysAnnouncementSend announcementSend = new SysAnnouncementSend();
		sysAnnouncementSendService.update(announcementSend, updateWrapper);
		JSONObject socketParams = new JSONObject();
		socketParams.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
		webSocket.sendMessage(socketParams.toJSONString());
		result.setSuccess(true);
		result.setMessage("All read");
		return result;
	}


	 /**
	  * Obtain the message content based on the ID of the message sending record
	  * @param sendId
	  * @return
	  */
	 @GetMapping(value = "/getOne")
	 public Result<AnnouncementSendModel> getOne(@RequestParam(name="sendId",required=true) String sendId) {
		 AnnouncementSendModel model = sysAnnouncementSendService.getOne(sendId);
		 return Result.ok(model);
	 }
}
