/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pomodoro.settings

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import org.jetbrains.annotations.Nls
import pomodoro.PomodoroComponent
import pomodoro.RingSound
import pomodoro.UIBundle
import pomodoro.model.Settings
import java.awt.event.ActionListener
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.event.ChangeListener

class SettingsPresenter constructor(private val settings: Settings = PomodoroComponent.settings) : SearchableConfigurable {
    private var settingsForm: SettingsForm? = null
    private var uiModel: Settings? = null
    private var updatingUI: Boolean = false
    private val ringSound: RingSound
    private var lastUIRingVolume = -1

    init {
        this.ringSound = RingSound()
    }

    override fun createComponent(): JComponent? {
        settingsForm = SettingsForm()
        uiModel = Settings()

        setupUIBindings()

        return settingsForm!!.rootPanel
    }

    private fun setupUIBindings() {
        lastUIRingVolume = uiModel!!.getRingVolume()
        val actionListener = ActionListener {
            updateUIModel()
            updateUI()
        }
        val changeListener = ChangeListener { actionListener.actionPerformed(null) }
        settingsForm!!.apply {
            pomodoroLengthComboBox.addActionListener(actionListener)
            breakLengthComboBox.addActionListener(actionListener)
            longBreakLengthComboBox.addActionListener(actionListener)
            longBreakFrequencyComboBox.addActionListener(actionListener)
            popupCheckBox.addChangeListener(changeListener)
            blockDuringBreak.addChangeListener(changeListener)
            ringVolumeSlider.addChangeListener(changeListener)
            showToolWindowCheckbox.addChangeListener(changeListener)
            showTimeInToolbarWidgetCheckbox.addChangeListener(changeListener)
        }
    }

    override fun disposeUIResources() {
        settingsForm = null
    }

    override fun isModified(): Boolean {
        return uiModel != settings
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        settings.loadState(uiModel)
    }

    override fun reset() {
        uiModel!!.loadState(settings)
        updateUI()
    }

    private fun updateUIModel() {
        if (uiResourcesDisposed()) return
        if (updatingUI) return

        try {
            uiModel!!.pomodoroLengthInMinutes = selectedItemAsInteger(settingsForm!!.pomodoroLengthComboBox)!!
        } catch (e: NumberFormatException) {
            uiModel!!.pomodoroLengthInMinutes = Settings.DEFAULT_POMODORO_LENGTH
        }

        try {
            uiModel!!.breakLengthInMinutes = selectedItemAsInteger(settingsForm!!.breakLengthComboBox)!!
        } catch (e: NumberFormatException) {
            uiModel!!.breakLengthInMinutes = Settings.DEFAULT_BREAK_LENGTH
        }

        try {
            uiModel!!.longBreakLengthInMinutes = selectedItemAsInteger(settingsForm!!.longBreakLengthComboBox)!!
        } catch (e: NumberFormatException) {
            uiModel!!.longBreakLengthInMinutes = Settings.DEFAULT_LONG_BREAK_LENGTH
        }

        try {
            uiModel!!.setLongBreakFrequency(selectedItemAsInteger(settingsForm!!.longBreakFrequencyComboBox)!!)
        } catch (e: NumberFormatException) {
            uiModel!!.setLongBreakFrequency(Settings.DEFAULT_LONG_BREAK_FREQUENCY)
        }

        uiModel!!.setRingVolume(settingsForm!!.ringVolumeSlider.value)
        if (lastUIRingVolume != uiModel!!.getRingVolume()) {
            lastUIRingVolume = uiModel!!.getRingVolume()
            ringSound.play(uiModel!!.getRingVolume())
        }

        uiModel!!.isPopupEnabled = settingsForm!!.popupCheckBox.isSelected
        uiModel!!.isBlockDuringBreak = settingsForm!!.blockDuringBreak.isSelected
        uiModel!!.isShowToolWindow = settingsForm!!.showToolWindowCheckbox.isSelected
        uiModel!!.isShowTimeInToolbarWidget = settingsForm!!.showTimeInToolbarWidgetCheckbox.isSelected
    }

    private fun updateUI() {
        if (uiResourcesDisposed()) return
        if (updatingUI) return
        updatingUI = true

        settingsForm!!.apply {
            pomodoroLengthComboBox.model.selectedItem = uiModel!!.pomodoroLengthInMinutes.toString()
            breakLengthComboBox.model.selectedItem = uiModel!!.breakLengthInMinutes.toString()
            longBreakLengthComboBox.model.selectedItem = uiModel!!.longBreakLengthInMinutes.toString()
            longBreakFrequencyComboBox.model.selectedItem = uiModel!!.getLongBreakFrequency().toString()

            ringVolumeSlider.value = uiModel!!.getRingVolume()
            ringVolumeSlider.toolTipText = ringVolumeTooltip(uiModel!!)

            popupCheckBox.isSelected = uiModel!!.isPopupEnabled
            blockDuringBreak.isSelected = uiModel!!.isBlockDuringBreak
            showToolWindowCheckbox.isSelected = uiModel!!.isShowToolWindow
            showTimeInToolbarWidgetCheckbox.isSelected = uiModel!!.isShowTimeInToolbarWidget
        }

        updatingUI = false
    }

    private fun uiResourcesDisposed(): Boolean {
        // ActionEvent might occur after disposeUIResources() was invoked
        return settingsForm == null
    }

    @Nls private fun ringVolumeTooltip(uiModel: Settings): String {
        if (uiModel.getRingVolume() == 0) {
            return UIBundle.message("settings.ringSlider.noSoundTooltip")
        } else {
            return UIBundle.message("settings.ringSlider.volumeTooltip", uiModel.getRingVolume())
        }
    }

    @Nls override fun getDisplayName() = UIBundle.message("settings.title")!!

    override fun getHelpTopic() = null

    override fun getId() = "Pomodoro"

    override fun enableSearch(option: String?) = null

    companion object {
        private const val MIN_TIME_INTERVAL = 1
        private const val MAX_TIME_INTERVAL = 240

        private fun selectedItemAsInteger(comboBox: JComboBox<*>): Int? {
            val s = (comboBox.selectedItem as String).trim { it <= ' ' }
            val value = Integer.valueOf(s)
            if (value < MIN_TIME_INTERVAL) return MIN_TIME_INTERVAL
            if (value > MAX_TIME_INTERVAL) return MAX_TIME_INTERVAL
            return value
        }
    }
}