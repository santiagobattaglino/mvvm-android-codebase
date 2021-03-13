package com.santiagobattaglino.mvvm.codebase.util

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import java.net.URL

class GlideUrlCustomCacheKey : GlideUrl {
    constructor(url: String?) : super(url)
    constructor(url: String?, headers: Headers?) : super(url, headers)
    constructor(url: URL?) : super(url)
    constructor(url: URL?, headers: Headers?) : super(url, headers)

    override fun getCacheKey(): String {
        val url = toStringUrl()
        return if (url.contains("?")) {
            url.substring(0, url.lastIndexOf("?"))
        } else {
            url
        }
    }
}