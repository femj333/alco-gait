package wpics.alcogait.models

import wpics.alcogait.R

data class DrunkState(
    val label: String = "SOBER",
    val image: Int = R.drawable.sober,
    val fontSize: Int = 60,
    val noCharacterText: String = "Don't drive. Get a ride home with a sober friend or use a rideshare app.",
    val noCharacterDrink: Int = R.drawable.wine_glasses_sober
)
