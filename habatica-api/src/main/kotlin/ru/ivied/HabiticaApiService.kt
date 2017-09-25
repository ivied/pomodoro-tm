package ru.ivied

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import org.junit.runner.Request.method
import com.intellij.openapi.vcs.changes.committed.IncomingChangeState.header
import sun.util.logging.resources.logging
import okhttp3.OkHttpClient
import ru.ivied.gson.GsonConverterFactory


interface HabiticaApiService {

    @GET("status")
    fun status(): Single<HabitResponse<Status>>

    companion object {
        fun create(): HabiticaApiService {
            val client = OkHttpClient.Builder()
                    .addNetworkInterceptor { chain ->
                        val original = chain.request()
                        val builder = original.newBuilder()
                                    .header("x-api-key", "fb12a3a5-a417-42cf-abcb-7dbbd34186a6")
                                    .header("x-api-user", "89b92405-60df-49b9-b576-0ccc6dd10139")

                        //builder = builder.header("x-client", "habitica-android")
                        //if (userAgent != null) {
                        //    builder = builder.header("user-agent", userAgent)
                        //}
                        //builder = builder.addHeader("Authorization", "Basic " + BuildConfig.STAGING_KEY)
                        val request = builder.method(original.method(), original.body())
                                .build()
                        //lastAPICallURL = original.url().toString()
                        chain.proceed(request)
                    }
                    .build()

            val retrofit = Retrofit.Builder()
                    .client(client)
                    .addCallAdapterFactory(
                            RxJava2CallAdapterFactory.create())

                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://habitica.com/api/v3/")
                    .build()

            return retrofit.create(HabiticaApiService::class.java)
        }
    }
}


