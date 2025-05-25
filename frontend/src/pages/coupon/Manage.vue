<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {ElMessage, ElMessageBox} from "element-plus";
import {router} from '../../router';
import { getTypeText, getStatusText, formatStringToDate } from "../../api/coupon.ts";
import { getAllCoupons, deleteTheCoupon, updateCouponStatus } from "../../api/coupon.ts";

// 删除优惠券需要STAFF权限
const role = sessionStorage.getItem('role');
if (role != 'STAFF') router.push('/');

type coupon = {
  couponId: number,
  couponName: string,
  couponType: string,
  discountAmount?: number     // when type is 'FIXED' or 'THRESHOLD'
  discountPercentage?: number // when type is 'PERCENTAGE'
  minPurchaseAmount?: number  // when type is 'THRESHOLD'
  validFrom: Date,          // example: 2024-01-01T00:00:00
  validUntil: Date,            // example: 2024-01-31T23:59:59
  status?: string,
  description?: string
}
const coupons = ref<Array<coupon>>([]);

onMounted(async () => {
  try {
    const response = await getAllCoupons();
    if (response.data.code === '200') {
      coupons.value = response.data.data.map((coupon: any) => ({
        couponId: coupon.couponId,
        couponName: coupon.couponName,
        couponType: coupon.couponType,
        discountAmount: coupon.discountAmount,
        discountPercentage: coupon.discountPercentage,
        minPurchaseAmount: coupon.minPurchaseAmount,
        validFrom: formatStringToDate(coupon.validFrom),
        validUntil: formatStringToDate(coupon.validUntil),
        status: coupon.status,
        description: coupon.description,
      }));
    }
  } catch (error) {
    ElMessage.error('获取信息失败！');
    console.log(error);
  }
})

function handleMoveTo(id: number) {
  router.push('/couponDetail/' + id);
}

function updateStatus(id: number, status: string) {
  if (status === 'EXPIRED') {
    ElMessage.error('已过期，如需修改状态请修改可用时间');
    return;
  }

  let targetStatus = 'ACTIVE';
  if (status === 'ACTIVE') targetStatus = 'INACTIVE';
  try {updateCouponStatus(
    {couponId: id, status: targetStatus}).then(res => {
      if (res.data.code === '200') {
        ElMessage.success('更新状态成功！');

        // 及时在前端更新显示
        const couponIndex = coupons.value.findIndex((c) => c.couponId === id);
        if (couponIndex !== -1) {
          coupons.value[couponIndex].status = targetStatus;
        }
      } else {
        ElMessage.error(res.data.msg)
      }
    })
  } catch (error) {
    ElMessage.error('更新状态失败！');
    console.log(error);
  }
}

function deleteCoupon(id: number) {
  ElMessageBox.confirm('确定删除该优惠券吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    deleteTheCoupon(id).then((res) => {
      if (res.data.code === '200') {
        ElMessage.success('删除成功！');
        coupons.value = coupons.value.filter((c) => c.couponId !== id);
      } else {
        ElMessage.error(res.data.msg);
      }
    }).catch(() => {
      ElMessage.error('删除失败');
    });
  }).catch(() => {});
}

</script>

<template>
  <div class="coupons-container">
    <div v-if="role==='STAFF'" class="create-button">
      <router-link to="/createAdvertisement">
        <el-button type="primary">新建优惠券</el-button>
      </router-link>
    </div>

    <el-card class="coupons-card">
      <template #header>
        <div class="card-header">
          <span>优惠券管理</span>
        </div>
      </template>

      <div v-if="coupons.length === 0" class="empty-coupons">
        <p>暂无优惠券</p>
      </div>

      <div v-else class="coupons-list">
        <div class="coupon-item" v-for="coupon in coupons" :key="coupon.couponId">
          <div class="coupon-info">
            <h3>{{ coupon.couponName }}</h3>
            <p>类型：{{ getTypeText(coupon.couponType) }}</p>
            <p v-if="coupon.couponType === 'FIXED' || coupon.couponType === 'THRESHOLD'">
              折扣金额：￥{{ coupon.discountAmount || '未设置' }}
            </p>
            <p v-if="coupon.couponType === 'PERCENTAGE'">
              折扣百分比：{{ coupon.discountPercentage || '未设置' }}%
            </p>
            <p>有效期：{{ coupon.validFrom.toLocaleDateString() }} - {{ coupon.validUntil.toLocaleDateString() }}</p>
            <p>状态：{{ getStatusText(coupon.status) }}</p>
          </div>
          <div class="coupon-actions">
            <el-button type="primary" plain size="default" @click="handleMoveTo(coupon.couponId)">
              查看详情
            </el-button>
            <el-button
                type="success"
                plain
                size="default"
                @click="updateStatus(coupon.couponId, coupon.status)"
            >
              {{ coupon.status === 'ACTIVE' ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" plain size="default" @click="deleteCoupon(coupon.couponId)">
              删除
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.create-button {
  text-align: center;
  margin: 0 20px 20px;
}

.coupons-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 15px;
}

.coupons-card {
  width: 100%;
}

.card-header {
  text-align: left;
  font-weight: bold;
  font-size: 18px;
}

.coupon-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
}

.coupon-info {
  flex-grow: 1;
}

.coupon-actions {
  display: flex;
  gap: 10px;
}

.empty-coupons {
  text-align: center;
  padding: 30px 0;
  color: #909399;
}
</style>