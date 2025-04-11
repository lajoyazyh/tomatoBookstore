<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus';
import { router } from '../../router';
import { uploadImage } from "../../api/images.ts";
import { createProduct } from "../../api/products.ts";
import type { Specification, CreateProductInfo } from "../../api/products.ts";

// 需要STAFF权限
const role = sessionStorage.getItem('role');
if (role != 'STAFF') ElMessage.error('您不是管理员，不能创建商品！')

const title = ref('')
const price = ref(0)
const rate = ref(0)
const description = ref('')
const currentFile = ref(null) // cover file
const cover = ref('')
const detail = ref('')
const specifications = ref<Specification[]>([]);

const newSpecification = ref<{
  item: string,
  value: string,
}>({
  item: '',
  value: '',
});
const addSpecificationDisabled = computed(() => {
  return !(!!newSpecification.value.item && !!newSpecification.value.value);
})

const hasTitle = ref(computed(() => !!title.value))
const hasValidPrice = ref(computed(() => !!price.value && price.value >= 0))
const hasValidRate = ref(computed(() => !!rate.value && rate.value >= 0 && rate.value <= 10))
const createDisabled = computed(() => {
  return !(role == 'STAFF' && hasTitle.value && hasValidPrice.value && hasValidRate.value)
})

function handleFileChange(file: any) {
  const formData = new FormData();
  formData.append('file', file.raw);

  uploadImage(formData).then(res => {
    if (res.data.code === '000') {
      cover.value = res.data.result; // 存储上传的图片 URL
      currentFile.value = file; // 存储当前文件
      ElMessage.success('文件上传成功！');
    } else {
      ElMessage.error(res.data.msg || '文件上传失败，请稍后再试！');
    }
  }).catch(error => {
    ElMessage.error('文件上传失败：' + error.message);
  });
}

function addNewSpecification() {
  // 不允许创建空的规格描述
  if (addSpecificationDisabled.value) return;

  // 创建新对象而非拷贝赋值，以确保响应
  specifications.value = [
    ...specifications.value,
    {
      id: -1,
      item: newSpecification.value.item,
      value: newSpecification.value.value,
      productId: -1,
    },
  ];

  newSpecification.value.item = '';
  newSpecification.value.value = '';
}

function createProductInfo(): CreateProductInfo {
  const createInfo: CreateProductInfo = {
    title: title.value,
    price: price.value,
    rate: rate.value,
    description: description.value,
    cover: cover.value,
    detail: detail.value,
    specifications: specifications.value
  }
  return createInfo;
}

function handleCreateProduct() {
  createProduct(createProductInfo()).then(res => {
    if (res.data.code === '200') {
      ElMessage.success('创建商品成功！');
      // 跳转详情界面
      router.push('/productDetail/' + res.data.data.id);
    } else {
      ElMessage.error(res.data.msg)
    }
  })
}

</script>

<template>
  <div class="product-create-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>创建商品</span>
        </div>
      </template>

      <el-form label-width="120px" :model="{}">
        <!-- 商品标题 -->
        <el-form-item label="商品标题" :required="true">
          <el-input
              v-model="title"
              placeholder="请输入商品标题"
              clearable
              style="width: 100%"
          ></el-input>
        </el-form-item>

        <!-- 商品价格 -->
        <el-form-item label="商品价格" :required="true">
          <el-input-number
              v-model="price"
              :min="0"
              placeholder="请输入商品价格"
              style="width: 100%"
          ></el-input-number>
        </el-form-item>

        <!-- 商品评分 -->
        <el-form-item label="商品评分" :required="true">
          <el-input-number
              v-model="rate"
              :min="0"
              :max="10"
              placeholder="请输入商品评分"
              style="width: 100%"
          ></el-input-number>
        </el-form-item>

        <!-- 商品描述 -->
        <el-form-item label="商品描述">
          <el-input
              v-model="description"
              type="textarea"
              placeholder="请输入商品描述"
              :rows="3"
              style="width: 100%"
          ></el-input>
        </el-form-item>

        <!-- 商品详情 -->
        <el-form-item label="商品详情">
          <el-input
              v-model="detail"
              type="textarea"
              placeholder="请输入商品详情"
              :rows="5"
              style="width: 100%"
          ></el-input>
        </el-form-item>

        <!-- 商品封面 -->
        <el-form-item label="商品封面">
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
          <div v-if="cover" class="cover-preview">
            <el-image :src="cover" fit="cover" style="width: 100px; height: 100px"></el-image>
          </div>
        </el-form-item>

        <!-- 商品规格 -->
        <el-form-item label="商品规格">
          <div class="specification-item" v-for="(spec, index) in specifications" :key="index">
            <el-input
                v-model="spec.item"
                placeholder="规格名称"
                style="width: 150px; margin-right: 10px"
                :disabled="false"
            ></el-input>
            <el-input
                v-model="spec.value"
                placeholder="规格值"
                style="width: 150px; margin-right: 10px"
                :disabled="false"
            ></el-input>
            <el-button type="danger" size="small" @click="specifications.splice(index, 1)">
              删除
            </el-button>
          </div>

          <div style="margin-top: 10px">
            <el-input
                v-model="newSpecification.item"
                placeholder="规格名称"
                style="width: 150px; margin-right: 10px"
            ></el-input>
            <el-input
                v-model="newSpecification.value"
                placeholder="规格值"
                style="width: 150px; margin-right: 10px"
            ></el-input>
            <el-button
                type="primary"
                :disabled="addSpecificationDisabled"
                @click="addNewSpecification"
            >
              添加规格
            </el-button>
          </div>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button
              type="primary"
              :disabled="createDisabled"
              @click="handleCreateProduct"
              style="width: 100%"
          >
            创建商品
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.product-create-container {
  max-width: 800px;
  margin: 20px auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.specification-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.cover-preview {
  margin-top: 10px;
}
</style>