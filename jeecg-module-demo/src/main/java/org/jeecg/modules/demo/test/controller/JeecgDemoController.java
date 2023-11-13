package org.jeecg.modules.demo.test.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.demo.test.entity.JeecgDemo;
import org.jeecg.modules.demo.test.service.IJeecgDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: Singular examples
 * @Author: jeecg-boot
 * @Date:2018-12-29
 * @Version:V2.0
 */
@Slf4j
@Api(tags = "Single Table Demo")
@RestController
@RequestMapping("/test/jeecgDemo")
public class JeecgDemoController extends JeecgController<JeecgDemo, IJeecgDemoService> {
    @Autowired
    private IJeecgDemoService jeecgDemoService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * Paginated list queries
     *
     * @param jeecgDemo
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @ApiOperation(value = "Obtain the demo data list", notes = "Obtain a list of all demo data")
    @GetMapping(value = "/list")
    @PermissionData(pageComponent = "jeecg/JeecgDemoList")
    public Result<?> list(JeecgDemo jeecgDemo, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                          HttpServletRequest req) {
        QueryWrapper<JeecgDemo> queryWrapper = QueryGenerator.initQueryWrapper(jeecgDemo, req.getParameterMap());
        queryWrapper.orderByDesc("create_time");
        Page<JeecgDemo> page = new Page<JeecgDemo>(pageNo, pageSize);

        IPage<JeecgDemo> pageList = jeecgDemoService.page(page, queryWrapper);
        log.info("To query the current page:" + pageList.getCurrent());
        log.info("Query the current number of pages:" + pageList.getSize());
        log.info("Number of query results:" + pageList.getRecords().size());
        log.info("Total number of data:" + pageList.getTotal());
        return Result.OK(pageList);
    }

    /**
     * Add to
     *
     * @param jeecgDemo
     * @return
     */
    @PostMapping(value = "/add")
    @AutoLog(value = "Add A Test Demo")
    @ApiOperation(value = "Add A Demo", notes = "Add A Demo")
    public Result<?> add(@RequestBody JeecgDemo jeecgDemo) {
        jeecgDemoService.save(jeecgDemo);
        return Result.OK("Added successfully!");
    }

    /**
     * EDIT
     *
     * @param jeecgDemo
     * @return
     */
    @AutoLog(value = "Edit the demo", operateType = CommonConstant.OPERATE_TYPE_3)
    @ApiOperation(value = "Edit the demo", notes = "Edit the demo")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<?> edit(@RequestBody JeecgDemo jeecgDemo) {
        jeecgDemoService.updateById(jeecgDemo);
        return Result.OK("Update Success!");
    }

    /**
     * Delete by ID
     *
     * @param id
     * @return
     */
    @AutoLog(value = "Delete The Test Demo")
    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "Delete A Demo By Id", notes = "Delete A Demo By Id")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        jeecgDemoService.removeById(id);
        return Result.OK("Deleted successfully!");
    }

    /**
     * Delete in bulk
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/deleteBatch")
    @ApiOperation(value = "Delete Demos In Batches", notes = "Delete Demos In Batches")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.jeecgDemoService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("Batch deletion successful!");
    }

    /**
     * Query by ID
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/queryById")
    @ApiOperation(value = "Query The Demo By Id", notes = "QUERY THE DEMO BY ID")
    public Result<?> queryById(@ApiParam(name = "id", value = "Example ID", required = true) @RequestParam(name = "id", required = true) String id) {
        JeecgDemo jeecgDemo = jeecgDemoService.getById(id);
        return Result.OK(jeecgDemo);
    }

    /**
     * Export to Excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    @PermissionData(pageComponent = "jeecg/JeecgDemoList")
    public ModelAndView exportXls(HttpServletRequest request, JeecgDemo jeecgDemo) {
        //Get the export table fields
        String exportFields = jeecgDemoService.getExportFields();
        //Export table fields in sheets
        return super.exportXlsSheet(request, jeecgDemo, JeecgDemo.class, "Single-table model",exportFields,500);
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
        return super.importExcel(request, response, JeecgDemo.class);
    }

    // =====Redis EXAMPLE===============================================================================================

    /**
     * redisOPERATE -- set
     */
    @GetMapping(value = "/redisSet")
    public void redisSet() {
        redisUtil.set("name", "TOM" + DateUtils.now());
    }

    /**
     * redisOPERATE -- get
     */
    @GetMapping(value = "/redisGet")
    public String redisGet() {
        return (String) redisUtil.get("name");
    }

    /**
     * redisOPERATE -- setObj
     */
    @GetMapping(value = "/redisSetObj")
    public void redisSetObj() {
        JeecgDemo p = new JeecgDemo();
        p.setAge(10);
        p.setBirthday(new Date());
        p.setContent("hello");
        p.setName("TOM");
        p.setSex("MAN");
        redisUtil.set("user-zdh", p);
    }

    /**
     * redisOPERATE -- setObj
     */
    @GetMapping(value = "/redisGetObj")
    public Object redisGetObj() {
        return redisUtil.get("user-zdh");
    }

    /**
     * redisOPERATE -- get
     */
    @GetMapping(value = "/redis/{id}")
    public JeecgDemo redisGetJeecgDemo(@PathVariable("id") String id) {
        JeecgDemo t = jeecgDemoService.getByIdCacheable(id);
        log.info(t.toString());
        return t;
    }

    // ===Freemaker示例================================================================================

    /**
     * freemakerMANNER 【Page Path: src/main/resources/templates】
     *
     * @param modelAndView
     * @return
     */
    @RequestMapping("/html")
    public ModelAndView ftl(ModelAndView modelAndView) {
        modelAndView.setViewName("demo3");
        List<String> userList = new ArrayList<String>();
        userList.add("admin");
        userList.add("user1");
        userList.add("user2");
        log.info("--------------test--------------");
        modelAndView.addObject("userList", userList);
        return modelAndView;
    }


    // ==========================================动态表单 JSON接收测试===========================================
    /**
     * online Added data
     */
    @PostMapping(value = "/testOnlineAdd")
    public Result<?> testOnlineAdd(@RequestBody JSONObject json) {
        log.info(json.toJSONString());
        return Result.OK("The addition was successful！");
    }

    /*----------------------------------------Example of obtaining permissions from outside------------------------------------*/

    /**
     * [Examples of data permissions - Programming] mybatis Plus Java-like loading permissions
     *
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @GetMapping(value = "/mpList")
    @PermissionData(pageComponent = "jeecg/JeecgDemoList")
    public Result<?> loadMpPermissonList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                         HttpServletRequest req) {
        QueryWrapper<JeecgDemo> queryWrapper = new QueryWrapper<JeecgDemo>();
        //Programmatically, load data permission rules to the query wrapper
        QueryGenerator.installAuthMplus(queryWrapper, JeecgDemo.class);
        Page<JeecgDemo> page = new Page<JeecgDemo>(pageNo, pageSize);
        IPage<JeecgDemo> pageList = jeecgDemoService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 【Data Permission Example - Programming】Mybatis XML Loading Permissions
     *
     * @param jeecgDemo
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @GetMapping(value = "/sqlList")
    @PermissionData(pageComponent = "jeecg/JeecgDemoList")
    public Result<?> loadSqlPermissonList(JeecgDemo jeecgDemo, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                          HttpServletRequest req) {
        IPage<JeecgDemo> pageList = jeecgDemoService.queryListWithPermission(pageSize, pageNo);
        return Result.OK(pageList);
    }
    /*----------------------------------------Example of obtaining permissions from outside------------------------------------*/

    /**
     * online api Enhance the list
     * @param params
     * @return
     */
    @PostMapping("/enhanceJavaListHttp")
    public Result enhanceJavaListHttp(@RequestBody JSONObject params) {
        log.info(" =========================================================== ");
        log.info("params: " + params.toJSONString());
        log.info("params.tableName: " + params.getString("tableName"));
        log.info("params.json: " + params.getJSONObject("json").toJSONString());
        JSONArray dataList = params.getJSONArray("dataList");
        log.info("params.dataList: " + dataList.toJSONString());
        log.info(" =========================================================== ");
        return Result.OK(dataList);
    }

    /**
     * online apiEnhance the form
     * @param params
     * @return
     */
    @PostMapping("/enhanceJavaFormHttp")
    public Result enhanceJavaFormHttp(@RequestBody JSONObject params) {
        log.info(" =========================================================== ");
        log.info("params: " + params.toJSONString());
        log.info("params.tableName: " + params.getString("tableName"));
        log.info("params.json: " + params.getJSONObject("json").toJSONString());
        log.info(" =========================================================== ");
        return Result.OK("1");
    }

    @GetMapping(value = "/hello")
    public String hello(HttpServletRequest req) {
        return "hello world!";
    }

    // =====Vue3 Native  Example of a native page===============================================================================================
    @GetMapping(value = "/oneNative/list")
    public Result oneNativeList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){
        Object oneNative = redisUtil.get("one-native");
        JSONArray data = new JSONArray();
        if(null != oneNative){
            JSONObject nativeObject = (JSONObject) oneNative;
            data = nativeObject.getJSONArray("data");
        }
        IPage<JSONObject> objectPage = queryDataPage(data, pageNo, pageSize);
        return Result.OK(objectPage);
    }
    
    @PostMapping("/oneNative/add")
    public Result<String> oneNativeAdd(@RequestBody JSONObject jsonObject){
        Object oneNative = redisUtil.get("one-native");
        JSONObject nativeObject = new JSONObject();
        JSONArray data = new JSONArray();
        if(null != oneNative){
            nativeObject = (JSONObject) oneNative;
            data = nativeObject.getJSONArray("data");
        }
        jsonObject.put("id", UUIDGenerator.generate());
        data.add(jsonObject);
        nativeObject.put("data",data);
        redisUtil.set("one-native",nativeObject);
        return Result.OK("The addition was successful");
    }
    
    @PutMapping("/oneNative/edit")
    public Result<String> oneNativeEdit(@RequestBody JSONObject jsonObject){
        JSONObject oneNative = (JSONObject)redisUtil.get("one-native");
        JSONArray data = oneNative.getJSONArray("data");
        data = getNativeById(data,jsonObject);
        oneNative.put("data", data);
        redisUtil.set("one-native", oneNative);
        return Result.OK("The modification was successful");
    }

    @DeleteMapping("/oneNative/delete")
    public Result<String> oneNativeDelete(@RequestParam(name = "ids") String ids){
        Object oneNative = redisUtil.get("one-native");
        if(null != oneNative){
            JSONObject nativeObject = (JSONObject) oneNative;
            JSONArray data = nativeObject.getJSONArray("data");
            data = deleteNativeById(data,ids);
            nativeObject.put("data",data);
            redisUtil.set("one-native",nativeObject);
        }
        return Result.OK("The deletion is successful");
    }
    
    /**
     * Obtain the data of the corresponding Redis ID
     * @param data
     * @param jsonObject
     * @return
     */
    public JSONArray getNativeById(JSONArray data,JSONObject jsonObject){
        String dbId = "id";
        String id = jsonObject.getString(dbId);
        for (int i = 0; i < data.size(); i++) {
            if(id.equals(data.getJSONObject(i).getString(dbId))){
                data.set(i,jsonObject);
                break;
            }
        }
        return data;
    }

    /**
     * Delete the ID data contained in Redis
     * @param data
     * @param ids
     * @return
     */
    public JSONArray deleteNativeById(JSONArray data,String ids){
        String dbId = "id";
        for (int i = 0; i < data.size(); i++) {
            //如果id包含直接清除data中的数据
            if(ids.contains(data.getJSONObject(i).getString(dbId))){
                data.fluentRemove(i);
            }
            //判断data的长度是否还剩1位
            if(data.size() == 1 && ids.contains(data.getJSONObject(0).getString(dbId))){
                data.fluentRemove(0);
            }
        }
        return data;
    }

    /**
     * Simulating query data, which can be queried based on the parent ID, can be paginated
     *
     * @param dataList 数据列表
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return
     */
    private IPage<JSONObject> queryDataPage(JSONArray dataList, Integer pageNo, Integer pageSize) {
        // Query children based on their parent IDs
        JSONArray dataDb = dataList;
        // Simulated pagination (pagination that comes with SQL is actually applied)
        List<JSONObject> records = new ArrayList<>();
        IPage<JSONObject> page;
        long beginIndex, endIndex;
        // If any parameter is null, it is not paged
        if (pageNo == null || pageSize == null) {
            page = new Page<>(0, dataDb.size());
            beginIndex = 0;
            endIndex = dataDb.size();
        } else {
            page = new Page<>(pageNo, pageSize);
            beginIndex = page.offset();
            endIndex = page.offset() + page.getSize();
        }
        for (long i = beginIndex; (i < endIndex && i < dataDb.size()); i++) {
            JSONObject data = dataDb.getJSONObject((int) i);
            data = JSON.parseObject(data.toJSONString());
            // No return children
            data.remove("children");
            records.add(data);
        }
        page.setRecords(records);
        page.setTotal(dataDb.size());
        return page;
    }
    // =====Vue3 Native  Example of a native page===============================================================================================


    /**
     * Get the creator
     * @return
     */
    @GetMapping(value = "/groupList")
    public Result<?> groupList() {
        return Result.ok(jeecgDemoService.getCreateByList());
    }

    /**
     * Test the Mono object
     * @return
     */
    @ApiOperation("MonoTEST")
    @GetMapping(value ="/test")
    public Mono<String> test() {
        //解决shiro报错No SecurityManager accessible to the calling code, either bound to the org.apache.shiro
        // https://blog.csdn.net/Japhet_jiu/article/details/131177210
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        SecurityUtils.setSecurityManager(securityManager);
        
        return Mono.just("TEST");
    }

}
