package org.jeecg.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.dto.DataLogDTO;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.system.entity.SysComment;
import org.jeecg.modules.system.service.ISysCommentService;
import org.jeecg.modules.system.vo.SysCommentFileVo;
import org.jeecg.modules.system.vo.SysCommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: System comment response form
 * @Author: jeecg-boot
 * @Date: 2022-07-19
 * @Version: V1.0
 */
@Api(tags = "System comment response form")
@RestController
@RequestMapping("/sys/comment")
@Slf4j
public class SysCommentController extends JeecgController<SysComment, ISysCommentService> {

    @Autowired
    private ISysCommentService sysCommentService;

    @Autowired
    private ISysBaseAPI sysBaseAPI;


    /**
     * Preview the file address online
     */
    @Value("${jeecg.file-view-domain}/onlinePreview")
    private String onlinePreviewDomain;

    /**
     * Query Comments + Files
     *
     * @param sysComment
     * @return
     */
    @ApiOperation(value = "System Comment Response Form - List Query", notes = "System Comment Response Form - List Query")
    @GetMapping(value = "/listByForm")
    public Result<IPage<SysCommentVO>> queryListByForm(SysComment sysComment) {
        List<SysCommentVO> list = sysCommentService.queryFormCommentInfo(sysComment);
        IPage<SysCommentVO> pageList = new Page();
        pageList.setRecords(list);
        return Result.OK(pageList);
    }

    /**
     * Query files
     *
     * @param sysComment
     * @return
     */
    @ApiOperation(value = "System Comment Response Form - List Query", notes = "System Comment Response Form - List Query")
    @GetMapping(value = "/fileList")
    public Result<IPage<SysCommentFileVo>> queryFileList(SysComment sysComment) {
        List<SysCommentFileVo> list = sysCommentService.queryFormFileList(sysComment.getTableName(), sysComment.getTableDataId());
        IPage<SysCommentFileVo> pageList = new Page();
        pageList.setRecords(list);
        return Result.OK(pageList);
    }

    @ApiOperation(value = "System Comment Form - Add text", notes = "System Comment Form - Add text")
    @PostMapping(value = "/addText")
    public Result<String> addText(@RequestBody SysComment sysComment) {
        String commentId = sysCommentService.saveOne(sysComment);
        return Result.OK(commentId);
    }

    @ApiOperation(value = "System Comment Form - Add Files", notes = "System Comment Form - Add Files")
    @PostMapping(value = "/addFile")
    public Result<String> addFile(HttpServletRequest request) {
        try {
            sysCommentService.saveOneFileComment(request);
            return Result.OK("success");
        } catch (Exception e) {
            log.error("The comment file failed to be uploaded：{}", e.getMessage());
            return Result.error("The operation failed," + e.getMessage());
        }
    }

    /**
     * Add a comment form on the app
     * @param request
     * @return
     */
    @ApiOperation(value = "System Comment Form - Add Files", notes = "System Comment Form - Add Files")
    @PostMapping(value = "/appAddFile")
    public Result<String> appAddFile(HttpServletRequest request) {
        try {
            sysCommentService.appSaveOneFileComment(request);
            return Result.OK("success");
        } catch (Exception e) {
            log.error("The comment file failed to be uploaded：{}", e.getMessage());
            return Result.error("The operation failed," + e.getMessage());
        }
    }

    @ApiOperation(value = "System Comment Response Form - Deleted by ID", notes = "System Comment Response Form - Deleted by ID")
    @DeleteMapping(value = "/deleteOne")
    public Result<String> deleteOne(@RequestParam(name = "id", required = true) String id) {
        SysComment comment = sysCommentService.getById(id);
        if(comment==null){
            return Result.error("The comment has since been deleted！");
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String username = sysUser.getUsername();
        String admin = "admin";
        //除了admin外 其他人只能删除自己的评论
        if((!admin.equals(username)) && !username.equals(comment.getCreateBy())){
            return Result.error("You can only delete your own comments！");
        }
        sysCommentService.deleteOne(id);
        //删除评论添加日志
        String logContent = "The comment was deleted， "+ comment.getCommentContent();
        DataLogDTO dataLog = new DataLogDTO(comment.getTableName(), comment.getTableDataId(), logContent, CommonConstant.DATA_LOG_TYPE_COMMENT);
        sysBaseAPI.saveDataLog(dataLog);
        return Result.OK("The deletion is successful!");
    }


    /**
     * Get the address of the file preview
     * @return
     */
    @GetMapping(value = "/getFileViewDomain")
    public Result<String> getFileViewDomain() {
        return Result.OK(onlinePreviewDomain);
    }


    /**
     * Paginated list queries
     *
     * @param sysComment
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "系统评论回复表-分页列表查询")
    @ApiOperation(value = "System Comment Response Form - Paginated List Query", notes = "System Comment Response Form - Paginated List Query")
    @GetMapping(value = "/list")
    public Result<IPage<SysComment>> queryPageList(SysComment sysComment,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
        QueryWrapper<SysComment> queryWrapper = QueryGenerator.initQueryWrapper(sysComment, req.getParameterMap());
        Page<SysComment> page = new Page<SysComment>(pageNo, pageSize);
        IPage<SysComment> pageList = sysCommentService.page(page, queryWrapper);
        return Result.OK(pageList);
    }


    /**
     * Add to
     *
     * @param sysComment
     * @return
     */
    @ApiOperation(value = "System Comment Response Form - Added", notes = "System Comment Response Form - Added")
    //@RequiresPermissions("org.jeecg.modules.demo:sys_comment:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SysComment sysComment) {
        sysCommentService.save(sysComment);
        return Result.OK("The addition was successful！");
    }

    /**
     * Edit
     *
     * @param sysComment
     * @return
     */
    //@AutoLog(value = "System Comment Response Form - Edit")
    @ApiOperation(value = "System Comment Response Form - Edit", notes = "System Comment Response Form - Edit")
    //@RequiresPermissions("org.jeecg.modules.demo:sys_comment:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody SysComment sysComment) {
        sysCommentService.updateById(sysComment);
        return Result.OK("Edited successfully!");
    }

    /**
     * Delete by ID
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "System Comment Response Form - Deleted by ID")
    @ApiOperation(value = "System Comment Response Form - Deleted by ID", notes = "System Comment Response Form - Deleted by ID")
    //@RequiresPermissions("org.jeecg.modules.demo:sys_comment:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        sysCommentService.removeById(id);
        return Result.OK("The deletion is successful!");
    }

    /**
     * Delete in bulk
     *
     * @param ids
     * @return
     */
    //@AutoLog(value = "System comment reply form - delete in bulk")
    @ApiOperation(value = "System comment reply form - delete in bulk", notes = "System comment reply form - delete in bulk")
    //@RequiresPermissions("org.jeecg.modules.demo:sys_comment:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.sysCommentService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("The batch deletion is successful!");
    }

    /**
     * Query by ID
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "System Comment Response Form - Query by ID")
    @ApiOperation(value = "System Comment Response Form - Query by ID", notes = "System Comment Response Form - Query by ID")
    @GetMapping(value = "/queryById")
    public Result<SysComment> queryById(@RequestParam(name = "id", required = true) String id) {
        SysComment sysComment = sysCommentService.getById(id);
        if (sysComment == null) {
            return Result.error("No data found");
        }
        return Result.OK(sysComment);
    }

    /**
     * Export to Excel
     *
     * @param request
     * @param sysComment
     */
    //@RequiresPermissions("org.jeecg.modules.demo:sys_comment:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysComment sysComment) {
        return super.exportXls(request, sysComment, SysComment.class, "System comment response form");
    }

    /**
     * Import data via Excel
     *
     * @param request
     * @param response
     * @return
     */
    //@RequiresPermissions("sys_comment:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SysComment.class);
    }


}
