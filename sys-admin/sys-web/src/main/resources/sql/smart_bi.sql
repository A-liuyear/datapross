SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- 如果不存在数据库smart_bi,创建数据库并选择该数据库
CREATE DATABASE IF NOT EXISTS `smart_bi`;
USE `smart_bi`;

-- ----------------------------
-- 系统菜单表结构定义
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` int(0) NOT NULL AUTO_INCREMENT,
  `parent_id` int(0) NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `parent_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `order_num` int(0) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `v_show` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- 系统菜单表初始数据插入
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, '系统管理', 'sys:manage', 'system', '/system', '', 'Setting', '顶级菜单', 1, '2024-11-14 17:06:32', '2024-11-14 17:06:32', '0', 2);
INSERT INTO `sys_menu` VALUES (2, 1, '用户管理', 'sys:user:index', 'userList', '/userList', 'views/system/User/UserList.vue', 'UserFilled', '系统管理', 2, '2024-11-14 17:13:22', '2024-11-14 17:13:22', '1', 2);
INSERT INTO `sys_menu` VALUES (3, 1, '角色管理', 'sys:role:index', 'roleList', '/roleList', 'views/system/Role/RoleList.vue', 'Wallet', '系统管理', 3, '2024-11-14 17:21:00', '2024-11-14 17:21:00', '1', 2);
INSERT INTO `sys_menu` VALUES (4, 1, '菜单管理', 'sys:menu:index', 'menuList', '/menuList', 'views/system/Menu/MenuList.vue', 'Menu', '系统管理', 4, '2024-11-14 17:24:09', '2024-11-20 18:06:10', '1', 2);
INSERT INTO `sys_menu` VALUES (5, 2, '新增', 'sys:user:add', '', '', '', '', '用户管理', 21, '2024-11-14 17:29:41', '2024-11-14 17:29:41', '2', 2);
INSERT INTO `sys_menu` VALUES (7, 2, '编辑', 'sys:user:edit', '', '', '', '', '用户管理', 22, '2024-11-20 18:37:29', '2024-11-20 18:37:29', '2', 2);
INSERT INTO `sys_menu` VALUES (8, 2, '删除', 'sys:user:delete', '', '', '', '', '用户管理', 23, '2024-11-20 18:37:51', '2024-11-20 18:37:51', '2', 2);
INSERT INTO `sys_menu` VALUES (9, 3, '新增', 'sys:role:add', '', '', '', '', '角色管理', 31, '2024-11-20 18:38:34', '2024-11-20 18:38:34', '2', 2);
INSERT INTO `sys_menu` VALUES (10, 3, '编辑', 'sys:role:edit', '', '', '', '', '角色管理', 32, '2024-11-20 18:38:53', '2024-11-20 18:38:53', '2', 2);
INSERT INTO `sys_menu` VALUES (11, 3, '删除', 'sys:role:delete', '', '', '', '', '角色管理', 33, '2024-11-20 18:39:16', '2024-11-20 18:39:16', '2', 2);
INSERT INTO `sys_menu` VALUES (12, 4, '新增', 'sys:menu:add', '', '', '', '', '菜单管理', 41, '2024-11-20 18:39:51', '2024-11-20 18:39:51', '2', 2);
INSERT INTO `sys_menu` VALUES (13, 4, '编辑', 'sys:menu:edit', '', '', '', '', '菜单管理', 42, '2024-11-20 18:40:12', '2024-11-20 18:40:12', '2', 2);
INSERT INTO `sys_menu` VALUES (14, 4, '删除', 'sys:menu:delete', '', '', '', '', '菜单管理', 43, '2024-11-20 18:40:36', '2024-11-20 18:40:36', '2', 2);
INSERT INTO `sys_menu` VALUES (15, 2, '重置密码', 'sys:user:resetPwd', '', '', '', '', '用户管理', 24, '2024-11-20 18:41:13', '2024-11-20 18:41:13', '2', 2);
INSERT INTO `sys_menu` VALUES (16, 3, '分配菜单', 'sys:role:assignMenu', '', '', '', '', '角色管理', 34, '2024-11-20 18:41:53', '2024-11-20 18:41:53', '2', 2);
INSERT INTO `sys_menu` VALUES (18, 19, '评价信息', 'sys:evaluate:manage', 'evaluateIndex', '/evaluateIndex', 'views/evaluate/evaluateManage.vue', 'Star', '评价管理', 51, '2025-04-03 17:29:03', '2025-04-03 17:32:11', '1', 2);
INSERT INTO `sys_menu` VALUES (19, 0, '评价管理', 'sys:evaluate:index', 'evaluateManage', '/evaluate', '', 'StarFilled', '顶级菜单', 5, '2025-04-03 17:31:36', '2025-04-03 17:31:36', '0', 2);
INSERT INTO `sys_menu` VALUES (20, 19, '新增评价', 'sys:evaluateAdd:add', 'evaluateAdd', '/evaluateAdd', 'views/evaluate/evaluateAdd.vue', 'UploadFilled', '评价管理', 52, '2025-04-03 18:26:54', '2025-04-03 18:27:14', '1', 1);
INSERT INTO `sys_menu` VALUES (21, 0, '数据管理', 'sys:manage:index', 'dataManage', '/dataManage', '', 'DataLine', '顶级菜单', 6, '2025-04-03 19:58:08', '2025-04-03 19:58:08', '0', 2);
INSERT INTO `sys_menu` VALUES (22, 21, '发布数据', 'sys:dataList:list', 'dataList', '/dataList', 'views/data/Data.vue', 'DataAnalysis', '数据管理', 61, '2025-04-03 19:59:26', '2025-04-05 15:09:34', '1', 2);
INSERT INTO `sys_menu` VALUES (23, 22, '新增', 'sys:data:add', '', '', '', '', '数据列表', 62, '2025-04-03 20:00:47', '2025-04-03 20:00:47', '2', 2);
INSERT INTO `sys_menu` VALUES (24, 22, '编辑', 'sys:data:edit', '', '', '', '', '数据列表', 63, '2025-04-03 20:01:10', '2025-04-03 20:01:10', '2', 2);
INSERT INTO `sys_menu` VALUES (25, 22, '删除', 'sys:data:delete', '', '', '', '', '数据列表', 64, '2025-04-03 20:01:26', '2025-04-03 20:01:26', '2', 2);
INSERT INTO `sys_menu` VALUES (26, 21, '智能分析', 'sys:smartAI:index', 'smartAi', '/smartAi', 'views/evaluate/smartAi.vue', 'HelpFilled', '数据管理', 66, '2025-04-04 20:48:19', '2025-04-04 20:48:19', '1', 2);
INSERT INTO `sys_menu` VALUES (27, 0, '我的购买', 'sys:order:index', 'order', '/order', '', 'Discount', '顶级菜单', 7, '2025-04-05 00:23:03', '2025-04-05 00:23:41', '0', 2);
INSERT INTO `sys_menu` VALUES (28, 27, '购买记录', 'sys:order:orderList', 'orderList', '/orderList', 'views/order/order.vue', 'HelpFilled', '我的购买', 71, '2025-04-05 00:24:50', '2025-04-05 00:24:50', '1', 2);
INSERT INTO `sys_menu` VALUES (29, 28, '下载', 'sys:order:download', '', '', '', '', '购买记录', 72, '2025-04-05 00:33:08', '2025-04-05 00:33:14', '2', 2);
INSERT INTO `sys_menu` VALUES (30, 0, '我的图表', 'sys:chart:index', 'myChart', '/myChart', '', 'DataAnalysis', '顶级菜单', 8, '2025-04-09 09:36:52', '2025-04-09 09:37:15', '0', 2);
INSERT INTO `sys_menu` VALUES (31, 30, '图表列表', 'sys:myChart:list', 'myChartList', '/myChartList', 'views/chart/myChart.vue', 'DataLine', '我的图表', 81, '2025-04-09 09:43:58', '2025-04-09 09:43:58', '1', 2);
INSERT INTO `sys_menu` VALUES (32, 21, '图片理解', 'sys:imageAnalysis:index', 'imageAnalysis', '/imageAnalysis', 'views/evaluate/imageAnalysis.vue', 'TrendCharts', '数据管理', 82, '2025-04-09 12:29:57', '2025-04-09 12:29:57', '1', 2);
INSERT INTO `sys_menu` VALUES (33, 30, '图片理解', 'sys:image:index', 'imageAi', '/imageAi', 'views/chart/imageList.vue', 'HelpFilled', '我的图表', 9, '2025-04-09 20:32:01', '2025-04-09 20:32:01', '1', 2);
INSERT INTO `sys_menu` VALUES (34, 21, '智能异步分析', 'sys:smartAsync:index', 'smartAsync', '/smartAsync', 'views/evaluate/smartAiAsync.vue', 'Discount', '数据管理', 67, '2025-04-16 18:37:42', '2025-04-16 18:39:50', '1', 2);
INSERT INTO `sys_menu` VALUES (35, 21, '图片异步理解', 'sys:imageAnalysisAsync:index', 'imageAnalysisAsync', '/imageAnalysisAsync', 'views/evaluate/imageAnalysisAsync.vue', 'Link', '数据管理', 83, '2025-04-16 18:39:17', '2025-04-16 18:40:48', '1', 2);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` int(0) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (7, '普通人员', NULL, '默认用户角色 误删', '2024-11-20 18:50:19', '2025-04-16 17:25:36');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_menu_id` int(0) NOT NULL AUTO_INCREMENT,
  `menu_id` int(0) NULL DEFAULT NULL,
  `role_id` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`role_menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
-- 系统角色菜单关联表初始数据记录
INSERT INTO `sys_role_menu` VALUES (12, 3, 6);
INSERT INTO `sys_role_menu` VALUES (13, 4, 6);
INSERT INTO `sys_role_menu` VALUES (14, 1, 6);
INSERT INTO `sys_role_menu` VALUES (56, 19, 7);
INSERT INTO `sys_role_menu` VALUES (57, 18, 7);
INSERT INTO `sys_role_menu` VALUES (58, 20, 7);
INSERT INTO `sys_role_menu` VALUES (59, 21, 7);
INSERT INTO `sys_role_menu` VALUES (60, 22, 7);
INSERT INTO `sys_role_menu` VALUES (61, 23, 7);
INSERT INTO `sys_role_menu` VALUES (62, 24, 7);
INSERT INTO `sys_role_menu` VALUES (63, 25, 7);
INSERT INTO `sys_role_menu` VALUES (64, 26, 7);
INSERT INTO `sys_role_menu` VALUES (65, 32, 7);
INSERT INTO `sys_role_menu` VALUES (66, 34, 7);
INSERT INTO `sys_role_menu` VALUES (67, 35, 7);
INSERT INTO `sys_role_menu` VALUES (68, 27, 7);
INSERT INTO `sys_role_menu` VALUES (69, 28, 7);
INSERT INTO `sys_role_menu` VALUES (70, 29, 7);
INSERT INTO `sys_role_menu` VALUES (71, 30, 7);
INSERT INTO `sys_role_menu` VALUES (72, 31, 7);
INSERT INTO `sys_role_menu` VALUES (73, 33, 7);


-- ----------------------------
-- 用户表结构
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` int(0) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sex` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `is_admin` tinyint(0) NULL DEFAULT NULL,
  `is_account_non_expired` tinyint(0) NULL DEFAULT NULL,
  `is_account_non_locked` tinyint(0) NULL DEFAULT NULL,
  `is_credentials_non_expired` tinyint(0) NULL DEFAULT NULL,
  `is_enabled` tinyint(0) NULL DEFAULT NULL,
  `nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `point_num` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- 用户表记录
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '4QrcOUm6Wau+VuBX8g+IPg==', '111', '111', '0', 1, 1, 1, 1, 1, '管理员', '2024-11-20 18:53:02', '2025-04-04 15:48:32', 40);
INSERT INTO `sys_user` VALUES (14, 'user', 'UnwfgaITENU2/oYJV6Jmwg==', '19934343434', '1111@qq.com', '0', 0, 1, 1, 1, 1, 'user', '2025-04-04 15:49:16', '2025-04-04 15:49:16', 100);
INSERT INTO `sys_user` VALUES (17, 'test', '4QrcOUm6Wau+VuBX8g+IPg==', NULL, NULL, NULL, NULL, 1, 1, 1, 1, 'test', '2025-04-16 17:36:20', '2025-04-16 17:36:20', 0);

-- ----------------------------
-- 用户角色表结构
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_role_id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NULL DEFAULT NULL,
  `role_id` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`user_role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- 用户角色表记录
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (39, 13, 7);
INSERT INTO `sys_user_role` VALUES (40, 12, 7);
INSERT INTO `sys_user_role` VALUES (41, 14, 7);

-- ----------------------------
-- 图表信息表结构
-- ----------------------------
DROP TABLE IF EXISTS `t_chart`;
CREATE TABLE `t_chart`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标名称',
  `goal` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '分析目标',
  `chart_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '图表数据',
  `chart_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图表类型',
  `gen_chart` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI生成的图表信息',
  `gen_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI生成的分析结果',
  `status` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'wait' COMMENT '图表的状态（wait,succeed,failed,running）',
  `exec_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '执行信息',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '创建的用户Id',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_delete` tinyint(0) NULL DEFAULT 0 COMMENT '是否删除（0-不删除 1-删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图表信息表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Records of t_chart
-- ----------------------------
-- 插入一条关于用户趋势的记录，包含图表的详细配置和分析结论

INSERT INTO `t_chart` VALUES (3, '用户趋势', '请分析用户趋势', '日期,用户数\n1号,10\n2号,20\n3号,30\n4号,70\n5号,20\n6号,29\n7号,24\n8号,31\n9号,40\n10号,38\n11号,43\n', '柱状图', '{\r\n  \"title\" : {\r\n    \"text\" : \"用户趋势分析\"\r\n  },\r\n  \"tooltip\" : {\r\n    \"trigger\" : \"axis\"\r\n  },\r\n  \"legend\" : {\r\n    \"data\" : [ \"用户数\" ]\r\n  },\r\n  \"xAxis\" : {\r\n    \"type\" : \"category\",\r\n    \"data\" : [ \"1号\", \"2号\", \"3号\", \"4号\", \"5号\", \"6号\", \"7号\", \"8号\", \"9号\", \"10号\", \"11号\" ]\r\n  },\r\n  \"yAxis\" : {\r\n    \"type\" : \"value\"\r\n  },\r\n  \"series\" : [ {\r\n    \"name\" : \"用户数\",\r\n    \"type\" : \"bar\",\r\n    \"data\" : [ 10, 20, 30, 70, 20, 29, 24, 31, 40, 38, 43 ],\r\n    \"toolbox\" : {\r\n      \"feature\" : {\r\n        \"saveAsImage\" : { }\r\n      }\r\n    }\r\n  } ],\r\n  \"toolbox\" : {\r\n    \"feature\" : {\r\n      \"saveAsImage\" : { }\r\n    }\r\n  }\r\n}', '分析结论：\n1. 用户数量在4号达到峰值，为70人。\n2. 3号和8号用户数量接近，分别为30人和31人。\n3. 1号和2号用户数量较少，分别为10人和20人。\n4. 5号、6号和7号用户数量相对较低，分别为20人、29人和24人。\n5. 9号和10号用户数量分别为40人和38人，相对较高。\n6. 11号用户数量为43人，较10号有所增加。', 'succeed', NULL, 13, '2025-04-04 23:08:48', '2025-04-04 23:08:48', 0);

-- ----------------------------
-- Table structure for t_data_file
-- ----------------------------
-- 删除并重新创建一个名为 t_data_file 的表，用于存储数据文件的相关信息

DROP TABLE IF EXISTS `t_data_file`;
CREATE TABLE `t_data_file`  (
  `data_id` bigint(0) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `file_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL,
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `image_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `data_total` bigint(0) NULL DEFAULT NULL,
  `user_id` bigint(0) NULL DEFAULT NULL,
  `status` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`data_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Records of t_data_file
-- ----------------------------
# INSERT INTO `t_data_file` VALUES (13, '8888模板下载.xlsx', 'xlsx', '2025-04-04 21:38:37', '2025-04-08 20:56:15', 10.00, '测试数据', '20250404213818_8888模板下载.xlsx', 'https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f', '基金数据', 2, 13, '0');

-- ----------------------------
-- 表结构 for t_data_order
-- ----------------------------
DROP TABLE IF EXISTS `t_data_order`;
CREATE TABLE `t_data_order`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `data_id` bigint(0) NULL DEFAULT NULL,
  `pay_amount` bigint(0) NULL DEFAULT NULL,
  `user_id` bigint(0) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `data_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `point_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_data_order
-- ----------------------------
# INSERT INTO `t_data_order` VALUES (5, 13, 10, 1, '2025-04-05 15:14:02', '2025-04-05 15:14:02', '8888模板下载.xlsx', '支出');

-- ----------------------------
-- 评价表结构
-- ----------------------------
DROP TABLE IF EXISTS `t_evaluate`;
CREATE TABLE `t_evaluate`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `data_id` bigint(0) NULL DEFAULT NULL,
  `score` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `create_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_user` bigint(0) NULL DEFAULT NULL,
  `data_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- 评价记录
-- ----------------------------
# INSERT INTO `t_evaluate` VALUES (1, 16, '4.0', '很好', '2025-04-08 12:10:03', '管理员', 1, '网站数据.xlsx');
# INSERT INTO `t_evaluate` VALUES (2, 16, '5.0', 'nice', '2025-04-08 14:55:31', '管理员', 1, '网站数据.xlsx');


-- ----------------------------
-- 定义并创建存储图像AI数据的表结构
-- ----------------------------
DROP TABLE IF EXISTS `t_image_ai`;
CREATE TABLE `t_image_ai`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `image_url` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_id` bigint(0) NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- 向图像AI表中插入初始记录
-- ----------------------------
INSERT INTO `t_image_ai` VALUES (1, 'https://images.unsplash.com/photo-1506744038136-46273834b3fb', 1, '这张图片展示了一片宁静的湖泊，湖面平静如镜，反射着周围的景色。湖边长满了绿色的植被，远处可以看到一些树木和山丘。天空晴朗，阳光洒在湖面上，形成了一道美丽的光影效果。整个场景给人一种宁静、和谐的感觉。', '2025-04-09 20:50:51');
INSERT INTO `t_image_ai` VALUES (2, 'https://images.unsplash.com/photo-1506744038136-46273834b3fb', 1, '这张图片展示了一片宁静的湖泊，湖面上有轻微的波纹，反射着天空的光线。湖边长满了绿色的植被，给人一种自然和谐的感觉。整个场景显得非常宁静和平静。', '2025-04-09 21:10:42');
INSERT INTO `t_image_ai` VALUES (3, 'https://images.unsplash.com/photo-1506744038136-46273834b3fb', 1, '这张图片展示了一个宁静的户外场景，背景是一片开阔的草地，天空晴朗，阳光明媚。在前景中，有一只小狗正在草地上奔跑，显得非常活泼和快乐。远处可以看到一些树木和灌木丛，为整个场景增添了一些自然的氛围。整体上，这张图片传达了一种轻松愉快的氛围，让人感受到大自然的美好。', '2025-04-10 09:13:14');

-- ----------------------------
-- 定义并创建存储消息日志的表结构
-- ----------------------------
DROP TABLE IF EXISTS `t_msg_log`;
CREATE TABLE `t_msg_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NULL DEFAULT NULL,
  `from_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `to_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `subject` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- 向消息日志表中插入初始记录
-- ----------------------------
INSERT INTO `t_msg_log` VALUES (1, NULL, '19939406893@163.com', '19939406893@163.com', '生成成功邮件', '2025-04-09 11:13:53', '2025-04-09 11:13:53', '第1部分【【【【【【\n{\n    \"title\": {\n        \"text\": \"用户趋势分析\"\n    },\n    \"tooltip\": {\n        \"trigger\": \"item\"\n    },\n    \"legend\": {\n        \"data\": [\"用户数\"]\n    },\n    \"series\": [\n        {\n            \"name\": \"用户数\",\n            \"type\": \"pie\",\n            \"radius\": \"55%\",\n            \"center\": [\"50%\", \"60%\"],\n            \"data\": [\n                {\"value\": 10, \"name\": \"1号\"},\n                {\"value\": 20, \"name\": \"2号\"},\n                {\"value\": 30, \"name\": \"3号\"},\n                {\"value\": 70, \"name\": \"4号\"},\n                {\"value\": 20, \"name\": \"5号\"},\n                {\"value\": 29, \"name\": \"6号\"},\n                {\"value\": 24, \"name\": \"7号\"},\n                {\"value\": 31, \"name\": \"8号\"},\n                {\"value\": 40, \"name\": \"9号\"},\n                {\"value\": 38, \"name\": \"10号\"},\n                {\"value\": 43, \"name\": \"11号\"}\n            ],\n            \"emphasis\": {\n                \"itemStyle\": {\n                    \"shadowBlur\": 10,\n                    \"shadowOffsetX\": 0,\n                    \"shadowColor\": \"rgba(0, 0, 0, 0.5)\"\n                }\n            }\n        }\n    ]\n}\n】】】】】\n第2部分【【【【【【\n分析结论：\n- 4号用户数量最多，达到70人，为一周中的峰值。\n- 1号和5号用户数量最少，均为10人。\n- 6号至11号用户数量逐渐增加，但增长速度较缓。\n- 8号和9号用户数量较为接近，分别为31人和40人。');
INSERT INTO `t_msg_log` VALUES (2, 1, '19939406893@163.com', '19939406893@163.com', '生成成功邮件', '2025-04-09 16:22:07', '2025-04-09 16:22:07', '第1部分【【【【【【\n{\n    \"title\": {\n        \"text\": \"用户趋势分析\"\n    },\n    \"tooltip\": {},\n    \"legend\": {\n        \"data\": [\"用户数\"]\n    },\n    \"xAxis\": {\n        \"data\": [\"1号\", \"2号\", \"3号\", \"4号\", \"5号\", \"6号\", \"7号\", \"8号\", \"9号\", \"10号\", \"11号\"]\n    },\n    \"yAxis\": {},\n    \"series\": [{\n        \"name\": \"用户数\",\n        \"type\": \"bar\",\n        \"data\": [10, 20, 30, 70, 20, 29, 24, 31, 40, 38, 43]\n    }]\n}\n】】】】】\n\n第2部分【【【【【【\n分析结论：\n1. 用户数量在3号达到峰值，为70人。\n2. 4号用户数量大幅下降，可能由于节假日或特殊事件。\n3. 5号和6号用户数量相对稳定。\n4. 7号至9号用户数量有所上升，但增长速度放缓。\n5. 10号和11号用户数量再次增长，但增长速度较慢。');
INSERT INTO `t_msg_log` VALUES (3, 1, '19939406893@163.com', '19939406893@163.com', '生成成功邮件', '2025-04-11 19:02:06', '2025-04-11 19:02:06', '第1部分【【【【【【\n{\n    \"title\": {\n        \"text\": \"用户趋势分析\"\n    },\n    \"tooltip\": {\n        \"trigger\": \"axis\"\n    },\n    \"legend\": {\n        \"data\": [\"用户数\"]\n    },\n    \" xAxis\": {\n        \"type\": \"category\",\n        \"data\": [\"1号\", \"2号\", \"3号\", \"4号\", \"5号\", \"6号\", \"7号\", \"8号\", \"9号\", \"10号\", \"11号\"]\n    },\n    \" yAxis\": {\n        \"type\": \"value\"\n    },\n    \"series\": [{\n        \"name\": \"用户数\",\n        \"type\": \"line\",\n        \"data\": [10, 20, 30, 70, 20, 29, 24, 31, 40, 38, 43],\n        \"markPoint\": {\n            \"data\": [\n                { \"type\": \"max\", \"name\": \"最大值\" },\n                { \"type\": \"min\", \"name\": \"最小值\" }\n            ]\n        },\n        \"markLine\": {\n            \"data\": [\n                { \"type\": \"average\", \"name\": \"平均值\" }\n            ]\n        }\n    }]\n}\n】】】】】\n\n第2部分【【【【【【\n{用户数量在4号达到峰值70，随后有所下降，但在10号和11号又有所回升。整体趋势呈现先上升后下降再上升的趋势。}】】】】】');
INSERT INTO `t_msg_log` VALUES (4, 1, '19939406893@163.com', '19939406893@163.com', '生成成功邮件', '2025-04-16 19:03:17', '2025-04-16 19:03:17', '第1部分【【【【【【\n{\n  \"title\": {\n    \"text\": \"用户趋势分析\"\n  },\n  \"tooltip\": {\n    \"trigger\": \"axis\"\n  },\n  \"legend\": {\n    \"data\": [\"用户数\"]\n  },\n  \"xAxis\": {\n    \"type\": \"category\",\n    \"data\": [\"1号\", \"2号\", \"3号\", \"4号\", \"5号\", \"6号\", \"7号\", \"8号\", \"9号\", \"10号\", \"11号\"]\n  },\n  \"yAxis\": {\n    \"type\": \"value\"\n  },\n  \"series\": [{\n    \"name\": \"用户数\",\n    \"type\": \"line\",\n    \"data\": [10, 20, 30, 70, 20, 29, 24, 31, 40, 38, 43]\n  }]\n}\n】】】】】\n第2部分【【【【【【\n用户趋势分析显示，在4号达到峰值70人后，用户数有所下降，但在9号和10号又有所回升，整体趋势呈现波动上升。');

SET FOREIGN_KEY_CHECKS = 1;

