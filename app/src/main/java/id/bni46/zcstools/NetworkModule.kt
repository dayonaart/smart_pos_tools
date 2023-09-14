package id.bni46.zcstools

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

object NetworkModule {
    private fun provideOkHttp(): OkHttpClient {
        val timeoutInSeconds = 90L
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient()
            .newBuilder()
            .addInterceptor(logging)
            .connectTimeout(timeoutInSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutInSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutInSeconds, TimeUnit.SECONDS)
            .build()
    }

    fun provideNetwork(
    ): Retrofit {
        val baseUrl = "http://edcwebdev.hq.bni.co.id:8001/AposHost/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(provideOkHttp())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
    }
}

interface NetworkInterface {
    @POST("AndroidApi/ApiHost/InitStep1")
    suspend fun init(@Body initDto: InitDto): InitResponseDto

    @POST("AndroidApi/ApiHost/Logon")
    suspend fun postLogon(@Body body: LogonRequestDto): LogonResponseDto
}

data class InitDto(
    val HSN: String = ""
)

data class LogonRequestDto(
    val mMID: String,
    val mTID: String,
    val serialNo: String,
    val iid: String = "2",
)

data class LogonResponseDto(
    @SerializedName("SECDATA")
    val secData: SECDATA?
)

data class SECDATA(
    @SerializedName("DATAKEY")
    val dataKey: String,
    @SerializedName("MACKEY")
    val macKey: String?,
    @SerializedName("PINKEY")
    val pinKey: String
)

data class InitResponseDto(
    @SerializedName("KEY1")
    val key1: String = "",
    @SerializedName("KEY2")
    val key2: String = "",

    @SerializedName("KEYS1")
    val keys1: String = "",

    @SerializedName("KEYS2")
    val keys2: String = "",

    @SerializedName("KODEAGEN")
    val kodeAgen: String = "",

    @SerializedName("MMID")
    val mmid: String = "",

    @SerializedName("MTID")
    val mtid: String = "",

    @SerializedName("BMID")
    val bmid: String = "",

    @SerializedName("BTID")
    val btid: String = "",

    @SerializedName("RSPC")
    val responseCode: String = "",

    @SerializedName("RSPM")
    val responseMessage: String = ""
)