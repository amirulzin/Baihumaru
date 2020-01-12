package moe.baihumaru.android.ui.home.nav

import androidx.lifecycle.ViewModel
import commons.android.arch.NonNullMutableLiveData
import javax.inject.Inject

class NavViewModel @Inject constructor(val selectionLive: SelectionLive) : ViewModel()

class SelectionLive @Inject constructor() : NonNullMutableLiveData<Int>(-1)