/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import { defineClientConfig } from '@vuepress/client';
import ElementPlus from 'element-plus';
import IoTDB from './components/IoTDB.vue';
import IoTDBZH from './components/IoTDBZH.vue';
import Contributor from './components/Contributor.vue';
import 'element-plus/dist/index.css';

export default defineClientConfig({

  enhance: ({ app }) => {
    app.use(ElementPlus);
    app.component('IoTDB', IoTDB);
    app.component('Contributor', Contributor);
    app.component('IoTDBZH', IoTDBZH);
  },
});