<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {ElMessage} from "element-plus";
import {router} from '../../router';
import { getTypeText, getStatusText, formatStringToDate } from "../../api/coupon.ts";
import { getAllCoupons, deleteTheCoupon, updateCouponStatus } from "../../api/coupon.ts";

// 删除优惠券需要STAFF权限
const role = sessionStorage.getItem('role');

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
        couponId: coupon.id,
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

function updateStatus(id: number, status: string) {
  try {
    updateCouponStatus(
    {couponId: id, status: status}).then(res => {
      if (res.data.code === '200') {
        ElMessage.success('更新状态成功！');
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
  try {
    deleteTheCoupon(id).then(res => {
      if (res.data.code === '200') {
        ElMessage.success('删除成功！');
      } else {
        ElMessage.error(res.data.msg)
      }
    })
  } catch (error) {
    ElMessage.error('删除失败！');
    console.log(error);
  }
}

</script>

<template>

</template>

<style scoped>

</style>