-- 注意：该页面对应的前台目录为views/notification7文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2023112110082860140', NULL, 'Notification 7', '/notification7/abwNotification7List', 'notification7/AbwNotification7List', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2023-11-21 10:08:14', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112110082860141', '2023112110082860140', '添加Notification 7', NULL, NULL, 0, NULL, NULL, 2, 'notification7:abw_notification7:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 10:08:14', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112110082860142', '2023112110082860140', '编辑Notification 7', NULL, NULL, 0, NULL, NULL, 2, 'notification7:abw_notification7:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 10:08:14', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112110082860143', '2023112110082860140', '删除Notification 7', NULL, NULL, 0, NULL, NULL, 2, 'notification7:abw_notification7:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 10:08:14', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112110082860144', '2023112110082860140', '批量删除Notification 7', NULL, NULL, 0, NULL, NULL, 2, 'notification7:abw_notification7:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 10:08:14', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112110082860145', '2023112110082860140', '导出excel_Notification 7', NULL, NULL, 0, NULL, NULL, 2, 'notification7:abw_notification7:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 10:08:14', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112110082860146', '2023112110082860140', '导入excel_Notification 7', NULL, NULL, 0, NULL, NULL, 2, 'notification7:abw_notification7:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-21 10:08:14', NULL, NULL, 0, 0, '1', 0);