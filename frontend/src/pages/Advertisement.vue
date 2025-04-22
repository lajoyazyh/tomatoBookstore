<script setup lang="ts">
import {ElForm, ElFormItem, ElMessage, ElMessageBox} from "element-plus"
import {ref, computed, onMounted} from 'vue'

import {
  getAllAdvertisement,
  createAdvertisement,
  updateAdvertisementsInfo,
  deleteAdvertisement
} from '../api/advertisement';
import type {advertiseInfo} from "../api/advertisement";


const advertisements = ref<advertiseInfo[]>([]);
const newAd = ref<advertiseInfo>({
  id: 0,
  title: '',
  content: '',
  image_url: '',
  product_id: 0
});

onMounted(async () => {
  try {
    const response = await getAllAdvertisement();
    if (response.data.code === '200') {
      advertisements.value = response.data.data;
    } else {
      alert(response.data.msg);
    }
  } catch (error) {
    console.error('获取广告信息失败', error);
  }
});

const handleCreateAdvertisement = async () => {
  try {
    const response = await createAdvertisement(newAd.value);
    if (response.data.code === '200') {
      alert('广告创建成功');
      newAd.value = { id: 0, title: '', content: '', image_url: '', product_id: 0 }; // 清空表单
      await refreshAdvertisements();
    } else {
      alert(response.data.msg);
    }
  } catch (error) {
    console.error('创建广告失败', error);
  }
};

const handleUpdateAdvertisement = async (ad: any) => {
  try {
    const response = await updateAdvertisementsInfo(ad);
    if (response.data.code === '200') {
      alert('广告更新成功');
      await refreshAdvertisements();
    } else {
      alert(response.data.msg);
    }
  } catch (error) {
    console.error('更新广告失败', error);
  }
};

const handleDeleteAdvertisement = async (id: number) => {
  if (confirm('确定要删除这条广告吗？')) {
    try {
      const response = await deleteAdvertisement(id);
      if (response.data.code === '200') {
        alert('广告删除成功');
        await refreshAdvertisements();
      } else {
        alert(response.data.msg);
      }
    } catch (error) {
      console.error('删除广告失败', error);
    }
  }
};

const refreshAdvertisements = async () => {
  try {
    const response = await getAllAdvertisement();
    if (response.data.code === '200') {
      advertisements.value = response.data.data;
    } else {
      alert(response.data.msg);
    }
  } catch (error) {
    console.error('刷新广告信息失败', error);
  }
};


</script>

<template>
  <div class="advertisement-container">
    <h2>广告管理</h2>

    <div class="advertisement-list">
      <div v-for="ad in advertisements" :key="ad.id" class="advertisement-item">
        <img :src="ad.image_url" alt="广告图片" class="ad-image">
        <div class="ad-info">
          <h3>{{ ad.title }}</h3>
          <p>{{ ad.content }}</p>
        </div>
        <button @click="deleteAdvertisement(ad.id)">删除</button>
      </div>
    </div>

    <div class="advertisement-form">
      <h3>添加新广告</h3>
      <form @submit.prevent="handleCreateAdvertisement">
        <input v-model="newAd.title" placeholder="标题" required />
        <input v-model="newAd.content" placeholder="内容" />
        <input v-model="newAd.image_url" placeholder="图片URL" />
        <input v-model="newAd.product_id" type="number" placeholder="产品ID" required />
        <button type="submit">创建广告</button>
      </form>
    </div>
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
  background-color: #4a56e2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.advertisement-form button:hover {
  background-color: #3a46d5;
}
</style>