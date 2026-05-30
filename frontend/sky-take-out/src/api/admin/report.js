import request from '../../utils/request'

export const getReportPageAPI = (params) => {
  const queryParams = {
    page: params.current || params.page || 1,
    pageSize: params.size || params.pageSize || 10,
    status: params.status,
    startTime: params.startTime,
    endTime: params.endTime
  }
  return request.get('/admin/report/selectPage', { params: queryParams })
}

export const handleReportAPI = (id, status) => {
  return request.put(`/admin/report/${id}`, { status })
}

export const deleteReportAPI = (id) => {
  return request.delete(`/admin/report/${id}`)
}

export const getReportStatisticsAPI = () => {
  return request.get('/admin/statistics/reportStatistics')
}