# Chromecast Background Downloader

This project automatically retrieves a list of backgrounds used for the Chromecast.  This is accomplished by parsing the backgrounds given on the Chromecast home-page and optionally adding a similar gradient and author watermark, both using standard library functionality (i.e. there are no external requirements other than an internet connection).

This program will download the best quality found, but not all are fantastic quality (though all appear to be at a minimum, 720).

At any given time, the chromecast homepage displays 100 images that it will rotate through.  However, each run of the program may find more (as the page is dynamic and growing).  So far, I have seen over 500 unique images used and they are all suitable for a solid background slideshow..

## Use

You can customize the bahavior based on a few command line arguments, all of which are optional:

* `settings`:  You can provide a string, which will be treated as the path to a settings file.  If this is provided, no other arguments will be applied.  This file can have the following (where "Boolean" is actually "true" or "false"):
    * ApplyWatermark=Boolean
    * ApplyGradient=Boolean
    * SaveTo=/Path/To/Where/I/Want/Images

**-or-**

* `outdir`:  Provide the path to wherever the images should be saved to.  The default is:
    * Windows: `%APPDATA%\\chromecastbg\\`
    * OSX: `~/Library/Application Support/chromecastbg/`
    * Linux: `not yet implemented` (really, if you run this as-is, it will create and save to the OSX path)
* `watermark`:  This is a switch, so no argument needs to be provided other than `-watermark`.  This will opt into having a watermark applied to the downloaded image.  By default, this uses OpenSans Regular (included with the program).
* `gradient`:  This is a switch, so no argument needs to be provided other than `-gradient`.  This will opt into having a gradient applied to the downloaded image, the result is a very similar looking image to what you would see on a chromecast.

## Planned additions

* Automatically set up a system to use these images as a background slideshow (optionally)