package wpics.alcogait.viewmodels

import wpics.alcogait.models.DrunkLevel

sealed interface AlcoGaitUIState {

    object Loading: AlcoGaitUIState

    data class Success(
        val drunkLevel: DrunkLevel?
    ) : AlcoGaitUIState

    data class Error(
        val message: String
    ) : AlcoGaitUIState
}