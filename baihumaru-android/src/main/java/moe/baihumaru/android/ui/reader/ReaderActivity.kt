package moe.baihumaru.android.ui.reader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import commons.android.dagger.compat.DaggerActivity
import commons.android.withParcel
import moe.baihumaru.android.R
import moe.baihumaru.android.databinding.ReaderFragmentBinding
import moe.baihumaru.android.ui.common.UIChapterId

class ReaderActivity : DaggerActivity() {
  companion object {
    private const val KEY_CHAPTER = "chapter"
    @JvmStatic
    fun intentFactory(context: Context, chapterId: UIChapterId) = Intent(context, ReaderActivity::class.java)
      .withParcel(KEY_CHAPTER, chapterId)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setNavBarColor(R.color.colorNavBarSecondary)
    val binding = ReaderFragmentBinding.inflate(layoutInflater)
    setContentView(binding.root)
  }

  private fun setNavBarColor(@ColorRes colorNavBar: Int) {
    window.navigationBarColor = ContextCompat.getColor(this, colorNavBar)
  }
}