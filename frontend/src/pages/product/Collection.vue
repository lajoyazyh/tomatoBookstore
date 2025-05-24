<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus';
import {router} from "../../router";
import { deleteCollectOf, getUserCollects } from '../../api/collects.ts';

const collections = ref<Array<{
  id: number,
  userId: number,
  productId: number,
  productTitle: string,
  price: number,
  cover: string,
}>>([]);

onMounted(async () => {
  try {
    const response = await getUserCollects();
    if (response.data.code === '200') {
      collections.value = response.data.data.map((collection: any) => ({
        id: collection.id,
        userId: collection.userId,
        productId: collection.productId,
        productTitle: collection.productTitle,
        price: collection.price,
        cover: collection.cover,
      }));
    } else {
      ElMessage.error(response.data.msg);
    }
  } catch (error) {
    ElMessage.error('获取信息失败！');
    console.log(error);
  }

  console.log("收藏数量：",collections.value.length);
  console.log("第一个收藏信息：",collections.value[0]);
})

function handleMoveTo(productId: number) {
  router.push(`productDetail/${productId}`)
}

function handleDeleteCollection(collectId: number) {
  ElMessageBox.confirm('确定删除该收藏吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    deleteCollectOf(collectId).then((res) => {
      if (res.data.code === '200') {
        ElMessage.success('删除成功！');
        // 筛出未被删除的部分，赋值给收藏列表
        collections.value = collections.value.filter(c => c.id !== collectId);
      } else {
        ElMessage.error(res.data.msg || '删除失败');
      }
    }).catch(() => {
      ElMessage.error('删除失败');
    });
  }).catch(() => {});
}

</script>

<template>
  <div class="collections-container">
    <el-card class="collections-card">
      <template #header>
        <div class="card-header">
          <span>我的收藏</span>
        </div>
      </template>

      <div v-if="collections.length === 0" class="empty-collections">
        <p>您还没有收藏任何商品</p>
      </div>

      <div v-else class="collections-list">
        <div class="collection-item" v-for="collection in collections" :key="collection.id">
          <el-image :src="collection.cover" class="collection-cover" fit="cover"></el-image>
          <div class="collection-info">
            <h3>{{ collection.productTitle }}</h3>
            <p class="collection-price">￥{{ collection.price.toFixed(2) }}</p>
          </div>
          <div class="collection-actions">
            <el-button type="primary" @click="handleMoveTo(collection.productId)">查看详情</el-button>
            <el-button type="danger" @click="handleDeleteCollection(collection.id)">删除</el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.collections-container {
  max-width: 1000px;
  margin: 20px auto;
  padding: 15px;
}

.collections-card {
  width: 100%;
}

.card-header {
  text-align: left;
  font-weight: bold;
  font-size: 18px;
}

.collections-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.collection-item {
  display: flex;
  align-items: center;
  border: 1px solid #ebeef5;
  padding: 10px;
  border-radius: 4px;
}

.collection-cover {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  margin-right: 15px;
}

.collection-info {
  flex-grow: 1;
}

.collection-price {
  color: #ff6700;
  font-weight: bold;
}

.collection-actions {
  display: flex;
  gap: 10px;
}

.empty-collections {
  text-align: center;
  padding: 30px 0;
  color: #909399;
}
</style>