<template>
    <div class="nutriment-page">
        <div class="page-header">
            <div class="header-left">
                <div class="back-btn" @click="goBack">
                    <i class="el-icon-arrow-left"></i>
                </div>
                <div class="title-section">
                    <h2 class="page-title">
                        <i class="el-icon-data-analysis"></i>
                        我的饮食
                    </h2>
                    <p class="page-subtitle">科学饮食，健康生活</p>
                </div>
            </div>
            <div class="header-right">
                <el-date-picker
                    v-model="selectedDate"
                    type="date"
                    placeholder="选择日期"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    @change="loadDataByDate"
                    class="date-picker"
                />
            </div>
        </div>

        <div class="stats-overview">
            <div class="stats-card calorie-card">
                <div class="card-header">
                    <span class="card-title">今日热量</span>
                    <span class="card-date">{{ formatDate(selectedDate) }}</span>
                </div>
                <div class="calorie-content">
                    <div class="calorie-ring">
                        <svg viewBox="0 0 120 120">
                            <circle class="ring-bg" cx="60" cy="60" r="52" />
                            <circle 
                                class="ring-progress" 
                                cx="60" cy="60" r="52" 
                                :style="{ strokeDashoffset: calorieOffset }"
                            />
                        </svg>
                        <div class="ring-center">
                            <span class="calorie-value">{{ todayCalories }}</span>
                            <span class="calorie-unit">kcal</span>
                        </div>
                    </div>
                    <div class="calorie-info">
                        <div class="info-row">
                            <span class="info-label">目标</span>
                            <span class="info-value">{{ dailyTarget }} kcal</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">剩余</span>
                            <span class="info-value remaining">{{ Math.max(0, dailyTarget - todayCalories) }} kcal</span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="stats-card nutrient-card">
                <div class="card-header">
                    <span class="card-title">营养素摄入</span>
                </div>
                <div class="nutrient-grid">
                    <div class="nutrient-item">
                        <div class="nutrient-icon carbs">
                            <i class="el-icon-grain"></i>
                        </div>
                        <div class="nutrient-info">
                            <span class="nutrient-name">碳水化合物</span>
                            <div class="nutrient-progress">
                                <div class="progress-bar" :style="{ width: getNutrientPercent('carbs') + '%' }"></div>
                            </div>
                            <span class="nutrient-value">{{ todayNutrients.carbs }}g / {{ targetNutrients.carbs }}g</span>
                        </div>
                    </div>
                    <div class="nutrient-item">
                        <div class="nutrient-icon protein">
                            <i class="el-icon-chicken"></i>
                        </div>
                        <div class="nutrient-info">
                            <span class="nutrient-name">蛋白质</span>
                            <div class="nutrient-progress protein-bar">
                                <div class="progress-bar" :style="{ width: getNutrientPercent('protein') + '%' }"></div>
                            </div>
                            <span class="nutrient-value">{{ todayNutrients.protein }}g / {{ targetNutrients.protein }}g</span>
                        </div>
                    </div>
                    <div class="nutrient-item">
                        <div class="nutrient-icon fat">
                            <i class="el-icon-water"></i>
                        </div>
                        <div class="nutrient-info">
                            <span class="nutrient-name">脂肪</span>
                            <div class="nutrient-progress fat-bar">
                                <div class="progress-bar" :style="{ width: getNutrientPercent('fat') + '%' }"></div>
                            </div>
                            <span class="nutrient-value">{{ todayNutrients.fat }}g / {{ targetNutrients.fat }}g</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="meals-overview">
            <div class="section-header">
                <h3 class="section-title">
                    <i class="el-icon-dish"></i>
                    各餐摄入
                </h3>
            </div>
            <div class="meals-grid">
                <div class="meal-card" v-for="meal in mealSummary" :key="meal.type">
                    <div class="meal-header">
                        <div class="meal-icon" :class="meal.type">
                            <i :class="meal.icon"></i>
                        </div>
                        <div class="meal-title">
                            <span class="meal-name">{{ meal.name }}</span>
                            <span class="meal-calories">{{ meal.calories }} kcal</span>
                        </div>
                    </div>
                    <div class="meal-nutrients">
                        <div class="nutrient-tag carbs">碳水 {{ meal.carbs }}g</div>
                        <div class="nutrient-tag protein">蛋白 {{ meal.protein }}g</div>
                        <div class="nutrient-tag fat">脂肪 {{ meal.fat }}g</div>
                    </div>
                    <div class="meal-foods" v-if="meal.foods && meal.foods.length > 0">
                        <div class="food-item" v-for="(food, index) in meal.foods.slice(0, 3)" :key="index">
                            <span class="food-name">{{ food.name }}</span>
                            <span class="food-amount">{{ food.amount }}g</span>
                        </div>
                        <div class="more-foods" v-if="meal.foods.length > 3">
                            +{{ meal.foods.length - 3 }} 更多
                        </div>
                    </div>
                    <div class="empty-meal" v-else>
                        <span>暂无记录</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="chart-section">
            <div class="section-header">
                <h3 class="section-title">
                    <i class="el-icon-trend-charts"></i>
                    营养趋势
                </h3>
                <div class="chart-tabs">
                    <span 
                        class="tab-item" 
                        :class="{ active: chartType === 'calories' }"
                        @click="chartType = 'calories'"
                    >热量</span>
                    <span 
                        class="tab-item" 
                        :class="{ active: chartType === 'carbs' }"
                        @click="chartType = 'carbs'"
                    >碳水</span>
                    <span 
                        class="tab-item" 
                        :class="{ active: chartType === 'protein' }"
                        @click="chartType = 'protein'"
                    >蛋白质</span>
                    <span 
                        class="tab-item" 
                        :class="{ active: chartType === 'fat' }"
                        @click="chartType = 'fat'"
                    >脂肪</span>
                </div>
            </div>
            <div class="chart-container">
                <LineChart 
                    :tag="chartTitle"
                    height="300px"
                    :values="chartData.values"
                    :date="chartData.dates"
                    @on-selected="onChartPeriodChange"
                />
            </div>
        </div>
    </div>
</template>

<script>
import LineChart from "@/components/LineChart"

export default {
    name: "Nutriment",
    components: { LineChart },
    data() {
        return {
            selectedDate: new Date().toISOString().split('T')[0],
            dailyTarget: 2000,
            todayCalories: 0,
            todayNutrients: {
                carbs: 0,
                protein: 0,
                fat: 0
            },
            targetNutrients: {
                carbs: 250,
                protein: 60,
                fat: 65
            },
            mealSummary: [
                { type: 'breakfast', name: '早餐', icon: 'el-icon-sunny', calories: 0, carbs: 0, protein: 0, fat: 0, foods: [] },
                { type: 'lunch', name: '午餐', icon: 'el-icon-partly-cloudy', calories: 0, carbs: 0, protein: 0, fat: 0, foods: [] },
                { type: 'dinner', name: '晚餐', icon: 'el-icon-moon', calories: 0, carbs: 0, protein: 0, fat: 0, foods: [] },
                { type: 'snack', name: '加餐', icon: 'el-icon-cookie', calories: 0, carbs: 0, protein: 0, fat: 0, foods: [] }
            ],
            chartType: 'calories',
            chartData: {
                values: [],
                dates: []
            },
            chartQuery: { day: 7, nutrimentId: null }
        }
    },
    computed: {
        calorieOffset() {
            const progress = Math.min(this.todayCalories / this.dailyTarget, 1);
            const circumference = 2 * Math.PI * 52;
            return circumference * (1 - progress);
        },
        chartTitle() {
            const titles = {
                calories: '热量摄入趋势',
                carbs: '碳水化合物趋势',
                protein: '蛋白质趋势',
                fat: '脂肪趋势'
            };
            return titles[this.chartType];
        }
    },
    watch: {
        chartType() {
            this.loadChartData();
        }
    },
    created() {
        this.loadTarget();
        this.loadTodayData();
        this.loadChartData();
    },
    methods: {
        goBack() {
            this.$router.back();
        },
        loadTarget() {
            const saved = localStorage.getItem('dailyTarget');
            if (saved) {
                this.dailyTarget = parseInt(saved);
                this.targetNutrients = {
                    carbs: Math.round(this.dailyTarget * 0.5 / 4),
                    protein: Math.round(this.dailyTarget * 0.25 / 4),
                    fat: Math.round(this.dailyTarget * 0.25 / 9)
                };
            }
        },
        loadTodayData() {
            const dateStr = this.selectedDate;
            const savedData = localStorage.getItem(`diet_${dateStr}`);
            
            if (savedData) {
                try {
                    const data = JSON.parse(savedData);
                    this.processLocalData(data);
                } catch (e) {
                    console.error('加载记录失败', e);
                    this.resetData();
                }
            } else {
                console.log('没有找到今日数据:', dateStr);
                this.resetData();
            }
        },
        processLocalData(data) {
            console.log('处理数据:', data);
            const mealFoods = data.mealFoods || { breakfast: [], lunch: [], dinner: [], snack: [] };
            console.log('餐食数据:', mealFoods);
            const nutrientSummary = data.nutrientSummary || { carbs: 0, protein: 0, fat: 0 };
            
            let totalCalories = 0;
            const mealMap = {
                breakfast: { calories: 0, carbs: 0, protein: 0, fat: 0, foods: [] },
                lunch: { calories: 0, carbs: 0, protein: 0, fat: 0, foods: [] },
                dinner: { calories: 0, carbs: 0, protein: 0, fat: 0, foods: [] },
                snack: { calories: 0, carbs: 0, protein: 0, fat: 0, foods: [] }
            };
            
            Object.keys(mealFoods).forEach(mealType => {
                const foods = mealFoods[mealType] || [];
                foods.forEach(food => {
                    const calories = food.calories || 0;
                    const carbs = food.carbs || 0;
                    const protein = food.protein || 0;
                    const fat = food.fat || 0;
                    
                    totalCalories += calories;
                    mealMap[mealType].calories += calories;
                    mealMap[mealType].carbs += carbs;
                    mealMap[mealType].protein += protein;
                    mealMap[mealType].fat += fat;
                    mealMap[mealType].foods.push({
                        name: food.name,
                        cover: food.cover || '',
                        amount: food.amount
                    });
                });
            });
            
            this.todayCalories = Math.round(totalCalories);
            this.todayNutrients = {
                carbs: Math.round(mealMap.breakfast.carbs + mealMap.lunch.carbs + mealMap.dinner.carbs + mealMap.snack.carbs),
                protein: Math.round(mealMap.breakfast.protein + mealMap.lunch.protein + mealMap.dinner.protein + mealMap.snack.protein),
                fat: Math.round(mealMap.breakfast.fat + mealMap.lunch.fat + mealMap.dinner.fat + mealMap.snack.fat)
            };
            
            this.mealSummary = this.mealSummary.map(meal => ({
                ...meal,
                calories: Math.round(mealMap[meal.type].calories),
                carbs: Math.round(mealMap[meal.type].carbs * 10) / 10,
                protein: Math.round(mealMap[meal.type].protein * 10) / 10,
                fat: Math.round(mealMap[meal.type].fat * 10) / 10,
                foods: mealMap[meal.type].foods
            }));
        },
        resetData() {
            this.todayCalories = 0;
            this.todayNutrients = { carbs: 0, protein: 0, fat: 0 };
            this.mealSummary = [
                { type: 'breakfast', name: '早餐', icon: 'el-icon-sunny', calories: 0, carbs: 0, protein: 0, fat: 0, foods: [] },
                { type: 'lunch', name: '午餐', icon: 'el-icon-partly-cloudy', calories: 0, carbs: 0, protein: 0, fat: 0, foods: [] },
                { type: 'dinner', name: '晚餐', icon: 'el-icon-moon', calories: 0, carbs: 0, protein: 0, fat: 0, foods: [] },
                { type: 'snack', name: '加餐', icon: 'el-icon-cookie', calories: 0, carbs: 0, protein: 0, fat: 0, foods: [] }
            ];
        },
        getNutrientPercent(type) {
            const value = this.todayNutrients[type];
            const target = this.targetNutrients[type];
            return Math.min((value / target) * 100, 100);
        },
        loadChartData() {
            const values = [];
            const dates = [];
            const today = new Date();
            const days = this.chartQuery.day || 7;
            
            for (let i = days - 1; i >= 0; i--) {
                const date = new Date(today);
                date.setDate(date.getDate() - i);
                const dateStr = date.toISOString().split('T')[0];
                const displayDate = `${date.getMonth() + 1}/${date.getDate()}`;
                
                const savedData = localStorage.getItem(`diet_${dateStr}`);
                let dayValue = 0;
                
                if (savedData) {
                    try {
                        const data = JSON.parse(savedData);
                        const mealFoods = data.mealFoods || {};
                        
                        Object.values(mealFoods).forEach(foods => {
                            (foods || []).forEach(food => {
                                if (this.chartType === 'calories') {
                                    dayValue += food.calories || 0;
                                } else if (this.chartType === 'carbs') {
                                    dayValue += food.carbs || 0;
                                } else if (this.chartType === 'protein') {
                                    dayValue += food.protein || 0;
                                } else if (this.chartType === 'fat') {
                                    dayValue += food.fat || 0;
                                }
                            });
                        });
                    } catch (e) {
                        console.error('解析数据失败', e);
                    }
                }
                
                dates.push(displayDate);
                values.push(Math.round(dayValue));
            }
            
            this.chartData = { values, dates };
        },
        onChartPeriodChange(days) {
            this.chartQuery.day = days;
            this.loadChartData();
        },
        loadDataByDate() {
            this.loadTodayData();
        },
        formatDate(date) {
            if (!date) return '';
            const d = new Date(date);
            const today = new Date();
            if (d.toDateString() === today.toDateString()) {
                return '今天';
            }
            return `${d.getMonth() + 1}月${d.getDate()}日`;
        }
    }
}
</script>

<style scoped lang="scss">
.nutriment-page {
    min-height: calc(100vh - 120px);
    padding: 24px;
    background: linear-gradient(180deg, #f8f9fc 0%, #f0f2f8 100%);
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 24px;
    
    .header-left {
        display: flex;
        align-items: flex-start;
        gap: 12px;
        
        .back-btn {
            width: 40px;
            height: 40px;
            border-radius: 10px;
            background: #f5f5f5;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.3s ease;
            
            i {
                font-size: 20px;
                color: #333;
            }
            
            &:hover {
                background: #eee;
            }
        }
        
        .title-section {
            .page-title {
                display: flex;
                align-items: center;
                gap: 10px;
                margin: 0;
                font-size: 24px;
                font-weight: 700;
                color: #333;
                
                i {
                    color: #11998e;
                }
            }
            
            .page-subtitle {
                margin: 6px 0 0;
                font-size: 14px;
                color: #999;
            }
        }
    }
    
    .date-picker {
        width: 160px;
    }
}

.stats-overview {
    display: grid;
    grid-template-columns: 1fr 2fr;
    gap: 20px;
    margin-bottom: 24px;
}

.stats-card {
    background: #fff;
    border-radius: 16px;
    padding: 20px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    
    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
        
        .card-title {
            font-size: 16px;
            font-weight: 600;
            color: #333;
        }
        
        .card-date {
            font-size: 12px;
            color: #999;
            padding: 4px 10px;
            background: #f5f5f5;
            border-radius: 10px;
        }
    }
}

.calorie-card {
    background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
    
    .card-header .card-title {
        color: #fff;
    }
    
    .card-header .card-date {
        background: rgba(255, 255, 255, 0.2);
        color: #fff;
    }
    
    .calorie-content {
        display: flex;
        align-items: center;
        gap: 20px;
    }
    
    .calorie-ring {
        position: relative;
        width: 100px;
        height: 100px;
        
        svg {
            transform: rotate(-90deg);
        }
        
        .ring-bg {
            fill: none;
            stroke: rgba(255, 255, 255, 0.2);
            stroke-width: 10;
        }
        
        .ring-progress {
            fill: none;
            stroke: #fff;
            stroke-width: 10;
            stroke-linecap: round;
            stroke-dasharray: 326.73;
            transition: stroke-dashoffset 0.5s ease;
        }
        
        .ring-center {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            text-align: center;
            
            .calorie-value {
                display: block;
                font-size: 22px;
                font-weight: 700;
                color: #fff;
            }
            
            .calorie-unit {
                font-size: 11px;
                color: rgba(255, 255, 255, 0.8);
            }
        }
    }
    
    .calorie-info {
        flex: 1;
        
        .info-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 8px;
            
            .info-label {
                font-size: 13px;
                color: rgba(255, 255, 255, 0.8);
            }
            
            .info-value {
                font-size: 14px;
                font-weight: 600;
                color: #fff;
                
                &.remaining {
                    color: #ffd93d;
                }
            }
        }
    }
}

.nutrient-card {
    .nutrient-grid {
        display: flex;
        flex-direction: column;
        gap: 16px;
    }
    
    .nutrient-item {
        display: flex;
        align-items: center;
        gap: 14px;
        
        .nutrient-icon {
            width: 42px;
            height: 42px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            
            i {
                font-size: 20px;
                color: #fff;
            }
            
            &.carbs { background: linear-gradient(135deg, #6bcb77 0%, #4ade80 100%); }
            &.protein { background: linear-gradient(135deg, #ff6b6b 0%, #f87171 100%); }
            &.fat { background: linear-gradient(135deg, #ffd93d 0%, #fbbf24 100%); }
        }
        
        .nutrient-info {
            flex: 1;
            
            .nutrient-name {
                display: block;
                font-size: 13px;
                color: #666;
                margin-bottom: 6px;
            }
            
            .nutrient-progress {
                height: 8px;
                background: #f0f0f0;
                border-radius: 4px;
                overflow: hidden;
                margin-bottom: 4px;
                
                .progress-bar {
                    height: 100%;
                    background: linear-gradient(90deg, #6bcb77 0%, #4ade80 100%);
                    border-radius: 4px;
                    transition: width 0.5s ease;
                }
                &.protein-bar .progress-bar {
                    background: linear-gradient(90deg, #ff6b6b 0%, #f87171 100%);
                }
                &.fat-bar .progress-bar {
                    background: linear-gradient(90deg, #ffd93d 0%, #fbbf24 100%);
                }
            }
            
            .nutrient-value {
                font-size: 12px;
                color: #999;
            }
        }
    }
}

.meals-overview {
    margin-bottom: 24px;
    
    .section-header {
        margin-bottom: 16px;
        
        .section-title {
            display: flex;
            align-items: center;
            gap: 8px;
            margin: 0;
            font-size: 18px;
            font-weight: 600;
            color: #333;
            
            i {
                color: #11998e;
            }
        }
    }
    
    .meals-grid {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 16px;
    }
    
    .meal-card {
        background: #fff;
        border-radius: 16px;
        padding: 16px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
        transition: all 0.3s ease;
        
        &:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
        }
        
        .meal-header {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 12px;
            
            .meal-icon {
                width: 36px;
                height: 36px;
                border-radius: 10px;
                display: flex;
                align-items: center;
                justify-content: center;
                
                i {
                    font-size: 18px;
                    color: #fff;
                }
                
                &.breakfast { background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%); }
                &.lunch { background: linear-gradient(135deg, #f97316 0%, #ea580c 100%); }
                &.dinner { background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%); }
                &.snack { background: linear-gradient(135deg, #ec4899 0%, #db2777 100%); }
            }
            
            .meal-title {
                .meal-name {
                    display: block;
                    font-size: 14px;
                    font-weight: 600;
                    color: #333;
                }
                
                .meal-calories {
                    font-size: 12px;
                    color: #11998e;
                }
            }
        }
        
        .meal-nutrients {
            display: flex;
            gap: 6px;
            margin-bottom: 12px;
            flex-wrap: wrap;
            
            .nutrient-tag {
                padding: 3px 8px;
                border-radius: 10px;
                font-size: 11px;
                
                &.carbs { background: rgba(107, 203, 119, 0.15); color: #22c55e; }
                &.protein { background: rgba(255, 107, 107, 0.15); color: #ef4444; }
                &.fat { background: rgba(255, 217, 61, 0.15); color: #f59e0b; }
            }
        }
        
        .meal-foods {
            .food-item {
                display: flex;
                align-items: center;
                gap: 8px;
                padding: 6px 0;
                border-bottom: 1px solid #f5f5f5;
                
                &:last-child {
                    border-bottom: none;
                }
                
                .food-name {
                    flex: 1;
                    font-size: 12px;
                    color: #333;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                }
                
                .food-amount {
                    font-size: 11px;
                    color: #999;
                }
            }
            
            .more-foods {
                text-align: center;
                padding: 6px 0;
                font-size: 11px;
                color: #11998e;
            }
        }
        
        .empty-meal {
            text-align: center;
            padding: 20px 0;
            color: #ccc;
            font-size: 13px;
        }
    }
}

.chart-section {
    background: #fff;
    border-radius: 16px;
    padding: 20px;
    margin-bottom: 24px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    
    .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
        
        .section-title {
            display: flex;
            align-items: center;
            gap: 8px;
            margin: 0;
            font-size: 18px;
            font-weight: 600;
            color: #333;
            
            i {
                color: #11998e;
            }
        }
        
        .chart-tabs {
            display: flex;
            gap: 8px;
            
            .tab-item {
                padding: 6px 14px;
                border-radius: 16px;
                font-size: 13px;
                color: #666;
                cursor: pointer;
                transition: all 0.3s ease;
                
                &:hover {
                    background: #f5f5f5;
                }
                
                &.active {
                    background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
                    color: #fff;
                }
            }
        }
    }
    
    .chart-container {
        padding: 10px 0;
    }
}

@media (max-width: 1200px) {
    .stats-overview {
        grid-template-columns: 1fr;
    }
    
    .meals-overview .meals-grid {
        grid-template-columns: repeat(2, 1fr);
    }
}

@media (max-width: 768px) {
    .meals-overview .meals-grid {
        grid-template-columns: 1fr;
    }
    
    .page-header {
        flex-direction: column;
        gap: 16px;
    }
}
</style>
