package com.example.emod.ui.parent

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.emod.R
import com.example.emod.databinding.ActivityParentPasswordBinding
import com.example.hackathon.core.ParentPasswordStore
import com.example.hackathon.ui.parent.ParentActivity

class ParentPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentPasswordBinding

    private enum class Mode { SETUP_1, SETUP_2, VERIFY }
    private var mode: Mode = Mode.VERIFY
    private var firstInput: String? = null

    private val input = StringBuilder()

    // 슬롯/커서
    private lateinit var slots: List<ImageView>
    private lateinit var caret: View
    private lateinit var blink: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        // 모드 결정
        mode = if (ParentPasswordStore.hasPasscode(this)) Mode.VERIFY else Mode.SETUP_1
        updateTitle()

        // 슬롯/커서 바인딩
        slots = listOf(binding.slot1, binding.slot2, binding.slot3, binding.slot4)
        caret = binding.caret

        // 커서 깜빡임
        blink = ObjectAnimator.ofFloat(caret, View.ALPHA, 1f, 0f).apply {
            duration = 650
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }
        caret.post {
            blink.start()
            moveCaret(0)
        }

        // 키패드 연결
        val ids = listOf(
            binding.keypad0, binding.keypad1, binding.keypad2, binding.keypad3, binding.keypad4,
            binding.keypad5, binding.keypad6, binding.keypad7, binding.keypad8, binding.keypad9
        )
        ids.forEach { btn ->
            btn.setOnClickListener { onDigit((it as Button).text.toString()) }
        }

        // 제출
        binding.passwordSubmit.setOnClickListener { onSubmit() }

        binding.passwordSubmit.setOnLongClickListener {
            showResetDialog()
            true
        }

        resetSlots()
    }

    private fun onDigit(d: String) {
        if (input.length >= 4) return
        input.append(d)

        // 채우기: 해당 슬롯 아이콘 교체
        val idx = input.length - 1
        slots[idx].setImageResource(R.drawable.ic_pwd_input)

        // 커서 다음 칸으로 이동
        moveCaret(input.length)

        // 4자리 다 채우면 자동 제출하고 싶으면 ↓ 주석 해제
        // if (input.length == 4) onSubmit()
    }

    private fun onSubmit() {
        if (input.length < 4) return

        when (mode) {
            Mode.SETUP_1 -> {
                firstInput = input.toString()
                input.clear()
                resetSlots()
                moveCaret(0)
                mode = Mode.SETUP_2
                updateTitle()
            }
            Mode.SETUP_2 -> {
                val second = input.toString()
                if (firstInput == second) {
                    ParentPasswordStore.setPasscode(this, second)
                    goToParentPage()
                } else {
                    alertWrong("비밀번호가 일치하지 않습니다. 다시 설정해주세요.")
                    input.clear()
                    firstInput = null
                    mode = Mode.SETUP_1
                    updateTitle()
                    resetSlots()
                    moveCaret(0)
                }
            }
            Mode.VERIFY -> {
                val ok = ParentPasswordStore.verify(this, input.toString())
                if (ok) {
                    goToParentPage()
                } else {
                    alertWrong("비밀번호가 올바르지 않습니다.")
                    input.clear()
                    resetSlots()
                    moveCaret(0)
                }
            }
        }
    }

    private fun updateTitle() {
        // 더 안전하게: 타이틀 TextView에 id를 주고 binding으로 접근 추천
        val titleView: TextView = binding.passwordLayout.getChildAt(0) as TextView
        titleView.text = when (mode) {
            Mode.SETUP_1 -> getString(R.string.parent_password_setting)
            Mode.SETUP_2 -> getString(R.string.parent_password_setting_two)
            Mode.VERIFY -> getString(R.string.parent_password)
        }
    }

    private fun goToParentPage() {
        startActivity(Intent(this, ParentActivity::class.java))
        finish()
    }

    private fun alertWrong(message: String) {
        AlertDialog.Builder(this)
            .setTitle("오류")
            .setMessage(message)
            .setPositiveButton("확인") { d, _ -> d.dismiss() }
            .show()
    }

    private fun resetSlots() {
        slots.forEach { it.setImageResource(R.drawable.seg_track_transparent) }
    }

    /** index = 0~4 (4는 마지막 칸의 오른쪽 끝) */
    private fun moveCaret(index: Int) {
        val targetIndex = index.coerceIn(0, slots.size)
        val targetView = if (targetIndex == slots.size) slots.last() else slots[targetIndex]

        binding.passwordFrame.post {
            // slotsRow 기준 좌표
            val baseX = binding.slotsRow.x
            val targetX = if (targetIndex == slots.size) {
                targetView.x + targetView.width * 0.95f
            } else {
                targetView.x + targetView.width * 0.05f
            }
            val caretX = baseX + targetX - caret.width / 2f
            caret.animate().x(caretX).setDuration(140).start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::blink.isInitialized) blink.cancel()
    }

    private fun showResetDialog() {
        AlertDialog.Builder(this)
            .setTitle("부모 비밀번호 초기화")
            .setMessage("비밀번호를 초기화하시겠습니까?")
            .setPositiveButton("초기화") { _, _ ->
                ParentPasswordStore.clear(this)
                mode = Mode.SETUP_1
                firstInput = null
                input.clear()
                updateTitle()
                resetSlots()
                moveCaret(0)
            }
            .setNegativeButton("취소", null)
            .show()
    }
}

