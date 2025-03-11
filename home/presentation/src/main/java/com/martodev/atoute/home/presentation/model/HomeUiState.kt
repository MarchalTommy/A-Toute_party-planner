package com.martodev.atoute.home.presentation.model

import com.martodev.atoute.home.presentation.model.Party
import com.martodev.atoute.home.presentation.model.Todo
import com.martodev.atoute.home.presentation.model.ToBuy

data class HomeUiState(
    val parties: List<Party> = listOf(),
    val todos: List<Todo> = listOf(),
    val toBuys: List<ToBuy> = listOf(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isQrScanDialogVisible: Boolean = false,
    val qrScanResult: String? = null
) 