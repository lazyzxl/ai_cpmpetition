import request from '../utils/request'

export const loginAPI = (data) => {
  return request.post('/login', data)
}

export const registerAPI = (data) => {
  return request.post('login/register', data)
}

export const forgetPasswordAPI = (data) => {
  return request.put('/forgetPassword', data)
}

export const logoutAPI = () => {
  return request.post('/logout')
}