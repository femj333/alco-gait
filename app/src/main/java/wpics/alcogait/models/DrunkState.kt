package wpics.alcogait.models

import wpics.alcogait.R

data class DrunkState(
    val label: String = "SOBER",
    val image: Int = R.drawable.sober,
    val fontSize: Int = 60,
    val noCharacterText: String = "Your BAC is currently under the legal limit (0.08). It is safe to drive.",
    val noCharacterDrink: Int = R.drawable.wine_glasses_sober
)
