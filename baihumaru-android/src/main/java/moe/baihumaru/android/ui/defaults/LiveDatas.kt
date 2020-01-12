package moe.baihumaru.android.ui.defaults

import commons.android.arch.NonNullMutableLiveData
import moe.baihumaru.core.PageTraveler
import javax.inject.Inject

class PluginIdLive @Inject constructor() : NonNullMutableLiveData<String>()

class PageTravelerLive @Inject constructor() : NonNullMutableLiveData<PageTraveler>(PageTraveler())
