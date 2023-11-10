package org.jeecg.modules.system.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.system.entity.SysRoleIndex;
import org.jeecg.modules.system.service.ISysRoleIndexService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 角色首页配置
 * @Author: jeecg-boot
 * @Date: 2022-03-25
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "Character home page configuration")
@RestController
@RequestMapping("/sys/sysRoleIndex")
public class SysRoleIndexController extends JeecgController<SysRoleIndex, ISysRoleIndexService> {
    @Autowired
    private ISysRoleIndexService sysRoleIndexService;

    /**
     * Paginated list queries
     *
     * @param sysRoleIndex
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "Role Home Page Configuration - Paginated List Query")
    @ApiOperation(value = "Role Home Page Configuration - Paginated List Query", notes = "Role Home Page Configuration - Paginated List Query")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(SysRoleIndex sysRoleIndex,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<SysRoleIndex> queryWrapper = QueryGenerator.initQueryWrapper(sysRoleIndex, req.getParameterMap());
        Page<SysRoleIndex> page = new Page<SysRoleIndex>(pageNo, pageSize);
        IPage<SysRoleIndex> pageList = sysRoleIndexService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * Add to
     *
     * @param sysRoleIndex
     * @return
     */
    @RequiresPermissions("system:roleindex:add")
    @AutoLog(value = "Role Home Configuration - Add")
    @ApiOperation(value = "Role Home Configuration - Add", notes = "Role Home Configuration - Add")
    @PostMapping(value = "/add")
    //@DynamicTable(value = DynamicTableConstant.SYS_ROLE_INDEX)
    public Result<?> add(@RequestBody SysRoleIndex sysRoleIndex,HttpServletRequest request) {
        sysRoleIndexService.save(sysRoleIndex);
        return Result.OK("Added successfully!");
    }

    /**
     * EDIT
     *
     * @param sysRoleIndex
     * @return
     */
    @RequiresPermissions("system:roleindex:edit")
    @AutoLog(value = "Role Home Configuration - Edit")
    @ApiOperation(value = "Role Home Configuration - Edit", notes = "Role Home Configuration - Edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    //@DynamicTable(value = DynamicTableConstant.SYS_ROLE_INDEX)
    public Result<?> edit(@RequestBody SysRoleIndex sysRoleIndex,HttpServletRequest request) {
        sysRoleIndexService.updateById(sysRoleIndex);
        return Result.OK("Edit successful!");
    }

    /**
     * Delete by ID
     *
     * @param id
     * @return
     */
    @AutoLog(value = "Role Home Configuration - Delete by ID")
    @ApiOperation(value = "Role Home Configuration - Delete by ID", notes = "Role Home Configuration - Delete by ID")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        sysRoleIndexService.removeById(id);
        return Result.OK("Deleted successfully!");
    }

    /**
     * Delete in bulk
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "Configure the role home page - delete it in batches")
    @ApiOperation(value = "Configure the role home page - delete it in batches", notes = "Configure the role home page - delete it in batches")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.sysRoleIndexService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("Batch deletion successful!");
    }

    /**
     * Query by ID
     *
     * @param id
     * @return
     */
    @AutoLog(value = "Character Home Configuration - Query by ID")
    @ApiOperation(value = "Character Home Configuration - Query by ID", notes = "Character Home Configuration - Query by ID")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        SysRoleIndex sysRoleIndex = sysRoleIndexService.getById(id);
        return Result.OK(sysRoleIndex);
    }

    /**
     * Export to Excel
     *
     * @param request
     * @param sysRoleIndex
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysRoleIndex sysRoleIndex) {
        return super.exportXls(request, sysRoleIndex, SysRoleIndex.class, "Character home page configuration");
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
        return super.importExcel(request, response, SysRoleIndex.class);
    }

    /**
     * Query by code
     *
     * @param roleCode
     * @return
     */
    @AutoLog(value = "Character Home Configuration - Query by code")
    @ApiOperation(value = "Character Home Configuration - Query by code", notes = "Character Home Configuration - Query by code")
    @GetMapping(value = "/queryByCode")
    //@DynamicTable(value = DynamicTableConstant.SYS_ROLE_INDEX)
    public Result<?> queryByCode(@RequestParam(name = "roleCode", required = true) String roleCode,HttpServletRequest request) {
        SysRoleIndex sysRoleIndex = sysRoleIndexService.getOne(new LambdaQueryWrapper<SysRoleIndex>().eq(SysRoleIndex::getRoleCode, roleCode));
        return Result.OK(sysRoleIndex);
    }
}
