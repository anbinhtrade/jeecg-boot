package org.jeecg.modules.system.controller;


import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.service.ISysDictItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.RequestBody;
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
 *  Front-end controllers
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@Api(tags = "Data Dictionary")
@RestController
@RequestMapping("/sys/dictItem")
@Slf4j
public class SysDictItemController {

	@Autowired
	private ISysDictItemService sysDictItemService;
	
	/**
	 * @Function: Query dictionary data
	 * @param sysDictItem
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysDictItem>> queryPageList(SysDictItem sysDictItem,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		Result<IPage<SysDictItem>> result = new Result<IPage<SysDictItem>>();
		QueryWrapper<SysDictItem> queryWrapper = QueryGenerator.initQueryWrapper(sysDictItem, req.getParameterMap());
		queryWrapper.orderByAsc("sort_order");
		Page<SysDictItem> page = new Page<SysDictItem>(pageNo, pageSize);
		IPage<SysDictItem> pageList = sysDictItemService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	 * @Feature: New
	 * @return
	 */
    @RequiresPermissions("system:dict:item:add")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@CacheEvict(value= {CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries=true)
	public Result<SysDictItem> add(@RequestBody SysDictItem sysDictItem) {
		Result<SysDictItem> result = new Result<SysDictItem>();
		try {
			sysDictItem.setCreateTime(new Date());
			sysDictItemService.save(sysDictItem);
			result.success("The save was successful！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("The operation failed");
		}
		return result;
	}
	
	/**
	 * @Function: Editing
	 * @param sysDictItem
	 * @return
	 */
    @RequiresPermissions("system:dict:item:edit")
	@RequestMapping(value = "/edit",  method = { RequestMethod.PUT,RequestMethod.POST })
	@CacheEvict(value={CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries=true)
	public Result<SysDictItem> edit(@RequestBody SysDictItem sysDictItem) {
		Result<SysDictItem> result = new Result<SysDictItem>();
		SysDictItem sysdict = sysDictItemService.getById(sysDictItem.getId());
		if(sysdict==null) {
			result.error500("No corresponding entity found");
		}else {
			sysDictItem.setUpdateTime(new Date());
			boolean ok = sysDictItemService.updateById(sysDictItem);
			//TODO returns false？
			if(ok) {
				result.success("Edited successfully!");
			}
		}
		return result;
	}
	
	/**
	 * @FUNCTION：Delete dictionary data
	 * @param id
	 * @return
	 */
    @RequiresPermissions("system:dict:item:delete")
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@CacheEvict(value={CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries=true)
	public Result<SysDictItem> delete(@RequestParam(name="id",required=true) String id) {
		Result<SysDictItem> result = new Result<SysDictItem>();
		SysDictItem joinSystem = sysDictItemService.getById(id);
		if(joinSystem==null) {
			result.error500("No corresponding entity found");
		}else {
			boolean ok = sysDictItemService.removeById(id);
			if(ok) {
				result.success("The deletion is successful!");
			}
		}
		return result;
	}
	
	/**
	 * @Function: Delete dictionary data in batches
	 * @param ids
	 * @return
	 */
    @RequiresPermissions("system:dict:item:deleteBatch")
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	@CacheEvict(value={CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries=true)
	public Result<SysDictItem> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SysDictItem> result = new Result<SysDictItem>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("The parameter is not recognized！");
		}else {
			this.sysDictItemService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("The deletion is successful!");
		}
		return result;
	}

	/**
	 * Dictionary value is doubled
	 * @param sysDictItem
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/dictItemCheck", method = RequestMethod.GET)
	@ApiOperation("Dictionary duplicate verification interface")
	public Result<Object> doDictItemCheck(SysDictItem sysDictItem, HttpServletRequest request) {
		Long num = Long.valueOf(0);
		LambdaQueryWrapper<SysDictItem> queryWrapper = new LambdaQueryWrapper<SysDictItem>();
		queryWrapper.eq(SysDictItem::getItemValue,sysDictItem.getItemValue());
		queryWrapper.eq(SysDictItem::getDictId,sysDictItem.getDictId());
		if (StringUtils.isNotBlank(sysDictItem.getId())) {
			// Edit the page validation
			queryWrapper.ne(SysDictItem::getId,sysDictItem.getId());
		}
		num = sysDictItemService.count(queryWrapper);
		if (num == 0) {
			// 该值可用
			return Result.ok("This value is available！");
		} else {
			// 该值不可用
			log.info("The value is not available and already exists in the system！");
			return Result.error("The value is not available and already exists in the system！");
		}
	}
	
}
