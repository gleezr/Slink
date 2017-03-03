Slink
==================
Slink is a small library containing a wrapper / helper for Android's shared preferences.     
This library gives you the option to write encrypted shared preferences data using the Facebook's Conceal library.

Performance
=================
Most of other tools implementing secured shared preferences use encryption for each key and value you input separately.
Slink uses Facebook's conceal to save the entire set of encrypted objects, saving you a lot of unnecessary boilerplate code and performance overhead.

How to get started?
==================
To add the library to your project:
- Make sure you have JCenter in your Gradle repositories.
- Add the following Gradle dependency to your build.gradle.
```Groovy
  compile 'com.gleezr:slink:1.0.3'
```

Usage
==================
This library implements the Android SharedPreferences / Editor interfaces.      
For the documentation about the behavior of these interfaces please refer to the official Android documentation ([SharedPreferences][1]/[Editor][2]).

Additionally, this library contains a class called SlinkManager which helps you retrieving your Shared Preferences instances.

For example:
```Java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedprefs = SlinkManager.getSlink(this, "MySharedPrefrences");

        // Gets the editor file which helps you stage the changes
        SharedPreferences.Editor editor = sharedprefs.edit();

        editor.putInt("MyInteger", 69).apply();

        int myInteger = sharedprefs.getInt("MyInteger", -1);
    }
}
```

Support
===========
Please refer to the issues tab.

Enjoy using Slink.

#License
```
Copyright 2016 Gleezr (http://www.gleezr.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[1]: https://developer.android.com/reference/android/content/SharedPreferences.html
[2]: https://developer.android.com/reference/android/content/SharedPreferences.Editor.html

