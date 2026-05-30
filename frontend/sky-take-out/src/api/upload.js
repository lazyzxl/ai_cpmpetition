import request from './request'

export const uploadFileAPI = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/common/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
