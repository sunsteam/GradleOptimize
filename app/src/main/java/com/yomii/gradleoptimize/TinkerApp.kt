package com.yomii.gradleoptimize

import com.tencent.tinker.loader.app.TinkerApplication
import com.tencent.tinker.loader.shareutil.ShareConstants


/**
 * Created by Yomii on 2018/2/9.
 */

class TinkerApp : TinkerApplication(ShareConstants.TINKER_ENABLE_ALL,
        "com.yomii.gradleoptimize.App",
        "com.tencent.tinker.loader.TinkerLoader",
        false)
