<#---->
<#-- freemarker Some of the tool methods -->
<#---->
<#-- Hump to other characters -->
<#-- @param str       The text to be converted -->
<#-- @param character The character you want to convert to -->
<#-- @param case      Convert case (normal does not convert, lower lowercase, upper uppercase.)） -->
<#function camelToChar(str, character, case='normal')>
  <#assign text=str?replace("([a-z])([A-Z]+)","$1${character}$2","r")/>
  <#if case=="upper">
    <#return text?upper_case>
  <#elseif case=="lower">
    <#return text?lower_case>
  <#else>
    <#return text>
  </#if>
</#function>
<#--Underline to hump-->
<#function dashedToCamel(str)>
    <#assign text=""/>
    <#assign strlist = str?split("_")/>
    <#list strlist as v>
        <#assign text=text+v?cap_first/>
    </#list>
    <#return text?uncap_first>
</#function>
<#-- Hump turned underlined -->
<#function camelToDashed(str, case='lower')>
  <#return camelToChar(str, "_", case)>
</#function>
<#---->
<#-- Hump turns horizontal -->
<#function camelToHorizontal(str, case='normal')>
  <#return camelToChar(str, "-", case)>
</#function>
<#---->
<#-- Get the v-model properties -->
<#function getVModel po,suffix="">
  <#return "v-model=\"queryParam.${po.fieldName}${suffix}\"">
</#function>
<#-- FETCH placeholder ATTRIBUTE -->
<#function getPlaceholder po,prefix,fillComment=true>
  <#if fillComment>
    <#return "placeholder=\"${prefix}${po.filedComment}\"">
  <#else>
    <#return "placeholder=\"${prefix}\"">
  </#if>
</#function>
<#-- ** Check whether a field is configured with validation * -->
<#function poHasCheck po>
  <#if (po.fieldValidType!'')?trim?length gt 0 || po.nullable == 'N'>
    <#if po.fieldName != 'id'>
      <#if po.nullable == 'N'
      || po.fieldValidType == '*'
      || po.fieldValidType == 'only'
      || po.fieldValidType == 'n6-16'
      || po.fieldValidType == '*6-16'
      || po.fieldValidType == 's6-18'
      || po.fieldValidType == 'url'
      || po.fieldValidType == 'e'
      || po.fieldValidType == 'm'
      || po.fieldValidType == 'p'
      || po.fieldValidType == 's'
      || po.fieldValidType == 'n'
      || po.fieldValidType == 'z'
      || po.fieldValidType == 'money'
      || po.fieldValidType != ''
      >
        <#return true>
      </#if>
    </#if>
  </#if>
  <#return false>
</#function>
<#-- ** It is displayed if a check is configured validatorRules * -->
<#function autoWriteRules po>
  <#if poHasCheck(po)>
    <#return ", validatorRules.${po.fieldName}">
  <#else>
    <#return "">
  </#if>
</#function>

<#-- ** If the blob is displayed String * -->
<#function autoStringSuffix po>
  <#if  po.fieldDbType=='Blob'>
    <#return "'${po.fieldName}String'">
  <#else>
    <#return "'${po.fieldName}'">
  </#if>
</#function>

<#-- ** If the blob is displayed, the model mode is displayed String * -->
<#function autoStringSuffixForModel po>
    <#if  po.fieldDbType=='Blob'>
        <#return "${po.fieldName}String">
    <#else>
        <#return "${po.fieldName}">
    </#if>
</#function>

<#-- ** Advanced query generation * -->
<#function superQueryFieldList po>
    <#assign superQuery_dictTable="">
    <#assign superQuery_dictText="">
    <#if po.dictTable?default("")?trim?length gt 1>
        <#assign superQuery_dictTable="${po.dictTable}">
    </#if>
    <#if po.dictText?default("")?trim?length gt 1>
        <#assign superQuery_dictText="${po.dictText}">
    </#if>
  <#if po.classType=="popup">
      <#return "{type:'${po.classType}',value:'${po.fieldName}',text:'${po.filedComment}', popup:{code:'${po.dictTable}',field:'${po.dictField?split(',')[0]}',orgFields:'${po.dictField?split(',')[0]}',destFields:'${po.dictText?split(',')[0]}'}}">
  <#elseif po.classType=="sel_user" || po.classType=="sel_depart" || po.classType=="datetime" || po.classType=="date" || po.classType=="pca" || po.classType=="switch">
      <#return "{type:'${po.classType}',value:'${po.fieldName}',text:'${po.filedComment}'}">
  <#else>
      <#if po.classType=="sel_search" || po.classType=="list_multi">
          <#return "{type:'${po.classType}',value:'${po.fieldName}',text:'${po.filedComment}',dictTable:\"${superQuery_dictTable}\", dictText:'${superQuery_dictText}', dictCode:'${po.dictField}'}">
      <#elseif po.dictTable?? && po.dictTable!="" && po.classType!="sel_tree" && po.classType!="cat_tree" && po.classType!="link_down">
          <#return "{type:'${po.fieldDbType}',value:'${po.fieldName}',text:'${po.filedComment}',dictCode:\"${po.dictTable},${po.dictText},${po.dictField}\"}">
      <#elseif po.dictField?? && po.classType!="sel_tree" && po.classType!="cat_tree" && po.classType!="link_down">
          <#return "{type:'${po.fieldDbType}',value:'${po.fieldName}',text:'${po.filedComment}',dictCode:'${po.dictField}'}">
      <#elseif po.fieldDbType=="Text">
          <#return "{type:'string',value:'${po.fieldName}',text:'${po.filedComment}'}">
      <#elseif po.fieldDbType=="Blob">
          <#return "{type:'byte',value:'${po.fieldName}',text:'${po.filedComment}'}">
      <#elseif po.fieldDbType=="BigDecimal" || po.fieldDbType=="double">
          <#return "{type:'number',value:'${po.fieldName}',text:'${po.filedComment}'}">
      <#else>
          <#return "{type:'${po.fieldDbType}',value:'${po.fieldName}',text:'${po.filedComment}'}">
      </#if>
  </#if>
</#function>


<#-- vue3 Get the width of the form modal-->
<#function getModalWidth fieldRowNum>
    <#assign modal_width = 800>
    <#if fieldRowNum==2>
        <#assign modal_width = 896>
    <#elseif fieldRowNum==3>
        <#assign modal_width = 1024>
    <#elseif fieldRowNum==4>
        <#assign modal_width = 1280>
    </#if>
    <#return modal_width>
</#function>

<#-- vue3 Get the form colspan -->
<#function getFormSpan fieldRowNum>
    <#assign form_span = 24>
    <#if fieldRowNum==2>
        <#assign form_span = 12>
    <#elseif fieldRowNum==3>
        <#assign form_span = 8>
    <#elseif fieldRowNum==4>
        <#assign form_span = 6>
    </#if>
    <#return form_span>
</#function>

<#-- vue3 native Determine that the field name is not pidField  -->
<#function isNotPidField(tableVo, fieldDbName) >
  <#assign flag = true>
  <#if tableVo??>
    <#if tableVo.extendParams??>
      <#if tableVo.extendParams.pidField?default("")?trim == fieldDbName>
        <#assign flag = false>
      </#if>
    </#if>
  </#if>
  <#return flag>
</#function>