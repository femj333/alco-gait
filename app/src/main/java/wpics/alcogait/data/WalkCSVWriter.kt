package wpics.alcogait.data

import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

object WalkCSVWriter {
    /** Writes phone.csv for the current WalkHolder */
    fun write(
        walkHolder: WalkHolder,
        folderPath: String
    ): Boolean {
       val fileName = folderPath + File.separator + "phone.csv"
        val space = arrayOf("")

        return try {
            val writer = CSVWriter(FileWriter(fileName, false))

            for (walkType in WalkType.values()) {
                if (walkHolder.hasWalk(walkType)) {
                    writer.writeNext(arrayOf(walkType.toString()))
                    writer.writeNext(space)
                    walkHolder.get(walkType)?.toCSVFormat()?.forEach { writer.writeNext(it) }
                    writer.writeNext(space)
                    writer.writeNext(space)
                    writer.writeNext(space)
                    writer.writeNext(space)
                }
            }

            writer.close()
            true
        } catch(e: IOException) {
            e.printStackTrace()
            false
        }
    }
}