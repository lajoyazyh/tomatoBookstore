<script setup lang="ts">
import {ElForm, ElFormItem, ElMessage, ElMessageBox} from "element-plus"
import { ref, computed, onMounted } from 'vue'
import {router} from '../../router'
import { userInfo, userInfoUpdate } from '../../api/accounts'
import { uploadImage } from "../../api/images.ts";
import type { UpdateInfo } from "../../api/accounts";

const isEditing = ref(false); // 控制编辑状态

// 更改用户信息时，不必也不能更改username，username只作为标志
const username = sessionStorage.getItem('username') || ''
const password = ref('')
const confirmPassword = ref('')
const name = ref('')
const currentFile = ref(null) // avatar file
const avatar = ref('')      // avatar url
const role = ref('')
const telephone = ref('')
const email = ref('')
const location = ref('')

const isPasswordIdentical = ref(computed(() => password.value == confirmPassword.value))
const isValidTelephone = computed(() => /^1\d{10}$/.test(telephone.value))
const isValidEmail = computed(() =>
    /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email.value))

const hasChangedInfo = computed(() =>
  !!password.value || !!name.value || !!avatar.value || !!role.value || !!telephone.value || !!email.value || !!location.value)
const isLegalTel = computed(() => !telephone.value || isValidTelephone.value)
const isLegalEmail = computed(() => !email.value || isValidEmail.value)
const updateDisabled = computed(() => {
  return !(hasChangedInfo.value && isPasswordIdentical.value && isLegalTel.value && isLegalEmail.value)
})

// 是否改变密码（改变密码需要重新登录）
const passwordChanged = ref(computed(() => !!password.value && !!confirmPassword.value && isPasswordIdentical.value))

function getUserInfo() {
  userInfo(username).then((res) => {
    if (res.data.code == '200') {
      name.value = res.data.data.name
      avatar.value = res.data.data.avatar
      role.value = res.data.data.role
      telephone.value = res.data.data.telephone
      email.value = res.data.data.email
      location.value = res.data.data.location

      sessionStorage.setItem('role', res.data.data.role)
    }
    else if (res.data.code == '400') {
      ElMessage.error(res.data.message)
    }
  })
}

onMounted(async () => {
  getUserInfo()

  // 如果需要钩子获取其他信息在这里补充
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

function createUpdateInfo(): UpdateInfo {
  const updateInfo: UpdateInfo = {
    username: username,
    avatar: avatar.value,
    telephone: telephone.value,
    email: email.value,
    location: location.value,
  };
  // 检查只有修改了密码才会提交，不能删除的部分无法改为空串
  if (!!password.value) updateInfo.password = password.value;
  if (!!name.value) updateInfo.name = name.value;
  if (!!role.value) updateInfo.role = role.value;

  return updateInfo;
}

function handleUpdate() {
  userInfoUpdate(createUpdateInfo()).then((res) => {
    if(res.data.code == '200') {
      ElMessage({
        customClass: 'customMessage',
        type: 'success',
        message: '更新成功！',
      })
      if (passwordChanged.value) { relogin() }
      else { getUserInfo() }
    } else if (res.data.code === '400') {
      ElMessage({
        customClass: 'customMessage',
        type: 'error',
        message: res.data.msg,
      })
    }
  })
  toggleEdit();
}

function relogin() {
  password.value = ''
  confirmPassword.value = ''
  ElMessageBox.alert(
      `请重新登录`,
      '修改成功',
      {
        customClass: "customDialog",
        confirmButtonText: '跳转到登录',
        type: "success",
        showClose: false,
        roundButton: true,
        center: true
      }).then(() => router.push({path: "/login"}))
}

function toggleEdit() {
  isEditing.value = !isEditing.value;
}

</script>

<template>
  <el-main class="main-container" v-if="username">
    <el-card class="info-card">
      <div class="card-header">
        <h2>个人信息</h2>
      </div>

      <el-form>
        <el-form-item label="用户名">
          <el-input v-model="username" :disabled="true" placeholder="用户名" required></el-input>
        </el-form-item>

        <el-form-item label="姓名">
          <el-input v-model="name" :disabled="!isEditing" placeholder="请输入姓名" required></el-input>
        </el-form-item>

        <el-form-item label="身份">
          <el-select v-model="role" :disabled="!isEditing" placeholder="请选择身份" style="width: 100%" required>
            <el-option value="CUSTOMER" label="顾客"></el-option>
            <el-option value="STAFF" label="商家"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="电话">
          <el-input v-model="telephone" :disabled="!isEditing" placeholder="请输入合法电话号码"></el-input>
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="email" :disabled="!isEditing" placeholder="请输入合法邮箱"></el-input>
        </el-form-item>

        <el-form-item label="位置">
          <el-input v-model="location" :disabled="!isEditing" placeholder="请输入位置"></el-input>
        </el-form-item>

        <el-form-item label="头像">
          <el-upload
              class="avatar-uploader"
              :auto-upload="false"
              :on-change="handleFileChange"
              :show-file-list="false"
              accept="image/*"
              :disabled="!isEditing"
          >
            <img v-if="avatar" :src="avatar" class="avatar" />
            <el-button v-else type="primary" :disabled="!isEditing">选择头像</el-button>
          </el-upload>
        </el-form-item>

        <el-form-item label="密码">
          <el-input
              type="password"
              v-model="password"
              placeholder="请输入密码"
              :disabled="!isEditing"
              required
          ></el-input>
        </el-form-item>

        <el-form-item label="确认密码">
          <el-input
              type="password"
              v-model="confirmPassword"
              placeholder="请再次输入密码"
              :disabled="!isEditing"
              required
          ></el-input>
          <p v-if="!isPasswordIdentical" class="error-message">密码不一致</p>
        </el-form-item>

        <el-form-item>
          <div v-if="!isEditing">
            <el-button type="primary" @click="toggleEdit">编辑</el-button>
          </div>
          <div v-else>
            <el-button type="success" @click="handleUpdate" :disabled="updateDisabled">保存</el-button>
            <el-button type="warning" @click="toggleEdit">取消</el-button>
          </div>
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
  height: 100vh;
  padding: 20px;
}

.info-card {
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