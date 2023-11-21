import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: 'Subjects',
    align: "center",
    dataIndex: 'msgSubject'
  },
  {
    title: 'Message',
    align: "center",
    dataIndex: 'msgBody'
  },
];

//查询数据
export const searchFormSchema: FormSchema[] = [
];

//表单数据
export const formSchema: FormSchema[] = [
  {
    label: 'Subjects',
    field: 'msgSubject',
    component: 'Input',
  },
  {
    label: 'Message',
    field: 'msgBody',
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
