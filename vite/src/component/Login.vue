<template>
  <div class="login-container">
    <div :class="['login-card', { 'is-loading': isLoading }]">
      <h2 class="login-title">用户登录</h2>

      <form @submit.prevent="handleLogin" class="login-form">
        <div :class="['input-group', { 'is-focused': isUsernameFocused }]">
          <label for="username">账号</label>
          <input
              id="username"
              type="text"
              v-model="username"
              :disabled="isLoading"
              @focus="isUsernameFocused = true"
              @blur="isUsernameFocused = false"
              placeholder="请输入用户名 (admin/user)"
              required
          />
          <span class="input-border"></span>
        </div>

        <div :class="['input-group', { 'is-focused': isPasswordFocused }]">
          <label for="password">密码</label>
          <input
              id="password"
              type="password"
              v-model="password"
              :disabled="isLoading"
              @focus="isPasswordFocused = true"
              @blur="isPasswordFocused = false"
              placeholder="请输入密码 (123456)"
              required
              minlength="6"
          />
          <span class="input-border"></span>
        </div>

        <transition name="fade">
          <div v-if="message" :class="['message', messageType]">
            {{ message }}
          </div>
        </transition>

        <button type="submit" :disabled="isLoading" class="login-button">
          <span v-if="!isLoading">登 录</span>
          <span v-else class="loader"></span>
        </button>

        <p class="mock-info">
          **提示：** 虚假登录。账号：`admin/user`，密码：`123456`。
        </p>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref} from 'vue';

// --- 状态管理 ---
const username = ref('');
const password = ref('');
const isLoading = ref(false);
const message = ref('');
const messageType = ref<'success' | 'error'>('error');

// --- 焦点状态 (用于动效) ---
const isUsernameFocused = ref(false);
const isPasswordFocused = ref(false);

// --- 模拟异步登录函数 ---
const mockLogin = (user: string, pass: string): Promise<boolean> => {
  return new Promise((resolve) => {
    // 模拟网络延迟
    setTimeout(() => {
      // 检查成功条件
      const success = (user === 'admin' || user === 'user') && pass === '123456';
      resolve(success);
    }, 1500); // 1.5秒延迟
  });
};

// --- 登录处理函数 ---
const handleLogin = async () => {
  if (isLoading.value) return;

  // 1. 清除旧消息
  message.value = '';
  isLoading.value = true;

  try {
    // 2. 模拟请求
    const success = await mockLogin(username.value, password.value);

    // 3. 处理结果
    if (success) {
      message.value = '登录成功！欢迎回来。';
      messageType.value = 'success';
      // 可以在这里执行路由跳转等操作
    } else {
      message.value = '登录失败：账号或密码不正确，请重试。';
      messageType.value = 'error';
    }
  } catch (err) {
    // 模拟网络错误/其他异常
    console.error(err);
    message.value = '登录失败：系统异常，请稍后再试。';
    messageType.value = 'error';
  } finally {
    // 4. 结束加载
    isLoading.value = false;
  }
};
</script>

<style scoped lang="scss">
@use "sass:color";
* {
  box-sizing: border-box;
}

// 确保 body/html 没有默认的 margin/padding 导致溢出
// 注意：如果您的项目有全局 CSS 文件，最好在那里设置，但这里为了组件自包含而添加。
:global(html), :global(body) {
  margin: 0;
  padding: 0;
  // 防止背景动效在 body/html 级别引起溢出
  overflow-x: hidden;
}

// --- 颜色变量 ---
$primary-color: #42b983; // Vue 绿色
$primary-light: #5cb795;
$text-color: #333;
$placeholder-color: #999;
$card-bg: #fff;
$shadow-color: rgba(0, 0, 0, 0.1);
$error-color: #e53935;
$success-color: #66bb6a;

// --- 容器样式 (全屏/居中/背景动效) ---
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  // 确保高度占满视口
  min-height: 100vh;
  // 使用 100vw 确保不会出现水平滚动条
  width: 100vw;
  padding: 20px;
  box-sizing: border-box;
  overflow-x: hidden; // 隐藏可能出现的水平滚动条

  // 背景渐变动效
  background: linear-gradient(-45deg, $primary-light, #3498db, $primary-color, #2c3e50);
  background-size: 400% 400%;
  animation: backgroundShift 15s ease infinite;
}

@keyframes backgroundShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

// --- 卡片样式 ---
.login-card {
  width: 100%;
  max-width: 400px;
  padding: 40px;
  background-color: $card-bg;
  border-radius: 12px;
  box-shadow: 0 10px 30px $shadow-color;
  transition: transform 0.3s ease-in-out, opacity 0.3s ease-in-out;
  z-index: 10; // 确保卡片在背景动效之上

  &.is-loading {
    transform: scale(0.98);
    opacity: 0.8;
    pointer-events: none; // 禁用交互
  }

  // 响应式调整
  @media (max-width: 500px) {
    padding: 30px 20px;
    margin: 0 10px;
    max-width: 95%;
  }
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: $text-color;
  font-size: 26px;
  font-weight: 600;
  letter-spacing: 1px;
}

// --- 表单样式 ---
.login-form {
  display: flex;
  flex-direction: column;
}

.input-group {
  position: relative;
  margin-bottom: 30px;

  label {
    display: block;
    font-size: 14px;
    color: $placeholder-color;
    margin-bottom: 8px;
    transition: color 0.3s ease;
  }

  input {
    width: 100%;
    padding: 10px 0;
    font-size: 16px;
    color: $text-color;
    border: none;
    border-bottom: 2px solid #ddd;
    outline: none;
    background: transparent;
    transition: border-color 0.3s ease;

    &::placeholder {
      color: $placeholder-color;
      opacity: 1;
    }

    &:focus {
      border-color: $primary-color;
    }

    &:disabled {
      cursor: not-allowed;
      background-color: #f9f9f9;
    }
  }

  // 输入框下划线的动态效果
  .input-border {
    position: absolute;
    bottom: 0;
    left: 50%;
    width: 0;
    height: 2px;
    background-color: $primary-color;
    transform: translateX(-50%);
    transition: width 0.3s ease-out;
  }

  &.is-focused {
    label {
      color: $primary-color;
    }

    .input-border {
      width: 100%; // 焦点时宽度伸展
    }
  }
}

// --- 按钮样式 ---
.login-button {
  height: 50px;
  background-color: $primary-color;
  color: $card-bg;
  font-size: 18px;
  font-weight: 600;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.1s ease;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  margin-top: 10px;

  &:hover {
    background-color: darken($primary-color, 10%);
  }

  &:active {
    transform: scale(0.98);
  }

  &:disabled {
    background-color: color.adjust($primary-color, $lightness: 20%);
    cursor: not-allowed;
  }
}

// --- 加载动效 (Spinning Loader) ---
.loader {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

// --- 消息提示 ---
.message {
  padding: 12px;
  margin-bottom: 20px;
  border-radius: 6px;
  font-size: 14px;
  text-align: center;
  // 确保它不影响布局
  min-height: 40px;

  &.success {
    background-color: color.adjust($success-color, $lightness: 35%);
    color: $success-color;
    border: 1px solid color.adjust($success-color, $lightness: 20%);
  }

  &.error {
    background-color: color.adjust($error-color, $lightness: 35%);
    color: $error-color;
    border: 1px solid color.adjust($error-color, $lightness: 20%);
  }
}

// --- 消息渐变动效 ---
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

// --- 底部提示信息 ---
.mock-info {
  margin-top: 25px;
  font-size: 12px;
  text-align: center;
  color: $placeholder-color;
  padding-top: 10px;
  border-top: 1px dashed #eee;
}
</style>