package com.example.myapplication.keyboard

import android.view.inputmethod.InputConnection

/**
 * 매우 단순화된 한글 조합기
 * - 자모 단독 입력 허용: 모음만 누르면 모음 자체를 조합 상태로 표시
 * - 초성/중성/종성의 최소 조합 규칙만 지원 (복합 모음/복합 종성은 차후 확장 가능)
 * - 백스페이스: 종성→중성→초성 순 단계적으로 해제
 * - 스페이스/엔터/모드 전환 시 조합 글자를 먼저 커밋
 */
class HangulComposer {

    private var choseong: Int = -1
    private var jungseong: Int = -1
    private var jongseong: Int = -1

    data class Result(
        val commitText: String? = null,
        val composingText: String? = null
    )

    fun reset() {
        choseong = -1
        jungseong = -1
        jongseong = -1
    }

    /**
     * 조합 중인 글자가 있으면 커밋하고 내부 상태 초기화
     */
    fun commitPending(ic: InputConnection?) {
        val composing = getComposingText()
        if (composing.isNotEmpty()) {
            ic?.finishComposingText()
            ic?.commitText(composing, 1)
            reset()
        }
    }

    /**
     * 백스페이스 처리. 조합 단계에서 가능한 경우 내부 상태만 한 단계 해제하고 true
     * 해제할 조합이 없으면 false를 반환하여 외부에서 일반 삭제를 수행
     */
    fun backspace(ic: InputConnection?): Boolean {
        return when {
            jongseong != -1 -> {
                jongseong = -1
                updateComposing(ic)
                true
            }
            jungseong != -1 -> {
                jungseong = -1
                updateComposing(ic)
                true
            }
            choseong != -1 -> {
                choseong = -1
                ic?.finishComposingText()
                true
            }
            else -> false
        }
    }

    /**
     * 자모 입력 처리. 한글 자모면 내부 조합 상태를 갱신하고 composingText를 설정
     * 비한글은 현재 조합을 커밋한 뒤 그대로 commit
     */
    fun input(jamo: String, ic: InputConnection?): Result {
        val ch = jamo.firstOrNull() ?: return Result()
        if (!isHangulJamo(ch)) {
            // 비한글: 조합 커밋 후 그대로 전달
            commitPending(ic)
            return Result(commitText = jamo, composingText = null)
        }

        val ci = choseongIndex(ch)
        val vi = jungseongIndex(ch)
        val fi = jongseongIndex(ch)

        if (ci != -1) {
            handleConsonant(ci, ic)
        } else if (vi != -1) {
            handleVowel(vi, ic)
        } else if (fi != -1) {
            handleConsonant(finalToChoseong(fi), ic) // 종성 자모를 초성으로 간주
        } else {
            // 알 수 없는 경우 커밋
            commitPending(ic)
            return Result(commitText = jamo, composingText = null)
        }

        val composing = getComposingText()
        if (composing.isEmpty()) ic?.finishComposingText() else ic?.setComposingText(composing, 1)
        return Result(commitText = null, composingText = composing)
    }

    private fun handleConsonant(ci: Int, ic: InputConnection?) {
        when {
            choseong == -1 -> choseong = ci
            jungseong == -1 -> {
                // 중복 원인 제거: 커밋하지 말고 초성 교체만 수행
                choseong = ci
            }
            jongseong == -1 -> {
                // 종성 설정
                jongseong = consonantToJongseong(ci)
            }
            else -> {
                // 종성까지 있는데 또 자음 → 이전 음절 커밋, 새 초성 시작
                val syll = buildSyllable()
                ic?.finishComposingText()
                ic?.commitText(syll.toString(), 1)
                choseong = ci
                jungseong = -1
                jongseong = -1
            }
        }
    }

    private fun handleVowel(vi: Int, ic: InputConnection?) {
        when {
            jungseong == -1 -> {
                // 옵션 A: 초성 자동 'ㅇ' 삽입하지 않고 모음 단독 조합 시작
                jungseong = vi
            }
            else -> {
                if (jongseong != -1) {
                    // 종성이 있는 상태에서 모음 입력: 종성을 다음 음절의 초성으로 이월
                    val savedFinal = jongseong
                    // 이전 음절은 종성 제거 후 커밋
                    jongseong = -1
                    val commitSyll = buildSyllable()
                    ic?.finishComposingText()
                    ic?.commitText(commitSyll.toString(), 1)
                    // 새 음절 시작: 종성→초성 이월
                    choseong = finalToChoseong(savedFinal)
                    jungseong = vi
                    jongseong = -1
                } else {
                    // 복합 모음 미지원: 이전 음절 커밋 후 새 음절 시작(모음부터)
                    val syll = buildSyllable()
                    ic?.finishComposingText()
                    ic?.commitText(syll.toString(), 1)
                    choseong = -1
                    jungseong = vi
                    jongseong = -1
                }
            }
        }
    }

    private fun updateComposing(ic: InputConnection?) {
        val composing = getComposingText()
        if (composing.isEmpty()) ic?.finishComposingText() else ic?.setComposingText(composing, 1)
    }

    private fun getComposingText(): String {
        return when {
            choseong != -1 && jungseong != -1 -> buildSyllableWithoutFinal().toString()
            choseong != -1 -> CHOSEONG[choseong].toString()
            jungseong != -1 -> JUNGSEONG[jungseong].toString()
            else -> ""
        }
    }

    private fun buildSyllable(): Char {
        val c = choseong.coerceAtLeast(0)
        val v = jungseong.coerceAtLeast(0)
        val f = jongseong.coerceAtLeast(0)
        val code = 0xAC00 + (c * 21 + v) * 28 + f
        return code.toChar()
    }

    private fun buildSyllableWithoutFinal(): Char {
        val c = choseong.coerceAtLeast(0)
        val v = jungseong.coerceAtLeast(0)
        val code = 0xAC00 + (c * 21 + v) * 28
        return code.toChar()
    }

    private fun isHangulJamo(ch: Char): Boolean {
        return CHOSEONG.contains(ch) || JUNGSEONG.contains(ch) || JONGSEONG.contains(ch)
    }

    private fun choseongIndex(ch: Char): Int = CHOSEONG.indexOf(ch)
    private fun jungseongIndex(ch: Char): Int = JUNGSEONG.indexOf(ch)
    private fun jongseongIndex(ch: Char): Int = JONGSEONG.indexOf(ch)

    private fun consonantToJongseong(ci: Int): Int {
        val ch = CHOSEONG[ci]
        val ji = JONGSEONG.indexOf(ch)
        return if (ji >= 0) ji else 0
    }

    private fun finalToChoseong(fi: Int): Int {
        val ch = JONGSEONG[fi]
        val ci = CHOSEONG.indexOf(ch)
        return if (ci >= 0) ci else choseongIndex('ㅇ')
    }

    companion object {
        // 초성 19, 중성 21, 종성 28(첫 값은 없음)
        private val CHOSEONG = charArrayOf(
            'ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ','ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'
        )
        private val JUNGSEONG = charArrayOf(
            'ㅏ','ㅐ','ㅑ','ㅒ','ㅓ','ㅔ','ㅕ','ㅖ','ㅗ','ㅘ','ㅙ','ㅚ','ㅛ','ㅜ','ㅝ','ㅞ','ㅟ','ㅠ','ㅡ','ㅢ','ㅣ'
        )
        private val JONGSEONG = charArrayOf(
            '\u0000','ㄱ','ㄲ','ㄳ','ㄴ','ㄵ','ㄶ','ㄷ','ㄹ','ㄺ','ㄻ','ㄼ','ㄽ','ㄾ','ㄿ','ㅀ','ㅁ','ㅂ','ㅄ','ㅅ','ㅆ','ㅇ','ㅈ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'
        )
    }
}
