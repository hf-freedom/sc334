<template>
  <div>
    <h2>提交寄售商品</h2>
    <el-form :model="form" label-width="100px" style="max-width: 500px; margin-top: 20px;">
      <el-form-item label="用户ID">
        <el-input v-model="form.userId"></el-input>
      </el-form-item>
      <el-form-item label="用户名">
        <el-input v-model="form.userName"></el-input>
      </el-form-item>
      <el-form-item label="商品名称">
        <el-input v-model="form.itemName"></el-input>
      </el-form-item>
      <el-form-item label="商品描述">
        <el-input type="textarea" v-model="form.description"></el-input>
      </el-form-item>
      <el-form-item label="商品分类">
        <el-select v-model="form.category" placeholder="请选择分类">
          <el-option label="数码产品" value="数码产品"></el-option>
          <el-option label="服装鞋帽" value="服装鞋帽"></el-option>
          <el-option label="家居用品" value="家居用品"></el-option>
          <el-option label="图书音像" value="图书音像"></el-option>
          <el-option label="运动户外" value="运动户外"></el-option>
          <el-option label="其他" value="其他"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="期望价格">
        <el-input-number v-model="form.expectedPrice" :min="0" :precision="2"></el-input-number>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">提交寄售</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { submitItem } from '../api'

export default {
  data() {
    return {
      form: {
        userId: 'user001',
        userName: '张三',
        itemName: '',
        description: '',
        category: '',
        expectedPrice: 0
      }
    }
  },
  methods: {
    async submit() {
      if (!this.form.itemName) {
        this.$message.error('请输入商品名称')
        return
      }
      if (!this.form.category) {
        this.$message.error('请选择商品分类')
        return
      }
      const res = await submitItem(this.form)
      if (res.data.code === 200) {
        this.$message.success('提交成功，商品已进入鉴定队列')
        this.form.itemName = ''
        this.form.description = ''
        this.form.category = ''
        this.form.expectedPrice = 0
      } else {
        this.$message.error(res.data.message)
      }
    }
  }
}
</script>
