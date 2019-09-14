# Development Notes

1. Failed offline OkHttp cache implementation 

Tried utilizing a full offline behavior by *solely* relying on a custom OkHttp cache interceptor so I don't have to write a custom repository just for having a cold cache. 
Works nicely until I realized that some plugins might have to call the same URL but they might rely on their own custom behavior. 
Also, OkHttp cache seems to be relying on URL and cache headers, which might cause issues with these plugins behaviors.

Ends up with having the Room based cache implementation again.

2. Attempted `@Parcel` export for core library

I wanted to avoid `Parcelize` getting into the core library, thus plugins can benefit from a simple Java library. 
Since I have good experience with it, I used Parceler, thinking its `@Parcel` can simply be used marker annotations for these plugins.
Also, I wanted to avoid writing adapter classes to convert from the core library data classes.

Sadly, Parceler can't seems to generate the proper Kotlin class without having defaults for the declared property, which is a big nope for the core library. 
Ends up with simply keeping the Parcel class in the Android project and use Parcel converters.

3. Bottom Navigation design

At first, I'm really intrigued by the idea of having all the root navigation at the bottom. 
After few test runs, I feel that the bottom navigation is literally taking up spaces for no reason especially in a scrolling heavy app.
The auto hiding behavior also seems like a huge distraction. Side navigation works well so far and you can hide a lot of things in there.
Keeping it for now until I really feel that the distraction and hassle are becoming a bit too much.