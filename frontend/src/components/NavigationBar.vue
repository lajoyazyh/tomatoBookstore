<script setup lang="ts">
import { computed } from 'vue';
import { ElMenu, ElMenuItem, ElMessageBox } from "element-plus";
import {router} from '../router'

const isLoggedIn = computed(() => !!sessionStorage.getItem('token'))
const isStaff = computed(() => sessionStorage.getItem('role') === 'STAFF');

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm("确定要退出登录吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    sessionStorage.removeItem("token");
    router.push("/index");
    router.go(0);
  });
};
</script>

<template>
  <div class="nav-bar-container">
    <el-menu
        class="nav-bar"
        mode="horizontal"
        :router="true"
    >
      <div class="leaders">
        <el-menu-item index="/">番茄书城</el-menu-item>

        <el-menu-item v-if="!isStaff" index="/cart">购物车</el-menu-item>

        <el-menu-item index="/warehouse">
          <template #title>
            <span v-if="!isStaff">商品列表</span>
            <span v-else>商品管理</span>
          </template>
        </el-menu-item>

        <el-menu-item v-if="!isStaff" index="/collection">我的收藏</el-menu-item>
        <el-menu-item v-if="isStaff" index="/advertisement">广告管理</el-menu-item>
        <el-menu-item v-if="isStaff" index="/manageCoupon">优惠券管理</el-menu-item>

        <el-menu-item index="/dashboard">个人信息</el-menu-item>
      </div>

      <div class="functions">
        <!-- 登录/退出按钮 -->
        <el-menu-item>
          <el-button v-if="!isLoggedIn" type="primary" round @click="router.push('/login')">
            登录
          </el-button>
          <el-button v-else type="danger" round @click="handleLogout">
            退出
          </el-button>
        </el-menu-item>
        <!-- 登录/退出按钮 -->
      </div>

    </el-menu>
  </div>
</template>

<style scoped>
.nav-bar-container {
  width: 100%;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.nav-bar {
  display: flex;
  justify-content: space-between;
  width: 100%;
  height: 60px;
  border: none;
}

.leaders {
  display: flex;
  justify-content: flex-start;
}

.functions {
  display: flex;
  justify-content: flex-end;
}

.el-menu-item {
  min-width: 100px; /* 固定宽度 */
  height: 100%;
  font-size: 16px;
  font-weight: 500;
}

/* 禁用 functions 中的 el-menu-item 的交互效果 */
.functions .el-menu-item {
  background-color: transparent !important;
  color: inherit;
  cursor: default;
}

.el-button {
  margin: 0;
  height: 36px;
  padding: 0 15px;
}

.el-menu {
  background-color: #fff;
  border-bottom: none !important;
}

.el-menu-item:hover:not(.is-disabled) {
  background-color: #f5f7fa;
}
</style>