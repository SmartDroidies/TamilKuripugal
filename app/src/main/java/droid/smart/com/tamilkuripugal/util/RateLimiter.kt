/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package droid.smart.com.tamilkuripugal.util

import android.os.SystemClock
import androidx.collection.ArrayMap
import timber.log.Timber

import java.util.concurrent.TimeUnit

/**
 * Utility class that decides whether we should fetch some data or not.
 */
class RateLimiter() {
    private val timestamps = ArrayMap<String, Long>()

    @Synchronized
    fun shouldFetch(key: String,timeout: Int, timeUnit: TimeUnit ): Boolean {
        val timeout = timeUnit.toMillis(timeout.toLong())
        val lastFetched = timestamps[key]
        val now = now()
        if (lastFetched == null) {
            timestamps[key] = now
            return true
        }
        if (now - lastFetched > timeout) {
            Timber.d("ReteLimiter Time Comparision for %s : %s - %s  = %s vs %s", key, now, lastFetched,  now-lastFetched, timeout )
            timestamps[key] = now
            return true
        }
        return false
    }

    private fun now() = SystemClock.uptimeMillis()

    @Synchronized
    fun reset(key: String) {
        timestamps.remove(key)
    }

    @Synchronized
    fun elapsed(key: String) : Long {
        val lastFetched = timestamps[key]
        val now = now()
        if (lastFetched == null) {
            return -1
        } else {
            return TimeUnit.MILLISECONDS.toSeconds(now - lastFetched)
        }
    }

}
