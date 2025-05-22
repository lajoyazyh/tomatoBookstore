<script setup lang="ts">
import { ref, computed } from 'vue';
import { ElMessage, ElMessageBox, ElForm, ElFormItem, ElInput, ElInputNumber, ElUpload, ElButton, ElCard } from 'element-plus';
import { router } from '../../router';
import { createAdvertisement} from '../../api/advertisement';
import type {createAdvertiseInfo, updateAdvertiseInfo} from '../../api/advertisement';
import { uploadImage } from '../../api/tools.ts';


// 需要 STAFF 权限
const role = sessionStorage.getItem('role');
if (role !== 'STAFF') {
  ElMessage.error('您不是管理员，不能创建广告！');
  router.push('/advertisement'); // 非管理员直接跳转回广告列表页
}

// const newAd = ref({
//   title: '',
//   content: '',
//   image_url: '',
//   product_id: 0
// });

const title = ref('')
const content = ref('')
const imageUrl = ref('')
const productId = ref(0)

const currentFile = ref(null); // 用于存储当前上传的文件

const hasTitle = computed(() => title.value.length > 0);
// const hasContent = computed(() => !!newAd.value.content);
// const hasImage = computed(() => !!newAd.value.image_url);
const hasValidProductId = computed(() => !!productId && productId.value > 0);

const createDisabled = computed(() => {
  return !(role === 'STAFF' && hasTitle.value && hasValidProductId.value);
});

function handleFileChange(file: any) {
  const formData = new FormData();
  formData.append('file', file.raw);

  uploadImage(formData).then(res => {
    if (res.data.code === '200') {
      imageUrl.value = res.data.data; // 存储上传的图片 URL
      currentFile.value = file; // 存储当前文件
      ElMessage.success('文件上传成功！');
    } else {
      ElMessage.error(res.data.msg || '文件上传失败，请稍后再试！');
    }
  }).catch(error => {
    ElMessage.error('文件上传失败：' + error.message);
  });
}

function createAdvertisementInfo(): createAdvertiseInfo {
  const createInfo: createAdvertiseInfo = {
    title: title.value,
    content: content.value,
    imageUrl: imageUrl.value,
    productId: productId.value
  }
  return createInfo;
}

function handleCreateAdvertisement() {
  if (createDisabled.value) {
    ElMessage.error('请确保所有必填字段都已正确填写！');
    return;
  }

  const adInfo = createAdvertisementInfo();
  console.log('广告信息:', adInfo); // 输出广告信息，确保 product_id 不为空

  createAdvertisement(adInfo).then(res => {
    if (res.data.code === '200') {
      ElMessage.success('广告创建成功！');
      router.push('/advertisement'); // 创建成功后跳转到广告列表页
    } else {
      ElMessage.error(res.data.msg);
    }
  }).catch(error => {
    ElMessage.error('创建广告失败：' + error.message);
  });
}
</script>

<template>
  <div class="advertisement-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>创建广告</span>
        </div>
      </template>

      <el-form label-width="120px">
        <!-- 广告标题 -->
        <el-form-item label="广告标题" :required="true">
          <el-input
              v-model="title"
              placeholder="请输入广告标题"
              clearable
              style="width: 100%"
              :disabled="!role || role !== 'STAFF'"
          ></el-input>
        </el-form-item>

        <!-- 广告内容 -->
        <el-form-item label="广告内容" :required="false">
          <el-input
              v-model="content"
              type="textarea"
              placeholder="请输入广告内容"
              :rows="3"
              style="width: 100%"
              :disabled="!role || role !== 'STAFF'"
          ></el-input>
        </el-form-item>

        <!-- 广告图片 -->
        <el-form-item label="广告图片" :required="false">
          <el-upload
              class="upload-demo"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleFileChange"
              accept="image/*"
          >
            <el-button type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">只能上传图片文件</div>
            </template>
          </el-upload>
          <div v-if="imageUrl" class="cover-preview">
            <el-image :src="imageUrl" fit="cover" style="width: 100px; height: 100px"></el-image>
          </div>
        </el-form-item>

        <!-- 关联产品ID -->
        <el-form-item label="关联产品ID" :required="true">
          <el-input-number
              v-model="productId"
              :min="1"
              placeholder="请输入关联产品ID"
              style="width: 100%"
              :disabled="!role || role !== 'STAFF'"
          ></el-input-number>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button
              type="primary"
              :disabled="createDisabled"
              @click="handleCreateAdvertisement"
              style="width: 100%"
              :loading="createDisabled"
          >
            创建广告
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="advertisement-back">
      <el-button @click="router.push('/advertisement')">返回广告列表</el-button>
    </div>
  </div>
</template>

<style scoped>
.advertisement-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.box-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cover-preview {
  margin-top: 10px;
  display: flex;
  align-items: center;
}

.advertisement-back {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>