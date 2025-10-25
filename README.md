## Project Title: Movie Mate


## Project Description:
 Movie Mate is an Android app built with Jetpack Compose that displays movies and TV shows fetched from a remote API. The app demonstrates a modern Android architecture using MVVM, Retrofit, RxKotlin (Single.zip for concurrent API calls), and Koin for dependency injection. It includes loading shimmer effects, graceful error handling, and a clean details screen.
## Key Features:


• Toggle between Movies and TV Shows (top toggle or tabs)

• Shimmer effect while data loads (home &   details)

• Concurrent API requests for movies and TV shows via Single.zip (RxKotlin)

• Retrofit for networking

• MVVM architecture

• Dependency injection with Koin

• Graceful error handling (Snackbars / Toasts)

## Getting Started

Prerequisites

Android Studio Flamingo or later


Android SDK (latest) and an emulator or a physical device

Setup

Clone the repository: git clone https://github.com/<your-username>/movie-mate.git
cd movie-mate

## Api key
IMDb / OMDb

If you used IMDb / OMDb:OMDB_API_KEY=your_omdb_api_key_here

## Tech Stack

Kotlin

• Jetpack Compose

• Retrofit + OkHttp

• RxJava / RxKotlin (Single / Single.zip)

• Koin for DI

• Coil (or Glide) for image loading

• Shimmer effect library (e.g., accompanist-placeholder-material or other)






