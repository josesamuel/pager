# Pager

Pager  - An alternative to Cursor, providing paged access to a collection of data across process.


## Why Pager


* Unlike Cursor which represents a database row and only allows primitive access to each columns, Pager allows you to pass the actual object.

```java
@Remoter
public interface ISampleService {
    DataPager<MyData> getMyDatas();
}

```

* Pager allows you to notify the clients that a given data is replaced with another data. The change gets automatically refreshed at client.
* Pager allows you to even change the size of the data set which is seamlessly refreshed at client.
* Pass DataPager across process through AIDL or [Remoter](http://j.mp/Remoter)



Getting Pager
--------

Gradle dependency

```groovy
dependencies {
	//Replace "api" with "compile" for pre AndroidStudio 3
    api 'com.josesamuel:pager:1.0.0'
}
```


License
-------

    Copyright 2018 Joseph Samuel

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


