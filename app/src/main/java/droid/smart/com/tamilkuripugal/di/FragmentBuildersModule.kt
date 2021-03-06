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

package droid.smart.com.tamilkuripugal.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import droid.smart.com.tamilkuripugal.ui.draft.DraftKuripugalFragment
import droid.smart.com.tamilkuripugal.ui.favourite.FavouritesFragment
import droid.smart.com.tamilkuripugal.ui.kurippu.KurippuFragment
import droid.smart.com.tamilkuripugal.ui.kuripugal.KuripugalFragment
import droid.smart.com.tamilkuripugal.ui.main.MainFragment
import droid.smart.com.tamilkuripugal.ui.main.SigninFragment
import droid.smart.com.tamilkuripugal.ui.newkuripugal.NewKuripugalFragment
import droid.smart.com.tamilkuripugal.ui.policy.PrivacyFragment
import droid.smart.com.tamilkuripugal.ui.settings.ProfileFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun contributeKuripugalFragment(): KuripugalFragment

    @ContributesAndroidInjector
    abstract fun contributeNewKuripugalFragment(): NewKuripugalFragment

    @ContributesAndroidInjector
    abstract fun contributeDraftKuripugalFragment(): DraftKuripugalFragment

    @ContributesAndroidInjector
    abstract fun contributeKurippuFragment(): KurippuFragment

    @ContributesAndroidInjector
    abstract fun contributePrivacyFragment(): PrivacyFragment

    @ContributesAndroidInjector
    abstract fun contributeFavouriteKuripugalFragment(): FavouritesFragment

    @ContributesAndroidInjector
    abstract fun contributeSigninFragment(): SigninFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment

}
