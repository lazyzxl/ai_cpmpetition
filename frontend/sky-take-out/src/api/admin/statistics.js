import request from '../../utils/request'

export const getStatisticsAPI = () => {
  return request.get('/admin/statistics/overview')
}

export const getCompetitionRankingAPI = () => {
  return request.get('/admin/statistics/competitionRanking')
}

export const getUserActivityAPI = () => {
  return request.get('/admin/statistics/userActivity')
}

export const getReportStatisticsAPI = () => {
  return request.get('/admin/statistics/reportStatistics')
}

export const getSummaryAPI = () => {
  return request.get('/admin/statistics/summary')
}
