package com.zicheng.demo.bean

import com.zicheng.net.cxhttp.response.CxHttpResult

data class MyHttpResult<T>(val code: Int,
                           val errorMsg: String,
                           val data: T?): CxHttpResult<T>(code.toString(), errorMsg, data)