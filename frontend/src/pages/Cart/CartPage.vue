<script setup lang="ts">
import {ref, onMounted} from 'vue'
import { deleteProduct, updateCartItem, getCart } from "../../api/cart.ts";
import { checkoutOrder, payForOrder } from "../../api/orders.ts";
import type { addressInfo, checkoutOrderInfo } from "../../api/orders.ts";
import { ElMessage, ElMessageBox } from "element-plus";
import {router} from "../../router";

type item = {
  cartItemId: number,
  productId: number,
  title: string,
  price: number,
  description: string,
  cover: string,
  detail: string,
  quantity: number,
}
const cartItems = ref<item[]>([]);
const address = ref<addressInfo>({
  name: '',
  telephone: '',
  postcode: '',
  address: '',
});
const checkout = ref<checkoutOrderInfo>({
  cartItemIds: [],
  shipping_address: address.value,
  payment_method: 'ALIPAY',
})

onMounted(async () => {
  try {
    const response = await getCart();
    if (response.data.code === '200') {
      cartItems.value = response.data.data.items.map((item: any) => ({
        cartItemId: item.cartItemId,
        productId: item.productId,
        title: item.title,
        price: item.price,
        description: item.description,
        cover: item.cover,
        detail: item.detail,
        quantity: item.quantity,
      }))
    } else {
      ElMessage.error(response.data.msg);
    }
  } catch (error) {
    ElMessage.error('获取购物车信息失败！');
    console.log(error);
  }
})

function goToOrders() {
  router.push("/orders");
}

const deleteCartItem = (item: item) => {
  deleteProduct(item.cartItemId).then(() => {
    ElMessage.success('商品已从购物车中删除');
    // 使用 splice 删除指定的元素
    const index = cartItems.value.findIndex((i) => i.cartItemId === item.cartItemId);
    if (index !== -1) {
      cartItems.value.splice(index, 1);
    }
  }).catch(error => {
    ElMessage.error('删除商品失败');
    console.log(error);
  });
};

const orderId = ref();
function makeCheckout() {
  checkout.value.cartItemIds = cartItems.value.map(item => item.cartItemId);
  checkout.value.shipping_address = address.value;
}
function handleOrder() {
  makeCheckout();
  checkoutOrder(checkout.value).then(res => {
    if (res.data.code === '200') {
      orderId.value = res.data.data.orderId;
      router.push('/orders');
    } else {
      ElMessage.error(res.data.msg);
    }
  })
}

function showCheckoutConfirmation() {
  makeCheckout();

  // 总金额
  const totalAmount = cartItems.value.reduce((acc, item) => acc + (item.price * item.quantity), 0);

  // 生成带有唯一ID的输入框
  const messageHtml = `
    <div style="display: flex; gap: 20px;">
      <!-- 左侧商品列表 -->
      <div style="flex: 1; border-right: 1px solid #ddd; padding-right: 20px;">
        <h3>商品清单</h3>
        <ul style="list-style-type: none; padding: 0;">
          ${cartItems.value.map(item => `
            <li style="margin-bottom: 10px;">
              <strong>${item.title}</strong> - ¥${item.price.toFixed(2)} × ${item.quantity}
            </li>
          `).join('')}
        </ul>
        <div style="margin-top: 20px; font-weight: bold;">
          总计: ¥${totalAmount.toFixed(2)}
        </div>
      </div>

      <!-- 右侧地址表单 -->
      <div style="flex: 1;">
        <h3>配送地址</h3>
        <div>
          <label>收货人:</label>
          <input type="text" id="nameInput" style="width: 100%; margin-bottom: 10px;">

          <label>手机号:</label>
          <input type="text" id="telephoneInput" style="width: 100%; margin-bottom: 10px;">

          <label>邮政编码:</label>
          <input type="text" id="postCodeInput" style="width: 100%; margin-bottom: 10px;">

          <label>详细地址:</label>
          <textarea id="addressTextarea" style="width: 100%; height: 80px; margin-bottom: 10px;"></textarea>
        </div>
      </div>
    </div>
  `;

  ElMessageBox({
    title: '确认订单',
    message: messageHtml,
    dangerouslyUseHTMLString: true,
    showCancelButton: true,
    cancelButtonText: '取消',
    confirmButtonText: '确认订单',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        // 提交前手动获取最新值（确保同步）
        const telephoneInput = document.getElementById('telephoneInput') as HTMLInputElement;
        const postCodeInput = document.getElementById('postCodeInput') as HTMLInputElement;
        const addressTextarea = document.getElementById('addressTextarea') as HTMLTextAreaElement;

        // 更新address的值
        address.value.telephone = telephoneInput.value;
        address.value.postcode = postCodeInput.value;
        address.value.address = addressTextarea.value;

        // 验证逻辑
        if (!/^1\d{10}$/.test(address.value.telephone)) {
          ElMessage.error('请输入有效的手机号码');
          return;
        }
        if (!/^\d{6}$/.test(address.value.postcode)) {
          ElMessage.error('请输入有效的邮政编码');
          return;
        }
        if (!address.value.address) {
          ElMessage.error('请输入详细地址');
          return;
        }
        done();
        handleOrder()
      } else {
        done();
      }
    }
  });

  // 弹窗显示后，设置初始值并绑定input事件
  setTimeout(() => {
    const nameInput = document.getElementById('nameInput') as HTMLInputElement;
    const telephoneInput = document.getElementById('telephoneInput') as HTMLInputElement;
    const postCodeInput = document.getElementById('postCodeInput') as HTMLInputElement;
    const addressTextarea = document.getElementById('addressTextarea') as HTMLTextAreaElement;

    // 设置初始值
    if (nameInput && telephoneInput && postCodeInput && addressTextarea) {
      nameInput.value = address.value.name;
      telephoneInput.value = address.value.telephone;
      postCodeInput.value = address.value.postcode;
      addressTextarea.value = address.value.address;

      // 绑定input事件实时更新address
      nameInput.addEventListener('input', (e) => {
        address.value.name = (e.target as HTMLInputElement).value;
      });
      telephoneInput.addEventListener('input', (e) => {
        address.value.telephone = (e.target as HTMLInputElement).value;
      });
      postCodeInput.addEventListener('input', (e) => {
        address.value.postcode = (e.target as HTMLInputElement).value;
      });
      addressTextarea.addEventListener('input', (e) => {
        address.value.address = (e.target as HTMLTextAreaElement).value;
      });
    }
  }, 0);
}
</script>

<template>
  <div class="cart-container">
    <div class="nav-bar">
      <h2>购物车</h2>
      <button class="view-orders-btn" @click="goToOrders">查看订单</button>
    </div>

    <div v-if="cartItems.length > 0" class="cart-items">
      <div class="cart-item" v-for="item in cartItems" :key="item.cartItemId">
        <img :src="item.cover" :alt="item.title" class="item-image">

        <div class="item-info">
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
          <div class="item-price">¥{{ item.price.toFixed(2) }}</div>
        </div>

        <div class="quantity-control">
          <button :disabled="item.quantity <= 1" @click="() => {
            if (item.quantity > 1) {
              item.quantity--;
              updateCartItem({ cartItemId: item.cartItemId, quantity: item.quantity });
            }
          }">-</button>
          <span>{{ item.quantity }}</span>
          <button @click="() => {
            item.quantity++;
            updateCartItem({ cartItemId: item.cartItemId, quantity: item.quantity });
          }">+</button>
        </div>

        <button class="delete-btn" @click="deleteCartItem(item)">删除</button>
      </div>
    </div>

    <div v-else class="empty-cart">
      <p>购物车是空的</p>
      <p>前往商店选购商品</p>
    </div>

    <div v-if="cartItems.length > 0" class="checkout-section">
      <div class="total-price">
        总计: ¥{{ cartItems.reduce((acc, item) => acc + (item.price * item.quantity), 0).toFixed(2) }}
      </div>
      <button class="checkout-btn" @click="showCheckoutConfirmation">确认订单</button>
    </div>
  </div>
</template>

<style scoped>
.cart-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 50px;
}

.nav-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.view-orders-btn {
  background-color: #4a56e2;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.view-orders-btn:hover {
  background-color: #3a46d5;
}

h2 {
  margin-bottom: 20px;
  text-align: center;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 1px solid #eaeaea;
  border-radius: 8px;
  background-color: #fff;
}

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 15px;
}

.item-info {
  flex-grow: 1;
}

.item-info h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
}

.item-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.item-price {
  margin-top: 5px;
  color: #e53e3e;
  font-weight: bold;
}

.quantity-control {
  display: flex;
  align-items: center;
  margin: 0 15px;
}

.quantity-control button {
  width: 30px;
  height: 30px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

.quantity-control button:hover {
  background-color: #eaeaea;
}

.quantity-control button:disabled {
  background-color: #eaeaea;
  cursor: not-allowed;
}

.quantity-control span {
  margin: 0 10px;
  font-size: 14px;
}

.delete-btn {
  background-color: #e53e3e;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.delete-btn:hover {
  background-color: #c53030;
}

.checkout-section {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-price {
  font-size: 18px;
  font-weight: bold;
}

.checkout-btn {
  background-color: #4a56e2;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.checkout-btn:hover {
  background-color: #3a46d5;
}

.empty-cart {
  text-align: center;
  padding: 40px 0;
  color: #666;
}

.empty-cart p {
  margin: 10px 0;
}
</style>