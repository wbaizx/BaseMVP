<#import "../../common/shared_manifest_macros.ftl" as manifestMacros>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
       <activity android:name="${packageName}.${activityClass}"/>
    </application>
</manifest>
