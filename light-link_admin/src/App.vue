<template>
<div class="container">
    <h2 class="title">LightLink后台管理</h2>
    <div class="key">
        管理员凭证
        <input type="password" v-model="key" class="input"/>
        <button @click="loadApplyList" style="height: 26px;margin: 0 5px">获取</button>
    </div>
    <div class="empty" :style="{color: textColor}" v-if="applyList.length === 0">
      {{ tips }}
    </div>
    <div class="list" v-else>
      <div class="item" v-for="item in applyList">
        <div class="item-content">
          <div class="row"><div class="label">用户名：</div><p v-text="item.name"></p></div>
          <div class="row"><div class="label">邮箱：</div><p v-text="item.email"></p></div>
          <div class="row"><div class="label">申请理由：</div><textarea v-text="item.info" readonly rows="2" cols="100"></textarea></div>
        </div>
        <button @click="submit(item, 1)" class="btn">通过</button>
        <button @click="submit(item, 0)" class="btn">拒绝</button>
        <div class="line"></div>
      </div>
    </div>
</div>
</template>

<script setup>
    import {onMounted, getCurrentInstance, ref} from 'vue'
    const { proxy } = getCurrentInstance();

    const key = ref('')
    const applyList =ref([])
    const tips = ref('请输入管理凭证')
    const textColor = ref('black')
    
    const loadApplyList = async() => {
      if(!key.value){
        return;
      }
      const result = await proxy.request.get({
        url: '/user/loadApplyList',
        params: {
          adminKey: key.value
        }
      })

      if(!result){
        textColor.value = 'red'
        tips.value = '管理凭证错误'
        return;
      }
      if(result.code === 200){
        textColor.value = 'black'
        tips.value = '暂无申请'
      }
      applyList.value = result.data;
    }

    const submit = async(item, status) => {
      if(!key.value){
        return;
      }
      const result = await proxy.request.get({
        url: '/user/passAccount',
        params: {
          email: item.email,
          status: status,
          adminKey: key.value
        }
      })
      if(!result){
        return;
      }
      loadApplyList()
    }
</script>

<style lang="scss" scoped>
.container{
    padding: 20px;
    width: 100vw;
    height: 100vh;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    gap: 20px;
    .title{
      display: flex;
      gap: 10px;
      font-size: 30px;
      font-weight: bold;
    }
    .list{
      display: flex;
      flex-direction: column;
      gap: 10px;
      justify-content: flex-start;
      .item{
        width: 90%;
        .item-content{
          display: flex;
          flex-direction: column;
          line-height: 20px;
          gap: 10px;
          .row{
            .label{
              min-width: 80px;
            }
            display: flex;
          }
          margin: 5px 0;
        }
        .line{
          margin: 5px 0;
          height: 0px;
          width: 98%;
          border: 1px solid gray;
        }
      }
    }
}
.btn{
  width: 90px;
  height: 40px;
  margin: 5px;
}
.input{
  height: 26px;
}
</style>
