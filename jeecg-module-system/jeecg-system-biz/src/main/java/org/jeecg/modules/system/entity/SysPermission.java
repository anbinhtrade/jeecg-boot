package org.jeecg.modules.system.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * <p>
 * Menu permission table
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysPermission implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	 * 父id
	 */
	private String parentId;

	/**
	 * Menu name
	 */
	private String name;

	/**
	 * Menu permission encoding, for example：“sys:schedule:list,sys:schedule:info”,Multiple commas separated by each other
	 */
	private String perms;
	/**
	 * Permission Policy 1 shows 2 disabled
	 */
	private String permsType;

	/**
	 * MENU ICON
	 */
	private String icon;

	/**
	 * SUBASSEMBLY
	 */
	private String component;
	
	/**
	 * The name of the component
	 */
	private String componentName;

	/**
	 * PATH
	 */
	private String url;
	/**
	 * The address of the first-level menu jump
	 */
	private String redirect;

	/**
	 * Menu sorting
	 */
	private Double sortNo;

	/**
	 * Type (0: first-level menu; 1: submenu ;2: Button permissions)
	 */
	@Dict(dicCode = "menu_type")
	private Integer menuType;

	/**
	 * Whether the leaf node: 1: Yes  0: No
	 */
	@TableField(value="is_leaf")
	private boolean leaf;
	
	/**
	 * Whether to route the menu: 0: No  1: Yes (default value 1)
	 */
	@TableField(value="is_route")
	private boolean route;


	/**
	 * Whether to cache the page: 0: No  1: Yes (default value 1)
	 */
	@TableField(value="keep_alive")
	private boolean keepAlive;

	/**
	 * DESCRIPTION
	 */
	private String description;

	/**
	 * Created by
	 */
	private String createBy;

	/**
	 * Delete the status 0 is normal 1 Deleted
	 */
	private Integer delFlag;
	
	/**
	 * Whether to configure the data permissions for the menu 1 Yes 0 No Default 0 X
	 */
	private Integer ruleFlag;
	
	/**
	 * Whether to hide the routing menu: 0 No, 1 Yes (default value 0)
	 */
	private boolean hidden;

	/**
	 * Whether to hide Tab: 0 No, 1 Yes (default value 0)
	 */
	private boolean hideTab;

	/**
	 * Creation time
	 */
	private Date createTime;

	/**
	 * UPDATER
	 */
	private String updateBy;

	/**
	 * UPDATED
	 */
	private Date updateTime;
	
	/**Button permission status (0 invalid, 1 valid)*/
	private java.lang.String status;
	
	/**alwaysShow*/
    private boolean alwaysShow;

	/*update_begin author:wuxianquan date:20190908 for:实体增加字段 */
    /** How the backlinks menu opens 0/ Inside open 1/ Externally open */
    private boolean internalOrExternal;
	/*update_end author:wuxianquan date:20190908 for: The entity is added to the field */

    public SysPermission() {
    	
    }
    public SysPermission(boolean index) {
    	if(index) {
    		this.id = "9502685863ab87f0ad1134142788a385";
        	this.name="Home";
        	this.component="dashboard/Analysis";
        	this.componentName="dashboard-analysis";
        	this.url="/dashboard/analysis";
        	this.icon="home";
        	this.menuType=0;
        	this.sortNo=0.0;
        	this.ruleFlag=0;
        	this.delFlag=0;
        	this.alwaysShow=false;
        	this.route=true;
        	this.keepAlive=true;
        	this.leaf=true;
        	this.hidden=false;
    	}
    	
    }
}
