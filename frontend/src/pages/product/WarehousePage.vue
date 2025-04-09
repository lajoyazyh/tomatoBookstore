<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus';
import { router } from '../../router'
import { Specification, getAllProducts, getStockpile, updateStockpile } from "../../api/products.ts";

const role = sessionStorage.getItem('role');

const products = ref<Array<{
  id: string;
  title: string,
  price: number,
  rate: number,
  description?: string,
  cover?: string,        // 封面url
  detail?: string,
  specifications?: Specification[],
}>>([]);

const stockpile = ref<{
  id: string,
  amount: number,
  frozen: number,
  productId: string
}>({
  id: '-1',
  amount: 0,
  frozen: 0,
  productId: '-1',
});

onMounted(async () => {
  try {
    const response = await getAllProducts();
    if (response.data.code === '200') {
      // 接口字段映射到本地结构
      products.value = response.data.map((store: any) => ({
        id: store.id,
        title: store.title,
        price: store.price,
        rate: store.rate,
        description: store.description,
        cover: store.cover,
        detail: store.detail,
        specifications: store.specifications
      }));
    }
  } catch (error) {
    ElMessage.error('获取商品列表失败!');
  }
})

function getStockpileOf(productId: string) {
  if (stockpile.value.productId == productId) return;

  try {
    getStockpile(productId).then((res) => {
      stockpile.value.id = res.data.data.id;
      stockpile.value.amount = res.data.data.amount;
      stockpile.value.frozen = res.data.frozen;
      stockpile.value.productId = res.data.data.productId;
    })
  } catch (error) {
    ElMessage.error('获取当前商品库存状态失败！');
  }
}

function updateCurrentStockpile(productId: string) {
  if (stockpile.value.productId != productId) {
    ElMessage.error('错误指定商品');
  }

  updateStockpile({
    productId: productId,
    amount: stockpile.value.amount,
  }).then(res => {
    if (res.data.code === '200') {
      ElMessage.success('库存更新成功！');
      // 更新成功后重新获取库存，方便校对以及防止二次点击更改产生的不一致
      getStockpileOf(productId);
    }
    else if (res.data.code === '400') {
      ElMessage.error(res.data.msg);
    }
  })
}

</script>

<template>
  <h1>库存查看</h1>
</template>

<style scoped>

</style>
