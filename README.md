# Jawall
Based on GoWall and PyWall

## How to use
1. Create the file ```~/.config/jawall/config.yml```
2. The file should be structured like below
```
themes:
    - name: "rainbow"
    colors:
        - "#9c0000" #red
        - "#ad5400" #orange
        - "#a39300" #yellow
        - "#0c8f00" #green
        - "#0e008f" #blue
        - "#40008f" #purple
        - "#1ce1ce" #highlight
        - "#b5c2c7" #light
        - "#202324" #dark
```   
3. Then use a command (right now there is only 1, convert)
ex:
```
jawall convert [filepath] -t [theme name]
jawall convert ~/Pictures/wallpapers/image2.png -t rainbow
```
**WARNING:** 
Do NOT try to use '#' to have jawall convert every image in a directory!
I got lazy and did not implement that whatsoever :)

4. Lastly your image will be created and prepended with jawall_(imagename)