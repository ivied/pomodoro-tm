package pomodoro.widget

import com.android.tools.sherpa.drawing.decorator.ImageViewWidget
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.wm.CustomStatusBarWidget
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.util.ui.UIUtil
import pomodoro.PomodoroComponent
import pomodoro.modalwindow.ModalDialog
import pomodoro.model.PomodoroModel
import pomodoro.model.PomodoroState
import pomodoro.model.Settings
import pomodoro.model.time.Time
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.swing.text.IconView
import kotlin.concurrent.timerTask


class BulletWidget : CustomStatusBarWidget, StatusBarWidget.Multiframe, Settings.ChangeListener {
    var picLabel = JLabel()
    private val model:BulletModel
    private lateinit var statusBar: StatusBar



    init {
        val pomodoroComponent = ApplicationManager.getApplication().getComponent(PomodoroComponent::class.java)!!
        model = pomodoroComponent.bulletModel
        updateIcon(BulletModel.BulletState.Stop)
        model.addListener(this, object : BulletModel.Listener{
            override fun onStateChange(state: BulletModel.BulletState) {

                updateIcon(state)
                when(state){
                    BulletModel.BulletState.Start -> {
                        startBullet()

                    }

                }
            }

        })

        val settings = Settings.instance
       /* model.addListener(this, object : BulletModel.Listener {
            override fun onStateChange(state: PomodoroState, wasManuallyStopped: Boolean) {
            }
        })*/
        picLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {

                model.revertState()
            }

        })
    }

    private fun updateIcon(state: BulletModel.BulletState) {
        picLabel.icon = model.bulletState.icon(state)
        picLabel.repaint()
    }

    private fun startBullet() {
        Timer().schedule(object : TimerTask() {

            public override fun run() {
                model.askUserAboutPomodoro()
            }
        }, 60000)
    }

    override fun getPresentation(type: StatusBarWidget.PlatformType) = null

    override fun getComponent(): JComponent = picLabel

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
    }

    override fun copy(): StatusBarWidget = BulletWidget()

    override fun dispose() {
        //model.removeListener(this)
    }

    override fun ID() = "Bullet"


    override fun onChange(newSettings: Settings) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    companion object {
        private val bulletIconDracula = loadIcon("/resources/bullet.png")
        private val bulletFireIconDracula = loadIcon("/resources/bullet_fire.png")
        private val bulletFireIcon = loadIcon("/resources/bullet_fire_black.png")
        private val bulletIcon = loadIcon("/resources/bullet_black.png")

        private fun loadIcon(filePath: String) = ImageIcon(PomodoroWidget::class.java.getResource(filePath))
    }

    private fun BulletModel.BulletState.icon(state: BulletModel.BulletState) : ImageIcon {
        val underDarcula = UIUtil.isUnderDarcula()
        return when (state) {
            BulletModel.BulletState.Stop -> if (underDarcula) BulletWidget.bulletIconDracula else BulletWidget.bulletIcon
            BulletModel.BulletState.Start-> if (underDarcula) BulletWidget.bulletFireIconDracula else BulletWidget.bulletFireIcon
        }
    }

}

