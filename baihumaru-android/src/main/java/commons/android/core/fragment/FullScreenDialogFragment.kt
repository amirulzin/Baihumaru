package commons.android.core.fragment

import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import commons.android.ProjectConstants

abstract class FullScreenDialogFragment<V : ViewDataBinding> : DataBindingDialogFragment<V>() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(DialogFragment.STYLE_NO_FRAME, ProjectConstants.APP_THEME)
  }

  override fun onStart() {
    super.onStart()
    dialog?.let { d ->
      d.window?.apply {
        setBackgroundDrawableResource(ProjectConstants.SHADOW_85)
        setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
      }
    }
  }
}