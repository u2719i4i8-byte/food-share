-- 美食分享系统完整分类和美食数据
-- 请在MySQL Workbench或Navicat中执行此脚本

-- 清空现有数据（可选，谨慎使用）
-- DELETE FROM gourmet;
-- DELETE FROM category;

-- 1. 插入所有分类数据
INSERT INTO category (id, name) VALUES 
-- 菜系分类 (1-16)
(1, '家常菜'),
(2, '川菜'),
(3, '粤菜'),
(4, '湘菜'),
(5, '鲁菜'),
(6, '江浙菜'),
(7, '西北菜'),
(8, '东北菜'),
(9, '闽南菜'),
(10, '云南菜'),
(11, '新疆菜'),
(12, '韩式'),
(13, '日式'),
(14, '泰式'),
(15, '意式'),
(16, '美式'),
-- 场景分类 (17-25)
(17, '早餐'),
(18, '下午茶'),
(19, '夜宵'),
(20, '一人食'),
(21, '家庭聚餐'),
(22, '亲子友好'),
(23, '朋友小聚'),
(24, '节日家宴'),
(25, '户外野餐'),
-- 食材分类 (26-36)
(26, '海鲜'),
(27, '肉食'),
(28, '素食'),
(29, '菌菇'),
(30, '豆制品'),
(31, '根茎类'),
(32, '叶菜类'),
(33, '禽蛋类'),
(34, '谷物'),
(35, '坚果'),
(36, '时令水果'),
-- 做法分类 (37-45)
(37, '烧烤'),
(38, '火锅'),
(39, '煎炸'),
(40, '蒸煮'),
(41, '焖炖'),
(42, '凉拌'),
(43, '烘焙'),
(44, '爆炒'),
(45, '煲汤'),
-- 特色分类 (46-52)
(46, '非遗'),
(47, '网红'),
(48, '新手友好'),
(49, '减脂轻食'),
(50, '下饭神器'),
(51, '下酒小菜'),
(52, '复刻经典')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 2. 插入美食数据（请将 user_id = 1 改为实际的用户ID）

-- ========== 菜系分类美食 ==========

-- 家常菜
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(1, 1, '红烧肉', '【食材】五花肉500g、冰糖30g、生抽2勺、老抽1勺、料酒2勺、八角2个、桂皮1块、香叶2片、姜片5片、葱段适量

【步骤】
1. 五花肉切成3厘米见方的块，冷水下锅焯水去血沫，捞出沥干备用。
2. 锅中放少许油，放入冰糖小火炒出糖色，炒至枣红色冒泡。
3. 放入五花肉翻炒上色，加入料酒、生抽、老抽翻炒均匀。
4. 加入八角、桂皮、香叶、姜片、葱段，倒入没过肉的热水。
5. 大火烧开后转小火炖煮1小时，最后大火收汁即可。

【小贴士】炒糖色时火候要小，糖色炒过了会发苦。', 'https://images.unsplash.com/photo-1623689046286-01c05a5c74e8?w=400&h=300&fit=crop', true, true, NOW());

-- 川菜
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(2, 1, '麻婆豆腐', '【食材】嫩豆腐1块、牛肉末100g、豆瓣酱2勺、花椒粉1勺、辣椒面1勺、蒜末适量、葱花适量、生抽1勺、水淀粉适量

【步骤】
1. 豆腐切成2厘米见方的块，淡盐水浸泡备用。
2. 锅中放油，放入牛肉末炒散变色。
3. 加入豆瓣酱炒出红油，放入蒜末爆香。
4. 加入适量水，放入豆腐块，加生抽调味。
5. 中火煮3分钟，淋入水淀粉勾芡。
6. 撒上花椒粉、辣椒面和葱花即可。

【小贴士】豆腐提前用盐水泡可以去除豆腥味，不易碎。', 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=400&h=300&fit=crop', true, true, NOW());

-- 粤菜
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(3, 1, '白切鸡', '【食材】三黄鸡1只、姜片5片、葱段适量、料酒2勺、蘸料：姜蓉、葱花、盐、热油

【步骤】
1. 三黄鸡处理干净，冷水下锅，放入姜片、葱段、料酒。
2. 大火烧开后转小火，保持微沸状态煮15分钟。
3. 关火焖15分钟，取出放入冰水中浸泡至冷却。
4. 鸡肉斩块摆盘。
5. 姜蓉、葱花加盐，淋上热油制成蘸料。

【小贴士】冰水浸泡可以让鸡皮更加爽脆。', 'https://images.unsplash.com/photo-1598515214211-89d3c73ae83b?w=400&h=300&fit=crop', true, true, NOW());

-- 湘菜
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(4, 1, '剁椒鱼头', '【食材】胖头鱼鱼头1个、剁椒3勺、蒜末适量、葱花适量、生抽2勺、料酒1勺、姜片适量

【步骤】
1. 鱼头洗净对半切开，用料酒、姜片腌制15分钟去腥。
2. 剁椒加蒜末拌匀。
3. 将鱼头摆盘，铺上剁椒。
4. 上锅大火蒸10-12分钟。
5. 撒上葱花，淋上热油即可。

【小贴士】鱼头要选新鲜的，蒸的时间不宜过长。', 'https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?w=400&h=300&fit=crop', true, true, NOW());

-- 鲁菜
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(5, 1, '糖醋鲤鱼', '【食材】鲤鱼1条、番茄酱3勺、白糖3勺、醋2勺、淀粉适量、姜片适量、葱段适量

【步骤】
1. 鲤鱼处理干净，两面划花刀，用盐、料酒腌制。
2. 鱼身拍上干淀粉。
3. 油温七成热，将鱼炸至金黄酥脆，捞出摆盘。
4. 锅中留底油，放入番茄酱、白糖、醋、少许水熬成糖醋汁。
5. 将糖醋汁浇在鱼身上即可。

【小贴士】炸鱼时油温要高，鱼才能酥脆。', 'https://images.unsplash.com/photo-1534604973900-c43ab4c2e0ab?w=400&h=300&fit=crop', true, true, NOW());

-- 江浙菜
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(6, 1, '东坡肉', '【食材】五花肉500g、黄酒200ml、生抽2勺、老抽1勺、冰糖30g、姜片适量、葱段适量

【步骤】
1. 五花肉切成4厘米见方的块，焯水去血沫。
2. 砂锅底部铺上姜片和葱段。
3. 将肉块皮朝下码放，加入黄酒、生抽、老抽、冰糖。
4. 大火烧开后转小火炖2小时。
5. 最后大火收汁，肉块皮朝上装盘。

【小贴士】用黄酒代替水，肉更香更软烂。', 'https://images.unsplash.com/photo-1623689046286-01c05a5c74e8?w=400&h=300&fit=crop', true, true, NOW());

-- 西北菜
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(7, 1, '羊肉泡馍', '【食材】羊肉500g、馍2个、粉丝50g、香菜适量、葱花适量、盐适量、辣椒油适量

【步骤】
1. 羊肉冷水下锅，加姜片炖煮2小时至软烂。
2. 馍掰成小块备用。
3. 粉丝泡软，香菜切碎。
4. 将馍块放入碗中，加入粉丝、羊肉片。
5. 浇上热羊肉汤，撒上香菜、葱花，淋辣椒油即可。

【小贴士】馍要掰得越小越好，更容易入味。', 'https://images.unsplash.com/photo-1547592180-85f173990554?w=400&h=300&fit=crop', true, true, NOW());

-- 东北菜
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(8, 1, '锅包肉', '【食材】猪里脊肉300g、土豆淀粉100g、白糖3勺、醋2勺、生抽1勺、胡萝卜丝适量、葱丝适量

【步骤】
1. 猪里脊切薄片，用盐、料酒腌制。
2. 土豆淀粉加水调成糊，肉片裹上淀粉糊。
3. 油温六成热，下肉片炸至定型捞出。
4. 油温升高复炸至金黄酥脆。
5. 锅中放糖、醋、生抽熬成糖醋汁，放入肉片、胡萝卜丝、葱丝快速翻炒即可。

【小贴士】复炸是关键，才能外酥里嫩。', 'https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=400&h=300&fit=crop', true, true, NOW());

-- 闽南菜
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(9, 1, '佛跳墙', '【食材】鲍鱼4个、海参2条、花胶50g、干贝10粒、排骨200g、鸡腿2个、花雕酒适量、姜片适量

【步骤】
1. 鲍鱼、海参、花胶提前泡发处理。
2. 排骨、鸡腿焯水去血沫。
3. 砂锅底部放姜片，依次放入排骨、鸡腿、干贝。
4. 加入花雕酒和适量水，小火炖1小时。
5. 加入鲍鱼、海参、花胶继续炖30分钟。
6. 加盐调味即可。

【小贴士】食材要提前处理好，炖煮时间要够。', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400&h=300&fit=crop', true, true, NOW());

-- 云南菜
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(10, 1, '过桥米线', '【食材】米线200g、鸡汤500ml、生鹌鹑蛋2个、里脊肉片50g、鱼片50g、豆苗适量、葱花适量

【步骤】
1. 鸡汤烧开，保持滚烫。
2. 米线煮熟过凉水备用。
3. 将鹌鹑蛋打入碗中，放入肉片、鱼片。
4. 倒入滚烫的鸡汤，将肉片烫熟。
5. 加入米线、豆苗、葱花即可。

【小贴士】鸡汤要滚烫，才能烫熟肉片。', 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400&h=300&fit=crop', true, true, NOW());

-- 新疆菜
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(11, 1, '大盘鸡', '【食材】三黄鸡1只、土豆2个、青椒2个、干辣椒10个、花椒适量、豆瓣酱2勺、皮带面适量

【步骤】
1. 鸡肉切块焯水，土豆切块，青椒切块。
2. 锅中放油，放入冰糖炒糖色，放入鸡块翻炒上色。
3. 加入豆瓣酱、干辣椒、花椒炒香。
4. 加入土豆块和适量水，炖煮20分钟。
5. 放入青椒块再煮5分钟。
6. 配上皮带面食用。

【小贴士】土豆不要煮太烂，保持形状。', 'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=400&h=300&fit=crop', true, true, NOW());

-- 韩式
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(12, 1, '韩式炸鸡', '【食材】鸡腿4个、韩式辣酱2勺、蜂蜜1勺、蒜泥适量、生抽1勺、淀粉适量、炸鸡粉适量

【步骤】
1. 鸡腿用盐、蒜泥腌制30分钟。
2. 裹上淀粉和炸鸡粉。
3. 油温170度炸至金黄，复炸一次更酥脆。
4. 韩式辣酱、蜂蜜、生抽、蒜泥调成酱汁。
5. 将炸鸡裹上酱汁即可。

【小贴士】复炸是关键，外酥里嫩。', 'https://images.unsplash.com/photo-1527477396000-e27163b481c2?w=400&h=300&fit=crop', true, true, NOW());

-- 日式
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(13, 1, '日式拉面', '【食材】拉面200g、叉烧肉3片、溏心蛋1个、海苔2片、葱花适量、日式酱油2勺、猪骨汤500ml

【步骤】
1. 猪骨汤烧开，加入日式酱油调味。
2. 拉面煮熟捞出放入碗中。
3. 倒入热汤。
4. 摆上叉烧肉、溏心蛋、海苔。
5. 撒上葱花即可。

【小贴士】溏心蛋煮6分30秒，过冰水。', 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400&h=300&fit=crop', true, true, NOW());

-- 泰式
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(14, 1, '冬阴功汤', '【食材】大虾6只、草菇100g、香茅2根、南姜适量、柠檬叶4片、冬阴功酱2勺、椰浆100ml、青柠汁适量

【步骤】
1. 大虾去虾线，草菇洗净对半切。
2. 锅中放水，加入香茅、南姜、柠檬叶煮开。
3. 加入冬阴功酱搅拌均匀。
4. 放入草菇和大虾煮熟。
5. 加入椰浆和青柠汁调味即可。

【小贴士】青柠汁最后加，保持香味。', 'https://images.unsplash.com/photo-1548943487-a2e4e43b4853?w=400&h=300&fit=crop', true, true, NOW());

-- 意式
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(15, 1, '意式披萨', '【食材】披萨饼底1个、番茄酱3勺、马苏里拉芝士100g、培根3片、蘑菇适量、青椒适量

【步骤】
1. 披萨饼底刷上番茄酱。
2. 撒上一层马苏里拉芝士。
3. 铺上培根、蘑菇片、青椒圈。
4. 再撒上一层芝士。
5. 烤箱200度烤15分钟即可。

【小贴士】芝士要撒够，拉丝效果才好。', 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&h=300&fit=crop', true, true, NOW());

-- 美式
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(16, 1, '美式汉堡', '【食材】牛肉饼1个、汉堡胚1个、生菜2片、番茄2片、芝士片1片、洋葱圈适量、番茄酱适量

【步骤】
1. 牛肉饼煎至两面金黄，放上芝士片融化。
2. 汉堡胚切开，烤至微焦。
3. 底部汉堡胚放上生菜、番茄片。
4. 放上牛肉饼、洋葱圈。
5. 挤上番茄酱，盖上顶部汉堡胚即可。

【小贴士】牛肉饼不要压扁，保持多汁。', 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400&h=300&fit=crop', true, true, NOW());

-- ========== 场景分类美食 ==========

-- 早餐
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(17, 1, '营养早餐三明治', '【食材】吐司2片、鸡蛋1个、生菜2片、火腿2片、芝士片1片、沙拉酱适量

【步骤】
1. 鸡蛋煎成荷包蛋。
2. 吐司片烤至微焦。
3. 一片吐司上放生菜、火腿、芝士片、荷包蛋。
4. 挤上沙拉酱，盖上另一片吐司。
5. 对角切开即可。

【小贴士】吐司烤一下更香脆。', 'https://images.unsplash.com/photo-1525351484163-7529414344d8?w=400&h=300&fit=crop', true, true, NOW());

-- 下午茶
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(18, 1, '提拉米苏', '【食材】马斯卡彭芝士250g、淡奶油150ml、手指饼干适量、浓缩咖啡100ml、可可粉适量、朗姆酒1勺

【步骤】
1. 淡奶油打发，与马斯卡彭芝士混合。
2. 浓缩咖啡加入朗姆酒。
3. 手指饼干蘸咖啡，铺在容器底部。
4. 铺上一层芝士糊，再铺一层饼干。
5. 重复层次，冷藏4小时。
6. 吃前撒上可可粉。

【小贴士】饼干蘸咖啡要快，不要泡软。', 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400&h=300&fit=crop', true, true, NOW());

-- 夜宵
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(19, 1, '麻辣小龙虾', '【食材】小龙虾500g、麻辣火锅底料50g、啤酒1罐、干辣椒10个、花椒适量、蒜头1个、姜片适量

【步骤】
1. 小龙虾刷洗干净，去虾线。
2. 锅中放油，放入蒜头、姜片、干辣椒、花椒爆香。
3. 放入火锅底料炒化。
4. 放入小龙虾翻炒上色。
5. 倒入啤酒，焖煮15分钟。
6. 大火收汁即可。

【小贴士】啤酒焖煮去腥增香。', 'https://images.unsplash.com/photo-1559737558-2f5a35f4523b?w=400&h=300&fit=crop', true, true, NOW());

-- 一人食
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(20, 1, '日式蛋包饭', '【食材】米饭1碗、鸡蛋2个、洋葱适量、火腿适量、番茄酱2勺、黄油适量

【步骤】
1. 洋葱、火腿切丁，用黄油炒香。
2. 加入米饭翻炒，加番茄酱调味。
3. 将炒饭盛起整形。
4. 鸡蛋打散，煎成嫩滑蛋皮。
5. 将蛋皮盖在炒饭上，挤上番茄酱。

【小贴士】蛋皮要嫩滑，火候要小。', 'https://images.unsplash.com/photo-1512069772995-ec65ed45afd6?w=400&h=300&fit=crop', true, true, NOW());

-- 家庭聚餐
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(21, 1, '红烧狮子头', '【食材】猪肉馅500g、荸荠5个、鸡蛋1个、生抽2勺、老抽1勺、料酒1勺、淀粉适量、青菜适量

【步骤】
1. 猪肉馅加荸荠碎、鸡蛋、料酒、淀粉搅打上劲。
2. 团成大肉丸，油炸至表面金黄。
3. 锅中放水，加生抽、老抽烧开。
4. 放入肉丸，小火炖30分钟。
5. 青菜焯水摆盘，浇上汤汁。

【小贴士】肉馅要搅打上劲，口感才好。', 'https://images.unsplash.com/photo-1529042410759-befb1204b468?w=400&h=300&fit=crop', true, true, NOW());

-- 亲子友好
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(22, 1, '可爱饭团', '【食材】米饭1碗、海苔适量、火腿适量、芝士片适量、玉米粒适量、沙拉酱适量

【步骤】
1. 米饭拌入玉米粒和沙拉酱。
2. 用模具压出可爱形状，或手捏成圆形。
3. 海苔剪出眼睛、嘴巴。
4. 火腿、芝士做装饰。
5. 装盘即可。

【小贴士】米饭要趁热捏，更容易成型。', 'https://images.unsplash.com/photo-1563292657-59f270c38e4f?w=400&h=300&fit=crop', true, true, NOW());

-- 朋友小聚
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(23, 1, '芝士焗土豆', '【食材】土豆2个、培根2片、马苏里拉芝士100g、黄油适量、黑胡椒适量、葱花适量

【步骤】
1. 土豆蒸熟，对半切开，挖出部分土豆泥。
2. 土豆泥加黄油、黑胡椒拌匀。
3. 培根煎脆切碎。
4. 将土豆泥填回土豆壳，撒上培根和芝士。
5. 烤箱200度烤15分钟至芝士融化。

【小贴士】芝士要多放，拉丝效果才好。', 'https://images.unsplash.com/photo-1518013431117-eb1465fa5752?w=400&h=300&fit=crop', true, true, NOW());

-- 节日家宴
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(24, 1, '清蒸石斑鱼', '【食材】石斑鱼1条、葱丝适量、姜丝适量、蒸鱼豉油3勺、料酒1勺、红椒丝适量

【步骤】
1. 石斑鱼处理干净，两面划花刀。
2. 用料酒、姜片腌制15分钟去腥。
3. 上锅大火蒸8-10分钟。
4. 倒掉蒸出的水，放上葱丝、姜丝、红椒丝。
5. 淋上热油和蒸鱼豉油即可。

【小贴士】蒸鱼时间不宜过长，肉质才嫩。', 'https://images.unsplash.com/photo-1534604973900-c43ab4c2e0ab?w=400&h=300&fit=crop', true, true, NOW());

-- 户外野餐
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(25, 1, '野餐寿司卷', '【食材】寿司米1杯、海苔2片、黄瓜1根、蟹柳3根、蛋皮适量、寿司醋适量

【步骤】
1. 寿司米煮熟，拌入寿司醋晾凉。
2. 海苔铺在寿司帘上，铺上米饭。
3. 中间放上黄瓜条、蟹柳、蛋皮条。
4. 用寿司帘卷紧，切成小段。
5. 装入便当盒即可。

【小贴士】切寿司时刀要沾水，不粘刀。', 'https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=400&h=300&fit=crop', true, true, NOW());

-- ========== 食材分类美食 ==========

-- 海鲜
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(26, 1, '蒜蓉蒸大虾', '【食材】大虾8只、粉丝1把、大蒜1头、生抽2勺、蚝油1勺、葱花适量

【步骤】
1. 大虾开背去虾线，粉丝泡软。
2. 大蒜切成蒜蓉，用油炸至金黄。
3. 粉丝铺盘，放上大虾。
4. 虾背上铺蒜蓉，淋生抽、蚝油。
5. 上锅蒸6分钟，撒葱花即可。

【小贴士】蒸虾时间不宜过长，肉质才嫩。', 'https://images.unsplash.com/photo-1625943553852-781c6dd46faa?w=400&h=300&fit=crop', true, true, NOW());

-- 肉食
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(27, 1, '蜜汁叉烧', '【食材】猪梅花肉500g、叉烧酱3勺、蜂蜜2勺、生抽1勺、料酒1勺

【步骤】
1. 猪梅花肉切条，用叉子扎些小孔方便入味。
2. 加入叉烧酱、蜂蜜、生抽、料酒腌制4小时以上。
3. 烤箱预热200度，肉放烤架上。
4. 烤20分钟，翻面刷腌料再烤15分钟。
5. 取出切片即可食用。

【小贴士】腌制时间越长越入味。', 'https://images.unsplash.com/photo-1544025162-d76694265947?w=400&h=300&fit=crop', true, true, NOW());

-- 素食
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(28, 1, '清炒时蔬', '【食材】西兰花1颗、胡萝卜半根、木耳适量、蒜适量、盐1勺、蚝油1勺

【步骤】
1. 西兰花掰小朵，胡萝卜切片，木耳泡发。
2. 锅中烧开水，焯烫西兰花和胡萝卜。
3. 锅中放油，爆香蒜末。
4. 放入所有蔬菜翻炒。
5. 加盐、蚝油调味即可。

【小贴士】焯水时间要短，保持脆嫩。', 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400&h=300&fit=crop', true, true, NOW());

-- 菌菇
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(29, 1, '香菇滑鸡', '【食材】鸡腿2个、干香菇8朵、姜片适量、生抽2勺、蚝油1勺、淀粉适量

【步骤】
1. 鸡腿切块，用生抽、淀粉腌制。
2. 干香菇泡发切片。
3. 锅中放油，爆香姜片。
4. 放入鸡块翻炒至变色。
5. 加入香菇翻炒，加蚝油调味，焖煮10分钟。

【小贴士】香菇要提前泡发，更香。', 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400&h=300&fit=crop', true, true, NOW());

-- 豆制品
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(30, 1, '家常豆腐', '【食材】老豆腐1块、青椒1个、木耳适量、豆瓣酱1勺、生抽1勺、葱花适量

【步骤】
1. 豆腐切块，煎至两面金黄。
2. 青椒切块，木耳泡发。
3. 锅中放油，放入豆瓣酱炒香。
4. 放入豆腐、青椒、木耳翻炒。
5. 加生抽调味，撒葱花即可。

【小贴士】豆腐先煎，不易碎更香。', 'https://images.unsplash.com/photo-1582576163090-09d3b6f8a969?w=400&h=300&fit=crop', true, true, NOW());

-- 根茎类
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(31, 1, '红烧土豆', '【食材】土豆2个、五花肉100g、生抽2勺、老抽1勺、冰糖适量、葱花适量

【步骤】
1. 土豆去皮切块，五花肉切片。
2. 锅中放油，放入五花肉煸炒出油。
3. 放入土豆块翻炒。
4. 加入生抽、老抽、冰糖和适量水。
5. 焖煮15分钟，大火收汁，撒葱花。

【小贴士】土豆不要切太小，容易煮烂。', 'https://images.unsplash.com/photo-1518133910546-b6c2fb7d79e3?w=400&h=300&fit=crop', true, true, NOW());

-- 叶菜类
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(32, 1, '蒜蓉菠菜', '【食材】菠菜1把、大蒜5瓣、盐1勺、食用油适量

【步骤】
1. 菠菜洗净，大蒜切末。
2. 锅中烧开水，焯烫菠菜30秒，捞出沥干。
3. 锅中放油，爆香蒜末。
4. 放入菠菜快速翻炒。
5. 加盐调味即可。

【小贴士】菠菜焯水去除草酸。', 'https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400&h=300&fit=crop', true, true, NOW());

-- 禽蛋类
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(33, 1, '番茄炒蛋', '【食材】鸡蛋3个、番茄2个、葱花适量、盐1勺、糖1勺、食用油适量

【步骤】
1. 番茄洗净切块，鸡蛋打散加少许盐搅匀。
2. 锅中倒油烧热，倒入蛋液快速翻炒至凝固盛出。
3. 锅中留底油，放入番茄块翻炒出汁。
4. 加入少许盐和糖调味，倒入炒好的鸡蛋翻炒均匀。
5. 撒上葱花即可出锅。

【小贴士】番茄要选熟透的，炒出来汁水更多更甜。', 'https://images.unsplash.com/photo-1482049016gy-2d3b87a86a2c?w=400&h=300&fit=crop', true, true, NOW());

-- 谷物
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(34, 1, '腊味煲仔饭', '【食材】大米1杯、腊肠2根、腊肉适量、青菜适量、鸡蛋1个、生抽2勺

【步骤】
1. 大米洗净，加水浸泡30分钟。
2. 砂锅刷油，放入大米和水煮开。
3. 腊肠、腊肉切片。
4. 米饭水快干时，铺上腊味，打入鸡蛋。
5. 小火焖10分钟，摆上焯水的青菜，淋生抽。

【小贴士】锅底刷油，才有锅巴。', 'https://images.unsplash.com/photo-1512058564366-18510be2db19?w=400&h=300&fit=crop', true, true, NOW());

-- 坚果
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(35, 1, '宫保鸡丁', '【食材】鸡胸肉300g、花生米50g、干辣椒10个、花椒适量、葱段适量、蒜末适量、生抽2勺、醋1勺、糖1勺

【步骤】
1. 鸡胸肉切丁，加料酒、盐、淀粉腌制15分钟。
2. 花生米炸至酥脆备用。
3. 碗中调好料汁：生抽、醋、糖、淀粉、少许水。
4. 锅中放油，放入鸡丁滑炒至变色盛出。
5. 锅中留底油，放入干辣椒、花椒、蒜末爆香。
6. 放入鸡丁翻炒，倒入料汁炒匀，最后放入花生米和葱段即可。

【小贴士】花生米最后放，保持酥脆。', 'https://images.unsplash.com/photo-1525755662778-989d0524087e?w=400&h=300&fit=crop', true, true, NOW());

-- 时令水果
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(36, 1, '水果沙拉', '【食材】苹果1个、香蕉1根、草莓5颗、蓝莓适量、酸奶100g、蜂蜜适量

【步骤】
1. 苹果、香蕉切块，草莓对半切。
2. 将所有水果放入碗中。
3. 淋上酸奶。
4. 撒上蓝莓。
5. 淋上蜂蜜即可。

【小贴士】水果现切现吃，保持新鲜。', 'https://images.unsplash.com/photo-1564093497595-593b6dabbd2d?w=400&h=300&fit=crop', true, true, NOW());

-- ========== 做法分类美食 ==========

-- 烧烤
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(37, 1, '烤羊肉串', '【食材】羊肉500g、孜然粉2勺、辣椒粉1勺、盐适量、洋葱半个

【步骤】
1. 羊肉切块，洋葱切丝。
2. 羊肉用洋葱、盐腌制30分钟。
3. 将羊肉串在签子上。
4. 炭火或烤箱200度烤15分钟。
5. 撒上孜然粉和辣椒粉即可。

【小贴士】烤制过程中要翻面，刷油。', 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=400&h=300&fit=crop', true, true, NOW());

-- 火锅
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(38, 1, '麻辣火锅', '【食材】火锅底料1包、毛肚适量、肥牛卷适量、鸭血1块、豆皮适量、蔬菜适量

【步骤】
1. 火锅底料放入锅中，加水煮开。
2. 准备各种涮菜：毛肚、肥牛卷、鸭血、豆皮、蔬菜等。
3. 将食材依次下锅涮煮。
4. 配上蘸料：蒜泥、香油、香菜。

【小贴士】毛肚七上八下，口感最佳。', 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400&h=300&fit=crop', true, true, NOW());

-- 煎炸
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(39, 1, '炸鸡翅', '【食材】鸡翅中8个、生抽2勺、料酒1勺、蒜泥适量、淀粉适量、炸鸡粉适量

【步骤】
1. 鸡翅用生抽、料酒、蒜泥腌制30分钟。
2. 裹上淀粉和炸鸡粉。
3. 油温170度炸至金黄。
4. 复炸一次更酥脆。
5. 沥油装盘即可。

【小贴士】复炸是关键，外酥里嫩。', 'https://images.unsplash.com/photo-1527477396000-e27163b481c2?w=400&h=300&fit=crop', true, true, NOW());

-- 蒸煮
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(40, 1, '粉蒸排骨', '【食材】排骨400g、蒸肉米粉1包、豆瓣酱1勺、生抽1勺、料酒1勺、土豆1个

【步骤】
1. 排骨用豆瓣酱、生抽、料酒腌制30分钟。
2. 加入蒸肉米粉拌匀。
3. 土豆切片铺在蒸笼底部。
4. 将排骨铺在土豆上。
5. 上锅大火蒸40分钟即可。

【小贴士】蒸的时间要够，排骨才软烂。', 'https://images.unsplash.com/photo-1544025162-d76694265947?w=400&h=300&fit=crop', true, true, NOW());

-- 焖炖
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(41, 1, '土豆炖牛肉', '【食材】牛腩500g、土豆2个、胡萝卜1根、番茄酱2勺、姜片适量、八角2个

【步骤】
1. 牛腩切块焯水去血沫。
2. 土豆、胡萝卜切块。
3. 锅中放油，放入牛腩翻炒。
4. 加入番茄酱、姜片、八角和适量水。
5. 炖煮1小时后加入土豆、胡萝卜，再炖30分钟。

【小贴士】牛肉要炖够时间才软烂。', 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400&h=300&fit=crop', true, true, NOW());

-- 凉拌
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(42, 1, '凉拌黄瓜', '【食材】黄瓜2根、蒜末适量、辣椒油1勺、醋2勺、生抽1勺、糖少许、香油适量

【步骤】
1. 黄瓜洗净拍碎，切段。
2. 加入蒜末、辣椒油、醋、生抽、糖。
3. 淋上香油。
4. 搅拌均匀即可。

【小贴士】黄瓜拍碎比切更入味。', 'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=400&h=300&fit=crop', true, true, NOW());

-- 烘焙
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(43, 1, '戚风蛋糕', '【食材】鸡蛋4个、低筋面粉80g、细砂糖60g、玉米油40g、牛奶40g、柠檬汁几滴

【步骤】
1. 蛋黄蛋白分离，蛋黄加玉米油、牛奶搅匀。
2. 筛入低筋面粉，Z字形搅拌至无颗粒。
3. 蛋白加柠檬汁，分三次加糖打发至干性发泡。
4. 取1/3蛋白入蛋黄糊翻拌，再倒回蛋白中翻拌均匀。
5. 倒入模具，震出气泡，150度烤50分钟。
6. 出炉倒扣晾凉脱模。

【小贴士】翻拌手法要轻，避免消泡。', 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&h=300&fit=crop', true, true, NOW());

-- 爆炒
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(44, 1, '爆炒腰花', '【食材】猪腰2个、青椒1个、红椒1个、蒜末适量、料酒1勺、生抽1勺、醋少许

【步骤】
1. 猪腰去筋膜，切花刀，焯水去腥。
2. 青红椒切块。
3. 锅中放油，大火爆香蒜末。
4. 放入腰花快速翻炒。
5. 加入青红椒，调味出锅。

【小贴士】大火快炒，腰花才嫩。', 'https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400&h=300&fit=crop', true, true, NOW());

-- 煲汤
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(45, 1, '老火靓汤', '【食材】排骨400g、玉米1根、胡萝卜1根、姜片适量、枸杞适量、盐适量

【步骤】
1. 排骨焯水去血沫。
2. 玉米、胡萝卜切块。
3. 排骨放入砂锅，加姜片和足量水。
4. 大火烧开转小火炖2小时。
5. 加入玉米、胡萝卜再炖30分钟。
6. 加盐调味，撒枸杞即可。

【小贴士】小火慢炖，汤才浓郁。', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400&h=300&fit=crop', true, true, NOW());

-- ========== 特色分类美食 ==========

-- 非遗
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(46, 1, '北京烤鸭', '【食材】填鸭1只、甜面酱适量、大葱适量、黄瓜适量、荷叶饼适量

【步骤】
1. 填鸭处理干净，吹气分离皮肉。
2. 挂起晾干表皮。
3. 烤箱200度烤1小时至皮脆肉嫩。
4. 片鸭肉，配甜面酱、葱丝、黄瓜条。
5. 用荷叶饼卷食。

【小贴士】鸭皮要烤至枣红色才酥脆。', 'https://images.unsplash.com/photo-1518492104633-130d0cc84637?w=400&h=300&fit=crop', true, true, NOW());

-- 网红
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(47, 1, '脏脏奶茶', '【食材】红茶包2个、牛奶200ml、黑糖浆适量、珍珠适量、冰块适量

【步骤】
1. 红茶包用热水泡开。
2. 杯壁淋上黑糖浆，转动杯子形成挂壁效果。
3. 加入冰块和珍珠。
4. 倒入牛奶。
5. 加入红茶即可。

【小贴士】黑糖浆挂壁是关键，拍照好看。', 'https://images.unsplash.com/photo-1558857563-b371033873b8?w=400&h=300&fit=crop', true, true, NOW());

-- 新手友好
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(48, 1, '番茄鸡蛋面', '【食材】面条1份、鸡蛋1个、番茄1个、葱花适量、盐适量、食用油适量

【步骤】
1. 番茄切块，鸡蛋打散。
2. 锅中放油，炒鸡蛋盛出。
3. 锅中放油，炒番茄出汁。
4. 加水烧开，放入面条煮熟。
5. 加入鸡蛋，调味，撒葱花。

【小贴士】简单快手，零失败。', 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400&h=300&fit=crop', true, true, NOW());

-- 减脂轻食
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(49, 1, '鸡胸肉沙拉', '【食材】鸡胸肉150g、生菜适量、圣女果适量、黄瓜适量、橄榄油1勺、黑胡椒适量

【步骤】
1. 鸡胸肉用盐、黑胡椒腌制。
2. 平底锅煎至两面金黄，切片。
3. 生菜、圣女果、黄瓜洗净切好。
4. 将所有食材放入碗中。
5. 淋上橄榄油，撒黑胡椒即可。

【小贴士】鸡胸肉不要煎太久，保持嫩滑。', 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop', true, true, NOW());

-- 下饭神器
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(50, 1, '肉末茄子', '【食材】茄子2根、肉末100g、豆瓣酱1勺、蒜末适量、葱花适量、生抽1勺

【步骤】
1. 茄子切条，用盐腌制10分钟，挤干水分。
2. 锅中放油，放入肉末炒散。
3. 加入豆瓣酱、蒜末炒香。
4. 放入茄子翻炒。
5. 加生抽调味，撒葱花即可。

【小贴士】茄子用盐腌过后不吸油。', 'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=400&h=300&fit=crop', true, true, NOW());

-- 下酒小菜
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(51, 1, '香辣花生', '【食材】花生米200g、干辣椒10个、花椒适量、盐适量、食用油适量

【步骤】
1. 花生米洗净晾干。
2. 冷油下锅，小火慢炸至酥脆。
3. 捞出沥油。
4. 锅中留底油，放入干辣椒、花椒炒香。
5. 倒入花生米，加盐翻炒均匀即可。

【小贴士】冷油下锅，花生更酥脆。', 'https://images.unsplash.com/photo-1525755662778-989d0524087e?w=400&h=300&fit=crop', true, true, NOW());

-- 复刻经典
INSERT INTO gourmet (category_id, user_id, title, content, cover, is_audit, is_publish, create_time) VALUES
(52, 1, '肯德基炸鸡', '【食材】鸡腿4个、面粉100g、淀粉50g、泡打粉3g、盐适量、黑胡椒适量、蒜粉适量

【步骤】
1. 鸡腿用盐、黑胡椒、蒜粉腌制2小时。
2. 面粉、淀粉、泡打粉混合。
3. 鸡腿裹上粉，蘸水，再裹粉，形成鳞片状。
4. 油温170度炸12分钟。
5. 复炸一次更酥脆。

【小贴士】裹粉两次，鳞片效果更好。', 'https://images.unsplash.com/photo-1527477396000-e27163b481c2?w=400&h=300&fit=crop', true, true, NOW());
