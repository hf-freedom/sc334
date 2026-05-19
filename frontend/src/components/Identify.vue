<template>
  <div>
    <h2>商品鉴定</h2>
    <el-table :data="items" border stripe style="width: 100%; margin-top: 20px;">
      <el-table-column prop="itemName" label="商品名称" width="150"></el-table-column>
      <el-table-column prop="userName" label="寄售人" width="100"></el-table-column>
      <el-table-column prop="category" label="分类" width="100"></el-table-column>
      <el-table-column prop="description" label="描述" show-overflow-tooltip></el-table-column>
      <el-table-column prop="expectedPrice" label="期望价格" width="100">
        <template slot-scope="scope">¥{{ scope.row.expectedPrice }}</template>
      </el-table-column>
      <el-table-column prop="submitTime" label="提交时间" width="180"></el-table-column>
      <el-table-column label="操作" width="300">
        <template slot-scope="scope">
          <el-button size="small" type="success" @click="openIdentify(scope.row, true)">鉴定通过</el-button>
          <el-button size="small" type="danger" @click="openIdentify(scope.row, false)">鉴定不通过</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :visible.sync="dialogVisible" :title="isPass ? '鉴定通过 - 设置上架信息' : '鉴定不通过'" width="550px">
      <el-form :model="identifyForm" label-width="120px" v-if="isPass">
        <el-form-item label="商品名称">
          <span>{{ currentItem ? currentItem.itemName : '' }}</span>
        </el-form-item>
        <el-form-item label="期望价格">
          <span>¥{{ currentItem ? currentItem.expectedPrice : '' }}</span>
        </el-form-item>
        <el-form-item label="上架价格" required>
          <el-input-number v-model="identifyForm.finalPrice" :min="0" :precision="2" @change="calculateCommission"></el-input-number>
          <div style="color: #999; font-size: 12px; margin-top: 5px;">建议根据商品实际情况设置</div>
        </el-form-item>
        <el-form-item label="平台佣金">
          <span style="color: #f56c6c; font-weight: bold;">¥{{ commissionAmount.toFixed(2) }}</span>
          <span style="color: #999; font-size: 12px; margin-left: 10px;">(费率 5%)</span>
        </el-form-item>
        <el-form-item label="卖家实得">
          <span style="color: #67c23a; font-weight: bold;">¥{{ sellerAmount.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="寄售周期(天)" required>
          <el-input-number v-model="identifyForm.consignmentDays" :min="1" :max="365"></el-input-number>
          <div style="color: #999; font-size: 12px; margin-top: 5px;">默认30天，到期未售出自动下架</div>
        </el-form-item>
        <el-form-item label="鉴定备注">
          <el-input type="textarea" v-model="identifyForm.remark" placeholder="请输入鉴定意见（选填）"></el-input>
        </el-form-item>
      </el-form>
      <el-form :model="identifyForm" label-width="120px" v-else>
        <el-form-item label="商品名称">
          <span>{{ currentItem ? currentItem.itemName : '' }}</span>
        </el-form-item>
        <el-form-item label="鉴定备注" required>
          <el-input type="textarea" v-model="identifyForm.remark" placeholder="请输入不通过原因"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button :type="isPass ? 'success' : 'danger'" @click="doIdentify">
          {{ isPass ? '确认通过并上架' : '确认不通过' }}
        </el-button>
      </span>
    </el-dialog>

    <el-dialog :visible.sync="resultDialogVisible" title="鉴定完成" width="450px">
      <div v-if="resultData" style="text-align: center; padding: 20px 0;">
        <i class="el-icon-circle-check" style="font-size: 60px; color: #67c23a;"></i>
        <h3 style="margin: 20px 0;">商品已成功上架</h3>
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="上架价格">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ resultData.finalPrice }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="平台佣金 (5%)">
            ¥{{ resultData.commission }}
          </el-descriptions-item>
          <el-descriptions-item label="卖家实得">
            <span style="color: #67c23a; font-weight: bold;">¥{{ sellerResultAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="寄售周期">
            {{ resultData.consignmentDays }} 天
          </el-descriptions-item>
          <el-descriptions-item label="到期时间">
            {{ resultData.expireTime }}
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
import { getItems, identifyItem } from '../api'

export default {
  data() {
    return {
      items: [],
      dialogVisible: false,
      resultDialogVisible: false,
      isPass: true,
      currentItem: null,
      resultData: null,
      identifyForm: {
        itemId: '',
        pass: true,
        finalPrice: 0,
        consignmentDays: 30,
        remark: ''
      }
    }
  },
  computed: {
    commissionAmount() {
      return this.identifyForm.finalPrice * 0.05
    },
    sellerAmount() {
      return this.identifyForm.finalPrice - this.commissionAmount
    },
    sellerResultAmount() {
      if (!this.resultData) return '0.00'
      return (this.resultData.finalPrice - this.resultData.commission).toFixed(2)
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
      const res = await getItems({ status: 'PENDING_IDENTIFICATION' })
      if (res.data.code === 200) {
        this.items = res.data.data
      }
    },
    calculateCommission() {
    },
    openIdentify(item, pass) {
      this.currentItem = item
      this.isPass = pass
      this.identifyForm = {
        itemId: item.id,
        pass: pass,
        finalPrice: item.expectedPrice,
        consignmentDays: 30,
        remark: ''
      }
      this.dialogVisible = true
    },
    async doIdentify() {
      if (this.isPass && !this.identifyForm.finalPrice) {
        this.$message.error('请输入上架价格')
        return
      }
      if (!this.isPass && !this.identifyForm.remark) {
        this.$message.error('请输入鉴定不通过原因')
        return
      }
      const res = await identifyItem(this.identifyForm)
      if (res.data.code === 200) {
        this.dialogVisible = false
        if (this.isPass) {
          this.resultData = res.data.data
          this.resultDialogVisible = true
        } else {
          this.$message.success('鉴定不通过已记录')
        }
        this.loadItems()
      } else {
        this.$message.error(res.data.message)
      }
    }
  }
}
</script>
