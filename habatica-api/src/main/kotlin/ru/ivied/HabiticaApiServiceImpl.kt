package ru.ivied

import io.reactivex.functions.BiConsumer
import io.reactivex.schedulers.Schedulers

public class HabiticaApiServiceImpl(apiUser: String, apiKey: String) {

    var TITLE = "Сделать помидорку!"

    val habiticaApi by lazy {
        HabiticaApiService.create(apiUser, apiKey);
    }

    public fun takePomodoro() {
        habiticaApi.getTasks().subscribeOn(Schedulers.io())
                .subscribe { response ->
                    val id = response.data.first { task -> task.text.equals(TITLE) }.id
                    habiticaApi.postTaskDirection(id, "up").subscribeOn(Schedulers.io())
                            .subscribe { responseOnUp -> println(responseOnUp.success) }
                }

    }

    public fun makePomodoro() {
        habiticaApi.getTasks().subscribeOn(Schedulers.io())
                .subscribe { response ->
                    if (response.data.firstOrNull { task -> task.text.equals(TITLE) } == null) {

                        habiticaApi.createTask(Task(TITLE, Task.TYPE_HABIT)).subscribeOn(Schedulers.io()).subscribe()
                    }
                }
    }
}




