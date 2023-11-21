import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '所属部门',
    align: "center",
    sorter: true,
    dataIndex: 'sysOrgCode'
  },
  {
    title: 'Subject',
    align: "center",
    sorter: true,
    dataIndex: 'msgSubject'
  },
  {
    title: 'Body',
    align: "center",
    dataIndex: 'msgContent'
  },
  {
    title: 'Status',
    align: "center",
    dataIndex: 'msgStatus'
  },
];

//查询数据
export const searchFormSchema: FormSchema[] = [
];

//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '所属部门',
    field: 'sysOrgCode',
    component: 'Input',
    dynamicDisabled: true
  },
  {
    label: 'Subject',
    field: 'msgSubject',
    component: 'Input',
  },
  {
    label: 'Body',
    field: 'msgContent',
    component: 'Input',
  },
  {
    label: 'Status',
    field: 'msgStatus',
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
