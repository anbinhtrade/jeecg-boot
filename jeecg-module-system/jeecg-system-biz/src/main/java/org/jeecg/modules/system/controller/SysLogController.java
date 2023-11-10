package org.jeecg.modules.system.controller;


import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysLog;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.service.ISysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Syslog table Front-end controllers
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
@RestController
@RequestMapping("/sys/log")
@Slf4j
public class SysLogController {
	
	@Autowired
	private ISysLogService sysLogService;

    /**
     * Clear them all
     */
	private static final String ALL_ClEAR = "allclear";

	/**
	 * @功能: Query logging
	 * @param syslog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysLog>> queryPageList(SysLog syslog,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		Result<IPage<SysLog>> result = new Result<IPage<SysLog>>();
		QueryWrapper<SysLog> queryWrapper = QueryGenerator.initQueryWrapper(syslog, req.getParameterMap());
		Page<SysLog> page = new Page<SysLog>(pageNo, pageSize);
		//日志关键词
		String keyWord = req.getParameter("keyWord");
		if(oConvertUtils.isNotEmpty(keyWord)) {
			queryWrapper.like("log_content",keyWord);
		}
		//TODO Filtering logic processing
		//TODO Begin and end logic processing
		//TODO A powerful feature is that the frontend passes a field string, and the backend only returns the fields corresponding to those strings
		//Creation time/Assignment of creator
		IPage<SysLog> pageList = sysLogService.page(page, queryWrapper);
		log.info("To query the current page:"+pageList.getCurrent());
		log.info("Query the current number of pages:"+pageList.getSize());
		log.info("Number of query results:"+pageList.getRecords().size());
		log.info("Total number of data:"+pageList.getTotal());
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	 * @功能: Deletes a single log record
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<SysLog> delete(@RequestParam(name="id",required=true) String id) {
		Result<SysLog> result = new Result<SysLog>();
		SysLog sysLog = sysLogService.getById(id);
		if(sysLog==null) {
			result.error500("No corresponding entity found");
		}else {
			boolean ok = sysLogService.removeById(id);
			if(ok) {
				result.success("Deleted successfully!");
			}
		}
		return result;
	}
	
	/**
	 * @Function Clear all log records in batches
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public Result<SysRole> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SysRole> result = new Result<SysRole>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("The parameter is not recognized!");
		}else {
			if(ALL_ClEAR.equals(ids)) {
				this.sysLogService.removeAll();
				result.success("Clear Success!");
			}
			this.sysLogService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("Deleted successfully!");
		}
		return result;
	}
	
	
}
