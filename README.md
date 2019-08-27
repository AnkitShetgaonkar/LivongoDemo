# LivongoDemo
1. I spent most of my time architecting the app (might feel overkill for just one task feature).
2. Tried to show my skills writing modular code, with loose coupling with the new MVVM architecture.

Architecture
1. Used Kotlin+MVVM+RepositoryPattern+Coroutines+LiveData+Koin
2. Repository pattern: Abstracts the data source from ViewModel
3. The View is hidden from all the business logic.
4. Used Koin, lightweight dependency injection
5. Divided the project in 2 modules
    1. app - The main app
    2. googelfit - Does all the heavylifting of making async calls to Google Fit Api

Extra Features
1. Supports toggling of the chronological data in app using the livedata whose state is managed in the viewmodel
2. App supports orientation changes with the live data in place.
3. If User denies to give permission to access the google fit data, then a snackbar is persistantly shown to reattempt.
4. Used constraint layout, and card view as item of recycler view
5. Refresh button to refetch new data if any
