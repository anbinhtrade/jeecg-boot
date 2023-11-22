<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="800" @ok="handleSubmit">
      <BasicForm @register="registerForm"/>
  </BasicModal>
</template>

<script lang="ts" setup>
    import {ref, computed, unref} from 'vue';
    import {BasicModal, useModalInner} from '/@/components/Modal';
    import {BasicForm, useForm} from '/@/components/Form/index';
    import {formSchema} from '../AbwNotification10.data';
    import {saveOrUpdate} from '../AbwNotification10.api';
    // Emit Statement
    const emit = defineEmits(['register','success']);
    const isUpdate = ref(true);
    //Form configuration
    const [registerForm, {setProps,resetFields, setFieldsValue, validate}] = useForm({
        //labelWidth: 150,
        schemas: formSchema,
        showActionButtonGroup: false,
        baseColProps: {span: 24}
    });
    //Form assignment
    const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
        //Reset the form
        await resetFields();
        setModalProps({confirmLoading: false,showCancelBtn:!!data?.showFooter,showOkBtn:!!data?.showFooter});
        isUpdate.value = !!data?.isUpdate;
        if (unref(isUpdate)) {
            //Form assignment
            await setFieldsValue({
                ...data.record,
            });
        }
        // Disable entire form when hiding bottom
       setProps({ disabled: !data?.showFooter })
    });
    //Set the title
    const title = computed(() => (!unref(isUpdate) ? 'New' : 'Edit'));
    //Form submission events
    async function handleSubmit(v) {
        try {
            let values = await validate();
            setModalProps({confirmLoading: true});
            //Submit the form
            await saveOrUpdate(values, isUpdate.value);
            //Close the pop-up window
            closeModal();
            //Refresh the list
            emit('success');
        } finally {
            setModalProps({confirmLoading: false});
        }
    }
</script>

<style lang="less" scoped>
	/** 时间和数字输入框样式 */
  :deep(.ant-input-number){
		width: 100%
	}

	:deep(.ant-calendar-picker){
		width: 100%
	}
</style>