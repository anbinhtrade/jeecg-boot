<#include "../utils.ftl">
<#if po.isShow == 'Y' && poHasCheck(po)>
    <#if po.fieldName != 'id'>
           ${po.fieldName}: [
        <#assign fieldValidType = po.fieldValidType!''>
    <#-- Non-null check -->
        <#if po.nullable == 'N' || fieldValidType == '*'>
              { required: true, message: 'Please enter ${po.filedComment}!'},
        <#elseif fieldValidType!=''>
              { required: false},
        </#if>
    <#-- Unique verification -->
        <#if fieldValidType == 'only'>
              { validator: (rule, value, callback) => validateDuplicateValue(<#if sub?default("")?trim?length gt 1>'${sub.tableName}'<#else>'${tableName}'</#if>, '${po.fieldDbName}', value, this.model.id, callback)},
        <#-- 6 to 16 digits -->
        <#elseif fieldValidType == 'n6-16'>
              { pattern: /^\d{6,16}$/, message: 'Please enter 6 to 16 digits!'},
        <#-- Any character from 6 to 16 characters -->
        <#elseif fieldValidType == '*6-16'>
              { pattern: /^.{6,16}$/, message: 'Please enter any characters from 6 to 16 characters!'},
        <#-- 6 to 18 digit string -->
        <#elseif fieldValidType == 's6-18'>
              { pattern: /^.{6,18}$/, message: 'Please enter any characters from 6 to 18 characters!'},
        <#-- URL -->
        <#elseif fieldValidType == 'url'>
              { pattern: /^((ht|f)tps?):\/\/[\w\-]+(\.[\w\-]+)+([\w\-.,@?^=%&:\/~+#]*[\w\-@?^=%&\/~+#])?$/, message: 'Please enter the correct URL!'},
        <#-- Email -->
        <#elseif fieldValidType == 'e'>
              { pattern: /^([\w]+\.*)([\w]+)@[\w]+\.\w{3}(\.\w{2}|)$/, message: 'Please enter correct email!'},
        <#--Mobile phone number -->
        <#elseif fieldValidType == 'm'>
              { pattern: /^1[3456789]\d{9}$/, message: 'Please enter the correct phone number!'},
        <#-- Postal code -->
        <#elseif fieldValidType == 'p'>
              { pattern: /^[0-9]\d{5}$/, message: 'Please enter the correct postal code!'},
        <#-- letters -->
        <#elseif fieldValidType == 's'>
              { pattern: /^[A-Z|a-z]+$/, message: 'Please enter letters!'},
        <#-- Number -->
        <#elseif fieldValidType == 'n'>
              { pattern: /^-?\d+\.?\d*$/, message: 'Please enter the number!'},
        <#-- Integer -->
        <#elseif fieldValidType == 'z'>
              { pattern: /^-?\d+$/, message: 'Please enter an integer!'},
        <#-- Amount -->
        <#elseif fieldValidType == 'money'>
              { pattern: /^(([1-9][0-9]*)|([0]\.\d{0,2}|[1-9][0-9]*\.\d{0,2}))$/, message: 'Please enter the correct amount!'},
        <#-- Regularity verification -->
        <#elseif fieldValidType != '' && fieldValidType != '*'>
              { pattern: '${fieldValidType}', message: 'Does not meet the verification rules!'},
        <#-- No verification -->
        <#else>
            <#t>
        </#if>
           ],
    </#if>
</#if>