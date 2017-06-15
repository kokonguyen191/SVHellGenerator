# SVHellGenerator
Make any osu!mania maps go full SV. A little project I coded when I was bored.

Just run OsuSVHellGui from your favorite Java IDE. Eclipse recommended.

![ ](http://i.imgur.com/IcrzQxy.png)

'Select folder' will recursively search all subfolders for beatmaps.

Higher RATE means more sudden changes. BPM is by default auto detected. Or you can put in the right main tempo if the detection is wrong (only works when you select one beatmap, which makes sense because you cannot use a single tempo for multiple beatmaps). If you want to generate uninherited timing points, you can go with the default setting which generate a new timing point at **every single note**, or set a SNAP which makes the app only generate uninherited timing points at those beats **if** there is a note at that beat.
