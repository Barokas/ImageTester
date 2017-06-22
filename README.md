# Image Tester  [ ![Download](https://api.bintray.com/packages/yanirta/generic/ImageTester/images/download.svg) ](https://bintray.com/yanirta/generic/ImageTester/_latestVersion)
A Cli tool to perform Applitools visual UI validation.
Based on images or PDFs.

This tool bridges the gap in integration between frameworks that are able to take screenshots
of their applications and the ability to perform visual validation using Applitools based on them.

With Images Tester, all that required in order to perform visual test on application is to prepare
a folder with the recent screenshots on your disk and run the tool on it.

Please make sure you signed up at [Applitools](applitools.com) and you have your Applitools APIKEY.

# Example
Running the tool on a set of images located in a certain path using terminal/cmd

`\> java -jar ImageTester -k <APIKEY> -f /path/to/folder/`

# Supporting regions
To validate only a specific regions of a particular image the folder must contain .regions file with the same name as the image.
ie. If my image file is 'step1.png' then the regions file should be 'step1.regions'.
Internally the format should be 4 columns csv in the following order: left,top,width,height

Here is an example of what can be step1.regions:
```
0,0,100,200
500,100,240,123
```
In order to specify the enire image to be taken just add an empty line as part of the regions list.
