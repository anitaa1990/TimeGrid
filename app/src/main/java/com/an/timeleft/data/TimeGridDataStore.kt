package com.an.timeleft.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TimeGridDataStore(
    private val context: Context
) {
    companion object {
        val BIRTH_DATE_KEY = stringPreferencesKey("birth_date_key")

        private val Context.dataStore by preferencesDataStore("category_preferences")
    }
    suspend fun storeBirthDate(birthDate: String) {
        context.dataStore.edit {
            it[BIRTH_DATE_KEY] = birthDate
        }
    }

    val birthDate: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[BIRTH_DATE_KEY]
    }
}