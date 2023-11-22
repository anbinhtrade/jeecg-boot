<#assign base=springMacroRequestContext.getContextUrl("")>
<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>JEECG ROOM Operations Command Room</title>
    <link href="${base}/bigscreen/template1/css/easyui.css" rel="stylesheet" type="text/css">
    <link href="${base}/bigscreen/template1/css/room.css" rel="stylesheet" type="text/css" />


    <script language="javascript" type="text/javascript" src="${base}/bigscreen/template1/js/jquery.min.js"></script>
    <script language="javascript" type="text/javascript" src="${base}/bigscreen/template1/js/jquery.easyui.min.js"></script>
    <script language="javascript" type="text/javascript" src="${base}/bigscreen/template1/js/echarts.min.js"></script>
    <script language="javascript" type="text/javascript" src="${base}/bigscreen/template1/js/echarts-wordcloud.js"></script>
    <script language="javascript" type="text/javascript" src="${base}/bigscreen/template1/js/china.js"></script>
    <script language="javascript" type="text/javascript" src="${base}/bigscreen/template1/js/geoCoord.js"></script>
    <script language="javascript" type="text/javascript" src="${base}/bigscreen/template1/js/room.js"></script>
    <script language="javascript" type="text/javascript" src="${base}/bigscreen/template1/js/resize.js"></script>
</head>

<body>
    <div id="main">
        <!-- Refresh -->
        <div id="refresh">
            <span id="refreshTime">Last refresh time：2018-05-06 23:13.24</span>
        </div>
        <!-- Refresh ends -->

        <!-- Start of progress bar area-->
        <div id="y_gauge1"></div>
        <div id="y_gauge2"></div>
        <div id="y_gauge3"></div>
        <div id="y_gauge4"></div>
        <!-- End of progress bar area-->

        <!-- Spiral diagram begins -->
        <div id="orderStatus"></div>
        <div class="contentButton" style="top:822px;left:453px">
            <a class="a1" href="javascript:void(0);" onclick="javascript:openDialog('modalDlg');">&nbsp;</a>
        </div>
        <!-- Spiral ends -->

        <!-- The map begins -->
        <div id="map"></div>
        <!-- End of map -->

        <!-- Product pie chart begins -->
        <div id="productPie" style="width: 900px; height: 590px;"></div>
        <!-- End of product pie chart -->

        <!-- The business progress chart begins -->
        <div id="businessProgress"></div>
        <div class="contentButton" style="top:822px;left:3679px">
            <a class="a1" href="javascript:void(0);">&nbsp;</a>
        </div>
        <!-- End of the business progress chart -->

        <!-- The production plan presentation begins-->
        <div id="plan"></div>
        <div class="contentButton" style="top:1402px;left:453px">
            <a href="javascript:void(0);">&nbsp;</a>
        </div>
        <!-- The production plan presentation ends-->

        <!-- The production quality demonstration begins-->
        <div id="quality"></div>
        <div class="contentButton" style="top:1402px;left:1532px">
            <a href="javascript:void(0);">&nbsp;</a>
        </div>
        <!-- End of the production quality demonstration-->

        <!-- Customer service and complaint presentation begins-->
        <div id="produce">
            <table width="100%" cellpadding="6" cellspacing="0">
                <tr class="row1">
                    <td rowspan="2"><span id="currentDate">2018/04/25</span></td>
                    <td colspan="2">Product Complaints</td>
                    <td colspan="2">Logistics complaints</td>
                    <td colspan="2">After-sales complaints</td>
                </tr>
                <tr class="row1">
                    <td>QUALITY</td>
                    <td>SERVE</td>
                    <td>QUALITY</td>
                    <td>SERVE</td>
                    <td>QUALITY</td>
                    <td>SERVE</td>
                </tr>
                <tr class="row2">
                    <td>PROCESSED</td>
                    <td>48</td>
                    <td>48</td>
                    <td>48</td>
                    <td>48</td>
                    <td>48</td>
                    <td>48</td>
                </tr>
                <tr class="row1">
                    <td>PROCESSING</td>
                    <td>34</td>
                    <td>34</td>
                    <td>34</td>
                    <td>34</td>
                    <td>34</td>
                    <td>34</td>
                </tr>
                <tr class="row2">
                    <td>Not processed</td>
                    <td>30</td>
                    <td>28</td>
                    <td>28</td>
                    <td>26</td>
                    <td>25</td>
                    <td>8</td>
                </tr>
                <tr class="row2">
                    <td>TOTAL</td>
                    <td>30</td>
                    <td>28</td>
                    <td>28</td>
                    <td>26</td>
                    <td>25</td>
                    <td>8</td>
                </tr>
                <tr class="row1">
                    <td>TOTAL</td>
                    <td colspan="2">22</td>
                    <td colspan="2">65</td>
                    <td colspan="2">44</td>
                </tr>
            </table>
        </div>
        <div class="contentButton" style="top:1402px;left:2598px">
            <a href="javascript:void(0);">&nbsp;</a>
        </div>
        <!-- End of customer service and complaint presentation-->

        <!-- The word cloud presentation begins-->
        <div id="wordCloud"></div>
        <div class="contentButton" style="top:1402px;left:3679px">
            <a href="javascript:void(0);">&nbsp;</a>
        </div>
        <!-- End of word cloud-->

        <!-- Dashboard area begins-->
        <!-- <div id="gauge1"></div>
            <div class="gaugeTitle" style="left:2200px;top:480px;"><sapn id="vg1">32</sapn>&nbsp;m<sup>3</sup>/d</div>
            <div id="gauge2"></div>
            <div class="gaugeTitle" style="left:2550px;top:480px;"><sapn id="vg2">32</sapn>&nbsp;KVA</div>
            <div id="gauge3"></div>
            <div class="gaugeTitle" style="left:2910px;top:480px;"><sapn id="vg3">32</sapn>&nbsp;Nm<sup>3</sup>/h</div>
            <div id="gauge4"></div>
            <div class="gaugeTitle" style="left:2380px;top:750px;"><sapn id="vg4">32</sapn>&nbsp;m<sup>3</sup>/m</div>
            <div id="gauge5"></div>
            <div class="gaugeTitle" style="left:2730px;top:750px;"><sapn id="vg5">32</sapn>&nbsp;t/h</div> -->
        <!-- End of dashboard area-->

        <!--Pop-ups-->
        <!--<div id="popWindow">
                <div style="padding:20px;font-size:32px; background-color:#051E3C;color:#B7E1FF; border-bottom:1px solid #09F">Pop-up title</div>
            </div>-->
        <!--The pop-up window ends---->
    </div>>

    <!--Edit pop-ups for system users-->
    <div id="modalDlg" class="easyui-dialog" title="Pop-ups" data-options="modal:true,closed:true,buttons:
        [{
                    text:'Are you sure',
                    iconCls:'icon-ok',
                    handler:function(){
                        $('#modalDlg').dialog('close');
                    }
                },{
                    text:'CANCEL',
                    handler:function(){
                        $('#modalDlg').dialog('close');
                    }
                }]"
        style="padding:10px">
        <table width="100%" cellpadding="5">
            <tr>
                <td width="80" align="center">Username:</td>
                <td><input type="text" name="updateUsername" id="updateUsername" value=""></td>
            </tr>
            <tr>
                <td align="center">Login password:</td>
                <td><input type="text" name="updateUserpass" id="updateUserpass" value=""></td>
            </tr>
            <tr>
                <td align="center">&nbsp;</td>
                <td height="30">If you do not need to change your password, please leave it blank</td>
            </tr>
            <tr>
                <td align="center">User type:</td>
                <td>
                    <select name="updateUserType" id="updateUserType">
                        <option value="">--Please select--</option>
                        <option value="administrator">Administrator</option>
                        <option value="user">System users</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td align="center">User status:</td>
                <td>
                    <input type="radio" name="updateUserStatus" id="updateUserStatus1" value="0"><label for="updateUserStatus1">Enable</label>&nbsp;&nbsp;
                    <input type="radio" name="updateUserStatus" id="updateUserStatus2" value="1"><label for="updateUserStatus2">Disable</label>
                </td>
            </tr>
            <tr>
                <td align="center">User Instructions:</td>
                <td>
                    <input type="text" name="updateUserDescription" id="updateUserDescription" value="">
                </td>
            </tr>
        </table>
    </div>
    <!--The pop-up window for editing the system user ends-->

</body>

</html>