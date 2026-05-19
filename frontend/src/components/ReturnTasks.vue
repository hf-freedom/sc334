<template>
  <div>
    <h2>退回任务管理</h2>
    <el-alert
      title="寄售到期未售出的商品将自动下架并生成退回任务，请及时处理并将商品退回给用户"
      type="info"
      show-icon
      style="margin-top: 20px;">
    </el-alert>

    <el-tabs v-model="activeTab" style="margin-top: 20px;">
      <el-tab-pane label="已到期商品" name="expired">
        <div v-if="expiredItems.length === 0" style="text-align: center; padding: 60px 0;">
          <i class="el-icon-goods" style="font-size: 60px; color: #dcdfe6;"></i>
          <p style="color: #909399; margin-top: 20px;">暂无到期商品</p>
          <p style="color: #c0c4cc; font-size: 12px;">寄售到期未售出的商品将在此显示</p>
        </div>
        <el-table :data="expiredItems" border stripe v-else style="width: 100%;">
          <el-table-column prop="itemName" label="商品名称" width="150"></el-table-column>
          <el-table-column prop="userName" label="寄售人" width="100"></el-table-column>
          <el-table-column prop="category" label="分类" width="100"></el-table-column>
          <el-table-column prop="finalPrice" label="上架价格" width="100">
            <template slot-scope="scope">¥{{ scope.row.finalPrice }}</template>
          </el-table-column>
          <el-table-column prop="onSaleTime" label="上架时间" width="180"></el-table-column>
          <el-table-column prop="expireTime" label="到期时间" width="180"></el-table-column>
          <el-table-column label="寄售周期" width="100">
            <template slot-scope="scope">{{ scope.row.consignmentDays }}天</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120">
            <template slot-scope="scope">
              <el-tag type="info" size="small">已到期</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="退回任务" width="120">
            <template slot-scope="scope">
              <el-tag v-if="getReturnTaskStatus(scope.row.id)" :type="getReturnTaskStatus(scope.row.id) === 'PENDING' ? 'warning' : 'success'" size="small">
                {{ getReturnTaskStatus(scope.row.id) === 'PENDING' ? '待处理' : '已完成' }}
              </el-tag>
              <el-tag v-else type="info" size="small">生成中</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="退回任务" name="tasks">
        <div v-if="tasks.length === 0" style="text-align: center; padding: 60px 0;">
          <i class="el-icon-document" style="font-size: 60px; color: #dcdfe6;"></i>
          <p style="color: #909399; margin-top: 20px;">暂无退回任务</p>
          <p style="color: #c0c4cc; font-size: 12px;">商品到期后将自动生成退回任务</p>
        </div>
        <el-table :data="tasks" border stripe v-else style="width: 100%;">
          <el-table-column prop="itemName" label="商品名称" width="150"></el-table-column>
          <el-table-column prop="userName" label="用户" width="100"></el-table-column>
          <el-table-column label="商品价格" width="120">
            <template slot-scope="scope">
              ¥{{ getItemPrice(scope.row.itemId) }}
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
          <el-table-column prop="status" label="任务状态" width="120">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 'PENDING' ? 'warning' : 'success'" size="small">
                {{ scope.row.status === 'PENDING' ? '待处理' : '已完成' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template slot-scope="scope">
              <el-button
                v-if="scope.row.status === 'PENDING'"
                type="primary"
                size="small"
                @click="completeTask(scope.row)">
                确认已退回
              </el-button>
              <el-button
                v-if="scope.row.status === 'PENDING'"
                size="small"
                @click="viewDetail(scope.row)">
                查看详情
              </el-button>
              <el-tag
                v-else
                type="success"
                size="small">
                已完成
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog :visible.sync="detailDialogVisible" title="退回任务详情" width="500px">
      <div v-if="currentTask">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="任务ID">
            {{ currentTask.id }}
          </el-descriptions-item>
          <el-descriptions-item label="商品名称">
            {{ currentTask.itemName }}
          </el-descriptions-item>
          <el-descriptions-item label="用户">
            {{ currentTask.userName }} (ID: {{ currentTask.userId }})
          </el-descriptions-item>
          <el-descriptions-item label="商品信息" v-if="currentItem">
            <p>分类: {{ currentItem.category }}</p>
            <p>描述: {{ currentItem.description }}</p>
            <p>上架价格: <span style="color: #f56c6c; font-weight: bold;">¥{{ currentItem.finalPrice }}</span></p>
            <p>上架时间: {{ currentItem.onSaleTime }}</p>
            <p>到期时间: {{ currentItem.expireTime }}</p>
            <p>寄售周期: {{ currentItem.consignmentDays }}天</p>
          </el-descriptions-item>
          <el-descriptions-item label="任务创建时间">
            {{ currentTask.createTime }}
          </el-descriptions-item>
          <el-descriptions-item label="任务状态">
            <el-tag :type="currentTask.status === 'PENDING' ? 'warning' : 'success'">
              {{ currentTask.status === 'PENDING' ? '待处理' : '已完成' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <span slot="footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="currentTask && currentTask.status === 'PENDING'"
          type="primary"
          @click="completeTask(currentTask)">
          确认已退回
        </el-button>
      </span>
    </el-dialog>

    <el-dialog :visible.sync="resultDialogVisible" title="退回成功" width="450px">
      <div style="text-align: center; padding: 20px 0;">
        <i class="el-icon-circle-check" style="font-size: 60px; color: #67c23a;"></i>
        <h3 style="margin: 20px 0;">商品已退回给用户</h3>
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="商品名称">
            {{ resultData ? resultData.itemName : '' }}
          </el-descriptions-item>
          <el-descriptions-item label="用户">
            {{ resultData ? resultData.userName : '' }}
          </el-descriptions-item>
          <el-descriptions-item label="处理状态">
            <el-tag type="success">退回完成</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="处理时间">
            {{ formatNow() }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <span slot="footer">
        <el-button type="primary" @click="resultDialogVisible = false">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getReturnTasks, getItems, completeReturnTask } from '../api'

export default {
  data() {
    return {
      activeTab: 'expired',
      expiredItems: [],
      tasks: [],
      detailDialogVisible: false,
      resultDialogVisible: false,
      currentTask: null,
      currentItem: null,
      resultData: null
    }
  },
  mounted() {
    this.loadData()
    this.timer = setInterval(this.loadData, 5000)
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
  methods: {
    async loadData() {
      const [expiredRes, tasksRes] = await Promise.all([
        getItems({ status: 'EXPIRED' }),
        getReturnTasks()
      ])
      if (expiredRes.data.code === 200) {
        this.expiredItems = expiredRes.data.data
      }
      if (tasksRes.data.code === 200) {
        this.tasks = tasksRes.data.data
      }
    },
    getReturnTaskStatus(itemId) {
      const task = this.tasks.find(t => t.itemId === itemId)
      return task ? task.status : null
    },
    getItemPrice(itemId) {
      const item = this.expiredItems.find(i => i.id === itemId)
      return item ? item.finalPrice : '-'
    },
    formatNow() {
      const now = new Date()
      return now.toLocaleString('zh-CN', { hour12: false })
    },
    async viewDetail(task) {
      this.currentTask = task
      const itemRes = await getItems()
      if (itemRes.data.code === 200) {
        this.currentItem = itemRes.data.data.find(i => i.id === task.itemId)
      }
      this.detailDialogVisible = true
    },
    async completeTask(task) {
      this.$confirm('确认已将商品退回给用户吗？此操作不可撤销', '确认退回', {
        confirmButtonText: '确认退回',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const res = await completeReturnTask(task.id)
        if (res.data.code === 200) {
          this.detailDialogVisible = false
          this.resultData = task
          this.resultDialogVisible = true
          this.loadData()
        } else {
          this.$message.error(res.data.message)
        }
      }).catch(() => {})
    }
  }
}
</script>
