<?xml version="1.0"?>
<globals>
    <global id="hasNoActionBar" type="boolean" value="false" />
    <global id="simpleLayoutName" value="${layoutName}" />
    <global id="excludeMenu" type="boolean" value="true" />
    <global id="generateActivityTitle" type="boolean" value="false" />
    <global id="nativeSrcOut" value="${escapeXmlAttribute(projectOut)}/src/main/cpp" />
    <#include "../common/common_globals.xml.ftl" />
</globals>
