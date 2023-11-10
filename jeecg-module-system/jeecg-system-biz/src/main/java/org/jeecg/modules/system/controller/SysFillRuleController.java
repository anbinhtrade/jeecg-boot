package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import org.jeecg.common.util.FillRuleUtil;
import org.jeecg.modules.system.entity.SysFillRule;
import org.jeecg.modules.system.service.ISysFillRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: Filling rules
 * @Author: jeecg-boot
 * @Date: 2019-11-07
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "Filling rules")
@RestController
@RequestMapping("/sys/fillRule")
public class SysFillRuleController extends JeecgController<SysFillRule, ISysFillRuleService> {
    @Autowired
    private ISysFillRuleService sysFillRuleService;

    /**
     * Paginated list queries
     *
     * @param sysFillRule
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "Filling rules-Paginated list queries")
    @ApiOperation(value = "Filling rules-Paginated list queries", notes = "Filling rules-Paginated list queries")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(SysFillRule sysFillRule,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<SysFillRule> queryWrapper = QueryGenerator.initQueryWrapper(sysFillRule, req.getParameterMap());
        Page<SysFillRule> page = new Page<>(pageNo, pageSize);
        IPage<SysFillRule> pageList = sysFillRuleService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * TEST ruleCode
     *
     * @param ruleCode
     * @return
     */
    @GetMapping(value = "/testFillRule")
    public Result testFillRule(@RequestParam("ruleCode") String ruleCode) {
        Object result = FillRuleUtil.executeRule(ruleCode, new JSONObject());
        return Result.ok(result);
    }

    /**
     * Add to
     *
     * @param sysFillRule
     * @return
     */
    @AutoLog(value = "Filling rules-Add")
    @ApiOperation(value = "Filling rules-Add", notes = "Filling rules-Add")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody SysFillRule sysFillRule) {
        sysFillRuleService.save(sysFillRule);
        return Result.ok("The addition was successful！");
    }

    /**
     * EDIT
     *
     * @param sysFillRule
     * @return
     */
    @AutoLog(value = "Filling rules-Edit")
    @ApiOperation(value = "Filling rules-Edit", notes = "Filling rules-Edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<?> edit(@RequestBody SysFillRule sysFillRule) {
        sysFillRuleService.updateById(sysFillRule);
        return Result.ok("Edited successfully!");
    }

    /**
     * Delete by ID
     *
     * @param id
     * @return
     */
    @AutoLog(value = "Filling rules-delete by id")
    @ApiOperation(value = "Filling rules-delete by id", notes = "Filling rules-delete by id")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        sysFillRuleService.removeById(id);
        return Result.ok("The deletion is successful!");
    }

    /**
     * Delete in bulk
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "Filling rules-Delete in bulk")
    @ApiOperation(value = "Filling rules-Delete in bulk", notes = "Filling rules-Delete in bulk")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.sysFillRuleService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("The batch deletion is successful！");
    }

    /**
     * Query by ID
     *
     * @param id
     * @return
     */
    @AutoLog(value = "Filling rules-Query by ID")
    @ApiOperation(value = "Filling rules-Query by ID", notes = "Filling rules-Query by ID")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        SysFillRule sysFillRule = sysFillRuleService.getById(id);
        return Result.ok(sysFillRule);
    }

    /**
     * Export to Excel
     *
     * @param request
     * @param sysFillRule
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysFillRule sysFillRule) {
        return super.exportXls(request, sysFillRule, SysFillRule.class, "Filling rules");
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
        return super.importExcel(request, response, SysFillRule.class);
    }

    /**
     * Use ruleCode to execute custom value filling rules
     *
     * @param ruleCode The encoding of the fill rule to be executed
     * @param formData Form data, different value filling results can be generated according to different form data
     * @return The result after the run
     */
    @PutMapping("/executeRuleByCode/{ruleCode}")
    public Result executeByRuleCode(@PathVariable("ruleCode") String ruleCode, @RequestBody JSONObject formData) {
        Object result = FillRuleUtil.executeRule(ruleCode, formData);
        return Result.ok(result);
    }


    /**
     * Batch pass ruleCode Execute custom value filling rules
     *
     * @param ruleData A JSON array of the value filling rules to be executed：
     *                 Example： { "commonFormData": {}, rules: [ { "ruleCode": "xxx", "formData": null } ] }
     * @return After the run, an example is returned： [{"ruleCode": "order_num_rule", "result": "CN2019111117212984"}]
     *
     */
    @PutMapping("/executeRuleByCodeBatch")
    public Result executeByRuleCodeBatch(@RequestBody JSONObject ruleData) {
        JSONObject commonFormData = ruleData.getJSONObject("commonFormData");
        JSONArray rules = ruleData.getJSONArray("rules");
        // 遍历 rules ，批量执行规则
        JSONArray results = new JSONArray(rules.size());
        for (int i = 0; i < rules.size(); i++) {
            JSONObject rule = rules.getJSONObject(i);
            String ruleCode = rule.getString("ruleCode");
            JSONObject formData = rule.getJSONObject("formData");
            // If there is no delivery formData，就用common的
            if (formData == null) {
                formData = commonFormData;
            }
            // Execute the value filling rule
            Object result = FillRuleUtil.executeRule(ruleCode, formData);
            JSONObject obj = new JSONObject(rules.size());
            obj.put("ruleCode", ruleCode);
            obj.put("result", result);
            results.add(obj);
        }
        return Result.ok(results);
    }

}