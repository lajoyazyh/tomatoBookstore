<script setup lang="ts">
import {computed, onMounted, ref} from 'vue'
import {useRoute} from 'vue-router';
import {ElMessage} from "element-plus";
import {router} from '../../router';
import { getTypeText, getStatusText, formatDateToString, formatStringToDate } from "../../api/coupon.ts";
import { updateCoupon, getTheCoupon } from "../../api/coupon.ts";
import type { UpdateCouponInfo } from "../../api/coupon.ts";

// 修改优惠券信息需要STAFF权限
const role = sessionStorage.getItem('role');
if (role != 'STAFF') router.push('/');
const isEditing = ref(false);

const route = useRoute();
const couponId = Number(route.params.couponId);

const couponInfo = ref<{
  couponName: string,
  couponType: string,
  discountAmount?: number     // when type is 'FIXED' or 'THRESHOLD'
  discountPercentage?: number // when type is 'PERCENTAGE'
  minPurchaseAmount?: number  // when type is 'THRESHOLD'
  validFrom: Date,
  validUntil: Date,
  status?: string,
  description?: string
}>({
  couponName: '优惠券',
  couponType: '',
  validFrom: new Date(),
  validUntil: new Date(),
})

onMounted(async () => {
  try {
    getTheCoupon(couponId).then((res) => {
      if (res.data.code == '200') {
        couponInfo.value.couponName = res.data.data.couponName
        couponInfo.value.couponType = res.data.data.couponType
        couponInfo.value.discountAmount = res.data.data.discountAmount
        couponInfo.value.discountPercentage = res.data.data.discountPercentage
        couponInfo.value.minPurchaseAmount = res.data.data.minPurchaseAmount
        couponInfo.value.validFrom = formatStringToDate(res.data.data.validFrom)
        couponInfo.value.validUntil = formatStringToDate(res.data.data.validUntil)
        couponInfo.value.status = res.data.data.status
        couponInfo.value.description = res.data.data.description
      }
      else {
        ElMessage.error(res.data.msg)
      }
    })
  } catch (error) {
    ElMessage.error('获取信息失败！');
    console.log(error);
  }
})

const hasCouponName = computed(() => !!couponInfo.value.couponName)
const hasCouponType = computed(() => !!couponInfo.value.couponType)
const hasDiscountAmount = computed(() => !!couponInfo.value.discountAmount)
const hasDiscountPercentage = computed(() => !!couponInfo.value.discountPercentage)
const hasMinPurchaseAmount = computed(() => !!couponInfo.value.minPurchaseAmount)
const hasValidFrom = computed(() => !!couponInfo.value.validFrom)
const hasValidUntil = computed(() => !!couponInfo.value.validUntil)

const hasNecessaryInfo =  computed(() => {
  return hasCouponName.value && hasCouponType.value && hasValidFrom.value && hasValidUntil.value
})
const isValidTypeInfo = computed(() => {
  const case1 = couponInfo.value.couponType === 'FIXED' && hasDiscountAmount.value;
  const case2 = couponInfo.value.couponType === 'PERCENTAGE' && hasDiscountPercentage.value;
  const case3 = couponInfo.value.couponType === 'THRESHOLD' && hasDiscountAmount.value && hasMinPurchaseAmount.value;
  return case1 || case2 || case3
})
const isValidTimeInfo = computed(() => couponInfo.value.validFrom < couponInfo.value.validUntil)
const updateCouponDisabled = computed(() => {
  return !(role === 'STAFF' && hasNecessaryInfo.value && isValidTypeInfo.value && isValidTimeInfo.value)
})

function createUpdateCouponInfo(): UpdateCouponInfo {
  return {
    couponId: couponId,
    couponName: couponInfo.value.couponName,
    couponType: couponInfo.value.couponType,
    discountAmount: couponInfo.value.discountAmount,
    discountPercentage: couponInfo.value.discountPercentage,
    minPurchaseAmount: couponInfo.value.minPurchaseAmount,
    validFrom: formatDateToString(couponInfo.value.validFrom),
    validUntil: formatDateToString(couponInfo.value.validUntil),
    status: couponInfo.value.status,
    description: couponInfo.value.description,
  }
}

function handleUpdateCoupon(updateInfo: UpdateCouponInfo) {
  try {
    updateCoupon(createUpdateCouponInfo()).then(res => {
      if (res.data.code === '200') {
        ElMessage.success('修改成功！');
        // 刷新
        router.go(0);
      } else {
        ElMessage.error(res.data.msg)
      }
    })
  } catch (error) {
    ElMessage.error('修改失败！请联系技术部门');
    console.log(error);
  }
}

</script>

<template>
  <div class="coupon-edit-container">
    <el-card class="coupon-card">
      <template #header>
        <div class="card-header">
          <span>{{ isEditing ? '编辑优惠券' : '优惠券详情' }}</span>
          <el-button
              type="primary"
              style="margin-left: 20px;"
              @click="isEditing = !isEditing"
          >
            {{ isEditing ? '取消编辑' : '编辑' }}
          </el-button>
        </div>
      </template>

      <el-form label-width="120px" :model="couponInfo">
        <!-- 优惠券名称 -->
        <el-form-item label="优惠券名称" :required="true">
          <el-input
              v-model="couponInfo.couponName"
              placeholder="请输入优惠券名称"
              clearable
              style="width: 100%"
              :readonly="!isEditing"
          ></el-input>
        </el-form-item>

        <!-- 优惠券类型 -->
        <el-form-item label="优惠券类型" :required="true">
          <el-select
              v-model="couponInfo.couponType"
              placeholder="请选择优惠券类型"
              style="width: 100%"
              :disabled="!isEditing"
          >
            <el-option label="礼金" value="FIXED"></el-option>
            <el-option label="折扣" value="PERCENTAGE"></el-option>
            <el-option label="满减" value="THRESHOLD"></el-option>
          </el-select>
        </el-form-item>

        <!-- 折扣金额 -->
        <el-form-item
            v-if="couponInfo.couponType === 'FIXED' || couponInfo.couponType === 'THRESHOLD'"
            label="折扣金额"
            :required="true"
        >
          <el-input-number
              v-model="couponInfo.discountAmount"
              :min="0"
              placeholder="请输入折扣金额"
              style="width: 100%"
              :disabled="!isEditing"
          ></el-input-number>
        </el-form-item>

        <!-- 折扣百分比 -->
        <el-form-item v-if="couponInfo.couponType === 'PERCENTAGE'" label="折扣百分比" :required="true">
          <el-input-number
              v-model="couponInfo.discountPercentage"
              :min="0"
              :max="100"
              placeholder="请输入折扣百分比"
              style="width: 100%"
              :disabled="!isEditing"
          ></el-input-number>
        </el-form-item>

        <!-- 最低消费金额 -->
        <el-form-item v-if="couponInfo.couponType === 'THRESHOLD'" label="最低消费金额" :required="true">
          <el-input-number
              v-model="couponInfo.minPurchaseAmount"
              :min="0"
              placeholder="请输入最低消费金额"
              style="width: 100%"
              :disabled="!isEditing"
          ></el-input-number>
        </el-form-item>

        <!-- 有效期起始 -->
        <el-form-item label="有效期起始" :required="true">
          <el-date-picker
              v-model="couponInfo.validFrom"
              type="datetime"
              placeholder="选择起始时间"
              style="width: 100%"
              :disabled="!isEditing"
          ></el-date-picker>
        </el-form-item>

        <!-- 有效期结束 -->
        <el-form-item label="有效期结束" :required="true">
          <el-date-picker
              v-model="couponInfo.validUntil"
              type="datetime"
              placeholder="选择结束时间"
              style="width: 100%"
              :disabled="!isEditing"
          ></el-date-picker>
        </el-form-item>

        <!-- 状态 -->
        <el-form-item label="状态">
          <el-select
              v-model="couponInfo.status"
              placeholder="请选择状态"
              style="width: 100%"
              :disabled="!isEditing"
          >
            <el-option label="可用" value="ACTIVE"></el-option>
            <el-option label="禁用" value="INACTIVE"></el-option>
          </el-select>
        </el-form-item>

        <!-- 描述 -->
        <el-form-item label="描述">
          <el-input
              v-model="couponInfo.description"
              type="textarea"
              placeholder="请输入描述"
              :rows="3"
              style="width: 100%"
              :readonly="!isEditing"
          ></el-input>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item v-if="isEditing">
          <el-button
              type="primary"
              :disabled="updateCouponDisabled"
              @click="handleUpdateCoupon"
              style="width: 100%"
          >
            保存修改
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.coupon-edit-container {
  max-width: 800px;
  margin: 20px auto;
}

.coupon-card {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>