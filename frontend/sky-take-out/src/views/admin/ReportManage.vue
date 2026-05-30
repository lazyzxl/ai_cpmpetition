<template>
  <div class="report-manage">
    <div class="page-header">
      <h2 class="page-title">举报管理</h2>

    </div>

    <el-card class="main-card">
      <div class="stats-row">
        <div class="stat-item">
          <span class="stat-value">{{ stats.total }}</span>
          <span class="stat-label">总举报</span>
        </div>
        <div class="stat-item warning">
          <span class="stat-value">{{ stats.pending }}</span>
          <span class="stat-label">待处理</span>
        </div>
        <div class="stat-item success">
          <span class="stat-value">{{ stats.resolved }}</span>
          <span class="stat-label">已通过</span>
        </div>
        <div class="stat-item danger">
          <span class="stat-value">{{ stats.rejected }}</span>
          <span class="stat-label">已驳回</span>
        </div>
      </div>

      <el-table
        :data="tableData"
        border
        stripe
        :height="tableHeight"
        class="report-table"
      >
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column label="举报类型" width="120">
          <template #default="scope">
            <span>评论举报</span>
          </template>
        </el-table-column>
        <el-table-column 
          prop="comment" 
          label="评论内容" 
          min-width="250"
          show-overflow-tooltip
        />
        <el-table-column 
          prop="cause" 
          label="举报原因" 
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="举报时间" width="160" />
        <el-table-column prop="updateTime" label="处理时间" width="160">
          <template #default="scope">
            <span v-if="scope.row.updateTime">{{ scope.row.updateTime }}</span>
            <span v-else class="no-data">--</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default="scope">
            <template v-if="scope.row.status === 0">
              <el-button
                @click="handleResolve(scope.row.id, 1)"
                type="success"
                size="small"
                icon="Check"
              >
                通过
              </el-button>
              <el-button
                @click="handleResolve(scope.row.id, 2)"
                type="danger"
                size="small"
                icon="Close"
              >
                驳回
              </el-button>
            </template>
            <span v-else class="no-action">--</span>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="tableData.length === 0" class="empty-state">
        <el-empty description="暂无举报数据" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getReportPageAPI, handleReportAPI, getReportStatisticsAPI } from '@/api/admin/report'

const tableData = ref([])
const statusFilter = ref('')
const startDate = ref('')
const endDate = ref('')
const stats = ref({
  total: 0,
  pending: 0,
  resolved: 0,
  rejected: 0
})

const tableHeight = ref(400)

const getStatusTagType = (status) => {
  if (status === 0) return 'warning'
  if (status === 1) return 'success'
  return 'info'
}

const getStatusText = (status) => {
  if (status === 0) return '待处理'
  if (status === 1) return '已通过'
  return '已驳回'
}

const loadData = async () => {
  try {
    const params = {}
    if (statusFilter.value !== '') {
      params.status = parseInt(statusFilter.value)
    }
    if (startDate.value) {
      params.startTime = startDate.value
    }
    if (endDate.value) {
      params.endTime = endDate.value
    }
    const res = await getReportPageAPI(params)
    tableData.value = res.data.records || []
  } catch (error) {
    console.error(error)
  }
}

const loadStatistics = async () => {
  try {
    const res = await getReportStatisticsAPI()
    stats.value = {
      total: res.data.totalReports || 0,
      pending: res.data.pendingReports || 0,
      resolved: res.data.processedReports || 0,
      rejected: res.data.rejectedReports || 0
    }
  } catch (error) {
    console.error(error)
  }
}

const handleResolve = async (id, status) => {
  try {
    await handleReportAPI(id, status)
    ElMessage.success(status === 1 ? '已通过' : '已驳回')
    loadData()
    loadStatistics()
  } catch (error) {
    console.error(error)
  }
}

const resetFilters = () => {
  statusFilter.value = ''
  startDate.value = ''
  endDate.value = ''
  loadData()
}

const updateTableHeight = () => {
  const headerHeight = 180
  const footerHeight = 40
  tableHeight.value = window.innerHeight - headerHeight - footerHeight - 100
}

onMounted(() => {
  loadData()
  loadStatistics()
  updateTableHeight()
  window.addEventListener('resize', updateTableHeight)
})

onUnmounted(() => {
  window.removeEventListener('resize', updateTableHeight)
})
</script>

<style scoped>
.report-manage {
  padding: 20px;
  min-height: 100vh;
  background: #f5f7fa;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1f2329;
  margin: 0;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-label {
  font-size: 14px;
  color: #646a73;
}

.filter-select {
  width: 140px;
}

.date-picker {
  width: 150px;
}

.date-separator {
  color: #ccc;
}

.main-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}

.stats-row {
  display: flex;
  gap: 16px;
  padding: 20px 0;
  border-bottom: 1px solid #e8e8e8;
  margin-bottom: 16px;
}

.stat-item {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  padding: 16px;
  text-align: center;
  color: #fff;
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.stat-item.warning {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-item.success {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.stat-item.danger {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.report-table {
  width: 100%;
}

.no-action, .no-data {
  color: #bbb;
}

.empty-state {
  padding: 40px;
}
</style>
