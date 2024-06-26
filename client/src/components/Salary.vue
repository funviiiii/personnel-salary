<template>
  <el-container>
    <el-main>
      <el-table :data="data" empty-text="暂无数据" stripe>
        <el-table-column prop="name" label="姓名"/>
        <el-table-column prop="basicSalary" label="基本工资"/>
        <el-table-column prop="allowance" label="津贴"/>
        <el-table-column prop="reward" label="奖金"/>
        <el-table-column prop="shouldPay" label="应付工资"/>
        <el-table-column prop="cost" label="花费"/>
        <el-table-column prop="insurance" label="保险"/>
        <el-table-column prop="realSalary" label="实付工资"/>
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
      <el-dialog v-model="editBoardVisible" title="工资">
        <el-form :model="dialogForm" label-width="100px">
          <el-form-item label="基础工资">
            <el-input-number v-model="dialogForm.basicSalary"></el-input-number>
          </el-form-item>
          <el-form-item label="津贴">
            <el-input-number v-model="dialogForm.allowance"></el-input-number>
          </el-form-item>
          <el-form-item label="奖金">
            <el-input-number v-model="dialogForm.reward"></el-input-number>
          </el-form-item>
          <el-form-item label="花费">
            <el-input-number v-model="dialogForm.cost"></el-input-number>
          </el-form-item>
          <el-form-item label="保险">
            <el-input-number v-model="dialogForm.insurance"></el-input-number>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="editBoardVisible = false">取消</el-button>
            <el-button type="primary" @click="submit">
              确定更改
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
import {onMounted, reactive, ref} from "vue";
import axios from "../utils/axios.js";

const props = defineProps(["right"]);
const router = useRouter();

let data = ref([])

let editBoardVisible = ref(false);

function submit() {
  editBoardVisible.value = false;
  axios({
    url: "/salary",
    method: "PUT",
    data: dialogForm
  }).then(res => {
    if (res.status === 200) {
      // 更新成功
      ElMessage.success("更新工资成功")
      data.value.forEach(e => {
        if (e["uid"] === dialogForm.uid) {
          e["basicSalary"] = dialogForm.basicSalary.toFixed(2);
          e["allowance"] = dialogForm.allowance.toFixed(2);
          e["reward"] = dialogForm.reward.toFixed(2);
          e["cost"] = dialogForm.cost.toFixed(2);
          e["insurance"] = dialogForm.insurance.toFixed(2);
          e["shouldPay"] = (dialogForm.basicSalary + dialogForm.allowance + dialogForm.reward).toFixed(2);
          e["realSalary"] = (Number(e["shouldPay"]) - dialogForm.cost - dialogForm.insurance).toFixed(2);
        }
      })
    }
  })
}

let dialogForm = reactive({
  uid: "",
  basicSalary: 0.00,
  allowance: 0.00,
  reward: 0.00,
  cost: 0.00,
  insurance: 0.00
})

function edit(index) {
  editBoardVisible.value = true;
  dialogForm.uid = data.value[index]["uid"];
  dialogForm.basicSalary = Number(data.value[index]["basicSalary"]);
  dialogForm.allowance = Number(data.value[index]["allowance"]);
  dialogForm.reward = Number(data.value[index]["reward"]);
  dialogForm.shouldPay = dialogForm.basicSalary + dialogForm.allowance + dialogForm.reward;
  dialogForm.cost = Number(data.value[index]["cost"]);
  dialogForm.insurance = Number(data.value[index]["insurance"]);
  dialogForm.realSalary = dialogForm.shouldPay - dialogForm.cost - dialogForm.insurance;
}

onMounted(() => {
  if (!props.right) {
    // 检测权限
    ElMessage.warning("当前账户没有权限访问该页面")
    router.push("/index")
    return;
  }
  let token = localStorage.getItem("token");
  if (token == null) {
    router.push("/account/username");
    ElMessage.warning("您还未登录，请先登录");
    return
  }
  // 获取工资数据
  axios({
    url: `/salary/list?token=${token}`,
    method: "GET",
  }).then(res => {
    if (res.status === 200) {
      for (let item of res.data["content"]) {
        data.value.push(item)
      }
    }
  }).catch(() => ElMessage.warning("获取工资信息失败"))
})

</script>

<style scoped>

</style>