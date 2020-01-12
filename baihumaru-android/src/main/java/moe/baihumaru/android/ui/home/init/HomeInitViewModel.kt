package moe.baihumaru.android.ui.home.init

import commons.android.arch.RxMultiViewModel
import javax.inject.Inject

class HomeInitViewModel @Inject constructor(val homeLive: HomeInitLive) : RxMultiViewModel(homeLive.disposables)