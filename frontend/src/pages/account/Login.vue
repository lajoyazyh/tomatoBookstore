<script setup lang="ts">
import {ElForm, ElFormItem, ElMessage} from "element-plus"
import {ref, computed} from 'vue'
import {router} from '../../router'
import {userInfo, userLogin} from "../../api/accounts.ts"

// 输入框值（需要在前端拦截不合法输入：是否为空+额外规则）
const username = ref('')
const password = ref('')

// 用户名是否为空
const hasUsernameInput = computed(() => username.value != '')
// 密码是否为空
const hasPasswordInput = computed(() => password.value != '')

// 登录按钮可用性
const loginDisabled = computed(() => {
  return !(hasUsernameInput.value && hasPasswordInput.value)
})

// 登录按钮触发
function handleLogin() {
  userLogin({
    username: username.value,
    password: password.value
  }).then(res => {
    if (res.data.code === '200') {
      ElMessage({
        message: "登录成功！",
        type: 'success',
        center: true,
      })
      const token = res.data.data
      sessionStorage.setItem('token', token)

      userInfo(username.value).then(res => {
        sessionStorage.setItem('username', res.data.data.username)

        router.push({path: "/dashboard"})
      })
    } else if (res.data.code === '400') {
      ElMessage({
        message: res.data.msg,
        type: 'error',
        center: true,
      })
      password.value = ''
    }
  })
}
</script>


<template>
  <el-main class="main-frame bgimage">
    <el-card class="login-card">
      <div>
        <h1>登入您的账户</h1>
        <el-form>
          <el-form-item>
            <label v-if="!hasUsernameInput" for="username">注册用户名</label>
            <label v-else for="username">注册用户名</label>
            <el-input id="username" type="text" v-model="username"
                      required :class="{'error-warn-input' :(hasUsernameInput)}"
                      placeholder="请输入用户名"/>
          </el-form-item>

          <el-form-item>
            <label for="password">账户密码</label>
            <el-input id="password" type="password" v-model="password"
                      required
                      placeholder="••••••••"/>
          </el-form-item>

          <span class="button-group">
              <el-button @click.prevent="handleLogin" :disabled="loginDisabled"
                         type="primary">登入</el-button>
              <router-link to="/register" v-slot="{navigate}">
                <el-button @click="navigate">去注册</el-button>
              </router-link>
          </span>
        </el-form>
      </div>
    </el-card>
  </el-main>
</template>


<style scoped>
.main-frame {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  padding: 20px;
}

.bgimage {
  background-image: url("../../assets/shopping-1s-1084px.svg");
}

.login-card {
  width: 60%;
  padding: 10px;
}

.error-warn {
  color: red;
}

.error-warn-input {
  --el-input-focus-border-color: red;
}

.button-group {
  padding-top: 10px;
  display: flex;
  flex-direction: row;
  gap: 30px;
  align-items: center;
  justify-content: right;
}
</style>
