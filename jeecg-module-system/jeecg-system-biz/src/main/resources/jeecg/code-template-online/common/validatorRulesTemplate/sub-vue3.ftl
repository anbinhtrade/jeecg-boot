<#include "../utils.ftl">
    <#if col.isShow == 'Y' && poHasCheck(col)>
        validateRules: [
        <#if col.fieldName != 'id'>
            <#assign subFieldValidType = col.fieldValidType!''>
        <#-- Non-null check -->
            <#if col.nullable == 'N' || subFieldValidType == '*'>
          { required: true, message: '${'$'}{title}. It can't be empty' },
            <#elseif subFieldValidType!=''>
          { required: false},
            </#if>
        <#-- In other cases, as long as there is a value, it is considered a regular check -->
            <#if subFieldValidType?length gt 0>
            <#assign subMessage = 'Incorrect format'>
            <#if subFieldValidType == 'only' >
                <#assign subMessage = 'Cannot be repeated'>
            </#if>
          { pattern: "${subFieldValidType}", message: "${'$'}{title}${subMessage}" }
                <#t>
            </#if>
        </#if>
        ],
    </#if>
