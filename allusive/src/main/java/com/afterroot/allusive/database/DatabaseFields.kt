/*
 * Copyright (C) 2016-2019 Sandip Vaghela
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.afterroot.allusive.database

object DatabaseFields {
    //table 'users'
    val USERS = "users"
    val FIELD_NAME = "name"
    val FIELD_EMAIL = "email"
    val FIELD_UID = "uid"

    //table 'pointers'
    val POINTERS = "pointers"
    val FIELD_DESC = "description"
    val FIELD_FILENAME = "filename"
    val FIELD_TIME = "time"
    val FIELD_UPLOAD_BY = "uploadedBy"
}