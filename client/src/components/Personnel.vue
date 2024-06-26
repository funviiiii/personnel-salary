<template>
  <el-container>
    <el-main>
      <el-table :data="data" empty-text="暂无数据" stripe>
        <el-table-column prop="name" label="姓名"/>
        <el-table-column prop="gender" label="性别"/>
        <el-table-column prop="birthday" label="生日"/>
        <el-table-column prop="induction" label="入职时间"/>
        <el-table-column prop="department" label="所属部门"/>
        <el-table-column prop="role" label="职位"/>
        <el-table-column prop="isMarried" label="已婚">
          <template #default="scope">
            {{ data[scope.$index]["isMarried"] ? "是" : "否" }}
          </template>
        </el-table-column>
        <el-table-column prop="role" label="简历">
          <template #default="scope">
            <el-button
                link
                type="primary"
                size="small"
                @click="router.push(`/resume/${data[scope.$index]['resume']}?editable=false`)"
            >
              查看简历
            </el-button>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="120">
          <template #default="scope">
            <el-button
                link
                type="primary"
                size="small"
                @click="edit(scope.$index)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-dialog v-model="editBoardVisible" title="Shipping address">
        <el-form :model="editBoardForm" label-width="100px">
          <el-form-item label="姓名">
            <el-input v-model="editBoardForm.name" autocomplete="off"/>
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="editBoardForm.gender">
              <el-option value="男"></el-option>
              <el-option value="女"></el-option>
              <el-option value="保密"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="生日">
            <el-date-picker value-format="YYYY-MM-DD" v-model="editBoardForm.birthday"></el-date-picker>
          </el-form-item>
          <el-form-item label="入职时间">
            <el-date-picker value-format="YYYY-MM-DD" v-model="editBoardForm.induction"></el-date-picker>
          </el-form-item>
          <el-form-item label="所属部门">
            <el-select v-model="editBoardForm.department">
              <el-option v-for="(item, index) in departmentSelections" :value="item" :index="index"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="职位">
            <el-select v-model="editBoardForm.role"
                       :disabled="(editBoardForm.role === '管理员' && !isSM) || editBoardForm.role === '超级管理员'"
            >
              <el-option v-for="(item, index) in roleSelections" :value="item" :index="index"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="是否已婚">
            <el-checkbox v-model="editBoardForm.isMarried"></el-checkbox>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="editBoardVisible = false">取消</el-button>
            <el-button type="primary" @click="submit">
              确认
            </el-button>
          </span>
        </template>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script setup>
import {useRouter} from "vue-router";
import {ElMessage} from "element-plus";
import {onMounted, reactive, ref, watch} from "vue";
import axios from "../utils/axios.js";
import dayjs from "dayjs";

let departmentSelections = reactive([]);
let roleSelections = reactive([]);
let info
let content = ref("abc")
let isSM = JSON.parse(sessionStorage.getItem('role'))['rid'] === 'rid_super'

function submit() {
  // 校验token
  let token = localStorage.getItem("token");
  if (token == null) {
    ElMessage.warning("您还未登录，请先登录");
    router.push("/account/username");
    return
  }
  // 提交更改
  axios({
    url: "/user",
    method: "PUT",
    data: editBoardForm
  }).then(res => {
    ElMessage.success("更新数据成功");
  }).catch(res => {
    ElMessage.warning(res.response.data['msg']);
  })
  editBoardVisible.value = false;
  loadInfo()
}

function loadInfo() {
  data.length = 0
  departmentSelections.length = 0
  roleSelections.length = 0
  // 获取职员信息
  axios({
    url: `/user?token=${localStorage.getItem("token")}`,
    method: "GET"
  }).then(res => {
    if (res.status === 200) {
      for (let item of res.data["content"]) {
        item["birthday"] = dayjs(item["birthday"]).format("YYYY-MM-DD")
        item["induction"] = dayjs(item["induction"]).format("YYYY-MM-DD")
        data.push(item);
      }
    }
  }).catch(res => {
    ElMessage.warning(res.response.data["msg"]);
  })

  // 获取部门信息
  axios({
    url: "/department",
    method: "GET"
  }).then(res => {
    if (res.status === 200) {
      for (let item of res.data["content"]) {
        departmentSelections.push(item["departmentName"]);
      }
    } else {
      ElMessage.warning(res.data["msg"]);
    }
  }).catch(res => {
    ElMessage.warning(res.response.data["msg"]);
  })

  // 获取职位信息
  axios({
    url: "/role",
    method: "GET"
  }).then(res => {
    if (res.status === 200) {
      for (let item of res.data["content"]) {
        // 超级管理员不能被任命；只有当当前身份为超级管理员时，可以任命管理员
        if ((item['rid'] !== 'rid_super' && item['rid'] !== 'rid_manager') || (item['rid'] !== 'rid_super' && item['rid'] === 'rid_manager')) {
          roleSelections.push(item["roleName"]);
        }
      }
    } else {
      ElMessage.warning(res.data["msg"]);
    }
  }).catch(res => {
    ElMessage.warning(res.response.data["msg"]);
  })
}

function edit(index) {
  editBoardVisible.value = true;
  console.log(index)
  editBoardForm.uid = data[index].uid;
  editBoardForm.name = data[index].name;
  editBoardForm.gender = data[index].gender;
  editBoardForm.birthday = data[index].birthday;
  editBoardForm.induction = data[index].induction;
  editBoardForm.department = data[index].department;
  editBoardForm.role = data[index].role;
  editBoardForm.isMarried = data[index].isMarried;
  editBoardForm.resume = data[index].resume;
}

const editBoardForm = reactive({
  name: "", gender: "", birthday: "", induction: "", department: "", role: "", isMarried: false, resume: ""
})

const editBoardVisible = ref(false);

watch(editBoardVisible, value => {
  if (value["value"] === false) {
    editBoardForm.name = "";
    editBoardForm.gender = "";
    editBoardForm.birthday = "";
    editBoardForm.induction = "";
    editBoardForm.department = "";
    editBoardForm.role = "";
    editBoardForm.isMarried = false;
    editBoardForm.resume = "";
  }
}, {deep: true})

const data = reactive([])

const props = defineProps(["right"]);
const router = useRouter();

onMounted(() => {
  if (!props.right) {
    ElMessage.warning("当前账户没有权限访问该页面")
    router.push("/index")
  }
  loadInfo();
})
</script>

<style scoped>

</style>