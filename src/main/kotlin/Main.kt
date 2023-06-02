import com.zicheng.demo.bean.MyHttpResult
import com.zicheng.demo.bean.ProjectInfo
import com.zicheng.demo.bean.UserInfo
import com.zicheng.demo.cxhttp.MyHttpCall
import com.zicheng.net.cxhttp.CxHttp
import com.zicheng.net.cxhttp.CxHttpHelper
import com.zicheng.net.cxhttp.converter.JacksonConverter
import com.zicheng.net.cxhttp.entity.CxHttpResult
import com.zicheng.net.cxhttp.hook.HookRequest
import com.zicheng.net.cxhttp.hook.HookResult
import com.zicheng.net.cxhttp.request.Request
import kotlinx.coroutines.*


const val JSON_USER_INFO = "{\"code\":200,\n" +
        "\"errorMsg\":\"请求成功\",\n" +
        "\"data\":{\n" +
        "\"id\":\"001\",\n" +
        "\"name\":\"小张\",\n" +
        "\"age\":\"32\",\n" +
        "\"gender\":\"男\",\n" +
        "\"occupation\":\"搬砖\"\n" +
        "}\n" +
        "}"
const val JSON_PROJECTS = "{\n" +
        "    \"code\":200,\n" +
        "    \"errorMsg\":\"请求成功\",\n" +
        "    \"data\":[\n" +
        "        {\n" +
        "            \"name\":\"banner-android\",\n" +
        "            \"url\":\"https://github.com/zicheng2019/banner-android.git\",\n" +
        "            \"groupId\":\"banner-android\",\n" +
        "            \"artifactId\":\"com.github.zicheng2019\",\n" +
        "            \"version\":\"1.0.5\",\n" +
        "            \"mavenUrl\":\"https://jitpack.io\",\n" +
        "            \"desc\":\"Android Kotlin基于ViewPage2和ViewBinding的轻量级BannerView轮播图；简洁、高效、功能强大，一行代码轻松实现循环轮播，一屏三页任意变，Item样式任意定制扩展\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"name\":\"linechart-android\",\n" +
        "            \"url\":\"https://github.com/zicheng2019/linechart-android.git\",\n" +
        "            \"groupId\":\"linechart-android\",\n" +
        "            \"artifactId\":\"com.github.zicheng2019\",\n" +
        "            \"version\":\"1.0.4\",\n" +
        "            \"mavenUrl\":\"https://jitpack.io\",\n" +
        "            \"desc\":\"LineChartView（折线图，动态折线图）静态波形绘制View，LiveLineChartView（实时折线图）动态实时波形绘制View，简单易用\"\n" +
        "        }\n" +
        "    ]\n" +
        "}"

const val TEST_URL_USER_UPDATE = "test://www.******.com/user/update"
const val TEST_URL_USER_PROJECTS = "test://www.******.com/user/projects"

fun main(args: Array<String>) {
    val jacksonConverter = JacksonConverter(MyHttpResult::class.java)
    CxHttpHelper.init(scope = MainScope(), debugLog = true, call = MyHttpCall(), converter = jacksonConverter)
    CxHttpHelper.setHookRequest(object: HookRequest {
        override fun invoke(request: Request): Request {
            //此处可添加一些公共参数和头信息
            request.param("id", "123456")
            request.header("token", "1a2b3c4d5e6f")
            return request
        }
    })
    CxHttpHelper.setHookResult(object: HookResult{
        override suspend fun <RESULT : CxHttpResult<*>> invoke(result: RESULT): RESULT {
            //此处可以预处理请求结果，例如token失效自动刷新并重试功能、制作假数据测试等等
            result.request
            result.setReRequest(false)//设置是否重新请求，默认false
            return result
        }
    })
    runBlocking {
        val job = CxHttp.get("https://www.baidu.com")
            //此处可指定协程，不指定默认使用CxHttpHelper.scope
            .launch(String::class.java, this){ resultGet1 ->
            println("resultGet1: $resultGet1")
        }
        val resultGet2: MyHttpResult<String> = CxHttp.get("https://www.baidu.com").await(String::class.java)
        println("resultGet2: $resultGet2")

        val resultDeferred = CxHttp.get("https://www.baidu.com").async<String, MyHttpResult<String>>(String::class.java)
        val resultGet3 = resultDeferred.await()
        println("resultGet3: $resultGet3")

        //reqBodyConverter可单独自定义，实现RequestBodyConverter接口即可，默认使用CxHttpHelper.init()指定的全局converter
        CxHttp.post(TEST_URL_USER_UPDATE, paramMap= mapOf(
            "name" to "zhangzicheng",
            "age" to 32,
            "gender" to "男",
            "occupation" to "农民"), reqBodyConverter = jacksonConverter)
            .launch<UserInfo, MyHttpResult<UserInfo>>(UserInfo::class.java){ resultPost1 ->
                println("resultPost1: $resultPost1")
            }
        CxHttp.post(TEST_URL_USER_UPDATE, paramEntity = UserInfo("zhangzicheng", 32, "男", "农民"))
            .launch<UserInfo, MyHttpResult<UserInfo>>(UserInfo::class.java){ resultPost2 ->
                println("resultPost2: $resultPost2")
            }

        CxHttp.post(TEST_URL_USER_PROJECTS)
            .param("page", 1)
            .param("pageSize", 2)
            .launchToList<ProjectInfo, MyHttpResult<List<ProjectInfo>>>(ProjectInfo::class.java){ resultPost3 ->
                println("resultPost3: $resultPost3")
            }


    }
}