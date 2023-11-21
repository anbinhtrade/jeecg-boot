-- 注意：该页面对应的前台目录为views/notification8文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2023112102018210440', NULL, 'Notification 8', '/notification8/abwNotification8List', 'notification8/AbwNotification8List', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2023-11-21 14:01:44', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112102018210441', '2023112102018210440', '添加Notification 8', NULL, NULL, 0, NULL, NULL, 2, 'notification8:abw_notification8:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 14:01:44', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112102018210442', '2023112102018210440', '编辑Notification 8', NULL, NULL, 0, NULL, NULL, 2, 'notification8:abw_notification8:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 14:01:44', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112102018210443', '2023112102018210440', '删除Notification 8', NULL, NULL, 0, NULL, NULL, 2, 'notification8:abw_notification8:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 14:01:44', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112102018210444', '2023112102018210440', '批量删除Notification 8', NULL, NULL, 0, NULL, NULL, 2, 'notification8:abw_notification8:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 14:01:44', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112102018210445', '2023112102018210440', '导出excel_Notification 8', NULL, NULL, 0, NULL, NULL, 2, 'notification8:abw_notification8:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 14:01:44', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112102018210446', '2023112102018210440', '导入excel_Notification 8', NULL, NULL, 0, NULL, NULL, 2, 'notification8:abw_notification8:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 14:01:44', NULL, NULL, 0, 0, '1', 0);