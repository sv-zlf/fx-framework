<template>
  <div class="snow-page">
    <div class="snow-inner">
      <s-layout-tools>
        <template #left>
          <a-space wrap>
            <a-input v-model="searchForm.fileName" placeholder="请输入文件名称" allow-clear />
            <a-select placeholder="文件类型" v-model="searchForm.fileType" style="width: 120px" allow-clear>
              <a-option v-for="item in fileTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</a-option>
            </a-select>
            <a-button type="primary" @click="search">
              <template #icon><icon-search /></template>
              <span>查询</span>
            </a-button>
            <a-button @click="reset">
              <template #icon><icon-refresh /></template>
              <span>重置</span>
            </a-button>
          </a-space>
        </template>
        <template #right>
          <a-space wrap>
            <a-button type="primary" status="success" @click="onUpload">
              <template #icon><icon-upload /></template>
              <span>上传文件</span>
            </a-button>
            <a-button type="primary" status="danger" @click="onDeleteBatch">
              <template #icon><icon-delete /></template>
              <span>删除</span>
            </a-button>
          </a-space>
        </template>
      </s-layout-tools>


      <a-table
        row-key="id"
        :data="fileList"
        :bordered="{ cell: true }"
        :loading="loading"
        :scroll="{ x: '100%', y: '100%', minWidth: 1200 }"
        :pagination="pagination"
        :row-selection="{ type: 'checkbox', showCheckedAll: true }"
        :selected-keys="selectedKeys"
        @select="select"
        @select-all="selectAll"
      >
        <template #columns>
          <a-table-column title="序号" :width="64">
            <template #cell="cell">{{ cell.rowIndex + 1 }}</template>
          </a-table-column>
          <a-table-column title="文件名称" data-index="originalName" :width="200" ellipsis tooltip></a-table-column>
          <a-table-column title="文件类型" :width="100" align="center">
            <template #cell="{ record }">
              <a-tag bordered size="small" color="arcoblue" v-if="record.fileType === 'image'">图片</a-tag>
              <a-tag bordered size="small" color="green" v-else-if="record.fileType === 'document'">文档</a-tag>
              <a-tag bordered size="small" color="orange" v-else-if="record.fileType === 'video'">视频</a-tag>
              <a-tag bordered size="small" color="purple" v-else-if="record.fileType === 'audio'">音频</a-tag>
              <a-tag bordered size="small" color="gray" v-else-if="record.fileType === 'archive'">压缩包</a-tag>
              <a-tag bordered size="small" color="red" v-else>其他</a-tag>
            </template>
          </a-table-column>
          <a-table-column title="文件大小" :width="120" align="center">
            <template #cell="{ record }">
              {{ formatFileSize(record.fileSize) }}
            </template>
          </a-table-column>
          <a-table-column title="上传用户" data-index="username" :width="120"></a-table-column>
          <a-table-column title="上传时间" data-index="createTime" :width="180"></a-table-column>
          <a-table-column title="操作" :width="200" align="center" :fixed="tableFixed">
            <template #cell="{ record }">
              <a-space>
                <a-button type="primary" size="mini" @click="onPreview(record)">
                  <template #icon><icon-eye /></template>
                  <span>预览</span>
                </a-button>
                <a-button type="primary" size="mini" @click="onDownload(record)">
                  <template #icon><icon-download /></template>
                  <span>下载</span>
                </a-button>
                <a-popconfirm type="warning" content="确定删除该文件吗?" @ok="onDelete(record)">
                  <a-button type="primary" status="danger" size="mini">
                    <template #icon><icon-delete /></template>
                    <span>删除</span>
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </div>

    <!-- 文件上传对话框 -->
    <a-modal :width="dialogWidth()" v-model:visible="uploadVisible" @close="uploadClose" title="上传文件">
      <a-upload
        ref="uploadRef"
        :file-list="uploadFileList"
        :auto-upload="false"
        multiple
        :custom-request="customUpload"
        :limit="10"
        :show-file-list="true"
        @change="onFileChange"
        @remove="onFileRemove"
      >
        <template #upload-button>
          <a-button type="primary">
            <template #icon><icon-upload /></template>
            选择文件
          </a-button>
        </template>
      </a-upload>
      <template #footer>
        <a-space>
          <a-button @click="uploadClose">取消</a-button>
          <a-button type="primary" @click="handleUpload" :loading="uploading">开始上传</a-button>
        </a-space>
      </template>
    </a-modal>

    <!-- 文件预览对话框 -->
    <a-modal v-model:visible="previewVisible" @close="previewClose" :width="900" title="文件预览">
      <div v-if="previewFile" style="text-align: center;">
        <img v-if="previewFile.fileType === 'image'" :src="previewUrl" style="max-width: 100%; max-height: 600px;" alt="预览" />
        <div v-else style="padding: 40px; text-align: center;">
          <icon-file :style="{ fontSize: '100px', color: '#999' }" />
          <p style="margin-top: 20px; color: #666;">该文件类型不支持在线预览，请下载后查看</p>
        </div>
      </div>
      <template #footer>
        <a-space>
          <a-button @click="previewClose">关闭</a-button>
          <a-button v-if="previewFile && previewFile.fileType !== 'image'" type="primary" @click="onDownload(previewFile)">
            <template #icon><icon-download /></template>
            下载文件
          </a-button>
        </a-space>
      </template>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { useLayoutModel } from "@/hooks/useLayoutModel";
import { getFileList, upload, uploadBatch, deleteFile, } from "@/api/system/file";

import {quickDownloadFile} from "@/utils/download";
import {FilePreviewUtil, quickPreviewFile} from "@/utils/preview";
import {RequestOption} from "@arco-design/web-vue/es/upload/interfaces";

const { dialogWidth, tableFixed } = useLayoutModel();


const searchForm = ref({
  fileName: "",
  fileType: ""
});
const loading = ref(false);
const fileList = ref<any>([]);
const selectedKeys = ref([]);
const pagination = ref({
  total: null,
  current: 1,
  pageSize: 10,
  showPageSize: true,
  showTotal: true,
  onChange: (current: number) => {
    pagination.value.current = current;
    getFile();
  },
  onPageSizeChange: (pageSize: number) => {
    pagination.value.current = 1;
    pagination.value.pageSize = pageSize;
    getFile();
  }
});

const fileTypeOptions = [
  { label: "图片", value: "image" },
  { label: "文档", value: "document" },
  { label: "视频", value: "video" },
  { label: "音频", value: "audio" },
  { label: "压缩包", value: "archive" },
  { label: "其他", value: "other" }
];

// 文件上传相关
const uploadVisible = ref(false);
const uploadRef = ref();
const uploading = ref(false);
const uploadFileList = ref<File[]>([]);
const previewVisible = ref(false);
const previewFile = ref<any>(null);
const previewUrl = ref("");

// 获取文件列表
const getFile = async () => {
  loading.value = true;
  const params = {
    ...searchForm.value,
    pageIndex: pagination.value.current,
    pageSize: pagination.value.pageSize
  };
  let res = await getFileList(params);
  fileList.value = res.data.records;
  pagination.value.total = res.data.total;
  loading.value = false;
};

// 搜索
const search = () => {
  pagination.value.current = 1;
  getFile();
};

// 重置
const reset = () => {
  searchForm.value = {
    fileName: "",
    fileType: ""
  };
  getFile();
};

// 选择
const select = (list: []) => {
  selectedKeys.value = list;
};

const selectAll = (state: boolean) => {
  selectedKeys.value = state ? fileList.value.map((el: any) => el.id) : [];
};

// 上传
const onUpload = () => {
  uploadVisible.value = true;
};

const uploadClose = () => {
  uploadVisible.value = false;
  uploadFileList.value = [];
};

const onFileChange = (fileList: []) => {
  uploadFileList.value = fileList;
};

const onFileRemove = (file: File) => {
  const index = uploadFileList.value.indexOf(file);
  if (index > -1) {
    uploadFileList.value.splice(index, 1);
  }
};

const handleUpload = async () => {
  if (uploadFileList.value.length === 0) {
    return arcoMessage("warning", "请选择要上传的文件");
  }
  
  uploading.value = true;

  for (const item of uploadFileList.value) {
    await upload(item, ''); // 等待当前文件上传完成，再处理下一个
    arcoMessage('success', item.name+'上传成功')
  }
  uploadClose();
  uploading.value = false;
};

const customUpload = async (option: RequestOption) => {
  // 自定义上传逻辑
  const {onProgress, onError, onSuccess, fileItem, name} = option;
  console.log('file',fileItem.file)
  const response = await upload(fileItem, '');
  onSuccess(response);
  arcoMessage('success', fileItem.name+'上传成功')

};

// 预览
const onPreview = async (row: any) => {
  previewFile.value = row;
  if (row.fileType === "image") {
    previewUrl.value = await quickPreviewFile('/api/file/preview', { id: row.id });
  } else {
    previewUrl.value = "";
  }
  previewVisible.value = true;
};

const previewClose = async () => {
  await FilePreviewUtil.releasePreviewBlobUrl(previewUrl.value);
  previewVisible.value = false;
  previewFile.value = null;
  previewUrl.value = "";
};

// 下载
const onDownload = async (row: any) => {
  const params = {
    id: row.id
  };
  await quickDownloadFile('/api/file/download', params, row.fileName);
};

// 删除
const onDelete = async (row: any) => {
  await deleteFile(row.id);
  arcoMessage("success", "删除成功");
  getFile();
};

// 批量删除
const onDeleteBatch = async () => {
  if (selectedKeys.value.length === 0) {
    return arcoMessage("warning", "请选择要删除的文件");
  }
  
  for (const id of selectedKeys.value) {
    await deleteFile(id);
  }
  arcoMessage("success", "删除成功");
  getFile();
};

// 格式化文件大小
const formatFileSize = (bytes: number) => {
  if (bytes === 0) return "0 B";
  const k = 1024;
  const sizes = ["B", "KB", "MB", "GB", "TB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + " " + sizes[i];
};

onMounted(() => {
  getFile();
});
</script>

<style lang="scss" scoped>
</style>
