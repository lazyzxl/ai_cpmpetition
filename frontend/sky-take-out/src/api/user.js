import request from '../utils/request'

export const getUserInfoAPI = (id) => {
  return request.get('/user/information', { params: { id } })
}

export const updateUserInfoAPI = (data) => {
  return request.put('/user/information', data)
}

export const updatePasswordAPI = (data) => {
  return request.put('/user/information/updatePassword', data)
}

export const getMessagesAPI = () => {
  return request.get('/user/message/list')
}

export const getUnreadCountAPI = () => {
  return request.get('/user/message/unreadCount')
}

export const markAsReadAPI = (messageId) => {
  return request.put('/user/message/markAsRead', { params: { messageId } })
}

export const markAllReadAPI = () => {
  return request.put('/user/message/markAllRead')
}
