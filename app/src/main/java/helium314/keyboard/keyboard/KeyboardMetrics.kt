// SPDX-License-Identifier: GPL-3.0-only

package helium314.keyboard.keyboard

import android.content.res.Resources
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Central responsive keyboard sizing calculation layer.
 * Computes all keyboard dimensions from the actual available IME area.
 *
 * All values are in dp/sp (density-independent). The caller converts to pixels.
 *
 * Design target: Gboard-like compact proportions.
 */
object KeyboardMetrics {

    // ── Constraint Bounds ──────────────────────────────────────────────

    // Key dimensions (dp)
    const val MIN_KEY_WIDTH = 30f
    const val MAX_KEY_WIDTH = 44f
    const val MIN_KEY_HEIGHT = 42f
    const val MAX_KEY_HEIGHT = 49f
    const val PREFERRED_KEY_HEIGHT = 45f

    // Text sizes (sp)
    const val MIN_LETTER_SIZE = 17f
    const val MAX_LETTER_SIZE = 22f
    const val PREFERRED_LETTER_SIZE = 20f
    const val MIN_SECONDARY_SIZE = 9f
    const val MAX_SECONDARY_SIZE = 12f
    const val PREFERRED_SECONDARY_SIZE = 10.5f

    // Spacing (dp)
    const val MIN_GAP = 2.5f
    const val MAX_GAP = 5f
    const val PREFERRED_GAP = 3.5f
    const val MIN_SIDE_PADDING = 3f
    const val MAX_SIDE_PADDING = 8f

    // Corner radius (dp)
    const val MIN_CORNER_RADIUS = 8f
    const val MAX_CORNER_RADIUS = 12f
    const val PREFERRED_CORNER_RADIUS = 10f

    // Icon size (dp)
    const val MIN_ICON_SIZE = 18f
    const val MAX_ICON_SIZE = 24f
    const val PREFERRED_ICON_SIZE = 20f

    // Toolbar height (dp)
    const val MIN_TOOLBAR_HEIGHT = 38f
    const val MAX_TOOLBAR_HEIGHT = 48f
    const val PREFERRED_TOOLBAR_HEIGHT = 44f

    // Special key width multipliers (relative to normal key)
    const val SHIFT_WIDTH_RATIO = 1.25f
    const val BACKSPACE_WIDTH_RATIO = 1.25f
    const val BOTTOM_KEY_WIDTH_RATIO = 1.25f
    const val SPACEBAR_MIN_RATIO = 3.5f
    const val SPACEBAR_MAX_RATIO = 5.0f

    // ── Text size ratios (from key height) ─────────────────────────────

    const val LETTER_RATIO = 0.44f       // ~20sp from 45dp key
    const val SECONDARY_RATIO = 0.23f    // ~10.5sp from 45dp key
    const val LABEL_RATIO = 0.30f
    const val LARGE_LETTER_RATIO = 0.80f
    const val HINT_LABEL_RATIO = 0.26f
    const val SHIFTED_HINT_RATIO = 0.30f
    const val PREVIEW_TEXT_RATIO = 0.78f

    // ── Computed Metrics ───────────────────────────────────────────────

    data class Metrics(
        // Available area (dp)
        val availableWidth: Float,
        val availableHeight: Float,

        // Keyboard padding (dp)
        val topPadding: Float,
        val bottomPadding: Float,
        val sidePadding: Float,

        // Row dimensions (dp)
        val rowHeight: Float,
        val normalKeyHeight: Float,

        // Key widths (dp) - calculated per-layout
        val normalKeyWidth: Float,
        val shiftKeyWidth: Float,
        val backspaceKeyWidth: Float,
        val bottomKeyWidth: Float,
        val spacebarWidth: Float,

        // Spacing (dp)
        val horizontalGap: Float,
        val verticalGap: Float,

        // Typography (sp)
        val letterSize: Float,
        val secondarySize: Float,
        val labelSize: Float,
        val largeLetterSize: Float,
        val hintLabelSize: Float,
        val previewTextSize: Float,

        // Visual (dp)
        val cornerRadius: Float,
        val iconSize: Float,
        val toolbarHeight: Float,

        // Derived
        val qwertyRowWidth: Float,
        val asdfRowWidth: Float,
        val thirdRowWidth: Float,
        val bottomRowWidth: Float,
    )

    /**
     * Calculate all keyboard metrics from the available IME area.
     *
     * @param availableWidth  actual keyboard width in dp (after insets)
     * @param availableHeight actual keyboard height in dp (after insets, including suggestion strip)
     * @param suggestionStripHeight suggestion strip height in dp
     * @param isNumberLayout  true for number/symbol layouts (fewer keys per row)
     * @return complete Metrics object
     */
    fun calculate(
        availableWidth: Float,
        availableHeight: Float,
        suggestionStripHeight: Float,
        isNumberLayout: Boolean = false,
    ): Metrics {
        // ── 1. Padding ─────────────────────────────────────────────────
        val sidePadding = clamp(
            availableWidth * 0.035f,  // ~3.5% of width
            MIN_SIDE_PADDING,
            MAX_SIDE_PADDING
        )

        // ── 2. Available row width ─────────────────────────────────────
        val rowWidth = availableWidth - 2 * sidePadding

        // ── 3. Horizontal gap ──────────────────────────────────────────
        val horizontalGap = clamp(
            availableWidth * 0.008f,  // ~0.8% of width
            MIN_GAP,
            MAX_GAP
        )

        // ── 4. Normal key width (from QWERTY row: 10 keys + 9 gaps) ───
        val normalKeyWidth = if (isNumberLayout) {
            // Number row: 10 keys + 9 gaps
            clamp(
                (rowWidth - 9 * horizontalGap) / 10f,
                MIN_KEY_WIDTH,
                MAX_KEY_WIDTH
            )
        } else {
            // QWERTY: 10 keys + 9 gaps
            clamp(
                (rowWidth - 9 * horizontalGap) / 10f,
                MIN_KEY_WIDTH,
                MAX_KEY_WIDTH
            )
        }

        // ── 5. Special key widths ──────────────────────────────────────
        val shiftKeyWidth = normalKeyWidth * SHIFT_WIDTH_RATIO
        val backspaceKeyWidth = normalKeyWidth * BACKSPACE_WIDTH_RATIO
        val bottomKeyWidth = normalKeyWidth * BOTTOM_KEY_WIDTH_RATIO

        // ── 6. Spacebar width (remaining after bottom keys + gaps) ─────
        // Bottom row: ?123 + globe + space + period + enter = 5 keys, 4 gaps
        val bottomFixedWidth = bottomKeyWidth * 4  // ?123, globe, period, enter
        val bottomGaps = horizontalGap * 4
        val spacebarWidth = clamp(
            rowWidth - bottomFixedWidth - bottomGaps,
            normalKeyWidth * SPACEBAR_MIN_RATIO,
            normalKeyWidth * SPACEBAR_MAX_RATIO
        )

        // ── 7. Vertical spacing ────────────────────────────────────────
        val verticalGap = clamp(
            availableHeight * 0.008f,  // ~0.8% of height
            MIN_GAP,
            MAX_GAP
        )

        // ── 8. Top/bottom padding ──────────────────────────────────────
        val topPadding = clamp(
            availableHeight * 0.01f,   // ~1% of height
            2f,
            6f
        )
        val bottomPadding = clamp(
            availableHeight * 0.015f,  // ~1.5% of height
            2f,
            8f
        )

        // ── 9. Row height (from available height after padding/spacing) ─
        val usableHeight = availableHeight - suggestionStripHeight - topPadding - bottomPadding
        val numberOfRows = 4f
        val totalVerticalGaps = verticalGap * (numberOfRows - 1)
        val rowHeight = clamp(
            (usableHeight - totalVerticalGaps) / numberOfRows,
            MIN_KEY_HEIGHT,
            MAX_KEY_HEIGHT
        )
        val normalKeyHeight = rowHeight - verticalGap

        // ── 10. Corner radius (scales slightly with key height) ────────
        val cornerRadius = clamp(
            PREFERRED_CORNER_RADIUS * (normalKeyHeight / PREFERRED_KEY_HEIGHT),
            MIN_CORNER_RADIUS,
            MAX_CORNER_RADIUS
        )

        // ── 11. Typography ─────────────────────────────────────────────
        val letterSize = clamp(
            normalKeyHeight * LETTER_RATIO,
            MIN_LETTER_SIZE,
            MAX_LETTER_SIZE
        )
        val secondarySize = clamp(
            normalKeyHeight * SECONDARY_RATIO,
            MIN_SECONDARY_SIZE,
            MAX_SECONDARY_SIZE
        )
        val labelSize = normalKeyHeight * LABEL_RATIO
        val largeLetterSize = normalKeyHeight * LARGE_LETTER_RATIO
        val hintLabelSize = normalKeyHeight * HINT_LABEL_RATIO
        val previewTextSize = normalKeyHeight * PREVIEW_TEXT_RATIO

        // ── 12. Icon size ──────────────────────────────────────────────
        val iconSize = clamp(
            normalKeyHeight * 0.46f,  // ~20dp from 45dp key
            MIN_ICON_SIZE,
            MAX_ICON_SIZE
        )

        // ── 13. Toolbar height ─────────────────────────────────────────
        val toolbarHeight = clamp(
            normalKeyHeight * 0.98f,  // ~44dp from 45dp key
            MIN_TOOLBAR_HEIGHT,
            MAX_TOOLBAR_HEIGHT
        )

        // ── 14. Verify row widths fit ──────────────────────────────────
        // ASDF row: 9 keys + 8 gaps, should be centered
        val asdfRowWidth = normalKeyWidth * 9 + horizontalGap * 8
        // Third row: shift + 7 keys + backspace + 8 gaps
        val thirdRowWidth = shiftKeyWidth + normalKeyWidth * 7 + backspaceKeyWidth + horizontalGap * 8
        // Bottom row: ?123 + globe + space + period + enter + 4 gaps
        val qwertyRowWidth = normalKeyWidth * 10 + horizontalGap * 9
        val bottomRowWidth = bottomKeyWidth * 4 + spacebarWidth + horizontalGap * 4

        return Metrics(
            availableWidth = availableWidth,
            availableHeight = availableHeight,
            topPadding = topPadding,
            bottomPadding = bottomPadding,
            sidePadding = sidePadding,
            rowHeight = rowHeight,
            normalKeyHeight = normalKeyHeight,
            normalKeyWidth = normalKeyWidth,
            shiftKeyWidth = shiftKeyWidth,
            backspaceKeyWidth = backspaceKeyWidth,
            bottomKeyWidth = bottomKeyWidth,
            spacebarWidth = spacebarWidth,
            horizontalGap = horizontalGap,
            verticalGap = verticalGap,
            letterSize = letterSize,
            secondarySize = secondarySize,
            labelSize = labelSize,
            largeLetterSize = largeLetterSize,
            hintLabelSize = hintLabelSize,
            previewTextSize = previewTextSize,
            cornerRadius = cornerRadius,
            iconSize = iconSize,
            toolbarHeight = toolbarHeight,
            qwertyRowWidth = qwertyRowWidth,
            asdfRowWidth = asdfRowWidth,
            thirdRowWidth = thirdRowWidth,
            bottomRowWidth = bottomRowWidth,
        )
    }

    private fun clamp(value: Float, min: Float, max: Float): Float =
        max(min, min(max, value))
}
