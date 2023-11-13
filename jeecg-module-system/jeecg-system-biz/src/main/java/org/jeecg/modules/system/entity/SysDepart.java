package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * Department table
 * <p>
 * 
 * @Author Steve
 * @Since  2019-01-22
 */
@Data
@TableName("sys_depart")
public class SysDepart implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	/**Parent organization ID*/
	private String parentId;
	/**Organization/department name*/
	@Excel(name="Organization/department name",width=15)
	private String departName;
	/**English name*/
	@Excel(name="English name",width=15)
	private String departNameEn;
	/**Abridge*/
	private String departNameAbbr;
	/**Sort*/
	@Excel(name="Sort",width=15)
	private Integer departOrder;
	/**Bewrite*/
	@Excel(name="Bewrite",width=15)
	private String description;
	/**Institutions category 1=company, 2=organization, 3=position*/
	@Excel(name="Institutions category",width=15,dicCode="org_category")
	private String orgCategory;
	/**Organization Type*/
	private String orgType;
	/**Institution code*/
	@Excel(name="Institution code",width=15)
	private String orgCode;
	/**Phone number*/
	@Excel(name="Phone number",width=15)
	private String mobile;
	/**Portraiture*/
	@Excel(name="Portraiture",width=15)
	private String fax;
	/**Address*/
	@Excel(name="Address",width=15)
	private String address;
	/**Remark*/
	@Excel(name="Remark",width=15)
	private String memo;
	/**State（1 Enabled, 0 Not enabled)*/
	@Dict(dicCode = "depart_status")
	private String status;
	/**Deleted Status (0, Normal, 1 Deleted)*/
	@Dict(dicCode = "del_flag")
	private String delFlag;
	/**The ID of the WeCom*/
	private String qywxIdentifier;
	/**Created by*/
	private String createBy;
	/**Date of creation*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	/**Updater*/
	private String updateBy;
	/**Updated date*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	/**Tenant ID*/
	private java.lang.Integer tenantId;

	/**Is there a leaf node: 1 Yes 0 No*/
	private Integer izLeaf;

    //update-begin---author:wangshuai ---date:20200308  for：[JTC-119]在部门管理菜单下设置部门负责人，新增字段负责人ids和旧的负责人ids
    /**IDS of the head of the department*/
	@TableField(exist = false)
	private String directorUserIds;
    /**IDS of the old department head (for comparison of deleted and new)*/
	@TableField(exist = false)
    private String oldDirectorUserIds;
    //update-end---author:wangshuai ---date:20200308  for：[JTC-119]新增字段负责人ids和旧的负责人ids
	
	/**
	 * Rewrite the equals method
	 */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
			return true;
		}
        if (o == null || getClass() != o.getClass()) {
			return false;
		}
        if (!super.equals(o)) {
			return false;
		}
        SysDepart depart = (SysDepart) o;
        return Objects.equals(id, depart.id) &&
                Objects.equals(parentId, depart.parentId) &&
                Objects.equals(departName, depart.departName) &&
                Objects.equals(departNameEn, depart.departNameEn) &&
                Objects.equals(departNameAbbr, depart.departNameAbbr) &&
                Objects.equals(departOrder, depart.departOrder) &&
                Objects.equals(description, depart.description) &&
                Objects.equals(orgCategory, depart.orgCategory) &&
                Objects.equals(orgType, depart.orgType) &&
                Objects.equals(orgCode, depart.orgCode) &&
                Objects.equals(mobile, depart.mobile) &&
                Objects.equals(fax, depart.fax) &&
                Objects.equals(address, depart.address) &&
                Objects.equals(memo, depart.memo) &&
                Objects.equals(status, depart.status) &&
                Objects.equals(delFlag, depart.delFlag) &&
                Objects.equals(createBy, depart.createBy) &&
                Objects.equals(createTime, depart.createTime) &&
                Objects.equals(updateBy, depart.updateBy) &&
                Objects.equals(tenantId, depart.tenantId) &&
                Objects.equals(updateTime, depart.updateTime);
    }

    /**
     * Rewrite the hash code method
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, parentId, departName, 
        		departNameEn, departNameAbbr, departOrder, description,orgCategory, 
        		orgType, orgCode, mobile, fax, address, memo, status, 
        		delFlag, createBy, createTime, updateBy, updateTime, tenantId);
    }
}
