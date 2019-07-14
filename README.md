# GradleOptimize

Android Studio 3.0 以上项目的常用 Gradle 配置模版，目的是降低新项目接入 tinker+resguard 的配置成本，需要时可以方便找到一些 gradle 配置写法。

1. 使用 sign.properties+local.properties 管理签名文件设置

2. 引入 monitor.gradle 配置 BlockCanary 和 LeakCanary，Project 的 build.gradle 中添加对应插件

3. 引入 bugly.gradle 配置 bugly 异常上报（包括了版本升级功能），Project 的 build.gradle 中添加对应插件（符号表上传的 uploader 插件）

## Tinker+AndResReguard

1. Project 的 build.gradle 中添加对应插件 （tinker support）

2. 引入 tinker.gradle 配置 Tinker+AndResReguard （tinker 中配置了 resguard.gradle，这 2 个文件有加载顺序，所以不放在外面了），这 2 个文件不用改动，能适应正常项目的构建）

3. java 中的初始化按照正常的来，参照 app moudle。

4. 需要配置的全都在 gradle.properties 中。tinkerId 唯一不重复即可，建议保留如下格式分开写，命名规则自定义的，我用的是 versionCode.date.hour，可以修改。

```
#AndResGuard 插件版本
ANDRESGUARD_VERSION=1.2.16

# 基准包位置（没有就留空）
baseApkDir=app-0226-23-26-54
#flavor（没有就留空）
flavorName=
# 构建类型
variantName=release

# 基准包 tinkerId
#outTinkerId=1.0226.23

# 补丁包 tinkerId
outTinkerId=1.0226.23-patch1

```

### 基准包构建流程

1. 定义 gradle.properties 中的 baseApkDir（上一个基准包位置，没有就留空），flavorName（没有就留空），variantName（buildType，小写），tinkerId（每个基准包、补丁包都要不同），
执行 ` ./gradlew resguard[Flavor][BuildType]` 进行打包。

2. tinker 与 assemble 的构建任务绑定执行，生成 proguard mapping 与 resource to R.java 的索引文件， 这个是把资源名与资源 id 对应，比如 `int attr actionBarDivider 0x7f010075`，tinker support内置的 copy
   任务会把生成文件移动到备份文件夹下，多余了，需要手动删除。
   
3. 完成 assemble 后 resguard 会对内置 tinker的 apk 包进行资源的压缩混淆并再次签名对齐，产生 resource mapping 的索引文件，它是对资源混淆的索引，
比如 `com.yomii.gradleoptimize.R.attr.layout_constraintBottom_creator -> com.yomii.gradleoptimize.R.attr.g`

4. resguardTask.doLast 中 copy 任务会把 resguard 生成的 apk 和 resource mapping 文件复制到备份文件夹下，默认 ${module}/bakApk/${tinkerId}，如果是打补丁包那么会不会有备份文件。

5. 假如开启了加固热修复，因此基准包在发布前需要加固，再次签名对齐。打补丁包时对应的基础包是经过 AndResGuard 混淆，但没有加固的那个版本，补丁包不需要加固

### 补丁包构建流程

1. 定义 gradle.properties 中的 baseApkDir（基准包位置，别忘了改），flavorName（没有就留空），variantName（buildType，小写），tinkerId（每个基准包、补丁包都要不同, 别忘了改），执行 ` ./gradlew buildTinkerPatch[Flavor][BuildType]` 进行打包。tinkerPatchTask 执行前会自动执行 resguardTask。

2. tinkerPatchTask.doFirst 中，会将旧基准包（oldApk）与新基准包（buildApk）的位置进行替换，主要是将新基准包替换为经过 resguard 处理的那个版本，保证生成的 patch 是一致的。

3. 去 bak 文件夹下的基准包备份文件夹找生成的对应patch包和log文件，检查一遍 log 确认修改内容无误，bugly 上发布 patch 的 apk 即可，补丁不要加固。

### 参考信息

[每次编译我应该保留哪些文件如何兼容andresguard](https://github.com/Tencent/tinker/wiki/Tinker-%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98#%E6%AF%8F%E6%AC%A1%E7%BC%96%E8%AF%91%E6%88%91%E5%BA%94%E8%AF%A5%E4%BF%9D%E7%95%99%E5%93%AA%E4%BA%9B%E6%96%87%E4%BB%B6%E5%A6%82%E4%BD%95%E5%85%BC%E5%AE%B9andresguard)


