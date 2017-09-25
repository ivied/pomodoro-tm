package ru.ivied

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.http.*
import ru.ivied.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST






interface HabiticaApiService {

    @GET("status")
    fun status(): Single<HabitResponse<Status>>


    @GET("tasks/user")
    fun getTasks(): Single<HabitResponse<ArrayList<Task>>>


    @GET("tasks/{id}")
    fun getTask(@Path("id") id: String): Single<HabitResponse<Task>>

    @POST("tasks/user")
    fun createTask(@Body item: Task): Single<HabitResponse<Task>>


    @PUT("tasks/{id}")
    fun updateTask(@Path("id") id: String, @Body item: Task): Single<HabitResponse<Task>>


    @POST("tasks/{id}/score/{direction}")
    fun postTaskDirection(@Path("id") id: String, @Path("direction") direction: String): Single<HabitResponse<TaskDirectionData>>

    companion object {
        fun create(apiUser: String, apiKey: String): HabiticaApiService {
            val client = OkHttpClient.Builder()
                    .addNetworkInterceptor { chain ->
                        val original = chain.request()
                        val builder = original.newBuilder()
                                    .header("x-api-key", apiKey)
                                    .header("x-api-user", apiUser)

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


