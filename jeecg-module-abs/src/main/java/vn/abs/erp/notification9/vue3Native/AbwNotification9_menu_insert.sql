-- 注意：该页面对应的前台目录为views/notification9文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('202311210224890150', NULL, 'Notification 9', '/notification9/abwNotification9List', 'notification9/AbwNotification9List', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2023-11-21 14:24:15', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202311210224890151', '202311210224890150', '添加Notification 9', NULL, NULL, 0, NULL, NULL, 2, 'notification9:abw_notification9:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 14:24:15', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202311210224890152', '202311210224890150', '编辑Notification 9', NULL, NULL, 0, NULL, NULL, 2, 'notification9:abw_notification9:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 14:24:15', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202311210224890153', '202311210224890150', '删除Notification 9', NULL, NULL, 0, NULL, NULL, 2, 'notification9:abw_notification9:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 14:24:15', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202311210224890154', '202311210224890150', '批量删除Notification 9', NULL, NULL, 0, NULL, NULL, 2, 'notification9:abw_notification9:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 14:24:15', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202311210224890155', '202311210224890150', '导出excel_Notification 9', NULL, NULL, 0, NULL, NULL, 2, 'notification9:abw_notification9:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 14:24:15', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202311210224890156', '202311210224890150', '导入excel_Notification 9', NULL, NULL, 0, NULL, NULL, 2, 'notification9:abw_notification9:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 14:24:15', NULL, NULL, 0, 0, '1', 0);