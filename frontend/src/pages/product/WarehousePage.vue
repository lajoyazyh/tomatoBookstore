<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus';
import { router } from '../../router'
import { Specification, getAllProducts, deleteProduct, getStockpile, updateStockpile } from "../../api/products.ts";

const role = sessionStorage.getItem('role');

const products = ref<Array<{
  id: number;
  title: string,
  price: number,
  rate: number,
  description?: string,
  cover?: string,
  detail?: string,
  specifications?: Specification[],
}>>([]);

const stockpile = ref<{
  amount: number,
  frozen: number,
  productId: number
}>({
  amount: 0,
  frozen: 0,
  productId: -1,
});

const dialogVisible = ref(false);
const currentProductId = ref(-1);

onMounted(async () => {
  try {
    const response = await getAllProducts();
    if (response.data.code === '200') {
      products.value = response.data.data.map((store: any) => ({
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

async function getStockpileOf(productId: number) {
  if (stockpile.value.productId === productId) return;

  try {
    const res = await getStockpile(productId);
    stockpile.value.amount = res.data.data.amount;
    stockpile.value.frozen = res.data.data.frozen;
    stockpile.value.productId = res.data.data.productId;
  } catch (error) {
    ElMessage.error('获取当前商品库存状态失败！');
    throw error;
  }
}

async function updateCurrentStockpile(productId: number) {
  try {
    const res = await updateStockpile({
      productId: productId,
      amount: stockpile.value.amount,
    });
    if (res.data.code === '200') {
      ElMessage.success('库存更新成功！');
      await getStockpileOf(productId);
      dialogVisible.value = false;
    } else {
      ElMessage.error(res.data.msg || '库存更新失败');
    }
  } catch (error) {
    ElMessage.error('库存更新失败');
  }
}

function removeProduct(productId: number) {
  ElMessageBox.confirm('确定删除该商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    deleteProduct(productId).then((res) => {
      if (res.data.code === '200') {
        ElMessage.success('删除成功！');
        products.value = products.value.filter(p => p.id !== productId);
      } else {
        ElMessage.error(res.data.msg || '删除失败');
      }
    }).catch(() => {
      ElMessage.error('删除失败');
    });
  }).catch(() => {});
}

async function handleManageStock(productId: number) {
  try {
    await getStockpileOf(productId);
    currentProductId.value = productId;
    dialogVisible.value = true;
  } catch (error) {
    // 错误已处理
  }
}
</script>

<template>
  <div class="container">
    <el-row :gutter="20" justify="start">
      <el-col
          v-for="product in products"
          :key="product.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
      >
        <el-card class="product-card">
          <el-image
              :src="product.cover"
              fit="cover"
              class="product-image"
              v-if="product.cover"
          />
          <div class="product-info">
            <h3>{{ product.title }}</h3>
            <p class="price">¥{{ product.price.toFixed(2) }}</p>
            <div class="meta">
              <span class="rate">评分：{{ product.rate }}</span>
            </div>
            <p class="description">{{ product.description }}</p>
          </div>
          <div class="card-actions">
            <el-button
                type="primary"
                class="detail-btn"
                @click="router.push(`productDetail/${product.id}`)"
            >
              查看详情
            </el-button>
            <template v-if="role === 'STAFF'">
              <div class="staff-action">
                <el-button
                    type="warning"
                    @click="handleManageStock(product.id)"
                >
                  库存管理
                </el-button>
              </div>
              <div class="staff-action">
                <el-button
                    type="danger"
                    @click="removeProduct(product.id)"
                >
                  删除
                </el-button>
              </div>
            </template>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" title="库存管理" width="400px">
      <div class="stock-dialog">
        <div class="stock-item">
          <span>当前库存：</span>
          <el-input-number
              v-model="stockpile.amount"
              :min="0"
              controls-position="right"
          />
        </div>
        <div class="stock-tip">
          <span>冻结数量：</span>
          <el-input-number
              v-model="stockpile.frozen"
              :min="0"
              controls-position="right"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateCurrentStockpile(currentProductId)">
          确认修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.product-card {
  margin-bottom: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-image {
  width: 100%;
  height: 220px;
  border-radius: 4px 4px 0 0;
}

.product-info {
  padding: 15px;
  flex: 1;
}

.product-info h3 {
  margin: 0 0 10px;
  font-size: 18px;
  color: #333;
}

.price {
  color: #e65100;
  font-size: 20px;
  font-weight: bold;
  margin: 0 0 8px;
}

.meta {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 14px;
  margin-bottom: 12px;
}

.description {
  color: #666;
  font-size: 14px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 40px;
}

.card-actions {
  padding: 15px;
  border-top: 1px solid #eee;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.detail-btn {
  width: 100%;
  justify-content: center;
}

.staff-action .el-button {
  width: 100%;
  justify-content: center;
}
</style>