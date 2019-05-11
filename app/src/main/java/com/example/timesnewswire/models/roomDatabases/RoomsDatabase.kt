package com.example.timesnewswire.models.roomDatabases

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.*
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.articles.ArticleEntity
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.sections.SectionEntity
import java.util.*

@Database(entities = [ArticleEntity::class, SectionEntity::class], version = 2)
abstract class RoomsDatabase: RoomDatabase() {
    companion object {
        private lateinit var instance: RoomsDatabase

        @Synchronized
        fun GetInstance(context: Context): RoomsDatabase {
            if (::instance.isInitialized)
                return instance

            instance = Room.databaseBuilder(context.applicationContext,
                RoomsDatabase::class.java, "RoomsDatabase")
                .addCallback(DBPopulateTest)
                .build()

            return instance
        }
    }

    abstract fun GetArticleDAO(): TNWDao

    private object DBPopulateTest: RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            PopulateDBAsyncTask(instance).execute()
        }
    }

    /**
     * For tests
     */
    private class PopulateDBAsyncTask(dbPopulate: RoomsDatabase): AsyncTask<Unit, Unit, Unit>() {
        private val articleDAO: TNWDao by lazy { dbPopulate.GetArticleDAO() }

        override fun doInBackground(vararg params: Unit) {
            articleDAO.InsertArticle(
                ArticleEntity(
                    "Test1",
                    Date(),
                    "Test1",
                    "https://www.nytimes.com/2019/05/07/us/denver-mushroom-psilocybin-vote.html",
                    "Test1"
                )
            )
            articleDAO.InsertArticle(
                ArticleEntity(
                    "Test2",
                    Date(),
                    "Test2",
                    "https://www.nytimes.com/2019/05/07/us/denver-mushroom-psilocybin-vote.html",
                    "Test2"
                )
            )
            articleDAO.InsertArticle(
                ArticleEntity(
                    "Test3",
                    Date(),
                    "Test3",
                    "Test3",
                    "Test3"
                )
            )
        }
    }
}