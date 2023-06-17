package com.example.bledproject.data

import kotlinx.coroutines.flow.first

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

class UserStore(private val context: Context) {
	companion object {
		private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userToken")
	}

	suspend fun getAccessToken(key: String): String {
		return context.dataStore.data.first()[stringPreferencesKey(key)] ?: ""
	}

	suspend fun saveToken(key: String,token: String) {
		context.dataStore.edit { preferences ->
			preferences[stringPreferencesKey(key)] = token
		}
	}
}