package pomodoro.widget

import pomodoro.model.PomodoroModel
import pomodoro.model.PomodoroState
import java.util.HashMap

class BulletModel {


    var bulletState = BulletState.Stop;

    enum class BulletState{
        Start,
        Stop

    }


    private val listeners = HashMap<Any, Listener>()

    fun addListener(key: Any, listener: Listener) {
        listeners.put(key, listener)
    }


    interface Listener {
        fun onStateChange( state: BulletState)
    }

    fun askUserAboutPomodoro() {
        bulletState = BulletState.Stop
        listeners.values.forEach {it.onStateChange(bulletState)}
    }


    fun startBullet() {
        bulletState = BulletState.Start
        listeners.values.forEach {it.onStateChange(bulletState)}
    }

    fun revertState() {
        bulletState = if (bulletState == BulletState.Start) BulletState.Stop else BulletState.Start;

        listeners.values.forEach {it.onStateChange(bulletState)}
    }

}