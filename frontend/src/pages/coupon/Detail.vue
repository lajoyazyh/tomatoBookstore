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
const UpdateCouponDisabled = computed(() => {
  return !(role == 'STAFF' && hasNecessaryInfo.value && isValidTypeInfo.value && isValidTimeInfo.value)
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

</template>

<style scoped>

</style>