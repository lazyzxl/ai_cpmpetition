import request from '../../utils/request'

export const getMyMessagesAPI = () => {
  return request.get('/user/message/list')
}

export const markMessageAsReadAPI = (messageId) => {
  return request.put('/user/message/markAsRead', null, { params: { messageId } })
}

export const markAllAsReadAPI = () => {
  return request.put('/user/message/markAllRead')
}

export const getUnreadCountAPI = () => {
  return request.get('/user/message/unreadCount')
}
