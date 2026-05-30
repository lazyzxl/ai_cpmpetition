import request from '../../utils/request'

export const getCompetitionPageAPI = (params) => {
  const queryParams = {
    page: params.current || params.page || 1,
    pageSize: params.size || params.pageSize || 10,
    name: params.name
  }
  return request.get('/admin/competition/selectPage', { params: queryParams })
}

export const addCompetitionAPI = (data) => {
  return request.post('/admin/competition', data)
}

export const addCompetitionWithPhotoAPI = (data, file) => {
  const formData = new FormData()
  formData.append('name', data.name)
  formData.append('title', data.title || '')
  formData.append('description', data.description || '')
  formData.append('url', data.url || '')
  formData.append('date', data.date || '')
  formData.append('organizer', data.organizer || '')
  formData.append('location', data.location || '')
  formData.append('participantLimit', data.participantLimit || '')
  formData.append('prize', data.prize || '')
  formData.append('contact', data.contact || '')
  if (file) {
    formData.append('photo', file)
  }
  return request.post('/admin/competition/saveWithPhoto', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const updateCompetitionAPI = (data) => {
  return request.put('/admin/competition', data)
}

export const updateCompetitionWithPhotoAPI = (data, file) => {
  const formData = new FormData()
  formData.append('id', data.id)
  formData.append('name', data.name)
  formData.append('title', data.title || '')
  formData.append('description', data.description || '')
  formData.append('url', data.url || '')
  formData.append('date', data.date || '')
  formData.append('organizer', data.organizer || '')
  formData.append('location', data.location || '')
  formData.append('participantLimit', data.participantLimit || '')
  formData.append('prize', data.prize || '')
  formData.append('contact', data.contact || '')
  if (file) {
    formData.append('photo', file)
  }
  return request.put('/admin/competition/updateWithPhoto', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const deleteCompetitionAPI = (id) => {
  return request.delete(`/admin/competition/${id}`)
}