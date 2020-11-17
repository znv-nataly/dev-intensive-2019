package ru.skillbranch.devintensive


import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.extensions.hideKeyboard
import ru.skillbranch.devintensive.extensions.isKeyboardClosed
import ru.skillbranch.devintensive.models.Bender


const val SAVE_STATE_STATUS = "STATUS"
const val SAVE_STATE_QUESTION = "QUESTION"

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var benderImage: ImageView
    lateinit var textTxt: TextView
    lateinit var messageEt: EditText
    lateinit var sendBtn: ImageView

    lateinit var benderObj: Bender


    /**
     * Вызывается при первом создании или перезапуске Activity.
     * Здесь задается внешний вид активности (UI) через метод setContentView().
     * Инициализируются представления
     * Представления связываются с необходимыми данными и ресурсами
     * Связываются данные со списками
     *
     * Этот метод также предоставляет Bundle, содержащий ранее сохраненное состояние Activity, если оно было.
     *
     * Всегда сопровождается вызовом onStart().
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        benderImage = findViewById(R.id.iv_bender) as ImageView
        benderImage = iv_bender
        textTxt = tv_text
        messageEt = et_message
        sendBtn = iv_send

        val status = savedInstanceState?.getString(SAVE_STATE_STATUS) ?: Bender.Status.NORMAL.name
        val question = savedInstanceState?.getString(SAVE_STATE_QUESTION) ?: Bender.Question.NAME.name
        benderObj = Bender(Bender.Status.valueOf(status), Bender.Question.valueOf(question))

        Log.d("M_MainActivity", "onCreate $status $question")

        val (r, g, b) = benderObj.status.color
        benderImage.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY)

        textTxt.text = benderObj.askQuestion()
        sendBtn.setOnClickListener(this)

        et_message.setOnEditorActionListener() { view, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendBtn.callOnClick()
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }


    /**
     * Если Activity возвращается в приоритетный режим после вызова onStop(), то в этом случае вызывается метод onRestart().
     * Т.е. вызывается после того, как Activity была остановлена и снова была запущена пользователем.
     * Всегда сопровождается вызовом метода onStart().
     *
     * Используется для специальных действий, которые должны выполняться только при повторном запуске Activity
     */
    override fun onRestart() {
        super.onRestart()
        Log.d("M_MainActivity", "onRestart")
    }


    /**
     * При вызове onStart() окно еще не видно пользователю, но вскоре будет видно.
     * Вызывается непосредственно перед тем, как активность становится видимой пользователю.
     *
     * Чтение из базы данных
     * Запуск сложной анимации
     * Запуск потоков, отслеживания показаний датчиков, запросов к GPS, таймеров, сервисов или других процессов,
     * которые нужны исключительно для обновления пользовательского интерфейса
     *
     * Затем следует onResume(), если Activity выходит на передний план
     */
    override fun onStart() {
        super.onStart()
        Log.d("M_MainActivity", "onStart")
    }


    /**
     * Вызывается, когда Activity начинает взаимодействовать с пользователемю
     *
     * Запуск воспроизведения анимации, аудио и видео
     * Ругистрации любых BroadcastReceiver или других процессов, которые вы освободили/приостановили в OnPause()
     * Выполнение любых других инициализаций,которые должны происходить, когда Activity вновь активна (камера).
     *
     * Тут должен быть максимально легкий и быстрый код, чтобы приложение оставалось отзывчивым
     */
    override fun onResume() {
        super.onResume()
        Log.d("M_MainActivity", "onResume")
    }


    /**
     * Метод onPause() вызывается после сворачивания текущей активности или перехода к новому.
     * От onPause() можно перейти к вызову onResume() или onStop().
     *
     *
     * Остановка анимации, аудио и видео
     * Сохранение состояния пользовательского ввода (легкие процессы)
     * Сохранение в DB, если данные должны быть доступны в новой Activity
     * Остановка сервисов, подписок, BroadcastReceiver
     *
     * Тут должен быть максимально легкий и быстрый код, чтобы приложение оставалось отзывчивым
     */
    override fun onPause() {
        super.onPause()
        Log.d("M_MainActivity", "onPause")
    }


    /**
     * Метод onPause() вызывается, когда Activity становится невидимой для пользователя.
     * Это может произойти при её уничтожении, или если была запущена другая Activity (существующая или новая),
     * перекрывшая окно текущей Activity.
     *
     * Запись в БД
     * Приостановка сложной анимации
     * Приостановка потоков, отслеживания показаний датчиков, запросов к GPS, таймеров, сервисов или других процессов,
     * которые нужны исключительно для обновления пользовательского интерфейса
     *
     * Не вызывается при вызове метода finish() у Activity
     */
    override fun onStop() {
        super.onStop()
        Log.d("M_MainActivity", "onStop")
    }


    /**
     * Метод вызывается по окончании работы Activity, при вызове метода finish() или в случае,
     * когда система уничтожает этот экземпляр активности для освобождения ресурсов.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d("M_MainActivity", "onDestroy")
    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.iv_send) {
            val (phrase, color) = benderObj.listenAnswer(messageEt.text.toString())
            messageEt.setText("")
            val (r, g, b) = color
            benderImage.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY)
            textTxt.text = phrase
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(SAVE_STATE_STATUS, benderObj.status.name)
        outState.putString(SAVE_STATE_QUESTION, benderObj.question.name)
        Log.d("M_MainActivity", "onSaveInstanceState ${benderObj.status.name} ${benderObj.question.name}")
    }
}
