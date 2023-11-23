<template>
  <a-modal :title="title" :width="width" :visible="visible" @ok="handleOk" :okButtonProps="{ class: { 'jee-hidden': disableSubmit } }" @cancel="handleCancel" cancelText="Close">
    <AbwNotificationForm ref="registerForm" @ok="submitCallback" :formDisabled="disableSubmit" :formBpm="false"></AbwNotificationForm>
  </a-modal>
</template>

<script lang="ts" setup>
  import { ref, nextTick, defineExpose } from 'vue';
  import AbwNotificationForm from './AbwNotificationForm.vue'
  
  const title = ref<string>('');
  const width = ref<number>(800);
  const visible = ref<boolean>(false);
  const disableSubmit = ref<boolean>(false);
  const registerForm = ref();
  const emit = defineEmits(['register', 'success']);

  /**
   * New
   */
  function add() {
    title.value = 'New';
    visible.value = true;
    nextTick(() => {
      registerForm.value.add();
    });
  }
  
  /**
   * Edit
   * @param record
   */
  function edit(record) {
    title.value = disableSubmit.value ? 'Detail' : 'Edit';
    visible.value = true;
    nextTick(() => {
      registerForm.value.edit(record);
    });
  }
  
  /**
   * OK button click event
   */
  function handleOk() {
    registerForm.value.submitForm();
  }

  /**
   * form save callback event
   */
  function submitCallback() {
    handleCancel();
    emit('success');
  }

  /**
   * Cancel button callback event
   */
  function handleCancel() {
    visible.value = false;
  }

  defineExpose({
    add,
    edit,
    disableSubmit,
  });
</script>

<style>
  /**Hidden style-modal OK button */
  .jee-hidden {
    display: none !important;
  }
</style>
