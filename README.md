# Pager

Pager  - An alternative to Cursor, providing paged access to a collection of data, which can be shared across a process.


## Why Pager


* Unlike Cursor which represents a database row and only allows primitive access to each columns, Pager allows you to pass the actual object.

```java

//----------------------------------
//Using Cursor will be something like this

Cursor cursor = getCursor();

//Traverse each row
if (cursor.moveToFirst()) {
	do{
		//primitive access to each coloumn 
		String name = cursor.getString(firstColumn);
		int age = cursor.getInt(secondColumn);
		
		//create your object from each column read
		Employee employee = new Employee(name, age)
		
		//do something with employee
	} while (cursor.moveToNext())
}

//----------------------------------

//Using Pager

Pager<Employee> employees = getEmployeePager(); //Local or Remoter service call
for(Employee employee: employees){
	//do something with employee
}

```

* Pager allows you to notify the clients that a given data is replaced with another data. The change gets automatically refreshed at client. 
* Add or remove data at the source, or change the whole data source, which gets automatically refreshed at client.
	* No need of ContentObserver or requery. 
	* The original Pager automatically reflect the updated data
* Pass Pager across process using [Remoter](http://j.mp/Remoter)

```java
@Remoter
public interface ISampleService {
    Pager<Employee> getEmployeePager();
}

```



Getting Pager
--------

Gradle dependency

```groovy
dependencies {
	//Replace "api" with "compile" for pre AndroidStudio 3
    api 'com.josesamuel:pager:1.0.3'
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


