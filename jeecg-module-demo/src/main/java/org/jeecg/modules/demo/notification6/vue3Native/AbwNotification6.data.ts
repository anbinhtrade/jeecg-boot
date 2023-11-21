import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: 'Message Content',
    align: "center",
    dataIndex: 'msgContent'
  },
  {
    title: 'Message Title',
    align: "center",
    dataIndex: 'msgTitle'
  },
];

//查询数据
export const searchFormSchema: FormSchema[] = [
];

//表单数据
export const formSchema: FormSchema[] = [
  {
    label: 'Message Content',
    field: 'msgContent',
    component: 'Input',
  },
  {
    label: 'Message Title',
    field: 'msgTitle',
    component: 'Input',
  },
	// TODO 主键隐藏字段，目前写死为ID
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false,
  },
];
