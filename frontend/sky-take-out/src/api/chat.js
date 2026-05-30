import request from '../utils/request'

// 发送消息
export const sendMessageAPI = (receiverId, content) => {
  return request.post('/user/chat/send', null, {
    params: { receiverId, content }
  })
}

// 获取聊天记录
export const getChatHistoryAPI = (userId) => {
  return request.get(`/user/chat/history/${userId}`)
}

// 获取聊天会话列表
export const getChatSessionsAPI = () => {
  return request.get('/user/chat/sessions')
}

// 标记消息为已读
export const markAsReadAPI = (senderId) => {
  return request.put(`/user/chat/read/${senderId}`)
}

// 获取未读消息数
export const getUnreadCountAPI = () => {
  return request.get('/user/chat/unread')
}
