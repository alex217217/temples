### Android app to show all synagogues in Melbounre

https://github.com/alex217217/temples


## We support the following:
1. Loads the map and moves the camera to Melboune.
2. All synagouges in the vicinity of given location are shown.
3. Users can add a new synagouge.
4. Users must login before adding a new synagogue.
5. Users can chat and exchnage in real-time messages to organize prayers/lectures.


## Stratagies:
1. We rely on firestore to store and preload known locations
2. Users can dynamically change and modify the database.


## Authentication:
1. users can login through 3rd party provoiders like Google, etc.
2. Alternatively, users can provide username and password.


## Chat services:
1. As this is a dynamic and real-time service, users can notify each other about changes to locations and service time updates.
2. Users can also organize and send religious questions.
3. No authentication are required so far for chat services (may change in future.
