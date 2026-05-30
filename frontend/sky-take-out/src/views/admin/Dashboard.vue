<template>
  <div class="dashboard">
    <h2 class="page-title">数据概览</h2>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon blue">👥</div>
        <div class="stat-info">
          <span class="stat-value">{{ summary.userCount || 0 }}</span>
          <span class="stat-label">用户总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon purple">🏆</div>
        <div class="stat-info">
          <span class="stat-value">{{ summary.competitionCount || 0 }}</span>
          <span class="stat-label">比赛总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange">📝</div>
        <div class="stat-info">
          <span class="stat-value">{{ summary.postCount || 0 }}</span>
          <span class="stat-label">帖子总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green">❤️</div>
        <div class="stat-info">
          <span class="stat-value">{{ summary.totalCares || 0 }}</span>
          <span class="stat-label">关注总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon red">👁️</div>
        <div class="stat-info">
          <span class="stat-value">{{ summary.totalViews || 0 }}</span>
          <span class="stat-label">浏览总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon yellow">🚨</div>
        <div class="stat-info">
          <span class="stat-value">{{ summary.reportCount || 0 }}</span>
          <span class="stat-label">待处理举报</span>
        </div>
      </div>
    </div>

    <div class="charts-grid">
      <div class="card chart-card">
        <h3 class="card-title">🔥 比赛关注排行榜</h3>
        <div class="ranking-list" v-if="ranking.length > 0">
          <div class="ranking-item" v-for="(item, index) in ranking" :key="item.id">
            <span class="ranking-num" :class="{ top: index < 3 }">{{ index + 1 }}</span>
            <span class="ranking-name">{{ item.name }}</span>
            <span class="ranking-count">{{ item.careCount }} 关注</span>
          </div>
        </div>
        <div class="empty-state" v-else>暂无数据</div>
      </div>

      <div class="card chart-card">
        <h3 class="card-title">📊 用户活跃统计</h3>
        <div class="activity-stats" v-if="userActivity.totalUsers">
          <div class="activity-item">
            <span class="activity-label">总用户数</span>
            <span class="activity-value">{{ userActivity.totalUsers }}</span>
          </div>
          <div class="activity-item">
            <span class="activity-label">活跃用户</span>
            <span class="activity-value success">{{ userActivity.activeUsers }}</span>
          </div>
          <div class="activity-item">
            <span class="activity-label">沉默用户</span>
            <span class="activity-value muted">{{ userActivity.inactiveUsers }}</span>
          </div>
          <div class="activity-item">
            <span class="activity-label">活跃率</span>
            <span class="activity-value highlight">{{ userActivity.activeRate?.toFixed(1) || 0 }}%</span>
          </div>
        </div>
        <div class="empty-state" v-else>暂无数据</div>
      </div>

      <div class="card chart-card">
        <h3 class="card-title">⚠️ 举报处理统计</h3>
        <div class="report-stats" v-if="reportStats.totalReports">
          <div class="report-item">
            <span class="report-dot pending"></span>
            <span class="report-label">待处理</span>
            <span class="report-value">{{ reportStats.pendingReports }}</span>
          </div>
          <div class="report-item">
            <span class="report-dot processed"></span>
            <span class="report-label">已通过</span>
            <span class="report-value">{{ reportStats.processedReports }}</span>
          </div>
          <div class="report-item">
            <span class="report-dot rejected"></span>
            <span class="report-label">已驳回</span>
            <span class="report-value">{{ reportStats.rejectedReports }}</span>
          </div>
          <div class="report-item total">
            <span class="report-label">总计</span>
            <span class="report-value">{{ reportStats.totalReports }}</span>
          </div>
        </div>
        <div class="empty-state" v-else>暂无数据</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSummaryAPI, getCompetitionRankingAPI, getUserActivityAPI, getReportStatisticsAPI } from '@/api/admin/statistics'

const summary = ref({})
const ranking = ref([])
const userActivity = ref({})
const reportStats = ref({})

const loadSummary = async () => {
  try {
    const res = await getSummaryAPI()
    summary.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const loadRanking = async () => {
  try {
    const res = await getCompetitionRankingAPI()
    ranking.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const loadUserActivity = async () => {
  try {
    const res = await getUserActivityAPI()
    userActivity.value = res.data || {}
  } catch (error) {
    console.error(error)
  }
}

const loadReportStats = async () => {
  try {
    const res = await getReportStatisticsAPI()
    reportStats.value = res.data || {}
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadSummary()
  loadRanking()
  loadUserActivity()
  loadReportStats()
})
</script>

<style scoped>
.dashboard {
  padding: 10px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin-bottom: 30px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 36px;
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
}

.stat-icon.blue { background: #e6f7ff; }
.stat-icon.purple { background: #f3e8ff; }
.stat-icon.orange { background: #fff7e6; }
.stat-icon.green { background: #e7f7e7; }
.stat-icon.red { background: #fff1f0; }
.stat-icon.yellow { background: #fffbe6; }

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  color: #999;
  font-size: 13px;
  margin-top: 4px;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
}

.chart-card {
  min-height: 300px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.ranking-num {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f0f0;
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
  color: #666;
}

.ranking-num.top {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.ranking-name {
  flex: 1;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ranking-count {
  font-size: 12px;
  color: #999;
}

.activity-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.activity-item {
  display: flex;
  flex-direction: column;
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
}

.activity-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.activity-value {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.activity-value.success {
  color: #67c23a;
}

.activity-value.muted {
  color: #999;
}

.activity-value.highlight {
  color: #667eea;
}

.report-stats {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.report-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
}

.report-item.total {
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
  margin-top: 4px;
}

.report-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.report-dot.pending { background: #faad14; }
.report-dot.processed { background: #67c23a; }
.report-dot.rejected { background: #999; }

.report-label {
  flex: 1;
  font-size: 14px;
  color: #666;
}

.report-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 150px;
  color: #999;
  font-size: 14px;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }
}
</style>
