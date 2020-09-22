<?xml version="1.0"?>
<#import "root://activities/common/kotlin_macros.ftl" as kt>
<recipe>
	<!-- 生成manifest代码 -->
    <#include "../common/recipe_manifest.xml.ftl" />
    <@kt.addAllKotlinDependencies />

	<!-- 生成xml代码 -->
	<#if generateLayout || (includeCppSupport!false)>
		<#include "../common/recipe_simple.xml.ftl" />
		<open file="${escapeXmlAttribute(resOut)}/layout/${layoutName}.xml" />
	</#if>
				   
    <!-- 生成文件代码 -->
    <instantiate from="root/src/app_package/TemplateActivity.${ktOrJavaExt}.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${activityClass}.${ktOrJavaExt}" />
				   
    <instantiate from="root/src/app_package/TemplateContract.${ktOrJavaExt}.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${mvpCommonName}Contract.${ktOrJavaExt}" />
				   
    <instantiate from="root/src/app_package/TemplatePresenter.${ktOrJavaExt}.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${mvpCommonName}Presenter.${ktOrJavaExt}" />
				   
	<instantiate from="root/src/app_package/TemplateModel.${ktOrJavaExt}.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${mvpCommonName}Model.${ktOrJavaExt}" />

    <open file="${escapeXmlAttribute(srcOut)}/${activityClass}.${ktOrJavaExt}" />
</recipe>
