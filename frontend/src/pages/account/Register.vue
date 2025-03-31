<script setup lang="ts">
import { ref, computed } from 'vue'
import {ElMessage} from "element-plus";
import { router } from '../../router'
import { userRegister } from '../../api/accounts'
import { uploadImage } from "../../api/images";

// 必需输入框值
const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const name = ref('')
const role = ref('')
// 非必需输入框值
const currentFile = ref(null)  // avatar file
const avatar = ref('')         // avatar url
const telephone = ref('')
const email = ref('')
const location = ref('')

// 输入框值判断
const hasUsername = computed(() => !!username.value)
const hasPassword = computed(() => !!password.value)
const hasConfirmPassword = computed(() => !!confirmPassword.value)
const hasName = computed(() => !!name.value)
const hasRoleChosen = computed(() => !!role.value)
// 密码二次确认
const isPasswordIdentical = computed(() => password.value == confirmPassword.value)
// 若输入电话号码，需要符合格式
const hasTelephone = computed(() => !!telephone.value)
const isValidTelephone = computed(() => /^1\d{10}$/.test(telephone.value))
// 若输入邮箱，需要符合格式
const hasEmail = computed(() => !!email.value)
const isValidEmail = computed(() =>
    /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email.value))

// 最终注册按钮可用性
const hasNecessaryValue = computed(() =>
  hasUsername.value && hasPassword.value && hasConfirmPassword.value && hasName.value && hasRoleChosen.value)
const registerDisabled = computed(() => {
  if(hasTelephone.value && !isValidTelephone.value) { return true }
  if(hasEmail.value && !isValidEmail.value) { return true }
  return !hasNecessaryValue;
})

function handleFileChange(file: any) {
  const formData = new FormData();
  formData.append('file', file.raw);

  uploadImage(formData).then(res => {
    if (res.data.code === '000') {
      avatar.value = res.data.result; // 存储上传的图片 URL
      currentFile.value = file; // 存储当前文件
      ElMessage.success('文件上传成功！');
    } else {
      ElMessage.error(res.data.msg || '文件上传失败，请稍后再试！');
    }
  }).catch(error => {
    ElMessage.error('文件上传失败：' + error.message);
  });
}

function handleRegister() {
  userRegister({
    username: username.value,
    password: password.value,
    name: name.value,
    avatar: avatar.value,
    role: role.value,
    telephone: telephone.value,
    email: email.value,
    location: location.value,
  }).then(res => {
    if (res.data.code === '200') {  //类型守卫，它检查 res.data 对象中是否存在名为 code 的属性
      ElMessage({
        message: "注册成功！请登录账号",
        type: 'success',
        center: true,
      })
      router.push({path: "/login"})
    } else if (res.data.code === '400') {
      ElMessage({
        message: "用户名已经存在！",
        type: 'error',
        center: true,
      })
    } else if (res.data.code === '000') {
      ElMessage({
        message: res.data.msg,
        type: 'error',
        center: true,
      })
    }
  })
}

</script>

<template>
  <el-main class="main-container">
    <el-card class="register-card">
      <div class="card-header">
        <h2>创建账户</h2>
      </div>

      <el-form>
        <el-form-item label="用户名">
          <el-input v-model="username" placeholder="请输入用户名" required></el-input>
        </el-form-item>

        <el-form-item label="姓名">
          <el-input v-model="name" placeholder="请输入姓名" required></el-input>
        </el-form-item>

        <el-form-item label="身份">
          <el-select v-model="role" placeholder="请选择身份" style="width: 100%" required>
            <el-option value="CUSTOMER" label="顾客"></el-option>
            <el-option value="STAFF" label="商家"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="密码">
          <el-input
              type="password"
              v-model="password"
              placeholder="请输入密码"
              required
          ></el-input>
        </el-form-item>

        <el-form-item label="确认密码">
          <el-input
              type="password"
              v-model="confirmPassword"
              placeholder="请再次输入密码"
              required
          ></el-input>
          <p v-if="hasConfirmPassword && !isPasswordIdentical" class="error-message">密码不一致</p>
        </el-form-item>

        <el-form-item label="头像">
          <el-upload
              class="avatar-uploader"
              :auto-upload="false"
              :on-change="handleFileChange"
              :show-file-list="false"
              accept="image/*"
          >
            <img v-if="avatar" :src="avatar" class="avatar" />
            <el-button v-else type="primary">选择头像</el-button>
          </el-upload>
        </el-form-item>

        <el-form-item label="手机号">
          <el-input v-model="telephone" placeholder="请输入手机号"></el-input>
          <p v-if="hasTelephone && !isValidTelephone" class="error-message">手机号格式不正确</p>
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="email" placeholder="请输入邮箱"></el-input>
          <p v-if="hasEmail && !isValidEmail" class="error-message">邮箱格式不正确</p>
        </el-form-item>

        <el-form-item label="位置">
          <el-input v-model="location" placeholder="请输入位置"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button
              type="success"
              @click="handleRegister"
              :disabled="registerDisabled"
              style="width: 100%"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </el-main>
</template>

<style scoped>
.main-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 90vh;
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 500px;
  padding: 20px;
}

.card-header {
  text-align: center;
  margin-bottom: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.error-message {
  color: red;
  font-size: 12px;
  margin-top: 5px;
}

.avatar-uploader {
  text-align: center;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}
</style>