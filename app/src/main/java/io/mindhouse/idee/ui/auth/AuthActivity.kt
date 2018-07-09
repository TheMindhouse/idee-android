package io.mindhouse.idee.ui.auth

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.TimeAnimator
import android.animation.ValueAnimator
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.transition.TransitionManager
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.mindhouse.idee.R
import io.mindhouse.idee.data.AuthorizeRepository
import io.mindhouse.idee.ui.MainActivity
import io.mindhouse.idee.ui.base.MvvmActivity
import kotlinx.android.synthetic.main.activity_auth.*
import timber.log.Timber


class AuthActivity : MvvmActivity<AuthViewState, AuthViewModel>() {

    companion object {
        private const val RC_SIGN_IN = 0xdd
        fun newIntent(context: Context): Intent = Intent(context, AuthActivity::class.java)
    }

    private val fbCallbackManager = CallbackManager.Factory.create()
    private lateinit var googleSignInClient: GoogleSignInClient
    private var gradientAnimator: Animator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        initFbLogin()
        initGoogleLogin()
    }

    override fun onStart() {
        super.onStart()
        animateGradient()
    }

    override fun onLoggedOut() {
        //do nothing
    }

    override fun onStop() {
        stopGradientAnimation()
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            viewModel.onGoogleToken(task)
        } else {
            fbCallbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun render(state: AuthViewState) {
        TransitionManager.beginDelayedTransition(content)
        if (state.isLoading) {
            facebookLoginButton.visibility = View.GONE
            googleLoginButton.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            facebookLoginButton.visibility = View.VISIBLE
            googleLoginButton.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }

        if (state.isLoggedId) {
            val intent = MainActivity.newIntent(this)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }

    //==========================================================================

    private fun initGoogleLogin() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, options)

        googleLoginButton.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    private fun initFbLogin() {
        LoginManager.getInstance().registerCallback(fbCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                viewModel.onFacebookToken(result)
            }

            override fun onCancel() {
                Timber.w("Facebook login cancelled.")
            }

            override fun onError(error: FacebookException) {
                val message = error.localizedMessage
                renderError(message)
            }
        })

        facebookLoginButton.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, AuthorizeRepository.FACEBOOK_PERMISSIONS)
        }
    }

    private fun animateGradient() {
        val start = ContextCompat.getColor(this, R.color.turquoise)
        val mid = ContextCompat.getColor(this, R.color.green)
        val end = ContextCompat.getColor(this, R.color.lime)
        val gradient = content.background as GradientDrawable

        val evaluator = ArgbEvaluator()
        val animator = TimeAnimator.ofFloat(0.0f, 1.0f)

        gradientAnimator?.end()
        gradientAnimator = animator

        animator.duration = 3500
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener {
            val fraction = it.animatedFraction
            val newStart = evaluator.evaluate(fraction, start, end) as Int
            val newMid = evaluator.evaluate(fraction, mid, start) as Int
            val newEnd = evaluator.evaluate(fraction, end, mid) as Int

            gradient.colors = intArrayOf(newStart, newMid, newEnd)
        }

        animator.start()
    }

    private fun stopGradientAnimation() {
        gradientAnimator?.end()

        val start = ContextCompat.getColor(this, R.color.turquoise)
        val mid = ContextCompat.getColor(this, R.color.green)
        val end = ContextCompat.getColor(this, R.color.lime)
        val gradient = content.background as GradientDrawable
        gradient.colors = intArrayOf(start, mid, end)
    }


    override fun createViewModel() =
            ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]

}

