<?xml version="1.0"?>
<#import "root://activities/common/kotlin_macros.ftl" as kt>
<recipe>

    <!--  生成mainfest配置  -->
    <merge from="root/AndroidManifest.xml.ftl"
             to="${escapeXmlAttribute(manifestOut)}/AndroidManifest.xml" />

	<!--  生成布局文件 -->
    <instantiate from="root/res/layout/activity_template.xml.ftl"
                   to="${escapeXmlAttribute(resOut)}/layout/${layoutName}.xml" />
				   
    <!-- 生成文件代码 -->
    <instantiate from="root/src/app_package/TemplateActivity.${ktOrJavaExt}.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${activityClass}.${ktOrJavaExt}" />
				   
    <instantiate from="root/src/app_package/TemplateRepository.${ktOrJavaExt}.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${mvvmCommonName}Repository.${ktOrJavaExt}" />

    <instantiate from="root/src/app_package/TemplateViewModel.${ktOrJavaExt}.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${mvvmCommonName}ViewModel.${ktOrJavaExt}" />	
				   
	<instantiate from="root/src/app_package/TemplateDI.${ktOrJavaExt}.ftl"
                   to="${escapeXmlAttribute(srcOut)}/DI.${ktOrJavaExt}" />

    <open file="${escapeXmlAttribute(srcOut)}/${activityClass}.${ktOrJavaExt}" />
</recipe>
