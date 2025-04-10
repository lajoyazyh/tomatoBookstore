<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { router } from '../../router'
import { UpdateProductInfo, Specification, getTheProduct, updateProductInfo } from "../../api/products.ts";
import { uploadImage } from "../../api/images.ts";

// 修改商品信息需要STAFF权限
const role = sessionStorage.getItem('role');

const route = useRoute();
const productId = Number(route.params.productId);

const currentFile = ref(null) // cover file
const productInfo = ref<{
  id: number;             // id 仅仅作为更新的标志，不能修改
  title: string,
  price: number,
  rate: number,
  description?: string,
  cover?: string,        // 封面url
  detail?: string,
  specifications?: Specification[],
}>({
  id: productId,
  title: '封面',
  price: 0,
  rate: 0,
  specifications: []
});
const newSpecification = ref<{
  item: string,
  value: string,
  productId: number,
}>({
  item: '',
  value: '',
  productId: productId,
});

const addSpecificationDisabled = computed(() => {
  return !(!!newSpecification.value.item && !!newSpecification.value.value && !!newSpecification.value.productId);
})

const updateDisabled = computed(() => {
  return !(role == 'STAFF' &&
      !!productInfo.value.price && productInfo.value.price >= 0 &&
      !!productInfo.value.rate && productInfo.value.rate >= 0 && productInfo.value.rate <= 10 &&
      !!productInfo.value.id && !!productInfo.value.title);
})

onMounted(async () => {
  getTheProduct(productId).then((res) => {
    if (res.data.code == '200') {
      productInfo.value.id = productId
      productInfo.value.title = res.data.data.title
      productInfo.value.price = res.data.data.price
      productInfo.value.rate = res.data.rate
      productInfo.value.description = res.data.description
      productInfo.value.cover = res.data.cover
      productInfo.value.detail = res.data.detail
      productInfo.value.specifications = res.data.specifications
    }
    else if (res.data.code == '400') {
      ElMessage.error(res.data.message)
    }
  })
})

function handleFileChange(file: any) {
  const formData = new FormData();
  formData.append('file', file.raw);

  uploadImage(formData).then(res => {
    if (res.data.code === '000') {
      productInfo.value.cover = res.data.result; // 存储上传的图片 URL
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
  const specificationsCopy = JSON.parse(JSON.stringify(productInfo.value.specifications));
  specificationsCopy.push(newSpecification.value);
  productInfo.value.specifications = specificationsCopy;

  newSpecification.value.item = '';
  newSpecification.value.value = '';
}

function creatUpdateInfo(): UpdateProductInfo {
  const updateInfo: UpdateProductInfo = {
    id: productInfo.value.id,
    description: productInfo.value.description,
    cover: productInfo.value.cover,
    detail: productInfo.value.detail,
    specifications: productInfo.value.specifications
  }
  // 不能删除的部分也不可改为空串
  if (!!productInfo.value.title) updateInfo.title = productInfo.value.title
  if (!!productInfo.value.price && productInfo.value.price >= 0)
    updateInfo.price = productInfo.value.price
  if (!!productInfo.value.rate && productInfo.value.rate >= 0 && productInfo.value.rate <= 10)
    updateInfo.rate = productInfo.value.rate


  return updateInfo;
}

function handleUpdate() {
  updateProductInfo(creatUpdateInfo()).then(res => {
    if (res.data.code == '200') {
      ElMessage.success('更新商品信息成功！');
      // 重新获取商品信息
      productInfo.value.id = res.data.data.id;
      productInfo.value.title = res.data.data.title;
      productInfo.value.price = res.data.data.price;
      productInfo.value.rate = res.data.rate;
      productInfo.value.description = res.data.description;
      productInfo.value.cover = res.data.cover;
      productInfo.value.detail = res.data.detail;
      productInfo.value.specifications = res.data.specifications
    } else {
      ElMessage.error(res.data.message)
    }
  })
}



</script>

<template>

</template>

<style scoped>

</style>