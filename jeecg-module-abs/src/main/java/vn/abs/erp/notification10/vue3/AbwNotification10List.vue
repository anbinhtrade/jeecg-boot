<template>
  <div>
    <!--Reference table-->
   <BasicTable @register="registerTable" :rowSelection="rowSelection">
     <!--Slot: table title-->
      <template #tableTitle>
          <a-button type="primary" @click="handleAdd" preIcon="ant-design:plus-outlined"> New</a-button>
          <a-button  type="primary" preIcon="ant-design:export-outlined" @click="onExportXls"> Export</a-button>
          <j-upload-button  type="primary" preIcon="ant-design:import-outlined" @click="onImportXls">Import</j-upload-button>
          <a-dropdown v-if="selectedRowKeys.length > 0">
              <template #overlay>
                <a-menu>
                  <a-menu-item key="1" @click="batchHandleDelete">
                    <Icon icon="ant-design:delete-outlined"></Icon>
                    Delete
                  </a-menu-item>
                </a-menu>
              </template>
              <a-button>Bulk operations
                <Icon icon="mdi:chevron-down"></Icon>
              </a-button>
        </a-dropdown>
      </template>
       <!--Action bar-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)"/>
      </template>
      <!--字段回显插槽-->
      <template v-slot:bodyCell="{ column, record, index, text }">
      </template>
    </BasicTable>
    <!-- Form area -->
    <AbwNotification10Modal @register="registerModal" @success="handleSuccess"></AbwNotification10Modal>
  </div>
</template>

<script lang="ts" name="notification10-abwNotification10" setup>
  import {ref, computed, unref} from 'vue';
  import {BasicTable, useTable, TableAction} from '/@/components/Table';
  import {useModal} from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage'
  import AbwNotification10Modal from './components/AbwNotification10Modal.vue'
  import {columns, searchFormSchema} from './AbwNotification10.data';
  import {list, deleteOne, batchDelete, getImportUrl,getExportUrl} from './AbwNotification10.api';
  import { downloadFile } from '/@/utils/common/renderUtils';
  import { useUserStore } from '/@/store/modules/user';
  const checkedKeys = ref<Array<string | number>>([]);
  const userStore = useUserStore();
  //Register model
  const [registerModal, {openModal}] = useModal();
  //Register table data
  const { prefixCls,tableContext,onExportXls,onImportXls } = useListPage({
      tableProps:{
           title: 'Notification 10',
           api: list,
           columns,
           canResize:false,
           formConfig: {
              //labelWidth: 120,
              schemas: searchFormSchema,
              autoSubmitOnEnter:true,
              showAdvancedButton:true,
              fieldMapToNumber: [
              ],
              fieldMapToTime: [
              ],
            },
           actionColumn: {
               width: 120,
               fixed:'right'
            },
      },
       exportConfig: {
            name:"Notification 10",
            url: getExportUrl,
          },
          importConfig: {
            url: getImportUrl,
            success: handleSuccess
          },
  })

  const [registerTable, {reload},{ rowSelection, selectedRowKeys }] = tableContext

   /**
    * New event
    */
  function handleAdd() {
     openModal(true, {
       isUpdate: false,
       showFooter: true,
     });
  }
   /**
    * Edit event
    */
  function handleEdit(record: Recordable) {
     openModal(true, {
       record,
       isUpdate: true,
       showFooter: true,
     });
   }
   /**
    * Details
   */
  function handleDetail(record: Recordable) {
     openModal(true, {
       record,
       isUpdate: true,
       showFooter: false,
     });
   }
   /**
    * Delete event
    */
  async function handleDelete(record) {
     await deleteOne({id: record.id}, handleSuccess);
   }
   /**
    * Batch delete events
    */
  async function batchHandleDelete() {
     await batchDelete({ids: selectedRowKeys.value}, handleSuccess);
   }
   /**
    * Successful callback
    */
  function handleSuccess() {
      (selectedRowKeys.value = []) && reload();
   }
   /**
      * Action bar
      */
  function getTableAction(record){
       return [
         {
           label: 'Edit',
           onClick: handleEdit.bind(null, record),
         }
       ]
   }
     /**
        * Drop-down action bar
        */
  function getDropDownAction(record){
       return [
         {
           label: 'Detail',
           onClick: handleDetail.bind(null, record),
         }, {
           label: 'Delete',
           popConfirm: {
             title: 'Confirm to delete',
             confirm: handleDelete.bind(null, record),
             placement: 'topLeft',
           }
         }
       ]
   }


</script>

<style scoped>

</style>