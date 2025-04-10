<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus';
// 这里router应该用于创建成功后跳转到对应详情界面
import { router } from '../../router'
import { uploadImage } from "../../api/images.ts";
import { createProduct } from "../../api/products.ts";
import type { Specification, CreateProductInfo } from "../../api/products.ts";

// 需要STAFF权限
const role = sessionStorage.getItem('role');

const title = ref('')
const price = ref(0)
const rate = ref(0)
const description = ref('')
const currentFile = ref(null) // cover file
const cover = ref('')
const detail = ref('')
const specifications = ref([])

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

  // 用一个深拷贝更新specifications，避免直接修改
  const specificationsCopy = JSON.parse(JSON.stringify(specifications));
  specificationsCopy.push(newSpecification.value);
  specifications.value = specificationsCopy;

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

</script>

<template>

</template>

<style scoped>

</style>