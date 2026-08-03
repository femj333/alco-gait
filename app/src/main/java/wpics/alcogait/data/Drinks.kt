package wpics.alcogait.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "drinks", primaryKeys = ["userId", "timestamp"])
data class Drinks(
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "latitude") val latitude: Float,
    @ColumnInfo(name = "longitude") val longitude: Float,
    @ColumnInfo(name = "timestamp") val timestamp: String,
    @ColumnInfo(name = "drunk_state") val drunkState: String,
)
