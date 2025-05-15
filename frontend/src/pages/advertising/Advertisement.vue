<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from "element-plus";
import {getTheProduct} from "../../api/products.ts";
import { getAllAdvertisement, updateAdvertisementsInfo, deleteAdvertisement } from '../../api/advertisement.ts';
import type {updateAdvertiseInfo} from "../../api/advertisement.ts";
import { uploadImage } from "../../api/images.ts";
import Advertisement from "@/pages/advertising/Advertisement.vue";

const role = sessionStorage.getItem('role');

interface Advertisement {
  id: number;
  title: string;
  content?: string;
  imageUrl?: string;
  productId: number;
  isEditing?: boolean; // 新增编辑状态
  // 商品名称从商品的get方法获取
  productName?: string;
}

const advertisements = ref<Advertisement[]>([]);

onMounted(async () => {
  await refreshAdvertisements();
});

// 异步获取商品名称
async function getTheProductName(productId: number): Promise<string> {
  try {
    const res = await getTheProduct(productId);
    if (res.data.code === '200') {
      return res.data.data.title;
    } else {
      ElMessage.error(res.data.msg)
      return '名称获取失败';
    }
  } catch (error) {
    ElMessage.error('获取商品名称失败！');
    return '名称获取失败';
  }
}

const refreshAdvertisements = async () => {
  try {
    const response = await getAllAdvertisement();
    if (response.data.code === '200') {
      // 并发获取所有广告对应的商品名称
      const ads = await Promise.all(
          response.data.data.map(async (ad: any) => {
            const productName = await getTheProductName(ad.productId);
            return {
              ...ad,
              isEditing: false,
              productName: productName
            };
          })
      );
      advertisements.value = ads;
    } else {
      ElMessage.error(response.data.msg);
    }
  } catch (error) {
    ElMessage.error('获取广告信息失败');
  }
};

const handleFileChange = async (file: any, ad: Advertisement) => {
  const formData = new FormData();
  formData.append('file', file.raw);

  try {
    const res = await uploadImage(formData);
    if (res.data.code === '000') {
      ad.imageUrl = res.data.result;
      ElMessage.success('图片上传成功');
    }
  } catch (error) {
    ElMessage.error('图片上传失败');
  }
};



const handleUpdate = async (ad: Advertisement) => {

  const createInfo: updateAdvertiseInfo = {
    id: ad.id,
    title:ad.title || '',
    content: ad.content || '',
    imageUrl: ad.imageUrl || '',
    productId: ad.productId
  }
  console.log('广告更新信息:', createInfo);

  try {
    const response = await updateAdvertisementsInfo(createInfo);

    if (response.data.code === '200') {
      ElMessage.success('更新成功');
      ad.isEditing = false;
      await refreshAdvertisements();
    }
  } catch (error) {
    ElMessage.error('更新失败');
  }
};

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确认删除该广告？', '警告', { type: 'warning' });
    const response = await deleteAdvertisement(id);
    if (response.data.code === '200') {
      ElMessage.success('删除成功');
      await refreshAdvertisements();
    }
  } catch (error) {
    ElMessage.info('取消删除');
  }
};
</script>

<template>
  <div class="advertisement-container">
    <h2>广告管理</h2>

    <div class="advertisement-list">
      <el-card v-for="ad in advertisements" :key="ad.id" class="ad-item">
        <!-- 非编辑模式 -->
        <div v-if="!ad.isEditing">
          <h3>{{ ad.title }}</h3>
          <p class="content">{{ ad.content }}</p>
          <img v-if="ad.imageUrl" :src="ad.imageUrl" class="ad-image" />
          <p class="title">商品名称：{{ad.productName}}</p>
          <p class="productId">商品id：{{ad.productId}}</p>

          <div class="actions">
            <el-button v-if="role === 'STAFF'" type="primary" @click="ad.isEditing = true">
              编辑
            </el-button>
            <el-button v-if="role === 'STAFF'" type="danger" @click="handleDelete(ad.id)">删除</el-button>
          </div>
        </div>

        <!-- 编辑模式 -->
        <el-form v-else :model="ad" label-width="80px">
          <el-form-item label="标题">
            <el-input v-model="ad.title" />
          </el-form-item>

          <el-form-item label="内容">
            <el-input v-model="ad.content" type="textarea" />
          </el-form-item>

          <el-form-item label="图片">
            <el-upload
                :auto-upload="false"
                :on-change="(file: any) => handleFileChange(file, ad)"
            >
              <el-button>上传图片</el-button>
              <template v-if="ad.imageUrl">
                <img :src="ad.imageUrl" class="preview-image" />
              </template>
            </el-upload>
          </el-form-item>

          <el-form-item label="商品id">
            <el-input v-model="ad.productId" type="textarea" />
          </el-form-item>

          <el-form-item>
            <el-button type="success" @click="handleUpdate(ad)">保存</el-button>
            <el-button @click="ad.isEditing = false">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <div class="create-button">
      <router-link to="/createAdvertisement">
        <el-button type="primary">新建广告</el-button>
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.advertisement-container {
  max-width: 800px;
  margin: 20px auto;
}

.ad-item {
  margin-bottom: 20px;
  padding: 15px;
}

.ad-image {
  max-width: 200px;
  margin: 10px 0;
  border: 1px solid #ddd;
}

.preview-image {
  max-width: 100px;
  margin-left: 10px;
}

.actions {
  margin-top: 15px;
}

.create-button {
  text-align: center;
  margin-top: 20px;
}

.content {
  color: #666;
  margin: 10px 0;
}
</style>