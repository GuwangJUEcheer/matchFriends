<!--  -->
<template>
    <form action="/">
        <van-search v-model="searchText" show-action placeholder="请输入要搜索的标签" @search="onSearch" @cancel="onCancel" />
    </form>
    <van-divider content-position="left">已选标签</van-divider>
    <div v-if="activeIds.length === 0">请选择标签</div>
    <van-row gutter="20">
        <van-col v-for="tag in activeIds" :key="index">
            <van-tag closable :show="true" closeable size="medium" type="primary" @close="doClose(tag)">
                {{ tag }}
            </van-tag>
        </van-col>
    </van-row>
    <van-divider content-position="right">选择标签</van-divider>
    <van-tree-select v-model:active-id="activeIds" v-model:main-active-index="activeIndex" :items="tagList" />
    <div style="padding:20px"> <van-button type="primary" @click="toSearchResult" block>搜索按钮</van-button></div>
</template>

<script setup>

import {onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
const router = useRouter();
const searchText = ref('');
//响应式对象
const originTagList = [
    {
        text: '浙江',
        children: [
            { text: '杭州', id: '杭州' },
            { text: '温州', id: '温州' },
            { text: '宁波', id: '宁波', disabled: false },
        ],
    },
    {
        text: '江苏',
        children: [
            { text: '南京', id: '南京' },
            { text: '无锡', id: '无锡' },
            { text: '徐州', id: '徐州' },
        ],
    },
    {
        text: '河北',
        children: [
            { text: '邢台', id: '邢台' },
            { text: '邯郸', id: '邯郸' },
            { text: '石家庄', id: '石家庄' },
        ],
        disabled: false
    },
    {
        text: '辽宁',
        children: [
            { text: '大连', id: '大连' },
            { text: '旅顺', id: '旅顺' },
            { text: 'Java', id: 'Java' },
        ],
        disabled: false
    },
];
let tagList = ref(originTagList);
const onSearch = () => {
    tagList.value = originTagList.map((parentTag) => {
        const tempChildren = [...parentTag.children];
        //避免修改parentTag
        const tempTag = { ...parentTag };
        tempTag.children = tempChildren.filter((child) => child.text.includes(searchText.value));
        return tempTag;
    });
};
const onCancel = () => {
    searchText.value = '';
    tagList.value = originTagList;
};

//已选中的标签
const activeIds = ref([]);
const activeIndex = ref(0);


const doClose = (item) => {
    activeIds.value = activeIds.value.filter((id) => id !== item.id);
};
/**
 * 跳转到搜索结果页面
 */
const toSearchResult = () => {
    router.push(
        {
            path: '/user/list',
            query: {
                tags: activeIds.value
            }
        }
    )
}
</script>

<style scoped></style>