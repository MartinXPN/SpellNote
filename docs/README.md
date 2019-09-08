# SpellNote

SpellNote is an android application designed for creating texts, taking notes, editing and correcting essays, etc. 
It supports spell-checking in offline mode. The app is designed for multiple languages. Currently English, Russian and Armenian
are supported. The number of languages will grow in the near future.

SpellNote is the updated version of my first android project [ProCorrector](https://github.com/MartinXPN/ProCorrector---How-NOT-to-code-in-Android).
After publishing several updates of ProCorrector I realised that the code was written so terribly that it was almost impossible
to maintain and decided to write everything from scratch. As PlayStore doesn't allow changes in packagename I had to release
SpellNote as a separate app because the packagename of Procorrector didn't match with this project.

<p float="left">
  <img src="https://raw.githubusercontent.com/MartinXPN/SpellNote/develop/images/SpellNote%20Screenshot-1.png" width="120" />
  <img src="https://raw.githubusercontent.com/MartinXPN/SpellNote/develop/images/SpellNote%20Screenshot-2.png" width="120" /> 
  <img src="https://raw.githubusercontent.com/MartinXPN/SpellNote/develop/images/SpellNote%20Screenshot-3.png" width="120" />
  <img src="https://raw.githubusercontent.com/MartinXPN/SpellNote/develop/images/SpellNote%20Screenshot-4.png" width="120" />
  <img src="https://raw.githubusercontent.com/MartinXPN/SpellNote/develop/images/SpellNote%20Screenshot-5.png" width="120" />
  <img src="https://raw.githubusercontent.com/MartinXPN/SpellNote/develop/images/SpellNote%20Screenshot-6.png" width="120" />
  <img src="https://raw.githubusercontent.com/MartinXPN/SpellNote/develop/images/SpellNote%20Screenshot-7.png" width="120" />
</p>

## Structure
First thing that was missing in ProCorrector was a concrete scructure of the project that would allow easy navigation and a
more maintainable code. Some android projects use type-based structure having packages like `activities`, `fragments`, etc...
However I think that this kind of structure gets messy quite easy when the number of fragmetns or activities become large.

SpellNote uses something very similar to [Clean Architecture pattern](https://medium.com/@dmilicic/a-detailed-guide-on-developing-android-apps-using-the-clean-architecture-pattern-d38d71e94029).

```
app
|
|---> models   ->   EntityClasses
|---> services ---> one service-package for each entity
|---> ui       ---> MVVM
|---> util     -> UtilityClasses
```

* `Models` that are simple POJO classes don't have any dependencies. They just define the entities in the project.

* `Services` are simple interfaces that need to define the behaviour of a specific action (an http request to fetch all 
available languages or save the current document. The latter can be done either with an http request or by writing to
a local storage and it's the responsibility of a `repository` to define its behaviour). 
Services "know" about models, yet they have no idea about being in an android platform or any other package.

* `Repositories` are sub-packages of services and they implement a specific service. Say a service defines how to add a word to 
dictionary. It may have two separate subpackages one of which defines how to save a word locally and another one sends an http 
request to a server.

* `UI` package includes all the android code. It "knows" about all other packages. It defines both the behaviour of views
and the logic of which service to use and when. Yet, putting everything in one activity class would be too stupid and
everything would become unmaintainable again. Thats why we're using MVVM design patter to separate models from views and
the way each model is glued to its view.


## Design Pattern - MVVM
As android is currently supporting [data-binding](https://developer.android.com/topic/libraries/data-binding/index.html), 
MVVM paradigm becomes very appealing while developing android apps. MVVM = (Model + View + ViewModel).
* `Model`: Entity that contains needed information to be displayed inside a View. It doesn't "know" anything about view or
even ViewModel. It needs to be be self contained and depend only on itself (or maybe other models).
* `View`: Activities, Fragments, etc are considered to be views. Views don't do anything "smart". They just display information
provided by ViewModel.
* `ViewModel`: These classes glue together Views and Models. They are responsible for delegating triggers to services which 
make API calls, interact with databases etc. ViewModels are responsible for presentation logic and all the "clever" part of
the code for presenting information inside View. ViewModels "know" about Models, Services but they don't contain android
dependent code. That means they don't set onClick listeners or anything like that. However the logic of what needs to be done
when click is performed is implemented inside a ViewModel. View is responsible in delegating the click event to ViewModel.


## Concurrency - RxJava
For a good and easy support for threading we've used [RxJava](https://github.com/ReactiveX/RxJava)/[RxAndroid](https://github.com/ReactiveX/RxAndroid).
Any part of code that has something to do with concurrency is using RxJava. For making code cleaner and more understandable
we've used Java 8 (with its support for lambda expressions, etc).


## Libraries &amp; Tools
This project makes use of several libraries and tools that make code much more readable and a lot of things easier to implement.
Here are several of them.

* [Firebase](https://firebase.google.com/): Provides a lot of useful tools for app development. We've used 
[Firebase Analytics](https://firebase.google.com/docs/analytics/),
[Firebase Database](https://firebase.google.com/docs/database/) for keeping list of available languages and database change-suggestions,
[Firebase Storate](https://firebase.google.com/docs/storage/) for keeping the databases of languages,
[Firebase Crash Reporting](https://firebase.google.com/docs/crash/).
* [Realm](https://realm.io/docs/java/latest/): As a local database.
* [Timber](https://github.com/JakeWharton/timber): A powerful logging library
* [Picasso](http://square.github.io/picasso/): For image loading and caching
* [Retrofit](http://square.github.io/retrofit/): For http requests
