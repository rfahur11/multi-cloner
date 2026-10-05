package com.porto.multicloner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.porto.multicloner.core.ClonableAppInfo
import com.porto.multicloner.core.VirtualCore
import com.porto.multicloner.core.model.ClonedProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val virtualCore = VirtualCore.get()

    private val _clones = MutableStateFlow<List<ClonedProfile>>(emptyList())
    val clones: StateFlow<List<ClonedProfile>> = _clones.asStateFlow()

    private val _clonableApps = MutableStateFlow<List<ClonableAppInfo>>(emptyList())
    val clonableApps: StateFlow<List<ClonableAppInfo>> = _clonableApps.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        refreshClones()
        loadClonableApps()
    }

    fun refreshClones() {
        _clones.value = virtualCore.getAllClones()
    }

    fun loadClonableApps() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val apps = virtualCore.getInstalledClonableApps()
                _clonableApps.value = apps
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createClone(packageName: String, aliasName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            virtualCore.createClone(packageName, aliasName)
            refreshClones()
        }
    }

    fun launchClone(profileId: String): Boolean {
        val success = virtualCore.launchApp(profileId)
        refreshClones()
        return success
    }

    fun deleteClone(profileId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            virtualCore.deleteClone(profileId)
            refreshClones()
        }
    }
}
