package com.example.hackathon.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hackathon.data.repository.AuthRepository
import com.example.hackathon.domain.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AuthRepository(app)

    sealed interface UiState {
        object Idle : UiState
        object Loading : UiState
        data class Success(val session: UserSession) : UiState
        data class Error(val throwable: Throwable) : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state

    fun signup(deviceCode: String) {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val result = repo.signup(deviceCode)
            _state.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it) }
            )
        }
    }
}
