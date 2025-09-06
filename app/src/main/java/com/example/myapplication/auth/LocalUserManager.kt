package com.example.myapplication.auth

import android.content.Context
import android.content.SharedPreferences

/**
 * 로컬 사용자 정보를 관리하는 매니저
 *
 * Google 로그인 후 사용자 정보를 SharedPreferences에 저장하고 관리합니다. 서버를 거치지 않고 로컬에서만 사용자 정보를 처리합니다.
 *
 * @author SEASONTHON TEAM 8
 * @version 1.0.0
 */
class LocalUserManager(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "local_user_prefs"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_NAME = "user_name"
        private const val KEY_PROFILE_URL = "user_profile_url"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * 사용자 정보를 저장합니다.
     *
     * @param email 사용자 이메일
     * @param name 사용자 이름
     * @param profileUrl 사용자 프로필 사진 URL
     */
    fun saveUserInfo(email: String, name: String, profileUrl: String) {
        sharedPreferences.edit().apply {
            putString(KEY_EMAIL, email)
            putString(KEY_NAME, name)
            putString(KEY_PROFILE_URL, profileUrl)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
        println("✅ 사용자 정보 저장 완료: $email")
    }

    /** 현재 로그인된 사용자의 이메일을 반환합니다. */
    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_EMAIL, null)
    }

    /** 현재 로그인된 사용자의 이름을 반환합니다. */
    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_NAME, null)
    }

    /** 현재 로그인된 사용자의 프로필 사진 URL을 반환합니다. */
    fun getUserProfileUrl(): String? {
        return sharedPreferences.getString(KEY_PROFILE_URL, null)
    }

    /** 사용자가 로그인되어 있는지 확인합니다. */
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /** 사용자 정보를 모두 삭제합니다 (로그아웃). */
    fun clearUserInfo() {
        sharedPreferences.edit().apply {
            remove(KEY_EMAIL)
            remove(KEY_NAME)
            remove(KEY_PROFILE_URL)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
        println("✅ 사용자 정보 삭제 완료 (로그아웃)")
    }

    /** 현재 저장된 사용자 정보를 출력합니다 (디버깅용). */
    fun printUserInfo() {
        if (isLoggedIn()) {
            println("📱 현재 로그인된 사용자 정보:")
            println("   이메일: ${getUserEmail()}")
            println("   이름: ${getUserName()}")
            println("   프로필 사진: ${getUserProfileUrl()}")
        } else {
            println("📱 로그인된 사용자가 없습니다.")
        }
    }
}
