<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getAllAdvertisement,
  createAdvertisement,
  updateAdvertisementsInfo,
  deleteAdvertisement
} from '../../api/advertisement.ts';
import type { advertiseInfo } from "../../api/advertisement.ts";
import { uploadImage } from "../../api/images.ts";

const advertisements = ref<advertiseInfo[]>([]);
const editingAds = ref<Record<number, boolean>>({});

onMounted(async () => {
  try {
    const response = await getAllAdvertisement();
    if (response.data.code === '200') {
      advertisements.value = response.data.data;
    } else {
      ElMessage.error(response.data.msg);
    }
  } catch (error) {
    console.error('获取广告信息失败', error);
    ElMessage.error('获取广告信息失败');
  }

  // 写入样例数据
  advertisements.value = [
    {
      id: 1,
      title: '广告1',
      content: '这是广告1的内容',
      image_url: 'https://example.com/image1.jpg',
      product_id: 101
    },
    {
      id: 2,
      title: '广告2',
      content: '这是广告2的内容',
      image_url: 'https://example.com/image2.jpg',
      product_id: 102
    },
    {
      id: 3,
      title: '广告3',
      content: '这是广告3的内容',
      image_url: 'https://example.com/image3.jpg',
      product_id: 103
    }
  ];
});

const handleUpdateAdvertisement = async (ad: any) => {
  try {
    const response = await updateAdvertisementsInfo(ad);
    if (response.data.code === '200') {
      ElMessage.success('广告更新成功');
      await refreshAdvertisements();
    } else {
      ElMessage.error(response.data.msg);
    }
  } catch (error) {
    console.error('更新广告失败', error);
    ElMessage.error('更新广告失败');
  }
};

const handleDeleteAdvertisement = async (id: number) => {
  if (confirm('确定要删除这条广告吗？')) {
    try {
      const response = await deleteAdvertisement(id);
      if (response.data.code === '200') {
        ElMessage.success('广告删除成功');
        await refreshAdvertisements();
      } else {
        ElMessage.error(response.data.msg);
      }
    } catch (error) {
      console.error('删除广告失败', error);
      ElMessage.error('删除广告失败');
    }
  }
};

const refreshAdvertisements = async () => {
  try {
    const response = await getAllAdvertisement();
    if (response.data.code === '200') {
      advertisements.value = response.data.data;
      resetEditingAds();
    } else {
      ElMessage.error(response.data.msg);
    }
  } catch (error) {
    console.error('刷新广告信息失败', error);
    ElMessage.error('刷新广告信息失败');
  }
};

const startEditing = (id: number) => {
  editingAds.value[id] = true;
};

const saveAdvertisement = async (id: number) => {
  const ad = advertisements.value.find(a => a.id === id);
  if (ad) {
    try {
      const response = await updateAdvertisementsInfo(ad);
      if (response.data.code === '200') {
        ElMessage.success('广告更新成功');
        editingAds.value[id] = false; // Stop editing
        await refreshAdvertisements();
      } else {
        ElMessage.error(response.data.msg);
      }
    } catch (error) {
      console.error('更新广告失败', error);
      ElMessage.error('更新广告失败');
    }
  }
};

const resetEditingAds = () => {
  Object.keys(editingAds.value).forEach(key => {
    editingAds.value[Number(key)] = false;
  });
};

watch(advertisements, () => {
  resetEditingAds();
}, { deep: true });

function handleFileChange(file: any, adId: number) {
  const formData = new FormData();
  formData.append('file', file.raw);

  uploadImage(formData).then(res => {
    if (res.data.code === '000') {
      const index = advertisements.value.findIndex(a => a.id === adId);
      if (index !== -1) {
        advertisements.value[index].image_url = res.data.result; // 更新图片 URL
      }
      ElMessage.success('文件上传成功！');
    } else {
      ElMessage.error(res.data.msg || '文件上传失败，请稍后再试！');
    }
  }).catch(error => {
    ElMessage.error('文件上传失败：' + error.message);
  });
}
</script>

<template>
  <div class="advertisement-container">
    <h2>广告管理</h2>

    <div class="advertisement-list">
      <div v-for="ad in advertisements" :key="ad.id" class="advertisement-item">
        <el-form :model="ad" :disabled="!editingAds[ad.id]">
          <el-form-item label="标题">
            <el-input v-model="ad.title" placeholder="请输入标题"></el-input>
          </el-form-item>
          <el-form-item label="内容">
            <el-input type="textarea" v-model="ad.content" placeholder="请输入内容"></el-input>
          </el-form-item>
          <el-form-item label="图片">
            <el-upload
                class="avatar-uploader"
                :auto-upload="false"
                :on-change="(file) => handleFileChange(file, ad.id)"
                :show-file-list="false"
                accept="image/*"
                :disabled="!editingAds[ad.id]"
            >
              <img v-if="ad.image_url" :src="ad.image_url" class="avatar" />
              <el-button v-else type="primary" :disabled="!editingAds[ad.id]">选择图片</el-button>
            </el-upload>
          </el-form-item>
        </el-form>
        <div class="ad-actions">
          <button v-if="editingAds[ad.id]" @click="saveAdvertisement(ad.id)">保存</button>
          <button v-else @click="startEditing(ad.id)">编辑</button>
          <button @click="handleDeleteAdvertisement(ad.id)">删除</button>
        </div>
      </div>
    </div>

    <div class="advertisement-button">
      <router-link to="/createAdvertisement">
        <button>创建新广告</button>
      </router-link>
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
  flex-direction: column;
  margin-bottom: 20px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.ad-info {
  flex-grow: 1;
}

.ad-actions {
  display: flex;
  gap: 10px;
}

.advertisement-button {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.advertisement-button button {
  padding: 8px 16px;
  background-color: #3b93ce;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.avatar-uploader {
  text-align: center;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}
</style>