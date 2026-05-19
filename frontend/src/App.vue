<template>
  <div id="app">
    <el-container>
      <el-header style="background: #409EFF; color: white; font-size: 20px; line-height: 60px; padding: 0 20px;">
        二手商品寄售系统
      </el-header>
      <el-container>
        <el-aside width="200px" style="background: #f5f5f5; min-height: calc(100vh - 60px);">
          <el-menu
            :default-active="$route.path"
            class="el-menu-vertical-demo"
            router>
            <el-menu-item index="/items">
              <i class="el-icon-goods"></i>
              <span>所有商品</span>
            </el-menu-item>
            <el-menu-item index="/submit">
              <i class="el-icon-edit"></i>
              <span>提交寄售</span>
            </el-menu-item>
            <el-menu-item index="/identify">
              <i class="el-icon-check"></i>
              <span>商品鉴定</span>
            </el-menu-item>
            <el-menu-item index="/onsale">
              <i class="el-icon-shopping-cart-2"></i>
              <span>购买商品</span>
            </el-menu-item>
            <el-menu-item index="/settlement">
              <i class="el-icon-money"></i>
              <span>结算记录</span>
            </el-menu-item>
            <el-menu-item index="/return">
              <i class="el-icon-refresh"></i>
              <span>退回任务</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
body { margin: 0; }
.el-menu-vertical-demo { border-right: none; }
</style>
