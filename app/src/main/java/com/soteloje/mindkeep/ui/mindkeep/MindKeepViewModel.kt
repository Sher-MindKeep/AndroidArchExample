/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.soteloje.mindkeep.ui.mindkeep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.soteloje.mindkeep.data.MindKeepRepository
import com.soteloje.mindkeep.ui.mindkeep.MindKeepUiState.Error
import com.soteloje.mindkeep.ui.mindkeep.MindKeepUiState.Loading
import com.soteloje.mindkeep.ui.mindkeep.MindKeepUiState.Success
import javax.inject.Inject

@HiltViewModel
class MindKeepViewModel @Inject constructor(
    private val mindKeepRepository: MindKeepRepository
) : ViewModel() {

    val uiState: StateFlow<MindKeepUiState> = mindKeepRepository
        .mindKeeps.map<List<String>, MindKeepUiState>(::Success)
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun addMindKeep(name: String) {
        viewModelScope.launch {
            mindKeepRepository.add(name)
        }
    }
}

sealed interface MindKeepUiState {
    object Loading : MindKeepUiState
    data class Error(val throwable: Throwable) : MindKeepUiState
    data class Success(val data: List<String>) : MindKeepUiState
}
