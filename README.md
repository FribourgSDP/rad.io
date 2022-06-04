# rad.io

[![Build Status](https://api.cirrus-ci.com/github/FribourgSDP/rad.io.svg)](https://cirrus-ci.com/github/FribourgSDP/rad.io)
[![Maintainability](https://api.codeclimate.com/v1/badges/d819e783c8fc8700526b/maintainability)](https://codeclimate.com/github/FribourgSDP/rad.io/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/d819e783c8fc8700526b/test_coverage)](https://codeclimate.com/github/FribourgSDP/rad.io/test_coverage)


<img src="app/src/main/res/drawable-ldpi/ic_logo.png" width="200p" height="240p">

## About the game

Do you like the game [skribbl.io](http://skribbl.io) ? Do you like blind tests ? You're gonna like `rad.io` ! `rad.io` is a multiplayer game, where players take turns singing a random song to the others with the help
of lyrics. At the same time, the other players try to guess the song. The first one to guess the song,
gets the most points. The more players guess the song, the more points the singer gets. Also, if you are
too shy to sing, you can let a bot sing all the songs for you. Users can create their own playlist to play with
friends or download pre-existing playlists from Spotify.

If you are shy, you can still play without singing, in that case a robot will read out the lyrics of a song for you.

It is available as an Android application on android 7.0 and newer versions.

## Watch our trailer 

<iframe width="560" height="315" src="https://www.youtube.com/embed/CNpJROtBrTg" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>

## How to play ?

You can create your own playlist with a selection of songs you like, or simply use one of the provided starter playlists. You simply create a game that other players can join by typing the id of your lobby or scanning a QR code. When everyone is ready, you can launch the gam and have fun !

## Features

- Playlist creation: For each song you can enter the song's title and artist. If the song's lyrics are found by the lyrics provider (Musixmatch), the app will use them. Otherwise you can decide to enter his own lyrics or none. Guessers may have a hint consisting in the number of letters in the title.
- You can create a game that other players can join by entering the id of the lobby, or by simply scanning a QR code with their phone.

- 2 game-modes :
    1. Each player sings in turn to make the other guess. With the help of lyrics whenever there are any.
    2. Every player is guessing while a synthetic voice reads out the lyrics, replacing the title of the song  with a _beep_ if it appears in the lyrics.

- Song's title hint: for each letter in the title, there is a "_" display, to indicate the number of letter in the title. For example, radio gaga => _ _ _ _ _ ||  _ _ _ _. After a while, random letters appear. The singer can also choose to display the hint, showing it to the other players.
- 5 basic playlist are available to everyone to have fun with basic hits, french classics, movie or video theme songs, and famous classical music pieces.
- Players can put their playlist on the cloud in order to use them in a game with their friends. 
- A voice channel is setup to be able to sing and be heared, and moreover to hear the reactions of your friends ! 
- The app is available in english and french.
  


Voice channel : https://www.agora.io/

Lyrics provider : https://www.musixmatch.com/

We use spotify to retrieve the playlists of a user : https://developer.spotify.com

...and Firebase to store all data online : https://firebase.google.com
