package org.jeecg.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.system.entity.SysFiles;
import org.jeecg.modules.system.service.ISysFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: Knowledge Base - Document Management
 * @Author: jeecg-boot
 * @Date: 2022-07-21
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "Knowledge Base - Document Management")
@RestController
@RequestMapping("/sys/files")
public class SysFilesController extends JeecgController<SysFiles, ISysFilesService> {
    @Autowired
    private ISysFilesService sysFilesService;

    /**
     * Paginated list queries
     *
     * @param sysFiles
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "Knowledge Base - Document Management - Paginated List Query")
    @ApiOperation(value = "Knowledge Base - Document Management - Paginated List Query", notes = "Knowledge Base - Document Management - Paginated List Query")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(SysFiles sysFiles,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<SysFiles> queryWrapper = QueryGenerator.initQueryWrapper(sysFiles, req.getParameterMap());
        Page<SysFiles> page = new Page<SysFiles>(pageNo, pageSize);
        IPage<SysFiles> pageList = sysFilesService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * Add to
     *
     * @param sysFiles
     * @return
     */
    @AutoLog(value = "Knowledge Base - Document Management - Add")
    @ApiOperation(value = "Knowledge Base - Document Management - Add", notes = "Knowledge Base - Document Management - Add")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody SysFiles sysFiles) {
        sysFilesService.save(sysFiles);
        return Result.OK("The addition was successful！");
    }

    /**
     * Edit
     *
     * @param sysFiles
     * @return
     */
    @AutoLog(value = "Knowledge Base - Document Management - Editing")
    @ApiOperation(value = "Knowledge Base - Document Management - Editing", notes = "Knowledge Base - Document Management - Editing")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<?> edit(@RequestBody SysFiles sysFiles) {
        sysFilesService.updateById(sysFiles);
        return Result.OK("Edited successfully!");
    }

    /**
     * Delete by ID
     *
     * @param id
     * @return
     */
    @AutoLog(value = "Knowledge Base - Document Management - Deletion by ID")
    @ApiOperation(value = "Knowledge Base - Document Management - Deletion by ID", notes = "Knowledge Base - Document Management - Deletion by ID")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        sysFilesService.removeById(id);
        return Result.OK("The deletion is successful!");
    }

    /**
     * Delete in bulk
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "Knowledge Base - Document Management - Batch Deletion")
    @ApiOperation(value = "Knowledge Base - Document Management - Batch Deletion", notes = "Knowledge Base - Document Management - Batch Deletion")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.sysFilesService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("The batch deletion is successful！");
    }

    /**
     * Query by ID
     *
     * @param id
     * @return
     */
    @AutoLog(value = "Knowledge Base - Document Management - Query by ID")
    @ApiOperation(value = "Knowledge Base - Document Management - Query by ID", notes = "Knowledge Base - Document Management - Query by ID")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        SysFiles sysFiles = sysFilesService.getById(id);
        return Result.OK(sysFiles);
    }

    /**
     * Export to Excel
     *
     * @param request
     * @param sysFiles
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysFiles sysFiles) {
        return super.exportXls(request, sysFiles, SysFiles.class, "知识库-文档管理");
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
        return super.importExcel(request, response, SysFiles.class);
    }

}
