package wpics.alcogait.data

enum class WalkType(
    private val label: String,
    private val noSpace: String
) {
    NORMAL("NORMAL WALK", "NORMAL_WALK"),
    HEEL_TO_TOE("HEEL TO TOE", "HEEL_TO_TOE"),
    STANDING_ON_ONE_FOOT("STANDING ON ONE FOOT", "STANDING_ON_ONE_FOOT"),
    NYSTAGMUS("NYSTAGMUS", "NYSTAGMUS");

    override fun toString(): String = label

    fun toNoSpaceString(): String = noSpace
}