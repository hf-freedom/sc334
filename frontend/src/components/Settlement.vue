<template>
  <div>
    <h2>结算记录</h2>

    <el-alert
      title="商品售出后系统自动按5%佣金比例计算平台佣金，并生成用户结算记录"
      type="success"
      show-icon
      style="margin-top: 20px;">
    </el-alert>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <p style="color: #909399; font-size: 14px; margin: 0;">销售总额</p>
            <p style="color: #409EFF; font-size: 28px; font-weight: bold; margin: 10px 0;">
              ¥{{ totalSales.toFixed(2) }}
            </p>
            <p style="color: #c0c4cc; font-size: 12px; margin: 0;">共 {{ records.length }} 笔交易</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <p style="color: #909399; font-size: 14px; margin: 0;">平台佣金</p>
            <p style="color: #f56c6c; font-size: 28px; font-weight: bold; margin: 10px 0;">
              ¥{{ totalCommission.toFixed(2) }}
            </p>
            <p style="color: #c0c4cc; font-size: 12px; margin: 0;">佣金比例 5%</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <p style="color: #909399; font-size: 14px; margin: 0;">卖家实得</p>
            <p style="color: #67c23a; font-size: 28px; font-weight: bold; margin: 10px 0;">
              ¥{{ totalSettle.toFixed(2) }}
            </p>
            <p style="color: #c0c4cc; font-size: 12px; margin: 0;">已结算给卖家</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <p style="color: #909399; font-size: 14px; margin: 0;">平均成交价</p>
            <p style="color: #e6a23c; font-size: 28px; font-weight: bold; margin: 10px 0;">
              ¥{{ avgPrice.toFixed(2) }}
            </p>
            <p style="color: #c0c4cc; font-size: 12px; margin: 0;">已售出商品均价</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;">
      <div slot="header" style="display: flex; justify-content: space-between; align-items: center;">
        <span style="font-weight: bold; font-size: 16px;">结算记录明细</span>
        <el-tag type="info">自动生成 · 实时更新</el-tag>
      </div>

      <div v-if="records.length === 0" style="text-align: center; padding: 60px 0;">
        <i class="el-icon-document" style="font-size: 60px; color: #dcdfe6;"></i>
        <p style="color: #909399; margin-top: 20px;">暂无结算记录</p>
        <p style="color: #c0c4cc; font-size: 12px;">商品售出后将自动生成结算记录</p>
      </div>

      <el-table :data="records" border stripe v-else style="width: 100%;">
        <el-table-column prop="itemName" label="商品名称" width="150">
          <template slot-scope="scope">
            <span style="cursor: pointer; color: #409EFF;" @click="viewDetail(scope.row)">
              {{ scope.row.itemName }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="卖家" width="100"></el-table-column>
        <el-table-column prop="salePrice" label="售价" width="100">
          <template slot-scope="scope">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ scope.row.salePrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="commissionRate" label="佣金比例" width="100">
          <template slot-scope="scope">
            <el-tag size="small">{{ (scope.row.commissionRate * 100).toFixed(0) }}%</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="commissionAmount" label="平台佣金" width="110">
          <template slot-scope="scope">
            <span style="color: #f56c6c;">¥{{ scope.row.commissionAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="settleAmount" label="卖家实得" width="120">
          <template slot-scope="scope">
            <span style="color: #67c23a; font-weight: bold;">¥{{ scope.row.settleAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="settleTime" label="结算时间" width="180"></el-table-column>
        <el-table-column label="状态" width="80">
          <template slot-scope="scope">
            <el-tag type="success" size="small">已结算</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="viewDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :visible.sync="detailDialogVisible" title="结算详情" width="500px">
      <div v-if="currentRecord">
        <div style="text-align: center; padding: 20px 0; background: #f0f9eb; border-radius: 4px; margin-bottom: 20px;">
          <i class="el-icon-circle-check" style="font-size: 48px; color: #67c23a;"></i>
          <h3 style="margin: 10px 0; color: #67c23a;">结算完成</h3>
          <p style="color: #909399; font-size: 14px;">{{ currentRecord.settleTime }}</p>
        </div>

        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="结算单号">
            {{ currentRecord.id }}
          </el-descriptions-item>
          <el-descriptions-item label="商品名称">
            {{ currentRecord.itemName }}
          </el-descriptions-item>
          <el-descriptions-item label="商品ID">
            {{ currentRecord.itemId }}
          </el-descriptions-item>
          <el-descriptions-item label="卖家信息">
            {{ currentRecord.userName }} (ID: {{ currentRecord.userId }})
          </el-descriptions-item>
          <el-descriptions-item label="商品售价">
            <span style="color: #f56c6c; font-weight: bold; font-size: 16px;">¥{{ currentRecord.salePrice }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="佣金比例">
            <el-tag size="small">{{ (currentRecord.commissionRate * 100).toFixed(0) }}%</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="平台佣金">
            <span style="color: #f56c6c;">¥{{ currentRecord.commissionAmount }}</span>
            <span style="color: #909399; font-size: 12px; margin-left: 10px;">
              = ¥{{ currentRecord.salePrice }} × {{ (currentRecord.commissionRate * 100).toFixed(0) }}%
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="卖家实得金额">
            <span style="color: #67c23a; font-weight: bold; font-size: 18px;">¥{{ currentRecord.settleAmount }}</span>
            <div style="color: #909399; font-size: 12px; margin-top: 5px;">
              = 售价 ¥{{ currentRecord.salePrice }} - 佣金 ¥{{ currentRecord.commissionAmount }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="结算时间">
            {{ currentRecord.settleTime }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <span slot="footer">
        <el-button type="primary" @click="detailDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getSettlements } from '../api'

export default {
  data() {
    return {
      records: [],
      detailDialogVisible: false,
      currentRecord: null
    }
  },
  computed: {
    totalSales() {
      return this.records.reduce((sum, r) => sum + Number(r.salePrice), 0)
    },
    totalCommission() {
      return this.records.reduce((sum, r) => sum + Number(r.commissionAmount), 0)
    },
    totalSettle() {
      return this.records.reduce((sum, r) => sum + Number(r.settleAmount), 0)
    },
    avgPrice() {
      if (this.records.length === 0) return 0
      return this.totalSales / this.records.length
    }
  },
  mounted() {
    this.loadRecords()
    this.timer = setInterval(this.loadRecords, 5000)
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
  methods: {
    async loadRecords() {
      const res = await getSettlements()
      if (res.data.code === 200) {
        this.records = res.data.data.sort((a, b) => new Date(b.settleTime) - new Date(a.settleTime))
      }
    },
    viewDetail(record) {
      this.currentRecord = record
      this.detailDialogVisible = true
    }
  }
}
</script>
