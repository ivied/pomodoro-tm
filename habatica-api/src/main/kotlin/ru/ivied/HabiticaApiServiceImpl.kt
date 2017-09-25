package ru.ivied

public class HabiticaApiServiceImpl{


    val habiticaApi by lazy {
        HabiticaApiService.create();
    }

    public fun getStatus(){
        println(habiticaApi.status().blockingGet().data.status)
    }


}
