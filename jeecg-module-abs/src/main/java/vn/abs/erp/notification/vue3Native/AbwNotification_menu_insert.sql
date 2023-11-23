-- Note: The front-end directory corresponding to this page is under folder views/notification
-- If you want to change to another directory, please modify the value corresponding to the component field in sql


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2023112311235610010', NULL, 'Notification ABS', '/notification/abwNotificationList', 'notification/AbwNotificationList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2023-11-23 11:23:01', NULL, NULL, 0);

-- permission control sql
-- New
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112311235610011', '2023112311235610010', 'Add Notification ABS', NULL, NULL, 0, NULL, NULL, 2, 'notification:abw_notification:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-23 11:23:01', NULL, NULL, 0, 0, '1', 0);
-- Edit
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112311235610012', '2023112311235610010', 'Edit Notification ABS', NULL, NULL, 0, NULL, NULL, 2, 'notification:abw_notification:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-23 11:23:01', NULL, NULL, 0, 0, '1', 0);
-- Delete
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112311235610013', '2023112311235610010', 'Delete Notification ABS', NULL, NULL, 0, NULL, NULL, 2, 'notification:abw_notification:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-23 11:23:01', NULL, NULL, 0, 0, '1', 0);
-- Batch deletion
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112311235610014', '2023112311235610010', 'Batch delete Notification ABS', NULL, NULL, 0, NULL, NULL, 2, 'notification:abw_notification:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-23 11:23:01', NULL, NULL, 0, 0, '1', 0);
-- Export excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112311235610015', '2023112311235610010', 'Export excel_Notification ABS', NULL, NULL, 0, NULL, NULL, 2, 'notification:abw_notification:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-23 11:23:01', NULL, NULL, 0, 0, '1', 0);
-- Import excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2023112311235610016', '2023112311235610010', 'Import excel_Notification ABS', NULL, NULL, 0, NULL, NULL, 2, 'notification:abw_notification:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2023-11-23 11:23:01', NULL, NULL, 0, 0, '1', 0);