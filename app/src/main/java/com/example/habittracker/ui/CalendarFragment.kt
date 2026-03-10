package com.example.habittracker.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.content.res.Resources
import android.text.style.LineBackgroundSpan
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.habittracker.R
import com.example.habittracker.data.SessionManager
import com.example.habittracker.data.SettingsManager
import com.example.habittracker.databinding.FragmentCalendarBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private lateinit var settingsManager: SettingsManager
    private var flameAnimator: ObjectAnimator? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sessionManager = SessionManager(context)
        settingsManager = SettingsManager(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarCalendar.title = getString(R.string.calendar_title)
        startFlameAnimation()

        val userId = sessionManager.getUserId()
        if (userId == -1) return

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsManager.completedDates(userId).collect { dates ->
                    val calendarDays = dates.map {
                        CalendarDay.from(it.year, it.monthValue, it.dayOfMonth)
                    }.toSet()
                    val color = ContextCompat.getColor(requireContext(), R.color.calendar_complete_ring)
                    binding.calendarView.removeDecorators()
                    binding.calendarView.addDecorator(CompletedDayDecorator(color, calendarDays))

                    val streak = calculateCurrentStreak(dates)
                    binding.streakText.text = streak.toString()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        flameAnimator?.cancel()
        flameAnimator = null
        _binding = null
    }

    companion object {
        @JvmStatic fun newInstance() = CalendarFragment()
    }

    private fun startFlameAnimation() {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.08f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.12f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0.95f, 1.0f)
        flameAnimator = ObjectAnimator.ofPropertyValuesHolder(binding.streakText, scaleX, scaleY, alpha).apply {
            duration = 900
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }
}

private fun calculateCurrentStreak(dates: Set<LocalDate>): Int {
    if (dates.isEmpty()) return 0
    var count = 0
    var day = LocalDate.now()
    while (dates.contains(day)) {
        count++
        day = day.minusDays(1)
    }
    return count
}

private class CompletedDayDecorator(
    @ColorInt private val color: Int,
    private val dates: Set<CalendarDay>
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean = dates.contains(day)

    override fun decorate(view: DayViewFacade) {
        view.addSpan(CircleOutlineSpan(color))
    }
}

private class CircleOutlineSpan(@ColorInt private val color: Int) : LineBackgroundSpan {
    private val strokeWidthPx = 2f * Resources.getSystem().displayMetrics.density
    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lineNumber: Int
    ) {
        val centerX = (left + right) / 2f
        val centerY = (top + bottom) / 2f
        val radius = (right - left) / 2f

        val oldStyle = paint.style
        val oldColor = paint.color
        val oldStroke = paint.strokeWidth
        val oldAntiAlias = paint.isAntiAlias

        paint.style = Paint.Style.STROKE
        paint.color = color
        paint.strokeWidth = strokeWidthPx
        paint.isAntiAlias = true

        canvas.drawCircle(centerX, centerY, radius - paint.strokeWidth, paint)

        paint.style = oldStyle
        paint.color = oldColor
        paint.strokeWidth = oldStroke
        paint.isAntiAlias = oldAntiAlias
    }
}
