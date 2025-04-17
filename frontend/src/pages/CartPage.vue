<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCart } from "../api/cart.ts";
import {ElMessage} from "element-plus";

const cartItems = ref<Array<{
  cartItemId: number,
  productItemId: number,
  title: string,
  price: number,
  description: string,
  cover: string,
  detail: string,
  quantity: number,
}>>([]);

onMounted(async () => {
  try {
    const response = await getCart();
    if (response.data.code === '200') {
      cartItems.value = response.data.data.map((item: any) => ({
        cartItemId: item.cartItemId,
        productItemId: item.productItemId,
        title: item.title,
        price: item.price,
        description: item.description,
        cover: item.cover,
        detail: item.detail,
        quantity: item.quantity,
      }))
    } else {
      ElMessage.error(response.data.msg);
    }
  } catch (error) {
    ElMessage.error('获取购物车信息失败！');
    console.log(error);
  }
})


</script>

<template>
  <h1>购物车</h1>
</template>

<style scoped>

</style>
