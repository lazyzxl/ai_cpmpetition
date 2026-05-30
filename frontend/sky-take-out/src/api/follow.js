import request from '../utils/request'

// 关注用户
export const followUserAPI = (followUserId) => {
  return request.post(`/user/follow/${followUserId}`)
}

// 取消关注
export const unfollowUserAPI = (followUserId) => {
  return request.delete(`/user/follow/${followUserId}`)
}

// 检查是否已关注
export const checkFollowAPI = (followUserId) => {
  return request.get(`/user/follow/check/${followUserId}`)
}

// 获取关注列表
export const getFollowListAPI = () => {
  return request.get('/user/follow/list')
}

// 获取粉丝列表
export const getFansListAPI = () => {
  return request.get('/user/follow/fans')
}

// 获取粉丝数量
export const getFansCountAPI = () => {
  return request.get('/user/follow/fans/count')
}

// 获取关注数量
export const getFollowCountAPI = () => {
  return request.get('/user/follow/follow/count')
}

// 获取可关注的用户列表
export const getAllUsersAPI = () => {
  return request.get('/user/follow/users')
}
