import Vue from 'vue'
import VueRouter from 'vue-router'
import ItemList from '../components/ItemList.vue'
import SubmitItem from '../components/SubmitItem.vue'
import Identify from '../components/Identify.vue'
import OnSale from '../components/OnSale.vue'
import Settlement from '../components/Settlement.vue'
import ReturnTasks from '../components/ReturnTasks.vue'

Vue.use(VueRouter)

const routes = [
  { path: '/', redirect: '/items' },
  { path: '/items', component: ItemList, name: '所有商品' },
  { path: '/submit', component: SubmitItem, name: '提交寄售' },
  { path: '/identify', component: Identify, name: '商品鉴定' },
  { path: '/onsale', component: OnSale, name: '购买商品' },
  { path: '/settlement', component: Settlement, name: '结算记录' },
  { path: '/return', component: ReturnTasks, name: '退回任务' }
]

const router = new VueRouter({
  mode: 'history',
  routes
})

export default router
