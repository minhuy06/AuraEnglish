package com.example.auraenglish.listening.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityListeningQuestionBinding
import com.example.auraenglish.listening.data.ListeningExam
import com.example.auraenglish.listening.data.ListeningQuestion
import com.example.auraenglish.listening.data.ListeningSampleData
import com.example.auraenglish.practice.helper.PracticeConfirmHelper
import java.util.Locale
import java.util.concurrent.TimeUnit

// Làm từng câu nghe: play/seek + dictation + check từ
class ListeningQuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListeningQuestionBinding
    private lateinit var exam: ListeningExam
    private var questionIndex = 0
    private var player: MediaPlayer? = null
    private var userSeeking = false
    private val handler = Handler(Looper.getMainLooper())
    private val tick = object : Runnable {
        override fun run() {
            val p = player ?: return
            if (!userSeeking && p.isPlaying) {
                val dur = p.duration.coerceAtLeast(1)
                binding.seekBar.progress = (p.currentPosition * 1000L / dur).toInt()
                binding.tvTime.text = getString(
                    R.string.listen_time_fmt,
                    formatMs(p.currentPosition),
                    formatMs(dur)
                )
            }
            handler.postDelayed(this, 200)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListeningQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val examId = intent.getStringExtra(ListeningSampleData.EXTRA_EXAM_ID).orEmpty()
        exam = ListeningSampleData.examById(examId)
        questionIndex = intent.getIntExtra(ListeningSampleData.EXTRA_QUESTION, 0)
            .coerceIn(0, exam.questions.lastIndex)

        binding.tvExamTitle.text = exam.title
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = confirmLeave()
        })
        binding.btnClose.setOnClickListener { confirmLeave() }
        binding.btnPlay.setOnClickListener { togglePlay() }
        binding.btnSubmit.setOnClickListener { submitCurrent() }
        binding.btnPrev.setOnClickListener { moveQuestion(-1) }
        binding.btnNext.setOnClickListener { moveQuestion(1) }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                val p = player ?: return
                val pos = (progress / 1000f * p.duration).toInt()
                binding.tvTime.text = getString(
                    R.string.listen_time_fmt,
                    formatMs(pos),
                    formatMs(p.duration)
                )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                userSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val p = player ?: return
                val pos = ((seekBar?.progress ?: 0) / 1000f * p.duration).toInt()
                p.seekTo(pos)
                userSeeking = false
            }
        })

        showQuestion(questionIndex)
    }

    override fun onDestroy() {
        handler.removeCallbacks(tick)
        releasePlayer()
        super.onDestroy()
    }

    private fun confirmLeave() {
        PracticeConfirmHelper.showExitExam(this) { finish() }
    }

    private fun current(): ListeningQuestion = exam.questions[questionIndex]

    private fun showQuestion(index: Int) {
        questionIndex = index
        val q = current()
        binding.tvQuestionProgress.text = getString(
            R.string.listen_question_progress,
            index + 1,
            exam.questions.size
        )
        binding.tvBlankPattern.text = q.blankPattern
        binding.etAnswer.setText(q.userAnswer)
        binding.btnPrev.isEnabled = index > 0
        binding.btnPrev.alpha = if (index > 0) 1f else 0.4f
        binding.btnNext.isEnabled = index < exam.questions.lastIndex
        binding.btnNext.alpha = if (index < exam.questions.lastIndex) 1f else 0.4f
        binding.btnSubmit.text = getString(
            if (q.submitted) R.string.listen_resubmit else R.string.listen_submit
        )
        renderWordResult(q)
        preparePlayer(q.audioRes)
    }

    private fun renderWordResult(q: ListeningQuestion) {
        if (!q.submitted || q.wordResults.isEmpty()) {
            binding.tvBlankPattern.text = q.blankPattern
            binding.tvBlankPattern.setTextColor(getColor(R.color.listen_blank))
            binding.tvResultLabel.isVisible = false
            binding.tvWordResult.isVisible = false
            return
        }
        // Đúng → hiện chữ xanh; sai → hiện *** đỏ
        val green = getColor(R.color.listen_correct)
        val red = getColor(R.color.listen_wrong)
        val sb = SpannableStringBuilder()
        q.answerWords.forEachIndexed { i, word ->
            if (i > 0) sb.append(' ')
            val start = sb.length
            val ok = q.wordResults.getOrNull(i) == true
            if (ok) {
                sb.append(word)
                sb.setSpan(
                    ForegroundColorSpan(green),
                    start,
                    sb.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                val letters = word.count { it.isLetterOrDigit() }.coerceAtLeast(1)
                val punct = word.filterNot { it.isLetterOrDigit() }
                sb.append("*".repeat(letters))
                sb.append(punct)
                sb.setSpan(
                    ForegroundColorSpan(red),
                    start,
                    sb.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        binding.tvBlankPattern.text = sb
        binding.tvResultLabel.isVisible = true
        binding.tvWordResult.isVisible = true
        binding.tvWordResult.text = sb
    }

    private fun submitCurrent() {
        val q = current()
        val input = binding.etAnswer.text?.toString().orEmpty().trim()
        if (input.isBlank()) {
            Toast.makeText(this, R.string.listen_empty_answer, Toast.LENGTH_SHORT).show()
            return
        }
        q.userAnswer = input
        q.wordResults = ListeningSampleData.checkAnswer(q, input)
        q.submitted = true
        ListeningSampleData.refreshProgress(exam)
        binding.btnSubmit.text = getString(R.string.listen_resubmit)
        renderWordResult(q)
    }

    private fun moveQuestion(delta: Int) {
        val next = questionIndex + delta
        if (next !in exam.questions.indices) return
        // Lưu draft khi chuyển câu
        current().userAnswer = binding.etAnswer.text?.toString().orEmpty()
        showQuestion(next)
    }

    private fun preparePlayer(audioRes: Int) {
        handler.removeCallbacks(tick)
        releasePlayer()
        try {
            player = MediaPlayer.create(this, audioRes)?.also { p ->
                p.setOnCompletionListener {
                    binding.btnPlay.setImageResource(R.drawable.ic_listen_play)
                    binding.seekBar.progress = 1000
                    binding.tvTime.text = getString(
                        R.string.listen_time_fmt,
                        formatMs(p.duration),
                        formatMs(p.duration)
                    )
                }
                binding.seekBar.progress = 0
                binding.tvTime.text = getString(
                    R.string.listen_time_fmt,
                    formatMs(0),
                    formatMs(p.duration)
                )
                binding.btnPlay.setImageResource(R.drawable.ic_listen_play)
                handler.post(tick)
            }
        } catch (_: Exception) {
            Toast.makeText(this, "Không phát được audio mẫu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun togglePlay() {
        val p = player ?: return
        if (p.isPlaying) {
            p.pause()
            binding.btnPlay.setImageResource(R.drawable.ic_listen_play)
        } else {
            p.start()
            binding.btnPlay.setImageResource(R.drawable.ic_listen_pause)
        }
    }

    private fun releasePlayer() {
        player?.run {
            runCatching {
                if (isPlaying) stop()
                release()
            }
        }
        player = null
    }

    private fun formatMs(ms: Int): String {
        val m = TimeUnit.MILLISECONDS.toMinutes(ms.toLong())
        val s = TimeUnit.MILLISECONDS.toSeconds(ms.toLong()) % 60
        return String.format(Locale.US, "%d:%02d", m, s)
    }
}
