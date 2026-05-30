<template>
  <div class="message-manage">
    <h2 class="page-title">消息管理</h2>

    <div class="card">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>标题</th>
            <th>内容</th>
            <th>发送者</th>
            <th>接收者</th>
            <th>时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in tableData" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.title }}</td>
            <td class="content-cell">{{ item.content }}</td>
            <td>{{ item.senderName || '-' }}</td>
            <td>{{ item.receiverName || '-' }}</td>
            <td>{{ item.createTime }}</td>
            <td>
              <button @click="handleDelete(item.id)" class="btn btn-text">删除</button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMessagePageAPI, deleteMessageAPI } from '@/api/admin/message'

const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const loadData = async () => {
  try {
    const res = await getMessagePageAPI({
      current: pageNum.value,
      size: pageSize.value
    })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const handlePageChange = (page) => {
  pageNum.value = page
  loadData()
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteMessageAPI(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.message-manage {
  padding: 10px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
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

.content-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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