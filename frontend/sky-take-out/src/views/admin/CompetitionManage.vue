<template>
  <div class="competition-manage">
    <h2 class="page-title">比赛管理</h2>

    <div class="card">
      <div class="table-toolbar">
        <input
          v-model="searchKey"
          type="text"
          class="search-input"
          placeholder="搜索比赛名称..."
          @input="handleSearch"
        />
        <button @click="showAddDialog = true" class="btn btn-primary">添加比赛</button>
      </div>

      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>名称</th>
            <th>类别</th>
            <th>状态</th>
            <th>参赛人数</th>
            <th>截止时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in tableData" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.name }}</td>
            <td>{{ item.title || '-' }}</td>
            <td>{{ item.status }}</td>
            <td>{{ item.participantCount || 0 }}</td>
            <td>{{ item.deadline }}</td>
            <td>
              <button @click="handleEdit(item)" class="btn btn-text btn-black">修改</button>
              <button @click="handleDelete(item.id)" class="btn btn-text btn-black">删除</button>
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

    <div v-if="showAddDialog" class="dialog-overlay" @click.self="showAddDialog = false">
      <div class="dialog">
        <h3 class="dialog-title">{{ formData.id ? '修改比赛' : '添加比赛' }}</h3>
        <form @submit.prevent="handleAdd">
          <div class="form-item">
            <label class="form-label">比赛名称</label>
            <input v-model="formData.name" type="text" class="form-input" required />
          </div>
          <div class="form-item">
            <label class="form-label">比赛类别</label>
            <select v-model="formData.title" class="form-input">
              <option value="">请选择类别</option>
              <option value="工科类">工科类</option>
              <option value="理科类">理科类</option>
              <option value="文学">文学</option>
              <option value="法学">法学</option>
              <option value="教育学">教育学</option>
              <option value="其他">其他</option>
            </select>
          </div>
          <div class="form-item">
            <label class="form-label">描述</label>
            <textarea v-model="formData.description" class="form-input" rows="3"></textarea>
          </div>
          <div class="form-item">
            <label class="form-label">链接</label>
            <input v-model="formData.url" type="text" class="form-input" />
          </div>
          <div class="form-item">
            <label class="form-label">日期</label>
            <input v-model="formData.date" type="date" class="form-input" />
          </div>
          <div class="form-item">
            <label class="form-label">主办方</label>
            <input v-model="formData.organizer" type="text" class="form-input" />
          </div>
          <div class="form-item">
            <label class="form-label">举办地点</label>
            <input v-model="formData.location" type="text" class="form-input" />
          </div>
          <div class="form-item">
            <label class="form-label">参赛人数限制</label>
            <input v-model="formData.participantLimit" type="number" class="form-input" />
          </div>
          <div class="form-item">
            <label class="form-label">奖项设置</label>
            <textarea v-model="formData.prize" class="form-input" rows="2"></textarea>
          </div>
          <div class="form-item">
            <label class="form-label">联系方式</label>
            <input v-model="formData.contact" type="text" class="form-input" />
          </div>
          <div class="form-item">
            <label class="form-label">照片</label>
            <input type="file" @change="handlePhotoChange" accept="image/*" class="form-input" />
            <div v-if="photoPreview" class="photo-preview">
              <img :src="photoPreview" alt="照片预览" />
            </div>
          </div>
          <div class="dialog-actions">
            <button type="button" @click="showAddDialog = false" class="btn btn-text">取消</button>
            <button type="submit" class="btn btn-primary">确定</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCompetitionPageAPI, addCompetitionWithPhotoAPI, deleteCompetitionAPI, updateCompetitionWithPhotoAPI } from '@/api/admin/competition'

const tableData = ref([])
const searchKey = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showAddDialog = ref(false)
const formData = reactive({
  name: '',
  title: '',
  description: '',
  url: '',
  date: '',
  organizer: '',
  location: '',
  participantLimit: null,
  prize: '',
  contact: ''
})
const photoFile = ref(null)
const photoPreview = ref('')

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const loadData = async () => {
  try {
    const res = await getCompetitionPageAPI({
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

const handlePhotoChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    photoFile.value = file
    const reader = new FileReader()
    reader.onload = (e) => {
      photoPreview.value = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

const handleAdd = async () => {
  try {
    if (formData.id) {
      // 修改
      await updateCompetitionWithPhotoAPI(formData, photoFile.value)
      ElMessage.success('修改成功')
    } else {
      // 新增
      await addCompetitionWithPhotoAPI(formData, photoFile.value)
      ElMessage.success('添加成功')
    }
    showAddDialog.value = false
    Object.assign(formData, { id: null, name: '', title: '', description: '', url: '', date: '', organizer: '', location: '', participantLimit: null, prize: '', contact: '' })
    photoFile.value = null
    photoPreview.value = ''
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该比赛吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCompetitionAPI(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleEdit = (item) => {
  // 复制表单数据
  formData.id = item.id
  formData.name = item.name
  formData.title = item.title || ''
  formData.description = item.description || ''
  formData.url = item.url || ''
  formData.date = item.date || ''
  formData.organizer = item.organizer || ''
  formData.location = item.location || ''
  formData.participantLimit = item.participantLimit || null
  formData.prize = item.prize || ''
  formData.contact = item.contact || ''
  showAddDialog.value = true
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.competition-manage {
  padding: 10px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: #fff;
  border-radius: 10px;
  padding: 30px;
  width: 450px;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
}

.dialog form {
  overflow-y: auto;
  padding-right: 8px;
  max-height: calc(85vh - 80px);
}

.dialog form::-webkit-scrollbar {
  width: 6px;
}

.dialog form::-webkit-scrollbar-thumb {
  background: #d0d0d0;
  border-radius: 3px;
}

.dialog-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
}

.form-item {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  color: #666;
}

.form-input {
  width: 100%;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.btn-black {
  color: #333;
}

.btn-black:hover {
  background: #f5f5f5;
  color: #333;
}

.photo-preview {
  margin-top: 10px;
}

.photo-preview img {
  max-width: 100%;
  max-height: 200px;
  border-radius: 4px;
  border: 1px solid #e0e0e0;
}
</style>