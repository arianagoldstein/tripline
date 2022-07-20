# TRIPLINE
Ariana Goldstein

## Table of Contents
1. [Overview](#Overview)
2. [Product Spec](#Product-Spec)
3. [Wireframes](#Wireframes)
4. [Schema](#Schema)
5. [Resources](#Resources)

## Overview
### Description
Tripline is a hub for avid travelers to share their experiences and potential vacationers who are looking for inspiration. Like a combination of TripAdvisor and Instagram, it enables users to document and share trips they've taken in a social media post format.

They can save all of their previous flights, hotel stays, excursions, and photos for future reference and to preserve memories. Users will be able to post and update trips they go on by adding images, links, descriptions, and other information about their vacations.

### App Evaluation

- **Category:** travel, social
- **Mobile:** The app would be primarily designed for mobile since it will allow users to take/upload photos with their camera. It would also incorporate push notifications to notify a user when there is activity on their post or when someone has posted a trip in a location the user is interested in.
- **Story:** Currently, there is no platform that effectively combines the social aspect of Instagram with the details/events of users' travels. Tripline would create a worldwide community of travelers and foster exploration and connection among them.
- **Market:** A range of users-- from first-time to seasoned travelers-- would get value from Tripline. Infrequent travelers could use it for recommendations on where to go next while worldly vacationers could use it to display their experiences and maintain a digital scrapbook of their journeys.
- **Habit:** Like Instagram, users would scroll through frequently for entertainment and inspiration. At the very least, each time they travel, they would use the app to record their experiences.
- **Scope:** I could begin with the core functionality of our Week 3 Instagram project. Users can create trips and add them to their profile. Later, I'd expand to the "feed" page and incorporate interaction with other users' posts (likes, comments, a "save" feature, etc).

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can log in to access their profile
* User can register a new account
* User can view their profile with trips they've added
* User can add a new trip (with title, location, events, accommodations, and photos)
* User can view "feed" of public posts of people they follow
* User can see the details for a trip upon clicking on it
* User can search for trips and filter by location, trip duration, distance from current location, price, etc.


**Optional Nice-to-have Stories**

* User can interact with public posts (like, comment, "save" the post to refer to later)
* User can indicate which locations they're interested in (for example, they want to receive push notifications for trips in Hawaii)

### 2. Screen Archetypes

* Login
    * User can log in to access their profile
* Register
    * User can register a new account
* Profile
    *  can view their profile with trips they've added
* Add Trip
    * User can add a new trip (with title, location, events, accommodations, and photos)
* Stream
    * User can view "feed" of public posts of people they follow
* Detail
    * User can see the details for a trip upon clicking on it
* Search
    * User can search for trips and filter by location, trip duration, distance from current location, price, etc.

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Stream
* Search
* Add Trip
* Profile

**Flow Navigation** (Screen to Screen)

* Login
    * Stream
* Register
    * Stream
* Profile
    * Add Trip
* Add Trip
    * Profile
* Stream
    * Detail
* Detail
    * Stream
* Search
    * Detail

## Wireframes
For a user who follows at least one account
![userwithfollowing1](https://user-images.githubusercontent.com/48846986/174133001-97ecce07-6b2c-4083-9428-26c27bf53204.jpg)

For a first-time user
![firsttimeuser](https://user-images.githubusercontent.com/48846986/174132437-5cf0afa7-8ea3-4cbe-97c4-ed34790b9900.jpg)

## Schema

### Models
**User**
| Property | Type   | Description                                     |
|:-------- | ------ |:----------------------------------------------- |
| objectId | string | unique ID for the User (default field)          |
| username | string | unique username for the user (default field)    |
| email    | string | email address saved upon signup                 |
| password | string | password user can use to log in (default field) |


**Trip**
| Property    | Type            | Description                                            |
| ----------- | --------------- |:------------------------------------------------------ |
| objectId    | string          | unique ID for the Trip (default field)                 |
| title       | string          | title for the Trip, shows up at the top of a Trip post |
| description | string          | description of this Trip, displays below cover image   |
| location    | GeoPoint        | stores overall location of Trip                        |
| startDate   | Date            | stores starting date of Trip                           |
| endDate     | Date            | stores ending date of Trip                             |
| coverPhoto  | File            | stores a landscape oriented image to show with Trip    |
| author      | pointer to User | pointer to the User who created this post              |


**Event**
| Property     | Type            | Description                                                      |
| ------------ | --------------- |:---------------------------------------------------------------- |
| objectId     | string          | unique ID for the Event (default field)                          |
| title        | string          | title for the Event, shows up at the top of an Event             |
| description  | string          | description of this Event, displays below image gallery          |
| photos       | array of Files  | stores up to 3 photos for this Event                             |
| activityType | string          | stores category of this Event (restaurant, hotel, activity, etc) |
| trip         | pointer to Trip | pointer to the Trip this Event is associated with                |


### Networking
- Login
    - (Read/GET) check credentials against database
- Register
    - (Create/POST) creating a new User in the database
- Profile
    - (Read/GET) query logged-in User object
    - (Read/GET) query trips created by the logged-in User
- Add Trip
    - (Create/POST) creating a new Trip and Event(s) in the database
- Stream
    - (Read/GET) query Trips created by Users the logged-in User follows
- Detail
    - (Read/GET) query Events associated with the Trip being displayed
- Search
    - (Read/GET) query Trips that match the search term


### Resources
- [Google Maps API](https://developers.google.com/maps) for displaying interactive map of user's pins
- [Google Places API](https://developers.google.com/maps/documentation/places/web-service/overview) for location search autocomplete and reverse geocoding
- [Google Maps Static API](https://developers.google.com/maps/documentation/maps-static/overview) for displaying map of user pins on profile
- [Zipcode API](https://www.zipcodeapi.com/API#zipToLoc) for converting user-inputted zip code to a latitude-longitude pair
