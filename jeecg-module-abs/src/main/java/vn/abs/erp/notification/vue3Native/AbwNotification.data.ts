import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
//List data
export const columns: BasicColumn[] = [
  {
    title: 'Người nhận',
    align: "center",
    dataIndex: 'msgUserIds'
  },
  {
    title: 'Loại thông báo',
    align: "center",
    dataIndex: 'msgCategory'
  },
  {
    title: 'Tiêu đề',
    align: "center",
    dataIndex: 'msgTitle'
  },
  {
    title: 'Nội dung rút gọn',
    align: "center",
    dataIndex: 'msgContent'
  },
  {
    title: 'Nội dung',
    align: "center",
    dataIndex: 'msgBody'
  },
  {
    title: 'Thumbnail',
    align: "center",
    dataIndex: 'msgThumbnailImage'
  },
  {
    title: 'Banner',
    align: "center",
    dataIndex: 'msgBannerImage'
  },
  {
    title: 'Hẹn giờ',
    align: "center",
    dataIndex: 'msgPlan'
  },
];

//Query data
export const searchFormSchema: FormSchema[] = [
];

//Form data
export const formSchema: FormSchema[] = [
  {
    label: 'Người nhận',
    field: 'msgUserIds',
    component: 'Input',
  },
  {
    label: 'Loại thông báo',
    field: 'msgCategory',
    component: 'Input',
  },
  {
    label: 'Tiêu đề',
    field: 'msgTitle',
    component: 'Input',
  },
  {
    label: 'Nội dung rút gọn',
    field: 'msgContent',
    component: 'Input',
  },
  {
    label: 'Nội dung',
    field: 'msgBody',
    component: 'Input',
  },
  {
    label: 'Thumbnail',
    field: 'msgThumbnailImage',
    component: 'Input',
  },
  {
    label: 'Banner',
    field: 'msgBannerImage',
    component: 'Input',
  },
  {
    label: 'Hẹn giờ',
    field: 'msgPlan',
    component: 'DatePicker',
    componentProps: {
      showTime: true,
      valueFormat: 'YYYY-MM-DD HH:mm:ss'
    },
  },
	// TODO The primary key hidden field is currently hard-coded as ID.
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false,
  },
];
