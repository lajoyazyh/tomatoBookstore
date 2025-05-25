<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {ElMessage} from "element-plus";
import { getAllAdvertisement } from "../api/advertisement.ts";
import { formatStringToDate, getStatusText, getTypeText, getAllCoupons, receiveCoupon } from "../api/coupon.ts";
import {router} from '../router'

const role = sessionStorage.getItem('role');

async function handleRefreshAndCheck() {
  if (!sessionStorage.getItem('alreadyRefreshed')) {
    sessionStorage.setItem('alreadyRefreshed', 'true');
    router.go(0);
  } else {
    sessionStorage.removeItem('alreadyRefreshed');
  }

  if (!sessionStorage.getItem('token')) {
    console.log(!sessionStorage.getItem('token'));
    ElMessage.error("未登录！");
    await router.push('/login');
  }
}

type Advertisement = {
  id: number;
  title: string;
  content?: string;
  imgUrl?: string;
  productId: number;
  isEditing?: boolean; // 新增编辑状态
  // 商品名称从商品的get方法获取
  productName?: string;
}
const advertisements = ref<Array<Advertisement>>([]);
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

async function getAdvertisements() {
  try {
    const response = await getAllAdvertisement();
    if (response.data.code == '200') {
      advertisements.value = response.data.data.map((advertisement: any) => ({
        id: advertisement.id,
        title: advertisement.title,
        content: advertisement.content,
        imgUrl: advertisement.imgUrl,
        productId: advertisement.productId,
      }));
    } else {
      ElMessage.error(response.data.msg);
    }
  } catch (error) {
    ElMessage.error('获取广告信息失败！');
    console.log(error);
  }
}
async function getCoupons() {
  try {
    const response = await getAllCoupons();
    if (response.data.code == '200') {
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
    } else {
      ElMessage.error(response.data.msg);
    }
  } catch (error) {
    ElMessage.error('获取优惠券信息失败！');
    console.log(error);
  }
}
onMounted(() => {
  handleRefreshAndCheck().then(() => {
    getAdvertisements();
    getCoupons();
  })
});

function handleMoveToProduct(productId: number) {
  router.push(`productDetail/${productId}`)
}
function handleReceiveCoupon(couponId: number) {
  try {
    receiveCoupon(couponId).then(res => {
      if (res.data.code == '200') {
        ElMessage.success('领取成功');
        coupons.value = coupons.value.filter(c => c.couponId !== couponId);
      } else {
        ElMessage.error(res.data.msg);
      }
    })
  } catch (error) {
    ElMessage.error('获取失败！');
    console.log(error);
  }
}

const activeAdIndex = ref(0);
const activeCouponIndex = ref(0);
const adAspectRatio = ref(16 / 9); // 默认宽高比
function nextAd() {
  activeAdIndex.value = (activeAdIndex.value + 1) % advertisements.value.length;
}
function prevAd() {
  activeAdIndex.value = (activeAdIndex.value - 1 + advertisements.value.length) % advertisements.value.length;
}
function nextCoupon() {
  activeCouponIndex.value = (activeCouponIndex.value + 1) % coupons.value.length;
}

function prevCoupon() {
  activeCouponIndex.value = (activeCouponIndex.value - 1 + coupons.value.length) % coupons.value.length;
}
</script>

<template>
  <div class="main-container">
    <!-- 广告轮播 -->
    <div class="advertisement-carousel" v-if="advertisements.length > 0">
      <div class="ad-navigation">
        <button @click="prevAd">&lt;</button>
        <button @click="nextAd">&gt;</button>
      </div>
      <div class="ad-item" v-if="advertisements[activeAdIndex]">
        <div class="ad-content">
          <img
              v-if="advertisements[activeAdIndex].imgUrl"
              :src="advertisements[activeAdIndex].imgUrl"
              alt="广告图片"
              @click="handleMoveToProduct(advertisements[activeAdIndex].productId)"
          >
          <div class="ad-text" @click="handleMoveToProduct(advertisements[activeAdIndex].productId)">
            <h3>{{ advertisements[activeAdIndex].title }}</h3>
            <p>{{ advertisements[activeAdIndex].content }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 优惠券展示 -->
    <div class="coupon-carousel" v-if="coupons.length > 0">
      <div class="coupon-navigation">
        <button @click="prevCoupon">&lt;</button>
        <button @click="nextCoupon">&gt;</button>
      </div>
      <div class="coupon-item" v-if="coupons[activeCouponIndex]">
        <div class="coupon-content">
          <h3>{{ coupons[activeCouponIndex].couponName }}</h3>
          <p>类型: {{ getTypeText(coupons[activeCouponIndex].couponType) }}</p>
          <p>状态: {{ getStatusText(coupons[activeCouponIndex].status) }}</p>
          <p v-if="coupons[activeCouponIndex].couponType === 'FIXED' || coupons[activeCouponIndex].couponType === 'THRESHOLD'">
            优惠金额: ¥{{ coupons[activeCouponIndex].discountAmount }}
          </p>
          <p v-if="coupons[activeCouponIndex].couponType === 'PERCENTAGE'">
            折扣百分比: {{ coupons[activeCouponIndex].discountPercentage }}%
          </p>
          <p v-if="coupons[activeCouponIndex].couponType === 'THRESHOLD'">
            最低消费金额: ¥{{ coupons[activeCouponIndex].minPurchaseAmount }}
          </p>
          <p>有效期: {{ new Date(coupons[activeCouponIndex].validFrom).toLocaleDateString() }} 至 {{ new Date(coupons[activeCouponIndex].validUntil).toLocaleDateString() }}</p>
          <button @click="handleReceiveCoupon(coupons[activeCouponIndex].couponId)">领取优惠券</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.main-container {
  padding: 20px;
  max-width: 960px;
  margin: 0 auto;
}

/* 广告轮播样式 */
.advertisement-carousel {
  position: relative;
  margin-bottom: 30px;
}

.ad-item {
  width: 100%;
  margin: 20px 0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.ad-content {
  position: relative;
  width: 100%;
  height: 540px;    /* 广告框高度 */
}

.ad-content img {
  width: 100%;
  height: 100%;
  object-fit: contain; /* 图片按原始比例缩放，同时适应容器 */
  cursor: pointer;
}

.ad-text {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.6); /* 半透明黑色背景 */
  color: white;
  padding: 15px;
  box-sizing: border-box;
  cursor: pointer;
}

.ad-text h3 {
  margin-top: 0;
  margin-bottom: 5px;
}

.ad-navigation {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}

.ad-navigation button {
  padding: 6px 12px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  margin: 0 400px;
  cursor: pointer;
}

/* 优惠券轮播样式 */
.coupon-carousel {
  position: relative;
}

.coupon-item {
  background-color: #fff8e1;
  border-left: 4px solid #ff9800;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 4px;
}

.coupon-content {
  margin-bottom: 15px;
}

.coupon-content h3 {
  margin-top: 0;
  color: #ff9800;
}

.coupon-content p {
  margin: 8px 0;
}

.coupon-content button {
  background-color: #ff9800;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.coupon-navigation {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.coupon-navigation button {
  padding: 6px 12px;
  background-color: #ff9800;
  color: white;
  border: none;
  border-radius: 4px;
  margin: 0 400px 20px;
  cursor: pointer;
}
</style>
