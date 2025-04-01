<script setup lang="ts">
import {ElForm, ElFormItem, ElMessage} from "element-plus"
import { ref, computed, onMounted } from 'vue'
import {router} from '../../router'
import { userInfo,userInfoUpdate } from '../../api/accounts'

// 更改用户信息时，不必也不能更改username，username只作为标志
const username = sessionStorage.getItem('username')
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
const hasChangedInfo = computed(() =>
  !!password.value || !!name.value || !!avatar.value || !!role.value || !!telephone.value || !!email.value || !!location.value)
const updateDisabled = computed(() => {
  return !(hasChangedInfo.value && isPasswordIdentical.value)
})

function getUserInfo() {
  if(!username) return
  userInfo(username).then((res) => {
    name.value = res.data.result.name
    avatar.value = res.data.result.avatar
    role.value = res.data.result.role
    telephone.value = res.data.result.telephone
    email.value = res.data.result.email
    location.value = res.data.result.location
  })
}

onMounted(async () => {
  getUserInfo()
})

function handleUpdate() {

}

</script>

<template>
  <h1>个人信息</h1>
</template>

<style scoped>

</style>