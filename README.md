# GradleOptimize

Android Studio 3.0 以上项目的常用 Gradle 配置模版，目的是降低新项目接入 tinker+resguard 的配置成本，需要时可以方便找到一些 gradle 配置写法。

1. 使用 sign.properties+local.properties 管理签名文件设置

2. 引入 monitor.gradle 配置 BlockCanary 和 LeakCanary，Project 的 build.gradle 中添加对应插件

3. 引入 bugly.gradle 配置 bugly 异常上报（包括了版本升级功能），Project 的 build.gradle 中添加对应插件（符号表上传的 uploader 插件）

### Tinker+AndResReguard 详细讲一下

1. Project 的 build.gradle 中添加对应插件 （tinker support）

2. 引入 tinker.gradle 配置 Tinker+AndResReguard （tinker 中配置了 resguard.gradle，这 2 个文件有加载顺序，所以不放在外面了），这 2 个文件不用改动，能适应正常项目的构建）

3. java 中的初始化按照正常的来，参照 app moudle。

4. 需要配置的全都在 gradle.properties 中。tinkerId 唯一不重复即可，建议保留如下格式分开写，命名规则自定义的，我用的是 versionCode.date.hour，可以修改。

```
#AndResGuard 插件版本
ANDRESGUARD_VERSION=1.2.11

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

#### 基准包构建流程

1. 定义 gradle.properties 中的 baseApkDir（上一个基准包位置，没有就留空），flavorName（没有就留空），variantName（buildType，小写），tinkerId（每个基准包、补丁包都要不同），执行 ` ./gradlew resguard[Flavor][BuildType]` 进行打包。

2. tinker 与 assemble 的构建任务绑定执行，生成 proguard mapping 与 resource to R.java 的索引文件， 这个是把资源名与资源 id 对应，比如 `int attr actionBarDivider 0x7f010075`，tinker 内置的 copy 任务会把生成文件移动到备份文件夹下，默认 ${module}/bakApk 下。

3. 完成 assemble 后 resguard 会对内置 tinker 的 apk 包进行资源的压缩混淆并再次签名对齐，产生 resource mapping 的索引文件，它是对资源混淆的索引， 比如 `com.yomii.gradleoptimize.R.attr.layout_constraintBottom_creator -> com.yomii.gradleoptimize.R.attr.g`

4. resguardTask.doLast 中 copy 任务会把 resguard 生成的 apk 和 resource mapping 文件复制到备份文件夹下，默认 ${module}/bakApk，需要手动移到对应打包时间的文件夹里。

5. 默认开启了加固热修复，因此基准包在发布前需要加固，再次签名对齐。

#### 补丁包构建流程

1. 定义 gradle.properties 中的 baseApkDir（基准包位置，别忘了改），flavorName（没有就留空），variantName（buildType，小写），tinkerId（每个基准包、补丁包都要不同, 别忘了改），执行 ` ./gradlew buildTinkerPatch[Flavor][BuildType]` 进行打包。tinkerPatchTask 执行前会自动执行 resguardTask。

2. tinkerPatchTask.doFirst 中，会将旧基准包（oldApk）与新基准包（buildApk）的位置进行替换，主要是将新基准包替换为经过 resguard 处理的那个版本，保证生成的 patch 是一致的。

3. 去 build/outputs/patch 下找较小的那个 sign.apk（有时候补丁包 7zip 压缩完反而比较大），bugly 上发布补丁即可，补丁不要加固。

4. 打补丁包时在 bakApk 下生成的文件没什么用，删掉就行。毕竟这次构建要的是热修复补丁，下次发新版本时候没准别的地方改了，还要按照基准包流程构建的。补丁建议也保留到对应基准包那个 bak 文件夹下。


