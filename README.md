中原找房——Android版本

***************************

1.控件命名规则说明：
以在xml布局里设置id为名

```xml
<CheckBox
   android:id="@+id/main_cb_home"
   style="@style/mainTabStyle"
   android:drawableTop="@drawable/home"
   android:checked="true"
   android:text="首页"/>
```

```
private CheckBox main_cb_home;
```

2.添加第三方库，如果有代码混淆配置，需要到proguard-rules.pro设置一下

3.xml布局文件命名规则，activity对应act开头，fragment以frag开头，
adapter的布局以item开头，ViewStub布局以vs开头，merge布局以mer开头，
其他都以layout开头

4.热修复补丁包命令
java -jar BCFixPatchTools-1.3.0.jar -c patch -s old.apk -f new.apk -w patch-out -k findproperty.jks -p centanetfindproperty -a findproperty -e centanetfindproperty
5. 打包命令
gradlew assembleRelease