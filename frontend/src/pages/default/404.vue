<template>
  <div class="not-found">
    <h1>404 - 来到了没有书籍的荒原</h1>
    <p>您访问的页面不存在。</p>
    <router-link to="/index">返回首页</router-link>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';

onMounted(() => {
  // 检查页面是否已经刷新过
  if (!sessionStorage.getItem('alreadyRefreshed')) {
    sessionStorage.setItem('alreadyRefreshed', 'true');
    window.location.reload();
  } else {
    sessionStorage.removeItem('alreadyRefreshed');
  }
});
</script>

<style scoped>
.not-found {
  text-align: center;
  padding: 50px;
}
</style>