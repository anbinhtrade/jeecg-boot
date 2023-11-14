package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.firewall.SqlInjection.IDictTableWhiteListHandler;
import org.jeecg.modules.system.entity.SysTableWhiteList;
import org.jeecg.modules.system.mapper.SysTableWhiteListMapper;
import org.jeecg.modules.system.service.ISysTableWhiteListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description: The system whitelist
 * @Author: jeecg-boot
 * @Date: 2023-09-12
 * @Version: V1.0
 */
@Slf4j
@Service
public class SysTableWhiteListServiceImpl extends ServiceImpl<SysTableWhiteListMapper, SysTableWhiteList> implements ISysTableWhiteListService {

    @Lazy
    @Autowired
    IDictTableWhiteListHandler whiteListHandler;

    @Override
    public boolean add(SysTableWhiteList sysTableWhiteList) {
        this.checkEntity(sysTableWhiteList);
        if (super.save(sysTableWhiteList)) {
            // Clear the cache
            whiteListHandler.clear();
            return true;
        }
        return false;
    }

    @Override
    public boolean edit(SysTableWhiteList sysTableWhiteList) {
        this.checkEntity(sysTableWhiteList);
        if (super.updateById(sysTableWhiteList)) {
            // Clear the cache
            whiteListHandler.clear();
            return true;
        }
        return false;
    }

    /**
     * Check that the entities that need to be added or updated are compliant
     *
     * @param sysTableWhiteList
     */
    private void checkEntity(SysTableWhiteList sysTableWhiteList) {
        if (sysTableWhiteList == null) {
            throw new JeecgBootException("The operation failed and the entity is empty!");
        }
        if (oConvertUtils.isEmpty(sysTableWhiteList.getTableName())) {
            throw new JeecgBootException("If the operation fails, the table name cannot be empty!");
        }
        if (oConvertUtils.isEmpty(sysTableWhiteList.getFieldName())) {
            throw new JeecgBootException("The operation failed, and the field name cannot be empty!");
        }
        // 将表名和字段名转换成小写
        sysTableWhiteList.setTableName(sysTableWhiteList.getTableName().toLowerCase());
        sysTableWhiteList.setFieldName(sysTableWhiteList.getFieldName().toLowerCase());
        // 如果status为空，则默认启用
        if (oConvertUtils.isEmpty(sysTableWhiteList.getStatus())) {
            sysTableWhiteList.setStatus(CommonConstant.STATUS_1);
        }
    }

    @Override
    public boolean deleteByIds(String ids) {
        if (oConvertUtils.isEmpty(ids)) {
            return false;
        }
        List<String> idList = Arrays.asList(ids.split(","));
        if (super.removeByIds(idList)) {
            // 清空缓存
            whiteListHandler.clear();
            return true;
        }
        return false;
    }

    @Override
    public SysTableWhiteList autoAdd(String tableName, String fieldName) {
        if (oConvertUtils.isEmpty(tableName)) {
            throw new JeecgBootException("If the operation fails, the table name cannot be empty!");
        }
        if (oConvertUtils.isEmpty(fieldName)) {
            throw new JeecgBootException("The operation failed, and the field name cannot be empty!");
        }
        // 统一转换成小写
        tableName = tableName.toLowerCase();
        fieldName = fieldName.toLowerCase();
        // 查询是否已经存在
        LambdaQueryWrapper<SysTableWhiteList> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysTableWhiteList::getTableName, tableName);
        SysTableWhiteList getEntity = super.getOne(queryWrapper);
        if (getEntity != null) {
            // 如果已经存在，并且已禁用，则抛出异常
            if (CommonConstant.STATUS_0.equals(getEntity.getStatus())) {
                throw new JeecgBootException("[Whitelist] The table name already exists, but it is disabled, please enable it first！tableName=" + tableName);
            }
            // 合并字段
            Set<String> oldFieldSet = new HashSet<>(Arrays.asList(getEntity.getFieldName().split(",")));
            Set<String> newFieldSet = new HashSet<>(Arrays.asList(fieldName.split(",")));
            oldFieldSet.addAll(newFieldSet);
            getEntity.setFieldName(String.join(",", oldFieldSet));
            this.checkEntity(getEntity);
            super.updateById(getEntity);
            log.info("Modify the whitelist of the form item, the table name: {}, old field set: {}，newFieldSet：{}", tableName, oldFieldSet.toArray(), newFieldSet.toArray());
            return getEntity;
        } else {
            // 新增白名单项
            SysTableWhiteList saveEntity = new SysTableWhiteList();
            saveEntity.setTableName(tableName);
            saveEntity.setFieldName(fieldName);
            saveEntity.setStatus(CommonConstant.STATUS_1);
            this.checkEntity(saveEntity);
            super.save(saveEntity);
            log.info("Added form whitelist items: Table name: {}, configuration > {}", tableName, saveEntity.toString());
            return saveEntity;
        }
    }

    @Override
    public Map<String, String> getAllConfigMap() {
        Map<String, String> map = new HashMap<>();
        List<SysTableWhiteList> allData = super.list();
        for (SysTableWhiteList item : allData) {
            // Only enabled ones are put into the map
            if (CommonConstant.STATUS_1.equals(item.getStatus())) {
                // 表名和字段名都转成小写，防止大小写不一致
                map.put(item.getTableName().toLowerCase(), item.getFieldName().toLowerCase());
            }
        }
        return map;
    }

}
