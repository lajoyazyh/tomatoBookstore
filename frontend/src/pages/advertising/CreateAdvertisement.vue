<script setup lang="ts">
import {ElForm, ElFormItem, ElMessage, ElMessageBox} from "element-plus"
import {ref, computed, onMounted} from 'vue'

import {
  getAllAdvertisement,
  createAdvertisement,
  updateAdvertisementsInfo,
  deleteAdvertisement
} from '../../api/advertisement.ts';
import type {advertiseInfo} from "../../api/advertisement.ts";
import { router } from '../../router';


const advertisements = ref<advertiseInfo[]>([]);
const newAd = ref<advertiseInfo>({
  id: 0,
  title: '',
  content: '',
  image_url: '',
  product_id: 0
});

const handleCreateAdvertisement = async () => {
  try {
    const response = await createAdvertisement(newAd.value);
    if (response.data.code === '200') {
      alert('广告创建成功');
      newAd.value = { id: 0, title: '', content: '', image_url: '', product_id: 0 }; // 清空表单
      router.push('/advertisements');
    } else {
      alert(response.data.msg);
    }
  } catch (error) {
    console.error('创建广告失败', error);
  }
};



</script>

<template>
  <div class="advertisement-form">
    <h1>新建广告</h1>
    <form @submit.prevent="handleCreateAdvertisement">
      <input v-model="newAd.title" placeholder="标题" required />
      <input v-model="newAd.content" placeholder="内容" />
      <input v-model="newAd.image_url" placeholder="图片URL" />
      <input v-model="newAd.product_id" type="number" placeholder="产品ID" required />
      <button type="submit">创建广告</button>
    </form>
  </div>

  <div class="advertisement-back">
    <router-link to="/advertisement">
      <button>返回</button>
    </router-link>
  </div>

</template>

<style scoped>
.advertisement-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.advertisement-list {
  margin-bottom: 20px;
}

.advertisement-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.ad-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  margin-right: 20px;
}

.ad-info {
  flex-grow: 1;
}

.advertisement-form {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 4px;
}

.advertisement-form h3 {
  margin-top: 0;
}

.advertisement-form form {
  display: flex;
  flex-direction: column;
}

.advertisement-form input {
  margin-bottom: 10px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.advertisement-form button {
  padding: 8px 16px;
  background-color: #3b93ce;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.advertisement-back {
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中（如果需要） */
  height: 100%; /* 根据需要调整高度 */
}

.advertisement-back button {
  padding: 8px 16px;
  background-color: #8e8c8c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.advertisement-form button:hover {
  background-color: #3b93ce;
}
</style>