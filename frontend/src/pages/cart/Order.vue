<script setup lang="ts">
import {onMounted} from 'vue'
import {ElMessage} from "element-plus";
import { getOrders, payForOrder } from "../../api/orders.ts";

onMounted(async () => {
  try {
    const response = await getOrders()
    if (response.data.code === '200') {
      // TODO: 成功获取订单之后
    } else {
      ElMessage.error(response.data.msg);
    }
  } catch (error) {
    ElMessage.error('获取订单信息失败！');
    console.log(error);
  }
})

</script>

<template>

</template>

<style scoped>

</style>