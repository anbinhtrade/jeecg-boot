import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: 'Subject',
    align: "center",
    dataIndex: 'msgTitle'
  },
  {
    title: 'Message',
    align: "center",
    dataIndex: 'msgContent'
  },
];

//查询数据
export const searchFormSchema: FormSchema[] = [
];

//表单数据
export const formSchema: FormSchema[] = [
  {
    label: 'Subject',
    field: 'msgTitle',
    component: 'Input',
  },
  {
    label: 'Message',
    field: 'msgContent',
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
