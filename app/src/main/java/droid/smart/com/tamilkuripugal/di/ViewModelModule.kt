/*
 * Copyright (C) 2018 The Android Open Source Project
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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import droid.smart.com.tamilkuripugal.ui.kurippu.KurippuViewModel
import droid.smart.com.tamilkuripugal.ui.kuripugal.KuripugalViewModel
import droid.smart.com.tamilkuripugal.ui.main.MainViewModel
import droid.smart.com.tamilkuripugal.viewmodel.KuripugalViewModelFactory

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(KuripugalViewModel::class)
    abstract fun bindKuripugalViewModel(kuripugalViewModel: KuripugalViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(KurippuViewModel::class)
    abstract fun bindKurippuViewModel(kurippuViewModel: KurippuViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: KuripugalViewModelFactory): ViewModelProvider.Factory
}
