<script setup lang="ts">
import { onMounted } from 'vue';
import {ElMessage} from "element-plus";
import {router} from '../router'

onMounted(() => {
  if (!sessionStorage.getItem('alreadyRefreshed')) {
    sessionStorage.setItem('alreadyRefreshed', 'true');
    router.go(0);
  } else {
    sessionStorage.removeItem('alreadyRefreshed');
  }
});

if (!sessionStorage.getItem('token')) {
  console.log(!sessionStorage.getItem('token'));
  ElMessage.error("未登录！");
  router.push('/login');
}
</script>

<template>
  <h1>番茄书城</h1>
</template>

<style scoped>

</style>
