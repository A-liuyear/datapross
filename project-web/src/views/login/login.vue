<template>
  <div class="logincontainer">
    <div class="welcome-message">欢迎使用智能数据处理平台</div>

    <el-form class="loginform" :model="loginModel" ref="loginform" :rules="rules" :inline="false" size="large">

      <el-form-item>
        <div class="logintitle">系统登录</div>
      </el-form-item>

      <el-form-item prop="userName">
        <el-input placeholder="请输入账号" v-model="loginModel.userName"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input placeholder="请输入密码" type="password" v-model="loginModel.password"></el-input>
      </el-form-item>
      <el-form-item prop="code">
        <el-row style="width:100%">
          <el-col :span="16" :offset="0">
            <el-input placeholder="请输入验证码" v-model="loginModel.code" @keydown.enter="loginBtn"></el-input>
          </el-col>
          <el-col :span="8" :offset="0" style="padding-left: 10px;">
            <img class="images" @click="getImageBtn" :src="imgSrc" alt="验证码">
          </el-col>
        </el-row>
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12" :offset="0">
          <el-button class="loginbtn" type="primary" @click="loginBtn">登录</el-button>
        </el-col>
        <el-col :span="12" :offset="0">
          <el-button class="resetbtn" type="danger" plain @click="showRegisterDialog">注册</el-button>
        </el-col>
      </el-row>
    </el-form>

    <!-- 注册对话框 -->
    <el-dialog v-model="registerDialogVisible" title="用户注册" width="30%">
      <el-form :model="registerModel" ref="registerForm" :rules="registerRules">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerModel.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="registerModel.password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="registerModel.confirmPassword" placeholder="请再次输入密码"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="registerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRegister">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { FormInstance, ElMessage } from 'element-plus';
import { onMounted, reactive, ref } from 'vue';
import { getImgApi, loginApi, registerApi } from '@/api/user';
import { useUserStore } from '@/store/user';
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const router = useRouter();
const loginform = ref<FormInstance>();

const loginModel = reactive({
  userName: 'admin',
  password: '123456',
  code: '',
  codeToken: ''
});
const imgSrc = ref('');

// 注册相关状态
const registerDialogVisible = ref(false);
const registerForm = ref<FormInstance>();
const registerModel = reactive({
  username: '',
  password: '',
  confirmPassword: ''
});

// 注册表单验证规则
const registerRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: any) => {
        if (value !== registerModel.password) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
});

// 显示注册对话框
const showRegisterDialog = () => {
  registerDialogVisible.value = true;
  registerModel.username = '';
  registerModel.password = '';
  registerModel.confirmPassword = '';
};

// 处理注册
const handleRegister = async () => {
  registerForm.value?.validate(async (valid) => {
    if (valid) {
      try {
        const res = await registerApi({
          userName: registerModel.username,
          password: registerModel.password
        });
        if (res && res.code === 200) {
          ElMessage.success('注册成功');
          registerDialogVisible.value = false;
        }
      } catch (error) {
        ElMessage.error('注册失败');
      }
    }
  });
};

const getImageBtn = async () => {
  let res = await getImgApi();
  if (res && res.code === 200) {
    imgSrc.value = res.data.img;
    loginModel.codeToken = res.data.codeToken;
  }
};

const loginBtn = () => {
  loginform.value?.validate(async (valid) => {
    if (valid) {
      let res = await loginApi(loginModel);
      if (res && res.code === 200) {
        ElMessage.success(res.msg);
        // 存储用户数据
        userStore.setUserId(res.data.userId);
        userStore.setNickName(res.data.nickName);
        userStore.setToken(res.data.token);
        // 跳转页面
        router.push({ path: '/' });
      } else {
        ElMessage.error(res.msg);
        getImageBtn();
      }
    }
  });
};

const rules = reactive({
  userName: [
    {
      required: true,
      message: '请填写账号',
      trigger: ["change", "blur"]
    }
  ],
  password: [
    {
      required: true,
      message: '请输入密码',
      trigger: ["change", "blur"]
    }
  ],
  code: [
    {
      required: true,
      message: '请输入验证码',
      trigger: ["change", "blur"]
    }
  ]
});

onMounted(() => {
  getImageBtn();
});
</script>

<style scoped lang="scss">
.logincontainer {
  height: 100%;
  background-color: #409EFF; /* 默认背景颜色为蓝色 */
  background-image: url(../../assets/image.png);
  background-repeat: no-repeat;
  background-size: cover; /* 确保背景图覆盖整个背景 */
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;

  .welcome-message {
    font-size: 40px;
    color: #fff; /* 文字颜色改为白色 */
    font-weight: bold; /* 字体加粗 */
    margin-bottom: 40px;
    text-align: center;
  }

  .loginform {
    height: 380px;
    width: 450px;
    padding: 20px 35px;
    border-radius: 10px;
    background-color: #fff; /* 表单背景颜色为白色 */
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); /* 添加阴影效果 */

    .logintitle {
      display: flex;
      justify-content: center;
      color: #000;
      width: 100%;
      font-size: 24px;
      font-weight: 600;
    }

    .images {
      height: 40px;
      width: 100%;
      cursor: pointer;
      margin-top: 10px;
    }

    .resetbtn {
      width: 100%;
      margin-top: 20px;
    }

    .loginbtn {
      width: 100%;
      margin-top: 20px;
    }
  }
}
</style>
