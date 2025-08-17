package com.example.hackathon.ui.signUp

import androidx.lifecycle.ViewModel
import org.json.JSONObject
import com.example.hackathon.domain.model.AppCharacter

class SignupViewModel : ViewModel() {

    enum class Gender { MALE, FEMALE }
    enum class Env { KINDERGARTEN, SCHOOL, BUILDING, HOME }

    // --- 수집되는 값들 ---
    var character: AppCharacter? = null

    var name: String? = null
    var birthYear: Int? = null
    var birthMonth: Int? = null
    var gender: Gender? = null

    var q1: String? = null
    var q2: String? = null

    var learningPlace: Env? = null

    // --- 단계별 유효성 ---
    fun isStepValid(step: Int): Boolean = when (step) {
        1 -> character != null
        2 -> !name.isNullOrBlank() && birthYear != null && birthMonth != null && gender != null
        3 -> !q1.isNullOrBlank() && !q2.isNullOrBlank()
        4 -> learningPlace != null
        5 -> true
        else -> false
    }

    // --- 결과 JSON ---
    fun toResultJson(): String {
        val obj = JSONObject()
        obj.put("character", character?.name ?: "")
        obj.put("name", name ?: "")
        obj.put("birthYear", birthYear ?: 0)
        obj.put("birthMonth", birthMonth ?: 0)
        obj.put("gender", when (gender) {
            Gender.MALE -> "MALE"
            Gender.FEMALE -> "FEMALE"
            null -> ""
        })
        obj.put("q1", q1 ?: "")
        obj.put("q2", q2 ?: "")
        obj.put("learningPlace", learningPlace?.name ?: "")
        return obj.toString()
    }
}
