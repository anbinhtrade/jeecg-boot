-- 注意：该页面对应的前台目录为views/notification6文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2023112109581710250', NULL, 'Notification 6', '/notification6/abwNotification6List', 'notification6/AbwNotification6List', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2023-11-21 09:58:25', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112109581720251', '2023112109581710250', '添加Notification 6', NULL, NULL, 0, NULL, NULL, 2, 'notification6:abw_notification6:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 09:58:25', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112109581720252', '2023112109581710250', '编辑Notification 6', NULL, NULL, 0, NULL, NULL, 2, 'notification6:abw_notification6:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 09:58:25', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112109581720253', '2023112109581710250', '删除Notification 6', NULL, NULL, 0, NULL, NULL, 2, 'notification6:abw_notification6:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 09:58:25', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112109581720254', '2023112109581710250', '批量删除Notification 6', NULL, NULL, 0, NULL, NULL, 2, 'notification6:abw_notification6:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 09:58:25', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112109581720255', '2023112109581710250', '导出excel_Notification 6', NULL, NULL, 0, NULL, NULL, 2, 'notification6:abw_notification6:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 09:58:25', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112109581720256', '2023112109581710250', '导入excel_Notification 6', NULL, NULL, 0, NULL, NULL, 2, 'notification6:abw_notification6:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 09:58:25', NULL, NULL, 0, 0, '1', 0);