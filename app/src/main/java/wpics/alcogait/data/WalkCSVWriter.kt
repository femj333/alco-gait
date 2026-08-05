package wpics.alcogait.data

import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

object WalkCSVWriter {
    /** Writes phone.csv for the current WalkHolder */
    fun write(
        walk: Walk,
        folderPath: String
    ): Boolean {
       val fileName = folderPath + File.separator + "phone.csv"

        return try {
            val writer = CSVWriter(FileWriter(fileName, false))

            walk.toCSVFormat().forEach { writer.writeNext(it) }

            writer.close()
            true
        } catch(e: IOException) {
            e.printStackTrace()
            false
        }
    }
}