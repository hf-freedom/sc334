import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

export const submitItem = (data) => request.post('/items', data)
export const identifyItem = (data) => request.post('/items/identify', data)
export const placeOrder = (data) => request.post('/items/order', data)
export const payOrder = (id) => request.post(`/items/${id}/pay`)
export const releaseItem = (id) => request.post(`/items/${id}/release`)
export const getItems = (params) => request.get('/items', { params })
export const getItemById = (id) => request.get(`/items/${id}`)
export const getSettlements = (params) => request.get('/settlements', { params })
export const getReturnTasks = () => request.get('/return-tasks')
export const completeReturnTask = (id) => request.post(`/return-tasks/${id}/complete`)

export default request
