package com.eexposito.kickstarterdashboard.persistence

import androidx.room.*
import io.reactivex.Flowable

const val PROJECT_TABLE = "projects"
const val ID = "s_no"
@Entity(
    tableName = PROJECT_TABLE,
    indices = [Index(value = [ID], unique = true)]
)
data class ProjectEntity(
    @PrimaryKey
    @ColumnInfo(name = ID)
    var sNo: Int,
    @ColumnInfo(name = "amt_pledged")
    var amtPledged: Int,
    @ColumnInfo(name = "blurb")
    var blurb: String,
    @ColumnInfo(name = "by")
    var by: String,
    @ColumnInfo(name = "country")
    var country: String,
    @ColumnInfo(name = "currency")
    var currency: String,
    @ColumnInfo(name = "end_time")
    var endTime: String,
    @ColumnInfo(name = "location")
    var location: String,
    @ColumnInfo(name = "percentage_funded")
    var percentageFunded: Int,
    @ColumnInfo(name = "num_backers")
    var numBackers: Int,
    @ColumnInfo(name = "state")
    var state: String,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "type")
    var type: String,
    @ColumnInfo(name = "url")
    var url: String
)

@Dao
abstract class ProjectDao {
    @Query("select * from $PROJECT_TABLE")
    abstract fun getProjects(): Flowable<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertProjects(projectList: List<ProjectEntity>)
}
