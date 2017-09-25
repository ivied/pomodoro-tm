package ru.ivied

import io.reactivex.functions.BiConsumer

public class HabiticaApiServiceImpl(apiUser: String, apiKey: String) {

    var TITLE = "Сделать помидорку!"

    val habiticaApi by lazy {
        HabiticaApiService.create(apiUser, apiKey);
    }

    public fun takePomodoro() {
        //val task = habiticaApi.getTask("4ac36649-532c-4855-914c-90893dc2afd3").blockingGet().data
        habiticaApi.getTasks()
                .subscribe { response ->
                    val id = response.data.first { task -> task.text.equals(TITLE) }.id
                    habiticaApi.postTaskDirection(id, "up")
                            .subscribe { responseOnUp -> println(responseOnUp.success) }
                }
        //task.counterUp = task.counterUp.inc()
        // println(habiticaApi.updateTask("4ac36649-532c-4855-914c-90893dc2afd3", task).blockingGet().data.counterUp)

    }

    public fun makePomodoro() {
        habiticaApi.getTasks()
                .subscribe { response ->
                    if (response.data.firstOrNull { task -> task.text.equals(TITLE) } == null) {

                        habiticaApi.createTask(Task(TITLE, Task.TYPE_HABIT)).subscribe()
                    }
                }
    }
}




