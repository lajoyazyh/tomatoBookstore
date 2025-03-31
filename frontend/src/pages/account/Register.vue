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
    if (res.data.code === '000') {  //类型守卫，它检查 res.data 对象中是否存在名为 code 的属性
      ElMessage({
        message: "注册成功！请登录账号",
        type: 'success',
        center: true,
      })
      router.push({path: "/login"})
    } else if (res.data.code === '400') {
      ElMessage({
        message: res.data.msg,
        type: 'error',
        center: true,
      })
    }
  })
} // TODO: 我不太确定要求唯一用户名的逻辑要以什么形式实现

</script>

<template>
  <h1>注册</h1>
</template>

<style scoped>

</style>