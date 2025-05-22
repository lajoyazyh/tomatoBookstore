<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox, ElInput, ElButton, ElTable, ElTableColumn, ElCard } from 'element-plus';
import { router } from '../../router';
import { getUsers, blockUser } from '../../api/block';
import type { User } from '../../api/block';

// 权限校验：仅 STAFF（管理员）可访问
const role = sessionStorage.getItem('role');
if (role !== 'STAFF') {
  ElMessage.error('您没有访问权限！');
  router.push('/');
}

const searchQuery = ref<string>('');
const userList = ref<User[]>([]);

/**
 * 获取用户列表，支持搜索关键字
 */

// function fetchUsers() {
//   getUsers({ search: searchQuery.value })
//       .then(res => {
//         if (res.data.code === '200') {
//           userList.value = res.data.data;
//         } else {
//           ElMessage.error('获取用户列表失败：' + res.data.msg);
//         }
//       })
//       .catch(error => {
//         ElMessage.error('请求用户列表出错：' + error.message);
//       });
// }

/**
 * 获取所有用户列表
 */
function fetchUsers() {
  getUsers()
      .then(res => {
        if (res.data.code === '200') {
          ElMessage.info("获取用户列表成功")
          userList.value = res.data.data;
        } else {
          ElMessage.error('获取用户列表失败：' + res.data.msg);
        }
      })
      .catch(error => {
        ElMessage.error('请求用户列表出错：' + error.message);
      });
}

/**
 * 搜索用户
 */
function handleSearch() {
  fetchUsers();
}

/**
 * 拉黑用户
 * @param userId 用户的ID
 */
function handleBlock(userId: number) {
  ElMessageBox.confirm('确定要拉黑该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
      .then(() => {
        blockUser({ userId })
            .then(res => {
              if (res.data.code === '200') {
                ElMessage.success('用户已成功拉黑！');
                fetchUsers(); // 操作成功后刷新列表
              } else {
                ElMessage.error('拉黑失败：' + res.data.msg);
              }
            })
            .catch(error => {
              ElMessage.error('拉黑操作失败：' + error.message);
            });
      })
      .catch(() => {
        // 当用户取消操作时，不做任何处理
      });
}

onMounted(() => {
  fetchUsers();
});
</script>

<template>
  <el-card class="box-card">
    <template #header>
      <div class="card-header">
        <span>用户管理 - 拉黑用户</span>
      </div>
    </template>

    <!-- 搜索区 -->
    <div class="search-bar">
      <el-input
          v-model="searchQuery"
          placeholder="请输入用户名或邮箱"
          clearable
          style="width: 300px;"
      />
      <el-button type="primary" @click="handleSearch" style="margin-left: 10px;">
        搜索
      </el-button>
    </div>

    <!-- 用户列表表格 -->
    <el-table :data="userList" style="width: 100%; margin-top: 20px;">
      <el-table-column prop="id" label="用户ID" width="100" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="role" label="角色" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <span v-if="scope.row.isBlocked">已拉黑</span>
          <span v-else>正常</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button
              type="danger"
              size="small"
              v-if="!scope.row.isBlocked"
              @click="handleBlock(scope.row.id)"
          >
            拉黑
          </el-button>
          <span v-else>—</span>
        </template>
      </el-table-column>
    </el-table>

    <!-- 返回按钮 -->
    <div class="back-button">
      <el-button @click="router.push('/user-management')">返回用户管理主页</el-button>
    </div>
  </el-card>
</template>

<style scoped>
.box-card {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}
.card-header {
  font-size: 20px;
  font-weight: bold;
}
.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.back-button {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
