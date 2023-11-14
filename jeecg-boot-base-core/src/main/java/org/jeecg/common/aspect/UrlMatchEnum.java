package org.jeecg.common.aspect;

/**
 * @Author scott
 * @Date 2020/1/14 13:36
 * @Description: Conversion rules between request URLs and menu route URLs（It is convenient to use menu routing URLs to configure data permission rules）
 */
public enum UrlMatchEnum {
    /**Find the URL and menu routing URL conversion rules /online/cgform/api/getData/ */
    CGFORM_DATA("/online/cgform/api/getData/", "/online/cgformList/"),
    /**Find the URL and menu routing URL conversion rules /online/cgform/api/exportXls/ */
    CGFORM_EXCEL_DATA("/online/cgform/api/exportXls/", "/online/cgformList/"),
    /**Find the URL and menu routing URL conversion rules /online/cgform/api/getTreeData/ */
    CGFORM_TREE_DATA("/online/cgform/api/getTreeData/", "/online/cgformList/"),
    /**Find the URL and menu routing URL conversion rules /online/cgreport/api/getColumnsAndData/ */
    CGREPORT_DATA("/online/cgreport/api/getColumnsAndData/", "/online/cgreport/"),
    /** Find the URL and menu routing URL conversion rules/online/cgreport/api/getData/ 【vue3报表数据请求地址】 */
    CGREPORT_ONLY_DATA("/online/cgreport/api/getData/", "/online/cgreport/"),
    /**求URL与菜单路由URL转换规则 /online/cgreport/api/exportXls/ */
    CGREPORT_EXCEL_DATA("/online/cgreport/api/exportXls/", "/online/cgreport/"),
    /**求URL与菜单路由URL转换规则 /online/cgreport/api/exportManySheetXls/ */
    CGREPORT_EXCEL_DATA2("/online/cgreport/api/exportManySheetXls/", "/online/cgreport/");

    UrlMatchEnum(String url, String matchUrl) {
        this.url = url;
        this.matchUrl = matchUrl;
    }

    /**
     * Request Request URL prefix
     */
    private String url;
    /**
     * Menu routing URLPREFIX (Corresponding to the menu path)
     */
    private String matchUrl;

    /**
     * 根据req url 获取到菜单配置路径（前端页面路由URL）
     *
     * @param url
     * @return
     */
    public static String getMatchResultByUrl(String url) {
        //获取到枚举
        UrlMatchEnum[] values = UrlMatchEnum.values();
        //加强for循环进行遍历操作
        for (UrlMatchEnum lr : values) {
            //如果遍历获取的type和参数type一致
            if (url.indexOf(lr.url) != -1) {
                //返回type对象的desc
                return url.replace(lr.url, lr.matchUrl);
            }
        }
        return null;
    }

    public String getMatchUrl() {
        return matchUrl;
    }
    //    public static void main(String[] args) {
//        /**
//         * 比如request真实请求URL: /online/cgform/api/getData/81fcf7d8922d45069b0d5ba983612d3a
//         * 转换匹配路由URL后（对应配置的菜单路径）:/online/cgformList/81fcf7d8922d45069b0d5ba983612d3a
//         */
//        System.out.println(UrlMatchEnum.getMatchResultByUrl("/online/cgform/api/getData/81fcf7d8922d45069b0d5ba983612d3a"));
//    }
}