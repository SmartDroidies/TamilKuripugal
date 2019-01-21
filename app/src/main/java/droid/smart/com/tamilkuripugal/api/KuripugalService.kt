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

package droid.smart.com.tamilkuripugal.api

import androidx.lifecycle.LiveData
import droid.smart.com.tamilkuripugal.vo.Category
import droid.smart.com.tamilkuripugal.vo.Kurippu
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * REST API access points
 */
interface KuripugalService {
    @GET("category/list")
    fun getCategories(): LiveData<ApiResponse<List<Category>>>

    @GET("rest")
    fun getKuripugal(@Query("ct") category: Int): LiveData<ApiResponse<List<Kurippu>>>

    @GET("json")
    fun getKurippu(@Query("id") kurippuId: String): LiveData<ApiResponse<Kurippu>>

    //http://tamil.tips2stayhealthy.com/?json2=y&ct=health - Service to collect data from

//    @GET("users/{login}/repos")
//    fun getRepos(@Path("login") login: String): LiveData<ApiResponse<List<Repo>>>
//
//    @GET("repos/{owner}/{name}")
//    fun getRepo(
//        @Path("owner") owner: String,
//        @Path("name") name: String
//    ): LiveData<ApiResponse<Repo>>
//
//    @GET("repos/{owner}/{name}/contributors")
//    fun getContributors(
//        @Path("owner") owner: String,
//        @Path("name") name: String
//    ): LiveData<ApiResponse<List<Contributor>>>
//
//    @GET("search/repositories")
//    fun searchRepos(@Query("q") query: String): LiveData<ApiResponse<RepoSearchResponse>>
//
//    @GET("search/repositories")
//    fun searchRepos(@Query("q") query: String, @Query("page") page: Int): Call<RepoSearchResponse>
}
