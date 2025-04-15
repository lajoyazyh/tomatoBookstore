<script setup>
import {ElMessage} from "element-plus";
import {router} from '../router'

if (!sessionStorage.getItem('token')) {
  ElMessage.error("未登录！");
  router.push('/login');
}

</script>

<template>
  <h1>番茄书城</h1>
</template>

<style scoped>

</style>
