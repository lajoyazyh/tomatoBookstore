<script setup lang="ts">
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { router } from '../../router';
import { createCoupon } from "../../api/coupon.ts";
import type { CreateCouponInfo } from "../../api/coupon.ts";

// 需要STAFF权限
const role = sessionStorage.getItem('role');
if (role != 'STAFF') ElMessage.error('您不是管理员，不能创建优惠券！')

function getTypeText(type: string): string {
  switch (type) {
    case 'FIXED':
      return '礼金';
    case 'PERCENTAGE':
      return '折扣';
    case 'THRESHOLD':
      return '满减';
    default:
      return type;
  }
}
function getStatusText(status: string): string {
  switch (status) {
    case 'ACTIVE':
      return '可用';
    case 'INACTIVE':
      return '禁用';
    default:
      return status;
  }
}

const couponName = ref('')
const couponType = ref('')
const discountAmount = ref(0)
const discountPercentage =  ref(0)
const minPurchaseAmount = ref(0)
const validFrom = ref(new Date())
const validUntil = ref(new Date())

// 这两项不必需
const status = ref('')
const description = ref('')

const hasCouponName = computed(() => !!couponName.value)
const hasCouponType = computed(() => !!couponType.value)
const hasDiscountAmount = computed(() => !!discountAmount.value)
const hasDiscountPercentage = computed(() => !!discountPercentage.value)
const hasMinPurchaseAmount = computed(() => !!minPurchaseAmount.value)
const hasValidFrom = computed(() => !!validFrom.value)
const hasValidUntil = computed(() => !!validUntil.value)

const hasNecessaryInfo =  computed(() => {
  return hasCouponName.value && hasCouponType.value && hasValidFrom.value && hasValidUntil.value
})
const isValidTypeInfo = computed(() => {
  const case1 = couponType.value === 'FIXED' && hasDiscountAmount.value;
  const case2 = couponType.value === 'PERCENTAGE' && hasDiscountPercentage.value;
  const case3 = couponType.value === 'THRESHOLD' && hasDiscountAmount.value && hasMinPurchaseAmount.value;
  return case1 || case2 || case3
})
const isValidTimeInfo = computed(() => validFrom.value < validUntil.value)
const createCouponDisabled = computed(() => {
  return !(role == 'STAFF' && hasNecessaryInfo.value && isValidTypeInfo.value && isValidTimeInfo.value)
})

function formatDate(date: Date): string {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hours = date.getHours();
  const minutes = date.getMinutes();
  const seconds = date.getSeconds();

  function pad(num: number): string {
    if (num < 10) return '0' + num.toString();
    return num.toString();
  }
  return `${year}-${pad(month)}-${pad(day)}T${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;
}
function createCouponInfo(): CreateCouponInfo {
  const createInfo: CreateCouponInfo = {
    couponName: couponName.value,
    couponType: couponType.value,
    validFrom: formatDate(validFrom.value) || '2008-1-1T12:00:00',
    validUntil: formatDate(validUntil.value) || '2008-1-1T12:00:00',
  }
  if (hasDiscountAmount.value) createInfo.discountAmount = discountAmount.value
  if (hasDiscountPercentage.value) createInfo.discountPercentage = discountPercentage.value
  if (hasMinPurchaseAmount.value) createInfo.minPurchaseAmount = minPurchaseAmount.value
  if (!!status.value) createInfo.status = status.value
  if (!!description.value) createInfo.description = description.value
  return createInfo;
}

function handleCreateCoupon() {
  console.log(formatDate(validFrom.value), '+', formatDate(validUntil.value))
  try {
    createCoupon(createCouponInfo()).then(res => {
      if (res.data.code === '200') {
        ElMessage.success('创建优惠券成功！');
        // 跳转详情界面
        router.push('/couponDetail/' + res.data.data.couponId);
      } else {
        ElMessage.error(res.data.msg)
      }
    })
  } catch (error) {
    ElMessage.error('创建失败！');
    console.log(error);
  }
}

</script>

<template>
  <div class="coupon-create-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>创建优惠券</span>
        </div>
      </template>

      <el-form label-width="120px" :model="{}">
        <!-- 优惠券名称 -->
        <el-form-item label="优惠券名称" :required="true">
          <el-input
              v-model="couponName"
              placeholder="请输入优惠券名称"
              clearable
              style="width: 100%"
          ></el-input>
        </el-form-item>

        <!-- 优惠券类型 -->
        <el-form-item label="优惠券类型" :required="true">
          <el-select v-model="couponType" placeholder="请选择优惠券类型" style="width: 100%">
            <el-option label="礼金" value="FIXED"></el-option>
            <el-option label="折扣" value="PERCENTAGE"></el-option>
            <el-option label="满减" value="THRESHOLD"></el-option>
          </el-select>
        </el-form-item>

        <!-- 折扣金额 -->
        <el-form-item
            v-if="couponType === 'FIXED' || couponType === 'THRESHOLD'"
            label="折扣金额"
            :required="true"
        >
          <el-input-number
              v-model="discountAmount"
              :min="0"
              placeholder="请输入折扣金额"
              style="width: 100%"
          ></el-input-number>
        </el-form-item>

        <!-- 折扣百分比 -->
        <el-form-item v-if="couponType === 'PERCENTAGE'" label="折扣百分比" :required="true">
          <el-input-number
              v-model="discountPercentage"
              :min="0"
              :max="100"
              placeholder="请输入折扣百分比"
              style="width: 100%"
          ></el-input-number>
        </el-form-item>

        <!-- 最低消费金额 -->
        <el-form-item v-if="couponType === 'THRESHOLD'" label="最低消费金额" :required="true">
          <el-input-number
              v-model="minPurchaseAmount"
              :min="0"
              placeholder="请输入最低消费金额"
              style="width: 100%"
          ></el-input-number>
        </el-form-item>

        <!-- 有效期起始 -->
        <el-form-item label="有效期起始" :required="true">
          <el-date-picker
              v-model="validFrom"
              type="datetime"
              placeholder="选择起始时间"
              style="width: 100%"
          ></el-date-picker>
        </el-form-item>

        <!-- 有效期结束 -->
        <el-form-item label="有效期结束" :required="true">
          <el-date-picker
              v-model="validUntil"
              type="datetime"
              placeholder="选择结束时间"
              style="width: 100%"
          ></el-date-picker>
        </el-form-item>

        <!-- 状态 -->
        <el-form-item label="状态">
          <el-select v-model="status" placeholder="请选择状态" style="width: 100%">
            <el-option label="可用" value="ACTIVE"></el-option>
            <el-option label="禁用" value="INACTIVE"></el-option>
          </el-select>
        </el-form-item>

        <!-- 描述 -->
        <el-form-item label="描述">
          <el-input
              v-model="description"
              type="textarea"
              placeholder="请输入描述"
              :rows="3"
              style="width: 100%"
          ></el-input>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button
              type="primary"
              :disabled="createCouponDisabled"
              @click="handleCreateCoupon"
              style="width: 100%"
          >
            创建优惠券
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.coupon-create-container {
  max-width: 800px;
  margin: 20px auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>