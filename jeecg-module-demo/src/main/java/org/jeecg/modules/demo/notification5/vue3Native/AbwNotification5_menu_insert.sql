-- 注意：该页面对应的前台目录为views/notification5文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2023112109005460270', NULL, 'Notification5', '/notification5/abwNotification5List', 'notification5/AbwNotification5List', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2023-11-21 09:00:27', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112109005460271', '2023112109005460270', '添加Notification5', NULL, NULL, 0, NULL, NULL, 2, 'notification5:abw_notification:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 09:00:27', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112109005460272', '2023112109005460270', '编辑Notification5', NULL, NULL, 0, NULL, NULL, 2, 'notification5:abw_notification:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 09:00:27', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112109005460273', '2023112109005460270', '删除Notification5', NULL, NULL, 0, NULL, NULL, 2, 'notification5:abw_notification:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 09:00:27', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112109005460274', '2023112109005460270', '批量删除Notification5', NULL, NULL, 0, NULL, NULL, 2, 'notification5:abw_notification:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 09:00:27', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112109005460275', '2023112109005460270', '导出excel_Notification5', NULL, NULL, 0, NULL, NULL, 2, 'notification5:abw_notification:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 09:00:27', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112109005460276', '2023112109005460270', '导入excel_Notification5', NULL, NULL, 0, NULL, NULL, 2, 'notification5:abw_notification:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 09:00:27', NULL, NULL, 0, 0, '1', 0);