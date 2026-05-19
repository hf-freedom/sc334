<template>
  <div>
    <h2>所有商品列表</h2>
    <el-table :data="items" border stripe style="width: 100%; margin-top: 20px;">
      <el-table-column prop="itemName" label="商品名称" width="150"></el-table-column>
      <el-table-column prop="userName" label="寄售人" width="100"></el-table-column>
      <el-table-column prop="category" label="分类" width="100"></el-table-column>
      <el-table-column prop="expectedPrice" label="期望价格" width="100">
        <template slot-scope="scope">¥{{ scope.row.expectedPrice }}</template>
      </el-table-column>
      <el-table-column prop="finalPrice" label="上架价格" width="100">
        <template slot-scope="scope">¥{{ scope.row.finalPrice || '-' }}</template>
      </el-table-column>
      <el-table-column prop="commission" label="佣金" width="100">
        <template slot-scope="scope">¥{{ scope.row.commission || '-' }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="140">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submitTime" label="提交时间" width="180"></el-table-column>
      <el-table-column prop="expireTime" label="到期时间" width="180">
        <template slot-scope="scope">{{ scope.row.expireTime || '-' }}</template>
      </el-table-column>
      <el-table-column prop="buyerName" label="买家" width="100">
        <template slot-scope="scope">{{ scope.row.buyerName || '-' }}</template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { getItems } from '../api'

export default {
  data() {
    return {
      items: []
    }
  },
  mounted() {
    this.loadItems()
    this.timer = setInterval(this.loadItems, 5000)
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
  methods: {
    async loadItems() {
      const res = await getItems()
      if (res.data.code === 200) {
        this.items = res.data.data
      }
    },
    getStatusType(status) {
      const map = {
        'PENDING_IDENTIFICATION': 'warning',
        'IDENTIFICATION_REJECTED': 'danger',
        'ON_SALE': 'success',
        'PENDING_PAYMENT': 'warning',
        'SOLD': 'success',
        'EXPIRED': 'info',
        'RETURNED': 'info'
      }
      return map[status] || ''
    },
    getStatusText(status) {
      const map = {
        'PENDING_IDENTIFICATION': '待鉴定',
        'IDENTIFICATION_REJECTED': '鉴定不通过',
        'ON_SALE': '上架中',
        'PENDING_PAYMENT': '待支付',
        'SOLD': '已售出',
        'EXPIRED': '已到期',
        'RETURNED': '已退回'
      }
      return map[status] || status
    }
  }
}
</script>
