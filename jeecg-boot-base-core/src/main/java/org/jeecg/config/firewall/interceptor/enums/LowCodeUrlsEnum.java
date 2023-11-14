package org.jeecg.config.firewall.interceptor.enums;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author: qinfeng
 * @date: 2023/09/04 11:44
 */
public enum LowCodeUrlsEnum {
    /**
     * Online form configuration request TODO Additions, changes and deletions
     */
    NEW_LOW_APP_ADD_URL("/online/cgform/api/addAll", "Add an online form"),
    NEW_LOW_APP_EDIT_URL("/online/cgform/api/editAll", "Edit the online form"),
    ONLINE_DB_SYNC("/online/cgform/api/doDbSynch/**/**", "Online form synchronization database"),
    ONLINE_DEL_BATCH("/online/cgform/head/deleteBatch", "Online forms are deleted in batches"),
    ONLINE_DELETE("/online/cgform/head/delete", "Online form deletion"),
    ONLINE_REMOVE("/online/cgform/head/removeRecord", "Online form removal"),
    ONLINE_COPY("/online/cgform/head/copyOnline", "Online form generation view"),
    ONLINE_TABLE("/online/cgform/head/copyOnlineTable", "Online form copy table"),
    ONLINE_BUTTON_AI_TEST("/online/cgform/button/aitest", "Online form custom buttons generate data"),
    ONLINE_BUTTON_ADD("/online/cgform/button/add", "Online form customization button added"),
    ONLINE_BUTTON_EDIT("/online/cgform/button/edit", "Online form custom button editing"),
    ONLINE_BUTTON_DEL("/online/cgform/button/deleteBatch", "Online form custom button removed"),
    ONLINE_ENHANCE_JS("/online/cgform/head/enhanceJs/**", "Online Form JS enhancements"),
    ONLINE_ENHANCE_JAVA("/online/cgform/head/enhanceJava/**", "ONLINE FORM JAVA ENHANCEMENTS"),
    /**
     * Online Report Configuration Request
     */
    ONLINE_CG_REPORT_ADD("/online/cgreport/head/add", "Online reports are added"),
    ONLINE_CG_REPORT_EDIT("/online/cgreport/head/editAll", "Online report editing"),
    ONLINE_CG_REPORT_DEL("/online/cgreport/head/delete", "Online report deletion"),
    ONLINE_CG_REPORT_PARSE_SQL("/online/cgreport/head/parseSql", "SQL parsing of online reports"),
    /**
     * Online Chart Configuration Request
     */
    ONLINE_GRAPH_REPORT_ADD("/online/graphreport/head/add", "Online charts have been added"),
    ONLINE_GRAPH_REPORT_EDIT("/online/graphreport/head/edit", "Online Chart Editing"),
    ONLINE_GRAPH_REPORT_DEL("/online/graphreport/head/deleteBatch", "Online charts are deleted"),
    ONLINE_GRAPH_REPORT_PARSE_SQL("/online/cgreport/head/parseSql", "Online charts parse SQL statements"),

    /**
     * Request for large screen configuration
     */
    BIG_SCREEN_DB_ADD("/bigscreen/bigScreenDb/add", "Added a large screen data source"),
    BIG_SCREEN_DB_EDIT("/bigscreen/bigScreenDb/edit", "Edit data sources on the large screen"),
    BIG_SCREEN_DB_DEL("/bigscreen/bigScreenDb/delete", "Delete the dashboard data source"),
    BIG_SCREEN_DB_TEST_CONNECTION("/bigscreen/bigScreenDb/testConnection", "Large-screen data source connection test"),
//    BIG_SCREEN_SAVE("/bigscreen/visual/save", "Large screen added"),
//    BIG_SCREEN_EDIT("/bigscreen/visual/update", "Large-screen editing"),
//    BIG_SCREEN_COPY("/bigscreen/visual/copy", "Large-screen copying"),
//    BIG_SCREEN_REMOVE("/bigscreen/visual/remove", "Screen Removal"),
//    BIG_SCREEN_DEL("/bigscreen/visual/deleteById", "Delete the large screen"),

    /**
     * Dashboard configuration requests
     */
    DRAG_DB_ADD("/drag/onlDragDataSource/add", "Dashboard data sources are added"),
    DRAG_DB_TEST_CONNECTION("/drag/onlDragDataSource/testConnection", "Dashboard data source connection test"),
    DRAG_PARSE_SQL("/drag/onlDragDatasetHead/queryFieldBySql", "SQL parsing of dashboard datasets"),
    DRAG_DATASET_ADD("/drag/onlDragDatasetHead/add", "Dashboard datasets are added");

    /**
     * Other configuration requests
     */

    private String url;
    private String title;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    LowCodeUrlsEnum(String url, String title) {
        this.url = url;
        this.title = title;
    }

    /**
     * Get the available quantity based on the code
     *
     * @return
     */
    public static List<String> getLowCodeInterceptUrls() {
        return Arrays.stream(LowCodeUrlsEnum.values()).map(LowCodeUrlsEnum::getUrl).collect(Collectors.toList());
    }

}
