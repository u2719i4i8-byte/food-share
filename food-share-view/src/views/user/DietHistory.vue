<template>
    <div class="diet-record-page">
        <header class="page-header">
            <div class="header-left" @click="goBack">
                <i class="el-icon-arrow-left"></i>
            </div>
            <h1 class="header-title">饮食记录</h1>
            <div class="header-right"></div>
        </header>
        
        <div class="top-section">
            <div class="date-selector">
                <div class="date-nav">
                    <el-button class="nav-btn" @click="changeDate(-1)">
                        <i class="el-icon-arrow-left"></i>
                    </el-button>
                    <div class="current-date" @click="showDatePicker = true">
                        <span class="date-text">{{ formatDateDisplay(currentDate) }}</span>
                        <span class="week-text">{{ getWeekDay(currentDate) }}</span>
                        <i class="el-icon-date"></i>
                    </div>
                    <el-button class="nav-btn" @click="changeDate(1)">
                        <i class="el-icon-arrow-right"></i>
                    </el-button>
                </div>
            </div>

            <div class="calorie-card">
                <div class="calorie-header">
                    <div class="calorie-title">
                        <i class="el-icon-pie-chart"></i>
                        <span>今日摄入</span>
                    </div>
                    <div class="calorie-target" @click="showTargetDialog = true">
                        <span>目标: {{ dailyTarget }} kcal</span>
                        <i class="el-icon-edit"></i>
                    </div>
                </div>
                <div class="calorie-ring">
                    <div class="ring-container">
                        <svg viewBox="0 0 120 120">
                            <circle class="ring-bg" cx="60" cy="60" r="52" />
                            <circle 
                                class="ring-progress" 
                                cx="60" cy="60" r="52" 
                                :style="{ strokeDashoffset: ringOffset }"
                            />
                        </svg>
                        <div class="ring-center">
                            <span class="calorie-value">{{ totalCalories }}</span>
                            <span class="calorie-unit">千卡</span>
                        </div>
                    </div>
                    <div class="calorie-info">
                        <div class="info-item">
                            <span class="info-label">剩余</span>
                            <span class="info-value">{{ Math.max(0, dailyTarget - totalCalories) }}</span>
                        </div>
                        <div class="info-divider"></div>
                        <div class="info-item">
                            <span class="info-label">已摄入</span>
                            <span class="info-value consumed">{{ totalCalories }}</span>
                        </div>
                    </div>
                </div>
                <div class="nutrient-bars">
                    <div class="nutrient-item">
                        <div class="nutrient-header">
                            <span class="nutrient-name">碳水化合物</span>
                            <span class="nutrient-value">{{ nutrientSummary.carbs }}g</span>
                        </div>
                        <div class="nutrient-progress">
                            <div class="progress-bar carbs" :style="{ width: getNutrientProgress('carbs') + '%' }"></div>
                        </div>
                    </div>
                    <div class="nutrient-item">
                        <div class="nutrient-header">
                            <span class="nutrient-name">蛋白质</span>
                            <span class="nutrient-value">{{ nutrientSummary.protein }}g</span>
                        </div>
                        <div class="nutrient-progress">
                            <div class="progress-bar protein" :style="{ width: getNutrientProgress('protein') + '%' }"></div>
                        </div>
                    </div>
                    <div class="nutrient-item">
                        <div class="nutrient-header">
                            <span class="nutrient-name">脂肪</span>
                            <span class="nutrient-value">{{ nutrientSummary.fat }}g</span>
                        </div>
                        <div class="nutrient-progress">
                            <div class="progress-bar fat" :style="{ width: getNutrientProgress('fat') + '%' }"></div>
                        </div>
                    </div>
                </div>
                
                <div class="nutrient-ratio-section" v-if="totalCalories > 0">
                    <div class="ratio-header" @click="showRatioAdjust = !showRatioAdjust">
                        <span class="ratio-title">
                            <i class="el-icon-setting"></i>
                            营养素比例分配
                        </span>
                        <i class="el-icon-arrow-down" :class="{ rotated: !showRatioAdjust }"></i>
                    </div>
                    
                    <div class="ratio-content" v-show="showRatioAdjust">
                        <div class="ratio-display">
                            <div class="ratio-pie">
                                <svg viewBox="0 0 100 100">
                                    <circle cx="50" cy="50" r="40" fill="transparent" stroke="#6bcb77" stroke-width="20" 
                                        :stroke-dasharray="`${carbsRatio * 2.51} 251.2`" stroke-dashoffset="0"/>
                                    <circle cx="50" cy="50" r="40" fill="transparent" stroke="#ff6b6b" stroke-width="20" 
                                        :stroke-dasharray="`${proteinRatio * 2.51} 251.2`" 
                                        :stroke-dashoffset="`${-carbsRatio * 2.51}`"/>
                                    <circle cx="50" cy="50" r="40" fill="transparent" stroke="#ffd93d" stroke-width="20" 
                                        :stroke-dasharray="`${fatRatio * 2.51} 251.2`" 
                                        :stroke-dashoffset="`${-(carbsRatio + proteinRatio) * 2.51}`"/>
                                </svg>
                                <div class="pie-center">
                                    <span class="pie-value">{{ totalCalories }}</span>
                                    <span class="pie-unit">kcal</span>
                                </div>
                            </div>
                            <div class="ratio-legend">
                                <div class="legend-item carbs">
                                    <span class="legend-dot"></span>
                                    <span class="legend-name">碳水</span>
                                    <span class="legend-percent">{{ carbsRatio }}%</span>
                                    <span class="legend-gram">{{ ratioCarbs }}g</span>
                                </div>
                                <div class="legend-item protein">
                                    <span class="legend-dot"></span>
                                    <span class="legend-name">蛋白质</span>
                                    <span class="legend-percent">{{ proteinRatio }}%</span>
                                    <span class="legend-gram">{{ ratioProtein }}g</span>
                                </div>
                                <div class="legend-item fat">
                                    <span class="legend-dot"></span>
                                    <span class="legend-name">脂肪</span>
                                    <span class="legend-percent">{{ fatRatio }}%</span>
                                    <span class="legend-gram">{{ ratioFat }}g</span>
                                </div>
                            </div>
                        </div>
                        
                        <div class="ratio-sliders">
                            <div class="slider-item">
                                <div class="slider-label">
                                    <span class="slider-name carbs">碳水化合物</span>
                                    <span class="slider-value">{{ carbsRatio }}%</span>
                                </div>
                                <el-slider v-model="carbsRatio" :min="0" :max="100" :step="5" @input="adjustNutrientRatio('carbs')"/>
                            </div>
                            <div class="slider-item">
                                <div class="slider-label">
                                    <span class="slider-name protein">蛋白质</span>
                                    <span class="slider-value">{{ proteinRatio }}%</span>
                                </div>
                                <el-slider v-model="proteinRatio" :min="0" :max="100" :step="5" @input="adjustNutrientRatio('protein')"/>
                            </div>
                            <div class="slider-item">
                                <div class="slider-label">
                                    <span class="slider-name fat">脂肪</span>
                                    <span class="slider-value">{{ fatRatio }}%</span>
                                </div>
                                <el-slider v-model="fatRatio" :min="0" :max="100" :step="5" @input="adjustNutrientRatio('fat')"/>
                            </div>
                        </div>
                        
                        <div class="ratio-presets">
                            <span class="preset-label">快捷方案：</span>
                            <span class="preset-btn" :class="{ active: carbsRatio === 50 && proteinRatio === 25 && fatRatio === 25 }" 
                                @click="setPresetRatio('balanced')">均衡饮食</span>
                            <span class="preset-btn" :class="{ active: carbsRatio === 35 && proteinRatio === 40 && fatRatio === 25 }" 
                                @click="setPresetRatio('highProtein')">高蛋白</span>
                            <span class="preset-btn" :class="{ active: carbsRatio === 30 && proteinRatio === 30 && fatRatio === 40 }" 
                                @click="setPresetRatio('lowCarb')">低碳水</span>
                            <span class="preset-btn" :class="{ active: carbsRatio === 55 && proteinRatio === 30 && fatRatio === 15 }" 
                                @click="setPresetRatio('lowFat')">低脂</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="meals-section">
            <div class="meal-card" v-for="(meal, mealIndex) in meals" :key="meal.type">
                <div class="meal-header" @click="toggleMeal(meal.type)">
                    <div class="meal-icon" :style="{ background: meal.color }">
                        <i :class="meal.icon"></i>
                    </div>
                    <div class="meal-info">
                        <span class="meal-name">{{ meal.name }}</span>
                        <span class="meal-calorie">{{ getMealCalories(meal.type) }} kcal</span>
                    </div>
                    <div class="meal-actions">
                        <el-button class="add-food-btn" @click.stop="openAddFood(meal.type)">
                            <i class="el-icon-plus"></i>
                            添加食物
                        </el-button>
                        <i class="el-icon-arrow-down" :class="{ rotated: !meal.expanded }"></i>
                    </div>
                </div>
                <div class="meal-content" v-show="meal.expanded">
                    <div v-if="getMealFoods(meal.type).length === 0" class="empty-meal">
                        <i class="el-icon-food"></i>
                        <span>还没有添加食物</span>
                    </div>
                    <div v-else class="food-list">
                        <div class="food-item" v-for="(food, index) in getMealFoods(meal.type)" :key="index">
                            <div class="food-icon">
                                <i class="el-icon-food"></i>
                            </div>
                            <div class="food-info">
                                <span class="food-name">{{ food.name }}</span>
                                <span class="food-amount">{{ food.amount }}g · 蛋白{{ food.protein }}g · 脂肪{{ food.fat }}g · 碳水{{ food.carbs }}g</span>
                            </div>
                            <div class="food-calorie">{{ food.calories }} kcal</div>
                            <el-button class="delete-btn" @click="removeFood(meal.type, index)">
                                <i class="el-icon-close"></i>
                            </el-button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <el-dialog 
            :visible.sync="showFoodDialog" 
            width="90%"
            custom-class="food-dialog"
            :show-close="false"
        >
            <div class="dialog-content">
                <div class="dialog-header">
                    <el-button class="back-btn" @click="showFoodDialog = false">
                        <i class="el-icon-arrow-left"></i>
                    </el-button>
                    <span class="dialog-title">添加{{ currentMealName }}食物</span>
                    <div class="placeholder"></div>
                </div>

                <div class="search-section">
                    <el-input 
                        v-model="searchKeyword" 
                        placeholder="搜索任意食物（如：苹果、牛肉、蛋糕）"
                        prefix-icon="el-icon-search"
                        class="search-input"
                        @keyup.enter.native="searchFood"
                    />
                    <el-button class="search-btn" @click="searchFood" :loading="searching">
                        <i class="el-icon-search" v-if="!searching"></i>
                        <span>{{ searching ? '搜索中' : '搜索' }}</span>
                    </el-button>
                </div>
                
                <div class="api-tips" v-if="!searchKeyword && !searching">
                    <i class="el-icon-info"></i>
                    <span>输入食物名称搜索，支持中英文，可查询数万种食物营养数据</span>
                </div>
                
                <div class="loading-section" v-if="searching">
                    <div class="loading-spinner"></div>
                    <span>正在查询营养数据库...</span>
                </div>

                <div class="quick-add-section" v-if="!searchKeyword">
                    <div class="section-title">常用食物</div>
                    <div class="quick-foods">
                        <div 
                            class="quick-food-item" 
                            v-for="food in frequentFoods" 
                            :key="food.id"
                            @click="selectFood(food)"
                        >
                            <div class="quick-food-icon">
                                <i class="el-icon-food"></i>
                            </div>
                            <span class="quick-food-name">{{ food.name }}</span>
                            <span class="quick-food-calorie">{{ food.calories }}kcal</span>
                        </div>
                    </div>
                </div>

                <div class="food-results" v-if="searchKeyword && searchResults.length > 0">
                    <div class="section-title">
                        搜索结果 
                        <span class="result-count">共{{ searchResults.length }}条</span>
                    </div>
                    <div class="food-result-list">
                        <div 
                            class="food-result-item" 
                            v-for="food in searchResults" 
                            :key="food.id"
                            @click="selectFood(food)"
                        >
                            <div class="result-icon">
                                <i class="el-icon-food"></i>
                            </div>
                            <div class="result-info">
                                <div class="result-name-row">
                                    <span class="result-name">{{ food.name }}</span>
                                    <span class="result-source" v-if="food.source">{{ food.source }}</span>
                                </div>
                                <span class="result-unit">{{ food.calories }}kcal/100g · 蛋白{{ food.protein }}g · 脂肪{{ food.fat }}g · 碳水{{ food.carbs }}g</span>
                            </div>
                            <i class="el-icon-arrow-right"></i>
                        </div>
                    </div>
                </div>
                
                <div class="no-result" v-if="searchKeyword && !searching && searchResults.length === 0 && hasSearched">
                    <i class="el-icon-search"></i>
                    <span>未找到"{{ searchKeyword }}"相关食物</span>
                    <p class="no-result-tip">请尝试其他关键词，如：苹果、米饭、牛肉</p>
                </div>

                <div class="cookbook-section" v-if="!searchKeyword">
                    <div class="section-title">我的食谱</div>
                    <div class="cookbook-list">
                        <div 
                            class="cookbook-item" 
                            v-for="cookbook in cookbooks" 
                            :key="cookbook.id"
                            @click="selectCookbook(cookbook)"
                        >
                            <div class="cookbook-cover">
                                <img v-if="cookbook.cover" :src="cookbook.cover" />
                                <i v-else class="el-icon-picture"></i>
                            </div>
                            <div class="cookbook-info">
                                <span class="cookbook-name">{{ cookbook.name }}</span>
                                <span class="cookbook-desc">{{ cookbook.description || '暂无描述' }}</span>
                            </div>
                            <i class="el-icon-arrow-right"></i>
                        </div>
                    </div>
                </div>
            </div>
        </el-dialog>

        <el-dialog 
            :visible.sync="showAmountDialog" 
            width="400px"
            custom-class="amount-dialog"
            :show-close="false"
        >
            <div class="amount-content">
                <div class="amount-header">
                    <div class="selected-food-icon">
                        <i class="el-icon-food"></i>
                    </div>
                    <div class="selected-food-info">
                        <span class="selected-food-name">{{ selectedFood.name }}</span>
                        <span class="selected-food-unit">每100g: {{ selectedFood.calories || 0 }}kcal · 蛋白{{ selectedFood.protein || 0 }}g · 脂肪{{ selectedFood.fat || 0 }}g · 碳水{{ selectedFood.carbs || 0 }}g</span>
                    </div>
                </div>
                
                <div class="input-mode-tabs">
                    <div class="mode-tab" :class="{ active: inputMode === 'weight' }" @click="switchInputMode('weight')">
                        <i class="el-icon-scale"></i>
                        <span>按重量</span>
                    </div>
                    <div class="mode-tab" :class="{ active: inputMode === 'calorie' }" @click="switchInputMode('calorie')">
                        <i class="el-icon-aim"></i>
                        <span>按热量</span>
                    </div>
                </div>
                
                <div class="amount-input-section" v-if="inputMode === 'weight'">
                    <div class="input-label">食用量（克）</div>
                    <div class="input-wrapper">
                        <el-input-number 
                            v-model="foodAmount" 
                            :min="1" 
                            :max="9999"
                            :step="10"
                            class="amount-number"
                        />
                        <span class="input-unit">克</span>
                    </div>
                    <div class="quick-amounts">
                        <span 
                            class="quick-amount" 
                            v-for="amount in [50, 100, 150, 200, 250]" 
                            :key="amount"
                            :class="{ active: foodAmount === amount }"
                            @click="foodAmount = amount"
                        >{{ amount }}g</span>
                    </div>
                </div>
                
                <div class="amount-input-section" v-if="inputMode === 'calorie'">
                    <div class="input-label">目标热量（kcal）</div>
                    <div class="input-wrapper">
                        <el-input-number 
                            v-model="customCalories" 
                            :min="1" 
                            :max="9999"
                            :step="10"
                            class="amount-number"
                        />
                        <span class="input-unit">kcal</span>
                    </div>
                    <div class="quick-amounts">
                        <span 
                            class="quick-amount" 
                            v-for="cal in [100, 200, 300, 400, 500]" 
                            :key="cal"
                            :class="{ active: customCalories === cal }"
                            @click="customCalories = cal"
                        >{{ cal }}kcal</span>
                    </div>
                    <div class="calculated-weight">
                        <i class="el-icon-info"></i>
                        <span>约等于 {{ calculatedWeight }}g</span>
                    </div>
                </div>
                
                <div class="amount-preview">
                    <div class="preview-item">
                        <span class="preview-label">热量</span>
                        <span class="preview-value">{{ previewCalories }} kcal</span>
                    </div>
                    <div class="preview-nutrients">
                        <div class="preview-nutrient">
                            <span class="nutrient-label">蛋白质</span>
                            <span class="nutrient-val">{{ previewProtein }}g</span>
                        </div>
                        <div class="preview-nutrient">
                            <span class="nutrient-label">脂肪</span>
                            <span class="nutrient-val">{{ previewFat }}g</span>
                        </div>
                        <div class="preview-nutrient">
                            <span class="nutrient-label">碳水</span>
                            <span class="nutrient-val">{{ previewCarbs }}g</span>
                        </div>
                    </div>
                </div>
                <div class="amount-actions">
                    <el-button class="cancel-btn" @click="showAmountDialog = false">取消</el-button>
                    <el-button class="confirm-btn" @click="confirmAddFood">确认添加</el-button>
                </div>
            </div>
        </el-dialog>

        <el-dialog 
            :visible.sync="showDatePicker" 
            width="360px"
            custom-class="date-picker-dialog"
        >
            <div class="date-picker-content">
                <div class="picker-header">
                    <span>选择日期</span>
                </div>
                <el-calendar v-model="selectedDate" />
                <div class="picker-actions">
                    <el-button @click="showDatePicker = false">取消</el-button>
                    <el-button type="primary" @click="confirmDate">确定</el-button>
                </div>
            </div>
        </el-dialog>
        
        <el-dialog 
            :visible.sync="showTargetDialog" 
            width="360px"
            custom-class="target-dialog"
        >
            <div class="target-content">
                <div class="target-header">
                    <i class="el-icon-aim"></i>
                    <span>设置每日目标热量</span>
                </div>
                
                <div class="target-input-section">
                    <div class="target-input-wrapper">
                        <el-input-number 
                            v-model="tempTarget" 
                            :min="800" 
                            :max="5000"
                            :step="50"
                            class="target-input"
                        />
                        <span class="target-unit">kcal</span>
                    </div>
                    
                    <div class="target-presets">
                        <div class="preset-row">
                            <span class="preset-label">快捷选择：</span>
                        </div>
                        <div class="preset-btns">
                            <span class="preset-btn" :class="{ active: tempTarget === 1500 }" @click="tempTarget = 1500">1500</span>
                            <span class="preset-btn" :class="{ active: tempTarget === 1800 }" @click="tempTarget = 1800">1800</span>
                            <span class="preset-btn" :class="{ active: tempTarget === 2000 }" @click="tempTarget = 2000">2000</span>
                            <span class="preset-btn" :class="{ active: tempTarget === 2200 }" @click="tempTarget = 2200">2200</span>
                            <span class="preset-btn" :class="{ active: tempTarget === 2500 }" @click="tempTarget = 2500">2500</span>
                        </div>
                    </div>
                    
                    <div class="target-tips">
                        <div class="tip-item">
                            <i class="el-icon-info"></i>
                            <span>一般成年人每日所需热量：1500-2500 kcal</span>
                        </div>
                        <div class="tip-item">
                            <i class="el-icon-user"></i>
                            <span>具体需求因性别、年龄、活动量而异</span>
                        </div>
                    </div>
                </div>
                
                <div class="target-actions">
                    <el-button @click="showTargetDialog = false">取消</el-button>
                    <el-button type="primary" @click="confirmTarget">确定</el-button>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: "DietHistory",
    data() {
        return {
            currentDate: new Date(),
            selectedDate: new Date(),
            showDatePicker: false,
            showTargetDialog: false,
            tempTarget: 2000,
            dailyTarget: 2000,
            showRatioAdjust: false,
            carbsRatio: 50,
            proteinRatio: 25,
            fatRatio: 25,
            showFoodDialog: false,
            showAmountDialog: false,
            searchKeyword: '',
            currentMealType: 'breakfast',
            selectedFood: { protein: 0, fat: 0, carbs: 0 },
            foodAmount: 100,
            customCalories: 200,
            inputMode: 'weight',
            cookbooks: [],
            searchResults: [],
            searching: false,
            hasSearched: false,
            frequentFoods: [
                { id: 1, name: '米饭', calories: 116, protein: 2.6, fat: 0.3, carbs: 25.9 },
                { id: 2, name: '面条', calories: 137, protein: 4.5, fat: 0.6, carbs: 28.4 },
                { id: 3, name: '鸡蛋', calories: 144, protein: 13.3, fat: 8.8, carbs: 2.8 },
                { id: 4, name: '牛奶', calories: 54, protein: 3.0, fat: 3.2, carbs: 3.4 },
                { id: 5, name: '苹果', calories: 52, protein: 0.2, fat: 0.2, carbs: 13.8 },
                { id: 6, name: '面包', calories: 265, protein: 8.3, fat: 3.3, carbs: 50.0 },
                { id: 7, name: '豆浆', calories: 31, protein: 1.8, fat: 0.7, carbs: 3.3 },
                { id: 8, name: '香蕉', calories: 93, protein: 1.2, fat: 0.2, carbs: 22.0 },
                { id: 9, name: '馒头', calories: 223, protein: 7.0, fat: 1.1, carbs: 47.0 },
                { id: 10, name: '白粥', calories: 46, protein: 1.1, fat: 0.3, carbs: 9.9 },
                { id: 11, name: '包子', calories: 227, protein: 6.4, fat: 1.4, carbs: 45.6 },
                { id: 12, name: '饺子', calories: 198, protein: 7.0, fat: 6.2, carbs: 26.5 },
                { id: 13, name: '红烧肉', calories: 353, protein: 12.7, fat: 30.8, carbs: 3.4 },
                { id: 14, name: '宫保鸡丁', calories: 180, protein: 15.0, fat: 10.0, carbs: 6.0 },
                { id: 15, name: '番茄炒蛋', calories: 120, protein: 8.5, fat: 8.0, carbs: 4.5 },
                { id: 16, name: '清炒青菜', calories: 45, protein: 2.0, fat: 2.5, carbs: 4.0 },
                { id: 17, name: '土豆丝', calories: 76, protein: 2.0, fat: 3.0, carbs: 10.0 },
                { id: 18, name: '豆腐', calories: 81, protein: 8.1, fat: 3.7, carbs: 3.8 },
                { id: 19, name: '鱼香肉丝', calories: 165, protein: 12.0, fat: 9.0, carbs: 8.0 },
                { id: 20, name: '麻婆豆腐', calories: 140, protein: 9.0, fat: 8.5, carbs: 5.0 },
                { id: 21, name: '炒饭', calories: 188, protein: 4.5, fat: 5.0, carbs: 30.0 },
                { id: 22, name: '炒面', calories: 163, protein: 5.0, fat: 4.0, carbs: 26.0 },
                { id: 23, name: '小米粥', calories: 46, protein: 1.4, fat: 0.7, carbs: 9.7 },
                { id: 24, name: '燕麦片', calories: 367, protein: 15.0, fat: 6.7, carbs: 61.6 },
                { id: 25, name: '玉米', calories: 112, protein: 4.0, fat: 1.2, carbs: 22.8 },
                { id: 26, name: '红薯', calories: 99, protein: 1.1, fat: 0.2, carbs: 23.1 },
                { id: 27, name: '紫薯', calories: 82, protein: 1.0, fat: 0.2, carbs: 19.5 },
                { id: 28, name: '南瓜', calories: 22, protein: 0.7, fat: 0.1, carbs: 4.5 },
                { id: 29, name: '黄瓜', calories: 15, protein: 0.8, fat: 0.2, carbs: 2.4 },
                { id: 30, name: '番茄', calories: 15, protein: 0.9, fat: 0.2, carbs: 3.3 },
                { id: 31, name: '西瓜', calories: 25, protein: 0.6, fat: 0.1, carbs: 5.8 },
                { id: 32, name: '葡萄', calories: 43, protein: 0.4, fat: 0.2, carbs: 10.3 },
                { id: 33, name: '橙子', calories: 47, protein: 0.8, fat: 0.2, carbs: 11.8 },
                { id: 34, name: '梨', calories: 44, protein: 0.4, fat: 0.2, carbs: 11.3 },
                { id: 35, name: '桃子', calories: 42, protein: 0.9, fat: 0.1, carbs: 10.1 },
                { id: 36, name: '草莓', calories: 30, protein: 1.0, fat: 0.2, carbs: 6.0 },
                { id: 37, name: '蓝莓', calories: 57, protein: 0.7, fat: 0.3, carbs: 12.9 },
                { id: 38, name: '猪肉(瘦)', calories: 143, protein: 20.3, fat: 6.2, carbs: 0.0 },
                { id: 39, name: '牛肉(瘦)', calories: 125, protein: 20.0, fat: 4.2, carbs: 0.0 },
                { id: 40, name: '羊肉', calories: 203, protein: 19.0, fat: 14.1, carbs: 0.0 },
                { id: 41, name: '鸡肉', calories: 167, protein: 19.3, fat: 9.4, carbs: 1.3 },
                { id: 42, name: '鸭肉', calories: 240, protein: 15.5, fat: 19.7, carbs: 0.2 },
                { id: 43, name: '鱼肉(草鱼)', calories: 104, protein: 16.6, fat: 3.6, carbs: 0.0 },
                { id: 44, name: '虾', calories: 87, protein: 18.6, fat: 0.8, carbs: 2.8 },
                { id: 45, name: '螃蟹', calories: 95, protein: 17.5, fat: 2.6, carbs: 2.3 },
                { id: 46, name: '酸奶', calories: 72, protein: 2.5, fat: 2.7, carbs: 9.3 },
                { id: 47, name: '奶酪', calories: 328, protein: 25.7, fat: 23.5, carbs: 3.5 },
                { id: 48, name: '核桃', calories: 654, protein: 15.0, fat: 65.2, carbs: 13.7 },
                { id: 49, name: '花生', calories: 574, protein: 24.8, fat: 48.0, carbs: 21.7 },
                { id: 50, name: '开心果', calories: 614, protein: 20.0, fat: 53.0, carbs: 21.0 },
                { id: 51, name: '蛋糕', calories: 348, protein: 5.0, fat: 15.0, carbs: 50.0 },
                { id: 52, name: '饼干', calories: 433, protein: 6.0, fat: 17.0, carbs: 65.0 },
                { id: 53, name: '巧克力', calories: 589, protein: 5.0, fat: 40.0, carbs: 52.0 },
                { id: 54, name: '冰淇淋', calories: 207, protein: 3.5, fat: 11.0, carbs: 23.6 },
                { id: 55, name: '奶茶', calories: 65, protein: 0.8, fat: 2.5, carbs: 9.5 },
                { id: 56, name: '可乐', calories: 43, protein: 0.0, fat: 0.0, carbs: 10.6 },
                { id: 57, name: '咖啡(黑)', calories: 2, protein: 0.3, fat: 0.0, carbs: 0.0 },
                { id: 58, name: '绿茶', calories: 0, protein: 0.0, fat: 0.0, carbs: 0.0 },
                { id: 59, name: '啤酒', calories: 32, protein: 0.4, fat: 0.0, carbs: 2.1 },
                { id: 60, name: '红烧排骨', calories: 278, protein: 15.0, fat: 22.0, carbs: 3.0 },
                { id: 61, name: '糖醋里脊', calories: 265, protein: 12.0, fat: 15.0, carbs: 18.0 },
                { id: 62, name: '回锅肉', calories: 320, protein: 14.0, fat: 28.0, carbs: 4.0 },
                { id: 63, name: '水煮鱼', calories: 175, protein: 18.0, fat: 10.0, carbs: 3.0 },
                { id: 64, name: '酸菜鱼', calories: 145, protein: 17.0, fat: 7.0, carbs: 3.0 },
                { id: 65, name: '清蒸鱼', calories: 118, protein: 19.0, fat: 4.0, carbs: 1.0 },
                { id: 66, name: '可乐鸡翅', calories: 215, protein: 15.0, fat: 14.0, carbs: 6.0 },
                { id: 67, name: '红烧茄子', calories: 85, protein: 2.0, fat: 5.0, carbs: 8.0 },
                { id: 68, name: '地三鲜', calories: 125, protein: 3.0, fat: 8.0, carbs: 10.0 },
                { id: 69, name: '干煸豆角', calories: 145, protein: 4.0, fat: 10.0, carbs: 8.0 },
                { id: 70, name: '蛋炒饭', calories: 195, protein: 6.0, fat: 6.0, carbs: 28.0 },
                { id: 71, name: '扬州炒饭', calories: 210, protein: 7.0, fat: 8.0, carbs: 28.0 },
                { id: 72, name: '葱油拌面', calories: 185, protein: 6.0, fat: 7.0, carbs: 26.0 },
                { id: 73, name: '牛肉面', calories: 195, protein: 12.0, fat: 5.0, carbs: 28.0 },
                { id: 74, name: '炸鸡', calories: 295, protein: 18.0, fat: 20.0, carbs: 10.0 },
                { id: 75, name: '薯条', calories: 312, protein: 3.4, fat: 15.0, carbs: 41.0 },
                { id: 76, name: '汉堡', calories: 295, protein: 17.0, fat: 13.0, carbs: 28.0 },
                { id: 77, name: '披萨', calories: 266, protein: 11.0, fat: 10.0, carbs: 33.0 },
                { id: 78, name: '寿司', calories: 150, protein: 6.0, fat: 1.0, carbs: 30.0 },
                { id: 79, name: '沙拉', calories: 45, protein: 2.0, fat: 2.0, carbs: 5.0 },
                { id: 80, name: '三明治', calories: 235, protein: 10.0, fat: 10.0, carbs: 26.0 }
            ],
            meals: [
                { type: 'breakfast', name: '早餐', icon: 'el-icon-sunny', color: 'linear-gradient(135deg, #ff9a56 0%, #ff6b6b 100%)', expanded: true },
                { type: 'lunch', name: '午餐', icon: 'el-icon-partly-cloudy', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', expanded: true },
                { type: 'dinner', name: '晚餐', icon: 'el-icon-moon', color: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)', expanded: true },
                { type: 'snack', name: '加餐', icon: 'el-icon-star-off', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', expanded: true }
            ],
            mealFoods: {
                breakfast: [],
                lunch: [],
                dinner: [],
                snack: []
            },
            nutrientSummary: {
                carbs: 0,
                protein: 0,
                fat: 0
            }
        }
    },
    computed: {
        totalCalories() {
            let total = 0;
            Object.values(this.mealFoods).forEach(foods => {
                foods.forEach(food => {
                    total += food.calories || 0;
                });
            });
            return Math.round(total);
        },
        ringOffset() {
            const progress = Math.min(this.totalCalories / this.dailyTarget, 1);
            const circumference = 2 * Math.PI * 52;
            return circumference * (1 - progress);
        },
        currentMealName() {
            const meal = this.meals.find(m => m.type === this.currentMealType);
            return meal ? meal.name : '';
        },
        ratioCarbs() {
            return ((this.totalCalories * this.carbsRatio / 100) / 4).toFixed(1);
        },
        ratioProtein() {
            return ((this.totalCalories * this.proteinRatio / 100) / 4).toFixed(1);
        },
        ratioFat() {
            return ((this.totalCalories * this.fatRatio / 100) / 9).toFixed(1);
        },
        calculatedWeight() {
            const caloriesPer100g = this.selectedFood.calories || 100;
            return Math.round((this.customCalories / caloriesPer100g) * 100);
        },
        actualWeight() {
            if (this.inputMode === 'weight') {
                return this.foodAmount;
            } else {
                return this.calculatedWeight;
            }
        },
        previewCalories() {
            if (this.inputMode === 'weight') {
                return Math.round((this.selectedFood.calories || 0) * this.foodAmount / 100);
            } else {
                return this.customCalories;
            }
        },
        previewProtein() {
            const ratio = this.actualWeight / 100;
            return ((this.selectedFood.protein || 0) * ratio).toFixed(1);
        },
        previewFat() {
            const ratio = this.actualWeight / 100;
            return ((this.selectedFood.fat || 0) * ratio).toFixed(1);
        },
        previewCarbs() {
            const ratio = this.actualWeight / 100;
            return ((this.selectedFood.carbs || 0) * ratio).toFixed(1);
        }
    },
    created() {
        this.fetchCookbookList();
        this.loadTodayRecord();
        this.loadTarget();
    },
    methods: {
        goBack() {
            this.$router.back();
        },
        formatDateDisplay(date) {
            const today = new Date();
            const yesterday = new Date(today);
            yesterday.setDate(yesterday.getDate() - 1);
            
            if (date.toDateString() === today.toDateString()) {
                return '今天';
            } else if (date.toDateString() === yesterday.toDateString()) {
                return '昨天';
            }
            return `${date.getMonth() + 1}月${date.getDate()}日`;
        },
        getWeekDay(date) {
            const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
            return days[date.getDay()];
        },
        changeDate(delta) {
            const newDate = new Date(this.currentDate);
            newDate.setDate(newDate.getDate() + delta);
            this.currentDate = newDate;
            this.loadTodayRecord();
        },
        confirmDate() {
            this.currentDate = new Date(this.selectedDate);
            this.showDatePicker = false;
            this.loadTodayRecord();
        },
        confirmTarget() {
            this.dailyTarget = this.tempTarget;
            this.showTargetDialog = false;
            localStorage.setItem('dailyTarget', this.dailyTarget);
            this.$message.success('目标热量已更新');
        },
        loadTarget() {
            const saved = localStorage.getItem('dailyTarget');
            if (saved) {
                this.dailyTarget = parseInt(saved);
                this.tempTarget = this.dailyTarget;
            }
        },
        toggleMeal(type) {
            const meal = this.meals.find(m => m.type === type);
            if (meal) {
                meal.expanded = !meal.expanded;
            }
        },
        getMealCalories(type) {
            const foods = this.mealFoods[type] || [];
            return Math.round(foods.reduce((sum, food) => sum + (food.calories || 0), 0));
        },
        getMealFoods(type) {
            return this.mealFoods[type] || [];
        },
        getNutrientProgress(type) {
            const targets = { carbs: 300, protein: 60, fat: 65 };
            const value = this.nutrientSummary[type] || 0;
            return Math.min((value / targets[type]) * 100, 100);
        },
        adjustNutrientRatio(type) {
            const total = this.carbsRatio + this.proteinRatio + this.fatRatio;
            
            if (total > 100) {
                const excess = total - 100;
                const others = [];
                
                if (type === 'carbs') {
                    others.push({ key: 'protein', value: this.proteinRatio });
                    others.push({ key: 'fat', value: this.fatRatio });
                } else if (type === 'protein') {
                    others.push({ key: 'carbs', value: this.carbsRatio });
                    others.push({ key: 'fat', value: this.fatRatio });
                } else {
                    others.push({ key: 'carbs', value: this.carbsRatio });
                    others.push({ key: 'protein', value: this.proteinRatio });
                }
                
                const totalOther = others.reduce((sum, item) => sum + item.value, 0);
                
                if (totalOther > 0) {
                    others.forEach(item => {
                        const reduce = Math.round(item.value * excess / totalOther);
                        if (item.key === 'carbs') {
                            this.carbsRatio = Math.max(0, this.carbsRatio - reduce);
                        } else if (item.key === 'protein') {
                            this.proteinRatio = Math.max(0, this.proteinRatio - reduce);
                        } else {
                            this.fatRatio = Math.max(0, this.fatRatio - reduce);
                        }
                    });
                }
            }
        },
        setPresetRatio(preset) {
            const presets = {
                balanced: { carbs: 50, protein: 25, fat: 25 },
                highProtein: { carbs: 35, protein: 40, fat: 25 },
                lowCarb: { carbs: 30, protein: 30, fat: 40 },
                lowFat: { carbs: 55, protein: 30, fat: 15 }
            };
            
            const config = presets[preset];
            if (config) {
                this.carbsRatio = config.carbs;
                this.proteinRatio = config.protein;
                this.fatRatio = config.fat;
            }
        },
        openAddFood(type) {
            this.currentMealType = type;
            this.searchKeyword = '';
            this.searchResults = [];
            this.hasSearched = false;
            this.showFoodDialog = true;
        },
        async searchFood() {
            if (!this.searchKeyword || this.searchKeyword.trim() === '') {
                this.$message.warning('请输入食物名称');
                return;
            }
            
            this.searching = true;
            this.hasSearched = false;
            this.searchResults = [];
            
            try {
                const keyword = this.searchKeyword.trim();
                
                const localResults = this.frequentFoods.filter(food => 
                    food.name.toLowerCase().includes(keyword.toLowerCase())
                ).map(food => ({ ...food, source: '本地' }));
                
                const apiResults = await this.searchFromNutritionAPI(keyword);
                
                const allResults = [...localResults, ...apiResults];
                const uniqueResults = this.removeDuplicateFoods(allResults);
                
                this.searchResults = uniqueResults.slice(0, 50);
                this.hasSearched = true;
                
            } catch (error) {
                console.error('搜索失败:', error);
                this.$message.error('搜索失败，请稍后重试');
                this.searchResults = [];
                this.hasSearched = true;
            } finally {
                this.searching = false;
            }
        },
        async searchFromNutritionAPI(keyword) {
            const results = [];
            
            try {
                const response = await fetch(`https://api.nal.usda.gov/fdc/v1/foods/search?api_key=DEMO_KEY&query=${encodeURIComponent(keyword)}&pageSize=20&dataType=Foundation,SR Legacy`);
                const data = await response.json();
                
                if (data.foods && data.foods.length > 0) {
                    for (const food of data.foods) {
                        const nutrients = this.extractNutrients(food.foodNutrients);
                        if (nutrients.calories > 0) {
                            results.push({
                                id: `usda_${food.fdcId}`,
                                name: food.description,
                                calories: nutrients.calories,
                                protein: nutrients.protein,
                                fat: nutrients.fat,
                                carbs: nutrients.carbs,
                                source: 'USDA'
                            });
                        }
                    }
                }
            } catch (error) {
                console.warn('USDA API调用失败，使用本地数据:', error);
            }
            
            return results;
        },
        extractNutrients(foodNutrients) {
            const result = { calories: 0, protein: 0, fat: 0, carbs: 0 };
            
            if (!foodNutrients || !Array.isArray(foodNutrients)) {
                return result;
            }
            
            const nutrientMap = {
                'Energy': 'calories',
                'Protein': 'protein',
                'Total lipid (fat)': 'fat',
                'Carbohydrate, by difference': 'carbs'
            };
            
            for (const nutrient of foodNutrients) {
                const name = nutrient.nutrientName || nutrient.name || '';
                const value = nutrient.value || nutrient.amount || 0;
                
                for (const [key, target] of Object.entries(nutrientMap)) {
                    if (name.toLowerCase().includes(key.toLowerCase())) {
                        result[target] = target === 'calories' ? Math.round(value) : Math.round(value * 10) / 10;
                        break;
                    }
                }
            }
            
            return result;
        },
        removeDuplicateFoods(foods) {
            const seen = new Set();
            return foods.filter(food => {
                const key = food.name.toLowerCase();
                if (seen.has(key)) return false;
                seen.add(key);
                return true;
            });
        },
        selectFood(food) {
            this.selectedFood = { ...food };
            this.foodAmount = 100;
            this.customCalories = food.calories || 200;
            this.inputMode = 'weight';
            this.showFoodDialog = false;
            this.showAmountDialog = true;
        },
        selectCookbook(cookbook) {
            this.selectedFood = {
                id: cookbook.id,
                name: cookbook.name,
                calories: cookbook.calories || 100,
                protein: cookbook.protein || 5,
                fat: cookbook.fat || 3,
                carbs: cookbook.carbs || 15,
                isCookbook: true
            };
            this.foodAmount = 100;
            this.customCalories = this.selectedFood.calories;
            this.inputMode = 'weight';
            this.showFoodDialog = false;
            this.showAmountDialog = true;
        },
        switchInputMode(mode) {
            this.inputMode = mode;
            if (mode === 'calorie') {
                this.customCalories = Math.round((this.selectedFood.calories || 100) * this.foodAmount / 100);
            } else {
                this.foodAmount = 100;
            }
        },
        confirmAddFood() {
            const weight = this.actualWeight;
            const ratio = weight / 100;
            const calories = this.previewCalories;
            const protein = parseFloat(this.previewProtein);
            const fat = parseFloat(this.previewFat);
            const carbs = parseFloat(this.previewCarbs);
            
            const food = {
                id: this.selectedFood.id,
                name: this.selectedFood.name,
                amount: weight,
                calories: calories,
                protein: protein,
                fat: fat,
                carbs: carbs,
                isCookbook: this.selectedFood.isCookbook
            };
            this.mealFoods[this.currentMealType].push(food);
            this.updateNutrientSummary();
            this.showAmountDialog = false;
            this.saveRecord();
        },
        removeFood(type, index) {
            this.mealFoods[type].splice(index, 1);
            this.updateNutrientSummary();
            this.saveRecord();
        },
        updateNutrientSummary() {
            const allFoods = [
                ...this.mealFoods.breakfast,
                ...this.mealFoods.lunch,
                ...this.mealFoods.dinner,
                ...this.mealFoods.snack
            ];
            this.nutrientSummary = {
                carbs: Math.round(allFoods.reduce((sum, f) => sum + (f.carbs || 0), 0) * 10) / 10,
                protein: Math.round(allFoods.reduce((sum, f) => sum + (f.protein || 0), 0) * 10) / 10,
                fat: Math.round(allFoods.reduce((sum, f) => sum + (f.fat || 0), 0) * 10) / 10
            };
        },
        fetchCookbookList() {
            this.$axios.get('/cookbook/querySelectedItemsAll').then(res => {
                const { data } = res;
                if (data.code === 200) {
                    this.cookbooks = (data.data || []).map(item => ({
                        ...item,
                        calories: 100
                    }));
                }
            }).catch(error => {
                console.log("查询食谱信息异常：", error);
            });
        },
        loadTodayRecord() {
            const dateStr = this.currentDate.toISOString().split('T')[0];
            const savedData = localStorage.getItem(`diet_${dateStr}`);
            if (savedData) {
                try {
                    const data = JSON.parse(savedData);
                    this.mealFoods = data.mealFoods || { breakfast: [], lunch: [], dinner: [], snack: [] };
                    this.nutrientSummary = data.nutrientSummary || { carbs: 0, protein: 0, fat: 0 };
                } catch (e) {
                    console.error('加载记录失败', e);
                }
            } else {
                this.mealFoods = { breakfast: [], lunch: [], dinner: [], snack: [] };
                this.nutrientSummary = { carbs: 0, protein: 0, fat: 0 };
            }
        },
        saveRecord() {
            const dateStr = this.currentDate.toISOString().split('T')[0];
            const data = {
                mealFoods: this.mealFoods,
                nutrientSummary: this.nutrientSummary
            };
            localStorage.setItem(`diet_${dateStr}`, JSON.stringify(data));
        }
    }
};
</script>

<style scoped lang="scss">
.diet-record-page {
    min-height: 100vh;
    background: #f5f7fa;
    padding-bottom: 20px;
}

.page-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px;
    background: #fff;
    border-bottom: 1px solid #eee;
    
    .header-left, .header-right {
        width: 40px;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .header-left {
        cursor: pointer;
        
        i {
            font-size: 22px;
            color: #333;
        }
    }
    
    .header-title {
        font-size: 18px;
        font-weight: 600;
        color: #333;
        margin: 0;
    }
}

.top-section {
    background: linear-gradient(180deg, #11998e 0%, #38ef7d 100%);
    padding: 16px 16px 24px;
    border-radius: 0 0 24px 24px;
}

.date-selector {
    margin-bottom: 20px;
    
    .date-nav {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 20px;
        
        .nav-btn {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.2);
            border: none;
            color: #fff;
            
            &:hover {
                background: rgba(255, 255, 255, 0.3);
            }
        }
        
        .current-date {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 8px 20px;
            background: rgba(255, 255, 255, 0.2);
            border-radius: 20px;
            cursor: pointer;
            
            .date-text {
                font-size: 18px;
                font-weight: 600;
                color: #fff;
            }
            
            .week-text {
                font-size: 14px;
                color: rgba(255, 255, 255, 0.8);
            }
            
            i {
                color: rgba(255, 255, 255, 0.8);
            }
        }
    }
}

.calorie-card {
    background: #fff;
    border-radius: 20px;
    padding: 20px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    
    .calorie-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        
        .calorie-title {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 16px;
            font-weight: 600;
            color: #333;
            
            i {
                color: #11998e;
            }
        }
        
        .calorie-target {
            font-size: 13px;
            color: #fff;
            display: flex;
            align-items: center;
            gap: 6px;
            cursor: pointer;
            padding: 6px 14px;
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            border-radius: 20px;
            box-shadow: 0 2px 8px rgba(240, 147, 251, 0.3);
            transition: all 0.3s ease;
            font-weight: 500;
            
            &:hover {
                box-shadow: 0 4px 12px rgba(240, 147, 251, 0.5);
                transform: translateY(-1px);
            }
            
            i {
                font-size: 14px;
                opacity: 0.9;
            }
        }
    }
    
    .calorie-ring {
        display: flex;
        align-items: center;
        gap: 30px;
        margin-bottom: 24px;
        
        .ring-container {
            position: relative;
            width: 120px;
            height: 120px;
            
            svg {
                transform: rotate(-90deg);
                
                .ring-bg {
                    fill: none;
                    stroke: #f0f0f0;
                    stroke-width: 10;
                }
                
                .ring-progress {
                    fill: none;
                    stroke: url(#gradient);
                    stroke-width: 10;
                    stroke-linecap: round;
                    stroke-dasharray: 327;
                    transition: stroke-dashoffset 0.5s ease;
                    stroke: #11998e;
                }
            }
            
            .ring-center {
                position: absolute;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                text-align: center;
                
                .calorie-value {
                    display: block;
                    font-size: 24px;
                    font-weight: 700;
                    color: #333;
                }
                
                .calorie-unit {
                    font-size: 12px;
                    color: #999;
                }
            }
        }
        
        .calorie-info {
            flex: 1;
            display: flex;
            gap: 20px;
            
            .info-item {
                text-align: center;
                
                .info-label {
                    display: block;
                    font-size: 12px;
                    color: #999;
                    margin-bottom: 4px;
                }
                
                .info-value {
                    font-size: 24px;
                    font-weight: 700;
                    color: #333;
                    
                    &.consumed {
                        color: #11998e;
                    }
                }
            }
            
            .info-divider {
                width: 1px;
                background: #f0f0f0;
            }
        }
    }
    
    .nutrient-bars {
        .nutrient-item {
            margin-bottom: 12px;
            
            &:last-child {
                margin-bottom: 0;
            }
            
            .nutrient-header {
                display: flex;
                justify-content: space-between;
                margin-bottom: 6px;
                
                .nutrient-name {
                    font-size: 13px;
                    color: #666;
                }
                
                .nutrient-value {
                    font-size: 13px;
                    font-weight: 600;
                    color: #333;
                }
            }
            
            .nutrient-progress {
                height: 6px;
                background: #f0f0f0;
                border-radius: 3px;
                overflow: hidden;
                
                .progress-bar {
                    height: 100%;
                    border-radius: 3px;
                    transition: width 0.5s ease;
                    
                    &.carbs {
                        background: linear-gradient(90deg, #ff9a56, #ff6b6b);
                    }
                    
                    &.protein {
                        background: linear-gradient(90deg, #667eea, #764ba2);
                    }
                    
                    &.fat {
                        background: linear-gradient(90deg, #f093fb, #f5576c);
                    }
                }
            }
        }
    }
    
    .nutrient-ratio-section {
        margin-top: 16px;
        background: rgba(255, 255, 255, 0.15);
        border-radius: 16px;
        padding: 16px;
        
        .ratio-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            cursor: pointer;
            
            .ratio-title {
                display: flex;
                align-items: center;
                gap: 8px;
                font-size: 14px;
                font-weight: 600;
                color: #fff;
                
                i {
                    font-size: 16px;
                }
            }
            
            .el-icon-arrow-down {
                color: #fff;
                transition: transform 0.3s ease;
                
                &.rotated {
                    transform: rotate(180deg);
                }
            }
        }
        
        .ratio-content {
            margin-top: 16px;
            
            .ratio-display {
                display: flex;
                align-items: center;
                gap: 20px;
                margin-bottom: 20px;
                
                .ratio-pie {
                    width: 100px;
                    height: 100px;
                    position: relative;
                    
                    svg {
                        transform: rotate(-90deg);
                    }
                    
                    .pie-center {
                        position: absolute;
                        top: 50%;
                        left: 50%;
                        transform: translate(-50%, -50%);
                        text-align: center;
                        
                        .pie-value {
                            display: block;
                            font-size: 18px;
                            font-weight: 700;
                            color: #fff;
                        }
                        
                        .pie-unit {
                            font-size: 10px;
                            color: rgba(255, 255, 255, 0.8);
                        }
                    }
                }
                
                .ratio-legend {
                    flex: 1;
                    
                    .legend-item {
                        display: flex;
                        align-items: center;
                        gap: 8px;
                        margin-bottom: 8px;
                        
                        .legend-dot {
                            width: 10px;
                            height: 10px;
                            border-radius: 50%;
                        }
                        
                        &.carbs .legend-dot { background: #6bcb77; }
                        &.protein .legend-dot { background: #ff6b6b; }
                        &.fat .legend-dot { background: #ffd93d; }
                        
                        .legend-name {
                            font-size: 12px;
                            color: rgba(255, 255, 255, 0.9);
                            width: 40px;
                        }
                        
                        .legend-percent {
                            font-size: 14px;
                            font-weight: 600;
                            color: #fff;
                            width: 40px;
                        }
                        
                        .legend-gram {
                            font-size: 12px;
                            color: rgba(255, 255, 255, 0.8);
                        }
                    }
                }
            }
            
            .ratio-sliders {
                .slider-item {
                    margin-bottom: 12px;
                    
                    .slider-label {
                        display: flex;
                        justify-content: space-between;
                        margin-bottom: 4px;
                        
                        .slider-name {
                            font-size: 12px;
                            color: rgba(255, 255, 255, 0.9);
                            
                            &.carbs { color: #6bcb77; }
                            &.protein { color: #ff6b6b; }
                            &.fat { color: #ffd93d; }
                        }
                        
                        .slider-value {
                            font-size: 12px;
                            font-weight: 600;
                            color: #fff;
                        }
                    }
                    
                    ::v-deep .el-slider__runway {
                        background: rgba(255, 255, 255, 0.2);
                    }
                    
                    ::v-deep .el-slider__bar {
                        background: #fff;
                    }
                    
                    ::v-deep .el-slider__button {
                        border-color: #fff;
                    }
                }
            }
            
            .ratio-presets {
                display: flex;
                align-items: center;
                flex-wrap: wrap;
                gap: 8px;
                margin-top: 12px;
                padding-top: 12px;
                border-top: 1px solid rgba(255, 255, 255, 0.2);
                
                .preset-label {
                    font-size: 12px;
                    color: rgba(255, 255, 255, 0.7);
                }
                
                .preset-btn {
                    padding: 4px 10px;
                    background: rgba(255, 255, 255, 0.2);
                    border-radius: 12px;
                    font-size: 11px;
                    color: #fff;
                    cursor: pointer;
                    transition: all 0.3s ease;
                    
                    &:hover, &.active {
                        background: #fff;
                        color: #11998e;
                    }
                }
            }
        }
    }
}

.meals-section {
    padding: 16px;
    
    .meal-card {
        background: #fff;
        border-radius: 16px;
        margin-bottom: 12px;
        overflow: hidden;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
        
        .meal-header {
            display: flex;
            align-items: center;
            padding: 16px;
            cursor: pointer;
            
            .meal-icon {
                width: 44px;
                height: 44px;
                border-radius: 12px;
                display: flex;
                align-items: center;
                justify-content: center;
                margin-right: 12px;
                
                i {
                    font-size: 20px;
                    color: #fff;
                }
            }
            
            .meal-info {
                flex: 1;
                
                .meal-name {
                    display: block;
                    font-size: 16px;
                    font-weight: 600;
                    color: #333;
                }
                
                .meal-calorie {
                    font-size: 13px;
                    color: #999;
                }
            }
            
            .meal-actions {
                display: flex;
                align-items: center;
                gap: 12px;
                
                .add-food-btn {
                    background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
                    border: none;
                    color: #fff;
                    font-size: 13px;
                    padding: 8px 16px;
                    border-radius: 20px;
                    
                    i {
                        margin-right: 4px;
                    }
                }
                
                .el-icon-arrow-down {
                    color: #999;
                    transition: transform 0.3s ease;
                    
                    &.rotated {
                        transform: rotate(-90deg);
                    }
                }
            }
        }
        
        .meal-content {
            border-top: 1px solid #f0f0f0;
            
            .empty-meal {
                display: flex;
                flex-direction: column;
                align-items: center;
                padding: 30px;
                color: #ccc;
                
                i {
                    font-size: 32px;
                    margin-bottom: 8px;
                }
                
                span {
                    font-size: 14px;
                }
            }
            
            .food-list {
                .food-item {
                    display: flex;
                    align-items: center;
                    padding: 12px 16px;
                    border-bottom: 1px solid #f5f5f5;
                    
                    &:last-child {
                        border-bottom: none;
                    }
                    
                    .food-icon {
                        width: 36px;
                        height: 36px;
                        background: #f5f5f5;
                        border-radius: 8px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        margin-right: 12px;
                        
                        i {
                            color: #999;
                        }
                    }
                    
                    .food-info {
                        flex: 1;
                        
                        .food-name {
                            display: block;
                            font-size: 14px;
                            color: #333;
                        }
                        
                        .food-amount {
                            font-size: 12px;
                            color: #999;
                        }
                    }
                    
                    .food-calorie {
                        font-size: 14px;
                        font-weight: 600;
                        color: #11998e;
                        margin-right: 12px;
                    }
                    
                    .delete-btn {
                        padding: 4px;
                        color: #ccc;
                        background: transparent;
                        border: none;
                        
                        &:hover {
                            color: #ff6b6b;
                        }
                    }
                }
            }
        }
    }
}

.food-dialog {
    border-radius: 20px 20px 0 0;
    margin: 0;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    max-height: 90vh;
    
    .dialog-content {
        .dialog-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 16px;
            border-bottom: 1px solid #f0f0f0;
            
            .back-btn {
                background: transparent;
                border: none;
                padding: 8px;
            }
            
            .dialog-title {
                font-size: 16px;
                font-weight: 600;
                color: #333;
            }
            
            .placeholder {
                width: 40px;
            }
        }
        
        .search-section {
            padding: 16px;
            display: flex;
            gap: 12px;
            
            .search-input {
                flex: 1;
                
                ::v-deep .el-input__inner {
                    background: #f5f5f5;
                    border: none;
                    border-radius: 12px;
                    height: 44px;
                }
            }
            
            .search-btn {
                background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
                border: none;
                color: #fff;
                border-radius: 12px;
                padding: 0 20px;
                font-size: 14px;
                
                i {
                    margin-right: 4px;
                }
            }
        }
        
        .api-tips {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 0 16px 12px;
            font-size: 12px;
            color: #999;
            
            i {
                color: #11998e;
            }
        }
        
        .loading-section {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 60px 16px;
            color: #999;
            
            .loading-spinner {
                width: 40px;
                height: 40px;
                border: 3px solid #f3f3f3;
                border-top: 3px solid #11998e;
                border-radius: 50%;
                animation: spin 1s linear infinite;
                margin-bottom: 16px;
            }
            
            @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
        }
        
        .no-result {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 60px 16px;
            color: #999;
            
            i {
                font-size: 48px;
                margin-bottom: 16px;
                color: #ddd;
            }
            
            span {
                font-size: 16px;
                margin-bottom: 8px;
            }
            
            .no-result-tip {
                font-size: 12px;
                color: #bbb;
            }
        }
        
        .section-title {
            padding: 0 16px 12px;
            font-size: 14px;
            font-weight: 600;
            color: #333;
            display: flex;
            align-items: center;
            
            .result-count {
                font-size: 12px;
                font-weight: 400;
                color: #999;
                margin-left: 8px;
            }
        }
        
        .quick-add-section {
            margin-bottom: 20px;
            
            .quick-foods {
                display: flex;
                flex-wrap: wrap;
                gap: 12px;
                padding: 0 16px;
                max-height: 280px;
                overflow-y: auto;
                
                &::-webkit-scrollbar {
                    width: 4px;
                }
                
                &::-webkit-scrollbar-thumb {
                    background: #ddd;
                    border-radius: 2px;
                }
                
                .quick-food-item {
                    width: calc(25% - 9px);
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    padding: 12px 8px;
                    background: #f8f9fc;
                    border-radius: 12px;
                    cursor: pointer;
                    transition: all 0.3s ease;
                    
                    &:hover {
                        background: #f0f2f8;
                        transform: translateY(-2px);
                    }
                    
                    .quick-food-icon {
                        width: 40px;
                        height: 40px;
                        background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
                        border-radius: 10px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        margin-bottom: 8px;
                        
                        i {
                            color: #fff;
                            font-size: 18px;
                        }
                    }
                    
                    .quick-food-name {
                        font-size: 12px;
                        color: #666;
                        text-align: center;
                    }
                    
                    .quick-food-calorie {
                        font-size: 10px;
                        color: #11998e;
                        margin-top: 2px;
                    }
                }
            }
        }
        
        .cookbook-section {
            .cookbook-list {
                .cookbook-item {
                    display: flex;
                    align-items: center;
                    padding: 12px 16px;
                    cursor: pointer;
                    transition: background 0.3s ease;
                    
                    &:hover {
                        background: #f8f9fc;
                    }
                    
                    .cookbook-cover {
                        width: 50px;
                        height: 50px;
                        border-radius: 10px;
                        overflow: hidden;
                        margin-right: 12px;
                        background: #f5f5f5;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        
                        img {
                            width: 100%;
                            height: 100%;
                            object-fit: cover;
                        }
                        
                        i {
                            font-size: 24px;
                            color: #ccc;
                        }
                    }
                    
                    .cookbook-info {
                        flex: 1;
                        
                        .cookbook-name {
                            display: block;
                            font-size: 15px;
                            font-weight: 500;
                            color: #333;
                        }
                        
                        .cookbook-desc {
                            font-size: 12px;
                            color: #999;
                        }
                    }
                    
                    .el-icon-arrow-right {
                        color: #ccc;
                    }
                }
            }
        }
        
        .food-results {
            .food-result-list {
                max-height: 300px;
                overflow-y: auto;
                
                .food-result-item {
                    display: flex;
                    align-items: center;
                    padding: 12px 16px;
                    cursor: pointer;
                    transition: background 0.3s ease;
                    
                    &:hover {
                        background: #f8f9fc;
                    }
                    
                    .result-icon {
                        width: 40px;
                        height: 40px;
                        background: #f5f5f5;
                        border-radius: 10px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        margin-right: 12px;
                        
                        i {
                            color: #11998e;
                        }
                    }
                    
                    .result-info {
                        flex: 1;
                        
                        .result-name-row {
                            display: flex;
                            align-items: center;
                            gap: 8px;
                            margin-bottom: 4px;
                        }
                        
                        .result-name {
                            font-size: 15px;
                            color: #333;
                        }
                        
                        .result-source {
                            font-size: 10px;
                            color: #fff;
                            background: #11998e;
                            padding: 2px 6px;
                            border-radius: 4px;
                        }
                        
                        .result-unit {
                            font-size: 12px;
                            color: #999;
                        }
                    }
                    
                    .el-icon-arrow-right {
                        color: #ccc;
                    }
                }
            }
        }
    }
}

.amount-dialog {
    border-radius: 20px;
    
    .amount-content {
        padding: 20px;
        
        .amount-header {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
            
            .selected-food-icon {
                width: 56px;
                height: 56px;
                background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
                border-radius: 14px;
                display: flex;
                align-items: center;
                justify-content: center;
                margin-right: 16px;
                
                i {
                    font-size: 24px;
                    color: #fff;
                }
            }
            
            .selected-food-info {
                .selected-food-name {
                    display: block;
                    font-size: 18px;
                    font-weight: 600;
                    color: #333;
                }
                
                .selected-food-unit {
                    font-size: 12px;
                    color: #999;
                }
            }
        }
        
        .input-mode-tabs {
            display: flex;
            background: #f5f5f5;
            border-radius: 12px;
            padding: 4px;
            margin-bottom: 20px;
            
            .mode-tab {
                flex: 1;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 6px;
                padding: 10px;
                border-radius: 10px;
                cursor: pointer;
                transition: all 0.3s ease;
                font-size: 14px;
                color: #666;
                
                i {
                    font-size: 16px;
                }
                
                &.active {
                    background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
                    color: #fff;
                }
            }
        }
        
        .amount-input-section {
            margin-bottom: 20px;
            
            .input-label {
                font-size: 14px;
                color: #666;
                margin-bottom: 12px;
            }
            
            .input-wrapper {
                display: flex;
                align-items: center;
                gap: 12px;
                margin-bottom: 16px;
                
                .amount-number {
                    flex: 1;
                }
                
                .input-unit {
                    font-size: 14px;
                    color: #999;
                }
            }
            
            .quick-amounts {
                display: flex;
                gap: 8px;
                
                .quick-amount {
                    padding: 6px 12px;
                    background: #f5f5f5;
                    border-radius: 16px;
                    font-size: 13px;
                    color: #666;
                    cursor: pointer;
                    transition: all 0.3s ease;
                    
                    &:hover {
                        background: #e8e8e8;
                    }
                    
                    &.active {
                        background: #11998e;
                        color: #fff;
                    }
                }
            }
            
            .calculated-weight {
                display: flex;
                align-items: center;
                gap: 6px;
                margin-top: 12px;
                padding: 10px 12px;
                background: rgba(17, 153, 142, 0.1);
                border-radius: 8px;
                font-size: 13px;
                color: #11998e;
                
                i {
                    font-size: 14px;
                }
            }
        }
        
        .amount-preview {
            background: #f8f9fc;
            border-radius: 12px;
            padding: 16px;
            margin-bottom: 20px;
            
            .preview-item {
                display: flex;
                justify-content: space-between;
                margin-bottom: 12px;
                
                .preview-label {
                    font-size: 14px;
                    color: #666;
                }
                
                .preview-value {
                    font-size: 18px;
                    font-weight: 600;
                    color: #11998e;
                }
            }
            
            .preview-nutrients {
                display: flex;
                justify-content: space-between;
                padding-top: 12px;
                border-top: 1px dashed #e0e0e0;
                
                .preview-nutrient {
                    text-align: center;
                    
                    .nutrient-label {
                        display: block;
                        font-size: 12px;
                        color: #999;
                        margin-bottom: 4px;
                    }
                    
                    .nutrient-val {
                        font-size: 14px;
                        font-weight: 600;
                        color: #333;
                    }
                }
            }
        }
        
        .amount-actions {
            display: flex;
            gap: 12px;
            
            .cancel-btn {
                flex: 1;
                background: #f5f5f5;
                border: none;
                color: #666;
            }
            
            .confirm-btn {
                flex: 2;
                background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
                border: none;
                color: #fff;
            }
        }
    }
}

.date-picker-dialog {
    border-radius: 16px;
    
    .date-picker-content {
        .picker-header {
            padding: 16px;
            border-bottom: 1px solid #f0f0f0;
            font-size: 16px;
            font-weight: 600;
            color: #333;
            text-align: center;
        }
        
        .picker-actions {
            display: flex;
            gap: 12px;
            padding: 16px;
            border-top: 1px solid #f0f0f0;
        }
    }
}

.target-dialog {
    border-radius: 16px;
    
    .target-content {
        .target-header {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            padding: 20px;
            background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
            border-radius: 16px 16px 0 0;
            color: #fff;
            font-size: 18px;
            font-weight: 600;
            
            i {
                font-size: 22px;
            }
        }
        
        .target-input-section {
            padding: 24px 20px;
            
            .target-input-wrapper {
                display: flex;
                align-items: center;
                gap: 12px;
                margin-bottom: 20px;
                
                .target-input {
                    flex: 1;
                    
                    ::v-deep .el-input__inner {
                        font-size: 24px;
                        font-weight: 600;
                        text-align: center;
                    }
                }
                
                .target-unit {
                    font-size: 16px;
                    color: #666;
                }
            }
            
            .target-presets {
                margin-bottom: 20px;
                
                .preset-row {
                    margin-bottom: 10px;
                    
                    .preset-label {
                        font-size: 13px;
                        color: #999;
                    }
                }
                
                .preset-btns {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 10px;
                    
                    .preset-btn {
                        padding: 8px 16px;
                        background: #f5f5f5;
                        border-radius: 20px;
                        font-size: 14px;
                        color: #666;
                        cursor: pointer;
                        transition: all 0.3s ease;
                        
                        &:hover {
                            background: #e8e8e8;
                        }
                        
                        &.active {
                            background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
                            color: #fff;
                        }
                    }
                }
            }
            
            .target-tips {
                background: #f8f9fc;
                border-radius: 12px;
                padding: 16px;
                
                .tip-item {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    font-size: 12px;
                    color: #666;
                    margin-bottom: 8px;
                    
                    &:last-child {
                        margin-bottom: 0;
                    }
                    
                    i {
                        color: #11998e;
                    }
                }
            }
        }
        
        .target-actions {
            display: flex;
            gap: 12px;
            padding: 16px 20px;
            border-top: 1px solid #f0f0f0;
            
            .el-button {
                flex: 1;
            }
        }
    }
}
</style>
