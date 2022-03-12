package com.codepath.apps.restclienttemplate.models

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Tweet {
    var body: String = ""
    var createdAt: String = ""
    var user: User? = null

    companion object {
        private const val SECOND_MILLIS = 1000
        private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
        private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private const val DAY_MILLIS = 24 * HOUR_MILLIS

        fun getRelativeTimeAgo(rawJsonDate: String?): String {
            val twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy"
            val sf = SimpleDateFormat(twitterFormat, Locale.ENGLISH)
            sf.setLenient(true)
            try {
                val time: Long = sf.parse(rawJsonDate).getTime()
                val now = System.currentTimeMillis()
                val diff = now - time
                return if (diff < MINUTE_MILLIS) {
                    "just now"
                } else if (diff < 2 * MINUTE_MILLIS) {
                    "a minute ago"
                } else if (diff < 50 * MINUTE_MILLIS) {
                    (diff / MINUTE_MILLIS).toString() + " m"
                } else if (diff < 90 * MINUTE_MILLIS) {
                    "an hour ago"
                } else if (diff < 24 * HOUR_MILLIS) {
                    (diff / HOUR_MILLIS).toString() + " h"
                } else if (diff < 48 * HOUR_MILLIS) {
                    "yesterday"
                } else {
                    (diff / DAY_MILLIS).toString() + " d"
                }
            } catch (e: ParseException) {
                Log.i("getRelativeTimeAgo", "getRelativeTimeAgo failed")
                e.printStackTrace()
            }
            return ""
        }

        fun fromJson(jsonObject: JSONObject): Tweet {
            val tweet = Tweet()
            tweet.body = jsonObject.getString("text")
            tweet.createdAt = getRelativeTimeAgo(jsonObject.getString("created_at"))
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"))
            return tweet
        }

        fun fromJsonArray(jsonArray: JSONArray): List<Tweet> {
            val tweets = ArrayList<Tweet>()
            for (i in 0 until jsonArray.length()) {
                tweets.add(fromJson(jsonArray.getJSONObject(i)))
            }
            return tweets
        }
    }
}