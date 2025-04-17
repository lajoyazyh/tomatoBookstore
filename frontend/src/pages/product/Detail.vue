<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { UpdateProductInfo, Specification, getTheProduct, updateProductInfo } from "../../api/products.ts";
import { addNewProduct } from "../../api/cart.ts";
import { uploadImage } from "../../api/images.ts";

// 修改商品信息需要STAFF权限
const role = sessionStorage.getItem('role');
const isEditing = ref(false);

const route = useRoute();
const productId = Number(route.params.productId);

const newCartItemInfo = ref<{
  productId: number,
  quantity: number,
}>({
  productId: productId,
  quantity: 1,
})

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
  title: '标题',
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

function getProduct() {
  getTheProduct(productId).then((res) => {
    if (res.data.code == '200') {
      productInfo.value.id = productId
      productInfo.value.title = res.data.data.title
      productInfo.value.price = res.data.data.price
      productInfo.value.rate = res.data.data.rate
      productInfo.value.description = res.data.data.description
      productInfo.value.cover = res.data.data.cover
      productInfo.value.detail = res.data.data.detail
      productInfo.value.specifications = res.data.data.specifications || []
    }
    else if (res.data.code == '400') {
      ElMessage.error(res.data.msg)
    }
  })
}

onMounted(async () => {
  getProduct()
})

// 新增：处理添加到购物车的弹窗
function handleAddToCart() {
  ElMessageBox.prompt(
      '请输入添加到购物车的数量',
      '添加到购物车',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^[1-9]\d*$/, // 验证输入是否为正整数
        inputErrorMessage: '请输入有效的数量',
        inputValue: newCartItemInfo.value.quantity.toString(), // 确保初始值为字符串
      }
  ).then(({ value }) => {
    // 确保输入值是数字
    const quantity = parseInt(value);
    if (isNaN(quantity)) {
      ElMessage.error('请输入有效的数量');
      return;
    }
    newCartItemInfo.value.quantity = quantity;
    addCartItem();
  }).catch(() => {
    ElMessage.info('已取消添加到购物车');
  });
}

function addCartItem() {
  try {
    addNewProduct(newCartItemInfo.value).then((res) => {
      if (res.data.code == '200') {
        ElMessage.success('添加商品成功！')
      } else {
        ElMessage.error(res.data.msg)
      }
    })
  } catch (error) {
    ElMessage.error('添加商品失败！')
  }
}

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

  // 确保 specifications 是一个数组
  if (!Array.isArray(productInfo.value.specifications)) {
    productInfo.value.specifications = [];
  }

  // 创建新对象而非拷贝赋值，以确保响应
  productInfo.value.specifications = [
    ...productInfo.value.specifications,
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
      // 重新获取商品
      getProduct()
    } else {
      ElMessage.error(res.data.msg)
    }
  })
}

</script>

<template>
  <el-main class="main-container">
    <el-card class="product-detail-card">
      <!-- 商品图片放在最上方 -->
      <div class="product-cover-container">
        <el-image :src="productInfo.cover" class="product-cover" fit="cover"></el-image>
      </div>

      <div class="card-header">
        <h2>{{ productInfo.title }}</h2>
      </div>

      <div class="product-info">
        <div class="product-basic-info">
          <div class="product-price">
            <p><strong>价格：</strong>￥{{ productInfo.price.toFixed(2) }}</p>
          </div>
          <div class="product-rating">
            <p><strong>评分：</strong>{{ productInfo.rate.toFixed(1) }}/10</p>
          </div>
        </div>

        <!-- 编辑按钮，只对STAFF显示 -->
        <div v-if="role === 'STAFF'"style="margin-top: 20px; text-align: center;">
          <el-button type="primary" @click="isEditing = !isEditing">
            {{ isEditing ? '结束编辑' : '编辑' }}
          </el-button>
        </div>
        <!-- 添加商品到购物车按钮，只对CUSTOMER显示 -->
        <div v-if="role === 'CUSTOMER'" style="margin-top: 20px; text-align: center;">
          <el-button type="warning" @click="handleAddToCart">
            添加到购物车
          </el-button>
        </div>

        <!-- 标题、封面图片、价格、评分的编辑 -->
        <div v-if="isEditing" class="edit-form">
          <el-form label-width="100px">
            <el-form-item label="商品标题">
              <el-input v-model="productInfo.title" placeholder="请输入商品标题" style="width: 100%"></el-input>
            </el-form-item>

            <el-form-item label="封面图片">
              <el-upload
                  class="upload-demo"
                  drag
                  action="#"
                  :auto-upload="false"
                  :on-change="handleFileChange"
                  :show-file-list="false"
              >
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
              </el-upload>
            </el-form-item>

            <el-form-item label="商品价格">
              <el-input-number v-model="productInfo.price" :min="0" placeholder="请输入商品价格"></el-input-number>
            </el-form-item>

            <el-form-item label="商品评分">
              <el-input-number
                  v-model="productInfo.rate"
                  :min="0"
                  :max="10"
                  :precision="1"
                  placeholder="请输入商品评分"
              ></el-input-number>
            </el-form-item>
          </el-form>
        </div>

        <!-- 商品介绍放在详情前 -->
        <div class="product-description">
          <h3>商品介绍</h3>
          <el-input
              v-model="productInfo.description"
              type="textarea"
              :readonly="!isEditing"
              placeholder="请输入商品介绍"
              style="width: 100%"
          ></el-input>
        </div>

        <div class="product-detail">
          <h3>商品详情</h3>
          <el-input
              v-model="productInfo.detail"
              type="textarea"
              :readonly="!isEditing"
              placeholder="请输入商品详情"
              style="width: 100%"
          ></el-input>
        </div>

        <!-- 商品规格 -->
        <div class="product-specifications">
          <h3>商品规格</h3>
          <div v-if="isEditing">
            <el-form-item label="商品规格">
              <div class="specification-item" v-for="(spec, index) in productInfo.specifications" :key="index">
                <el-input
                    v-model="spec.item"
                    placeholder="规格名称"
                    style="width: 150px; margin-right: 10px"
                ></el-input>
                <el-input
                    v-model="spec.value"
                    placeholder="规格值"
                    style="width: 150px; margin-right: 10px"
                ></el-input>
                <el-button type="danger" size="small" @click="productInfo.specifications.splice(index, 1)">
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
          </div>
          <div v-else>
            <el-table :data="productInfo.specifications" style="width: 100%">
              <el-table-column prop="item" label="规格项"></el-table-column>
              <el-table-column prop="value" label="规格值"></el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 提交按钮，只在编辑模式下显示 -->
        <div v-if="isEditing" style="margin-top: 20px; text-align: center;">
          <el-button type="success" @click="handleUpdate" :disabled="updateDisabled">
            提交修改
          </el-button>
        </div>
      </div>
    </el-card>
  </el-main>
</template>

<style scoped>
.main-container {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.product-detail-card {
  width: 100%;
  max-width: 800px;
  padding: 20px;
}

.card-header {
  text-align: center;
  margin-bottom: 15px;
}

.product-info {
  margin-top: 20px;
}

.product-basic-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.product-cover-container {
  text-align: center;
  margin-bottom: 15px;
}

.product-cover {
  width: 200px;
  height: 300px;
  border-radius: 0;
  margin: 0 auto;
  display: block;
}

.product-description {
  margin-bottom: 20px;
}

.product-specifications {
  margin-top: 20px;
}

.edit-form {
  margin-bottom: 20px;
}
</style>