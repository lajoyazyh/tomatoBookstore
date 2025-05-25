<script setup lang="ts">
import {onMounted,ref} from 'vue'
import {ElMessage} from "element-plus";
import { getOrders, payForOrder } from "../../api/orders.ts";

if (sessionStorage.getItem("hasPendingOrder") === 'true') {
  ElMessage.error('请先完成待支付订单！');
  sessionStorage.removeItem("hasPendingOrder");
}

type order = {
  orderId: number,
  totalAmount: number,
  paymentMethod: string,
  status: string,
  createTime: string,
}
const orders = ref<order[]>([]);

onMounted(async () => {
  try {
    const response = await getOrders()
    if (response.data.code === '200') {
      orders.value = response.data.data.orders.map((order:any) => ({
        orderId: order.orderId,
        totalAmount: order.totalAmount,
        paymentMethod: order.paymentMethod,
        status: order.status,
        createTime: order.createTime,
      }))
    } else {
      ElMessage.error(response.data.msg);
    }
  } catch (error) {
    ElMessage.error('获取订单信息失败！');
    console.log(error);
  }
})

function handlePayment(orderId: number) {
  payForOrder(orderId).then(res => {
    if (res.data.code === '200') {
      const pfm = res.data.data?.paymentForm;
      if (!pfm) {
        ElMessage.error('支付表单不存在');
        return;
      }

      const container = document.createElement('div');
      container.style.display = 'none';
      container.innerHTML = pfm;
      document.body.appendChild(container);

      const form = container.querySelector('form');
      if (form) {
        form.submit();
        document.body.removeChild(container); // 提交后清理掉，防止页面堆垃圾
      } else {
        ElMessage.error('支付表单格式错误');
      }
    } else {
      ElMessage.error(res.data.msg || '支付请求失败');
    }
  }).catch(err => {
    ElMessage.error('网络异常，请稍后重试');
    console.error('支付接口异常', err);
  })
}


// 转换支付状态和支付方式
function getStatusText(status: string): string {
  switch (status) {
    case 'PENDING':
      return '待支付';
    case 'SUCCESS':
      return '已支付';
    case 'FAILED':
      return '支付失败';
    case 'TIMEOUT':
      return '超时未支付';
    default:
      return status;
  }
}

function getPaymentMethodText(paymentMethod: string): string {
  switch (paymentMethod) {
    case 'ALIPAY':
      return '支付宝';
    default:
      return paymentMethod;
  }
}
</script>

<template>
  <div class="orders-container">
    <h2>我的订单</h2>

    <div v-if="orders.length > 0" class="orders-list">
      <div class="order-item" v-for="order in orders" :key="order.orderId">
        <div class="order-header">
          <span class="order-id">订单号: {{ order.orderId }}</span>
          <span class="order-status">{{ getStatusText(order.status) }}</span>
        </div>

        <div class="order-content">
          <div class="order-info">
            <p>总金额: ¥{{ order.totalAmount.toFixed(2) }}</p>
            <p>支付方式: {{ getPaymentMethodText(order.paymentMethod) }}</p>
            <p>创建时间: {{ order.createTime }}</p>
          </div>
        </div>

        <div class="order-actions">
          <button
              v-if="getStatusText(order.status) === '待支付'"
              class="pay-btn"
              @click="handlePayment(order.orderId)"
          >
            立即支付
          </button>
        </div>
      </div>
    </div>

    <div v-else class="empty-orders">
      <p>您还没有订单</p>
      <p>去购物吧！</p>
    </div>
  </div>
</template>

<style scoped>
.orders-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 50px;
}

h2 {
  margin-bottom: 20px;
  text-align: center;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.order-item {
  border: 1px solid #eaeaea;
  border-radius: 8px;
  padding: 15px;
  background-color: #fff;
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eaeaea;
}

.order-id {
  font-weight: bold;
}

.order-status {
  color: #4a90e2;
}

.order-content {
  margin-bottom: 15px;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.order-actions {
  text-align: right;
}

.pay-btn {
  background-color: #4a90e2;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.pay-btn:hover {
  background-color: #3a7bc8;
}

.empty-orders {
  text-align: center;
  padding: 40px 0;
  color: #666;
}

.empty-orders p {
  margin: 10px 0;
}
</style>