<template>
  <div class="user-manage">
    <h2 class="page-title">用户管理</h2>

    <div class="card">
      <div class="table-toolbar">
        <input
          v-model="searchKey"
          type="text"
          class="search-input"
          placeholder="搜索用户名..."
          @input="handleSearch"
        />
      </div>

      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>手机号</th>
            <th>专业</th>
            <th>角色</th>
            <th>状态</th>
            <th>注册时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in tableData" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.phone || '-' }}</td>
            <td>{{ user.profession || '-' }}</td>
            <td>{{ user.role === 'admin' ? '管理员' : '普通用户' }}</td>
            <td>
              <span :class="['status-tag', user.status === 1 ? 'status-active' : 'status-locked']">
                {{ user.status === 1 ? '正常' : '锁定' }}
              </span>
            </td>
            <td>{{ user.createTime }}</td>
            <td>
              <button
                v-if="user.status === 1"
                @click="handleDisable(user.id)"
                class="btn btn-text"
              >
                锁定
              </button>
              <button
                v-else
                @click="handleEnable(user.id)"
                class="btn btn-text"
              >
                解锁
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="pagination" v-if="total > pageSize">
        <button
          @click="handlePageChange(pageNum - 1)"
          :disabled="pageNum === 1"
          class="btn btn-text"
        >
          上一页
        </button>
        <span class="page-info">{{ pageNum }} / {{ totalPages }}</span>
        <button
          @click="handlePageChange(pageNum + 1)"
          :disabled="pageNum >= totalPages"
          class="btn btn-text"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserPageAPI, enableUserAPI, disableUserAPI } from '@/api/admin/user'

const tableData = ref([])
const searchKey = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const loadData = async () => {
  try {
    const res = await getUserPageAPI({
      current: pageNum.value,
      size: pageSize.value,
      name: searchKey.value
    })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const handlePageChange = (page) => {
  pageNum.value = page
  loadData()
}

const handleEnable = async (id) => {
  try {
    await enableUserAPI(id)
    ElMessage.success('解锁成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDisable = async (id) => {
  try {
    await disableUserAPI(id)
    ElMessage.success('锁定成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.user-manage {
  padding: 10px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.table-toolbar {
  margin-bottom: 20px;
}

.search-input {
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  width: 200px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.data-table th {
  font-weight: 600;
  color: #666;
  font-size: 13px;
}

.data-table td {
  color: #333;
  font-size: 14px;
}

.status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-active {
  background: #e7f7e7;
  color: #67c23a;
}

.status-locked {
  background: #fdeaea;
  color: #f56c6c;
}

.btn {
  padding: 6px 14px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.btn-text {
  background: linear-gradient(135deg, #7c6cff 0%, #5a4fcf 100%);
  color: #fff;
  border: none;
}

.btn-text:hover {
  background: linear-gradient(135deg, #6b5ce7 0%, #4b3ec4 100%);
  box-shadow: 0 2px 8px rgba(90, 79, 207, 0.3);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
}

.page-info {
  color: #666;
}
</style>