package wpics.alcogait.data

import java.io.Serializable

class WalkHolder(
    val walkNumber: Int
): Serializable {
    // Maps each walk type recorded so far to its corresponding Walk data
    private val walkMap: HashMap<WalkType, Walk> = HashMap()

    // The ordered set of walk types expected for this walk number
    // Defaults to all WalkType values; can be narrowed via setAllowedWalkTypes()
    private var allowedWalkTypes: Array<WalkType> = WalkType.values()

    /** Returns the recorded Walk for the given type, or null if it hasn't been recorded yet */
    fun get(walkType: WalkType?): Walk? {
        return walkMap[walkType]
    }

    /** Stores a completed Walk under its walk type. Returns this holder for chaining */
    fun addWalk(walk: Walk): WalkHolder {
        walkMap[walk.walkType] = walk
        return this
    }

    /**
     * Finds the first walk type (in [allowedWalkTypes] order) that hasn't
     * been recorded yet, or null if every allowed walk type is already done
     */
    val getNextWalkType: WalkType? = allowedWalkTypes.firstOrNull { !walkMap.containsKey(it) }

    /** Whether a Walk has already been recorded for the given type */
    fun hasWalk(walkType: WalkType): Boolean {
        return walkMap.containsKey(walkType)
    }

    /** Sums the sample sizes of every recorded walk among the allowed walk types */
    fun getSampleSize(): Int = allowedWalkTypes.sumOf { walkMap[it]?.getSampleSize() ?: 0 }

    /**
     * Restricts how many walk types are expected for this walk number
     * (must be 1–4; anything else falls back to all WalkType values).
     * Returns this holder for chaining.
     */
    fun setAllowedWalkTypes(count: Int): WalkHolder {
        allowedWalkTypes = if (count in 1..4) WalkType.values().copyOfRange(0, count) else WalkType.values()
        return this
    }


}