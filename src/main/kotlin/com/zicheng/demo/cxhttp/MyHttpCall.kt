package com.zicheng.demo.cxhttp

import JSON_PROJECTS
import JSON_USER_INFO
import TEST_URL_USER_PROJECTS
import TEST_URL_USER_UPDATE
import com.zicheng.net.cxhttp.call.CxHttpCall
import com.zicheng.net.cxhttp.call.Okhttp3Call
import com.zicheng.net.cxhttp.response.Response
import com.zicheng.net.cxhttp.request.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class MyHttpCall: CxHttpCall {

    private val okhttp3Call = Okhttp3Call{
        callTimeout(15, TimeUnit.SECONDS)
        addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    }

    override suspend fun await(request: Request): Response {
        when (request.url) {
            TEST_URL_USER_UPDATE -> {
                return Response(200, "", object: Response.Body(){
                    override fun string(): String {
                        return JSON_USER_INFO
                    }
                })
            }
            TEST_URL_USER_PROJECTS -> {
                return Response(200, "", object: Response.Body(){
                    override fun string(): String {
                        return JSON_PROJECTS
                    }
                })
            }
            else -> {
                return okhttp3Call.await(request)
            }
        }
    }

}