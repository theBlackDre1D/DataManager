package com.example.seremtinameno.datamanager.core.navigation

import javax.inject.Singleton


@Singleton
class Navigator
//@Inject constructor(private val authenticator: Authenticator)
{

//    fun showMain(context: Context) {
//        when (authenticator.userLoggedIn()) {
//            true -> showMovies(context)
//            false -> showLogin(context)
//        }
//    }
//
//    private fun showMovies(context: Context) = context.startActivity(MoviesActivity.callingIntent(context))
//
//    fun showMovieDetails(activity: FragmentActivity, movie: MovieView, navigationExtras: Extras) {
//        val intent = MovieDetailsActivity.callingIntent(activity, movie)
//        val sharedView = navigationExtras.transitionSharedElement as ImageView
//        val activityOptions = ActivityOptionsCompat
//                .makeSceneTransitionAnimation(activity, sharedView, sharedView.transitionName)
//        activity.startActivity(intent, activityOptions.toBundle())
//    }
//
//    private val VIDEO_URL_HTTP = "http://www.youtube.com/watch?v="
//    private val VIDEO_URL_HTTPS = "https://www.youtube.com/watch?v="
//
//    fun openVideo(context: Context, videoUrl: String) {
//        try {
//            context.startActivity(createYoutubeIntent(videoUrl))
//        } catch (ex: ActivityNotFoundException) {
//            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)))
//        }
//    }
//
//    private fun createYoutubeIntent(videoUrl: String): Intent {
//        val videoId = when {
//            videoUrl.startsWith(VIDEO_URL_HTTP) -> videoUrl.replace(VIDEO_URL_HTTP, String.empty())
//            videoUrl.startsWith(VIDEO_URL_HTTPS) -> videoUrl.replace(VIDEO_URL_HTTPS, String.empty())
//            else -> videoUrl
//        }
//
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
//        intent.putExtra("force_fullscreen", true)
//
//        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.M)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//
//        return intent
//    }
//
//    class Extras(val transitionSharedElement: View)
}


