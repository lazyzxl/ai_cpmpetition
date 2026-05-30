import request from '../utils/request'

export const getPostByIdAPI = (id) => {
  return request.get(`/post/${id}`)
}

export const getPostListAPI = (params) => {
  return request.get('/post', { 
    params: {
      page: params.page,
      pageSize: params.pageSize
    } 
  })
}

export const createPostAPI = (data) => {
  return request.post('/post', data)
}

export const saveCommentAPI = (postId, content) => {
  return request.post(`/post/${postId}`, null, { params: { content } })
}

export const reportPostAPI = (data) => {
  // 转换字段名以匹配后端 ReportDTO
  const reportData = {
    commentId: data.targetId,
    cause: data.reason
  }
  return request.post('/post/report', reportData)
}