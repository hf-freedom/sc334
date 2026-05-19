<template>
  <div>
    <h2>购买商品</h2>

    <el-tabs v-model="activeTab" style="margin-top: 20px;">
      <el-tab-pane label="可购买商品" name="onsale">
        <div v-if="items.length === 0" style="text-align: center; padding: 60px 0;">
          <i class="el-icon-goods" style="font-size: 60px; color: #dcdfe6;"></i>
          <p style="color: #909399; margin-top: 20px;">暂无上架商品</p>
        </div>
        <el-row :gutter="20" v-else>
          <el-col :span="8" v-for="item in items" :key="item.id" style="margin-bottom: 20px;">
            <el-card shadow="hover">
              <div style="display: flex; justify-content: space-between; align-items: start;">
                <h3 style="margin: 0 0 10px 0;">{{ item.itemName }}</h3>
                <el-tag type="success" size="small">可购买</el-tag>
              </div>
              <p style="color: #666; font-size: 14px;">分类: {{ item.category }}</p>
              <p style="color: #999; font-size: 12px; height: 40px; overflow: hidden; margin: 5px 0;">{{ item.description }}</p>
              <p style="color: #f56c6c; font-size: 24px; font-weight: bold; margin: 10px 0;">
                ¥{{ item.finalPrice }}
              </p>
              <p style="color: #999; font-size: 12px; margin: 5px 0;">
                寄售人: {{ item.userName }} | 寄售周期: {{ item.consignmentDays }}天
              </p>
              <p style="color: #999; font-size: 12px;">
                到期时间: {{ formatDate(item.expireTime) }}
              </p>
              <div style="margin-top: 15px;">
                <el-button type="primary" @click="buy(item)" size="small" style="width: 100%;">立即下单</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="待支付订单" name="pending">
        <div v-if="pendingItems.length === 0" style="text-align: center; padding: 60px 0;">
          <i class="el-icon-time" style="font-size: 60px; color: #dcdfe6;"></i>
          <p style="color: #909399; margin-top: 20px;">暂无待支付订单</p>
        </div>
        <el-table :data="pendingItems" border stripe v-else style="width: 100%;">
          <el-table-column prop="itemName" label="商品名称" width="150"></el-table-column>
          <el-table-column prop="userName" label="卖家" width="100"></el-table-column>
          <el-table-column prop="finalPrice" label="价格" width="120">
            <template slot-scope="scope">
              <span style="color: #f56c6c; font-weight: bold;">¥{{ scope.row.finalPrice }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="orderTime" label="下单时间" width="180"></el-table-column>
          <el-table-column label="支付状态" width="120">
            <template slot-scope="scope">
              <el-tag type="warning" size="small">
                <i class="el-icon-time"></i> 待支付
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="剩余支付时间" width="150">
            <template slot-scope="scope">
              <span :style="{ color: getRemainingTime(scope.row.orderTime) <= 5 ? '#f56c6c' : '#e6a23c' }">
                {{ formatRemainingTime(scope.row.orderTime) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template slot-scope="scope">
              <el-button type="primary" size="small" @click="continuePay(scope.row)">立即支付</el-button>
              <el-button size="small" @click="cancelOrderDirect(scope.row)">取消订单</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog :visible.sync="orderDialogVisible" title="确认下单" width="450px">
      <el-form :model="orderForm" label-width="100px">
        <el-alert title="下单后商品将锁定30分钟，请在规定时间内完成支付，超时订单将自动取消" type="warning" show-icon style="margin-bottom: 20px;"></el-alert>
        <el-form-item label="商品名称">
          <span>{{ currentItem ? currentItem.itemName : '' }}</span>
        </el-form-item>
        <el-form-item label="商品价格">
          <span style="color: #f56c6c; font-size: 18px; font-weight: bold;">¥{{ currentItem ? currentItem.finalPrice : '' }}</span>
        </el-form-item>
        <el-form-item label="商品描述">
          <span>{{ currentItem ? currentItem.description : '' }}</span>
        </el-form-item>
        <el-form-item label="买家ID">
          <el-input v-model="orderForm.buyerId"></el-input>
        </el-form-item>
        <el-form-item label="买家姓名">
          <el-input v-model="orderForm.buyerName"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="orderDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doOrder">确认下单</el-button>
      </span>
    </el-dialog>

    <el-dialog :visible.sync="payDialogVisible" title="支付订单" width="450px" :close-on-click-modal="false">
      <div style="text-align: center; padding: 20px 0;">
        <i class="el-icon-warning" style="font-size: 48px; color: #e6a23c;"></i>
        <h3 style="margin: 15px 0;">订单支付</h3>
        <el-alert title="请在30分钟内完成支付，超时订单将自动取消并释放商品库存" type="warning" show-icon style="margin-bottom: 20px;"></el-alert>
        <div style="background: #f5f7fa; padding: 20px; border-radius: 4px; margin: 20px 0;">
          <p style="margin: 5px 0;">商品: {{ currentItem ? currentItem.itemName : '' }}</p>
          <p style="margin: 5px 0;">卖家: {{ currentItem ? currentItem.userName : '' }}</p>
          <p style="margin: 15px 0 5px 0; color: #f56c6c; font-size: 28px; font-weight: bold;">
            ¥{{ currentItem ? currentItem.finalPrice : '' }}
          </p>
        </div>
        <div style="color: #909399; font-size: 14px;">
          <p>下单时间: {{ currentItem ? currentItem.orderTime : '' }}</p>
          <p v-if="currentItem">剩余支付时间: <span :style="{ color: getRemainingTime(currentItem.orderTime) <= 5 ? '#f56c6c' : '#e6a23c' }">{{ formatRemainingTime(currentItem.orderTime) }}</span></p>
        </div>
      </div>
      <span slot="footer">
        <el-button @click="cancelOrder">取消订单</el-button>
        <el-button type="primary" @click="doPay">立即支付</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getItems, placeOrder, payOrder, releaseItem } from '../api'

export default {
  data() {
    return {
      activeTab: 'onsale',
      items: [],
      pendingItems: [],
      orderDialogVisible: false,
      payDialogVisible: false,
      currentItem: null,
      orderForm: {
        itemId: '',
        buyerId: 'buyer001',
        buyerName: '李四'
      }
    }
  },
  mounted() {
    this.loadItems()
    this.loadPendingItems()
    this.timer = setInterval(() => {
      this.loadItems()
      this.loadPendingItems()
    }, 5000)
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
  methods: {
    async loadItems() {
      const res = await getItems({ status: 'ON_SALE' })
      if (res.data.code === 200) {
        this.items = res.data.data
      }
    },
    async loadPendingItems() {
      const res = await getItems({ status: 'PENDING_PAYMENT' })
      if (res.data.code === 200) {
        this.pendingItems = res.data.data
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return '-'
      return dateStr.replace('T', ' ').substring(0, 19)
    },
    getRemainingTime(orderTimeStr) {
      if (!orderTimeStr) return 0
      const orderTime = new Date(orderTimeStr.replace('T', ' '))
      const now = new Date()
      const diffMs = now - orderTime
      const diffMins = diffMs / 60000
      return Math.max(0, 30 - diffMins)
    },
    formatRemainingTime(orderTimeStr) {
      const remaining = this.getRemainingTime(orderTimeStr)
      if (remaining <= 0) return '已超时'
      const mins = Math.floor(remaining)
      const secs = Math.floor((remaining - mins) * 60)
      return `${mins}分${secs}秒`
    },
    buy(item) {
      this.currentItem = item
      this.orderForm.itemId = item.id
      this.orderDialogVisible = true
    },
    async doOrder() {
      const res = await placeOrder(this.orderForm)
      if (res.data.code === 200) {
        this.$message.success('下单成功，商品已锁定，请尽快支付')
        this.orderDialogVisible = false
        this.currentItem = res.data.data
        this.payDialogVisible = true
        this.loadItems()
        this.loadPendingItems()
      } else {
        this.$message.error(res.data.message)
      }
    },
    continuePay(item) {
      this.currentItem = item
      this.payDialogVisible = true
    },
    async doPay() {
      const res = await payOrder(this.currentItem.id)
      if (res.data.code === 200) {
        this.$message.success('支付成功！商品已售出')
        this.payDialogVisible = false
        this.loadItems()
        this.loadPendingItems()
      } else {
        this.$message.error(res.data.message)
      }
    },
    async cancelOrder() {
      const res = await releaseItem(this.currentItem.id)
      if (res.data.code === 200) {
        this.$message.success('订单已取消，商品已释放')
        this.payDialogVisible = false
        this.loadItems()
        this.loadPendingItems()
      } else {
        this.$message.error(res.data.message)
      }
    },
    async cancelOrderDirect(item) {
      this.$confirm('确定要取消该订单吗？取消后商品将释放回库存', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const res = await releaseItem(item.id)
        if (res.data.code === 200) {
          this.$message.success('订单已取消')
          this.loadItems()
          this.loadPendingItems()
        } else {
          this.$message.error(res.data.message)
        }
      }).catch(() => {})
    }
  }
}
</script>
