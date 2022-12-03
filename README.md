# PinMap

This repo is for the public release version of the Pin Map software created in Colorado School of Mines Fall 2022 Field Session class by Clare Garski, 
Jordan Ehrlich, and Wes Golliher. 

This software is an interactive map program to help library and museum visitors discover resources about their hometown and help the institution learn about who is visiting so they can improve their collections. 

## How to set up enviroment
1. Download Apache Netbeans IDE 15 application: https://netbeans.apache.org/download/index.html. 
2. Pull the project repo or download the project as a zip file (find and unzip the download in file explorer if using zip).  
3. Open Netbeans and open the project: File > Open Project > navigate to and select the project, it should have a little java coffee cup symbol next to the folder name > Open Project 
4. Insure python is installed on you machine, pip install pandas, xlrd, openpyxl. 

## How to modify the code

### Home Screen Image
1. Copy your image into the source packages (src)/map folder. 
2. In Home_Screen.java, select design, right click on the current image of Colorado.
3. Navigate to Properties > Properties > Icon > click the arrow down next to the current image name, yours may appear.
4. If your image name doesn't appear, click the 3 dots and find the image within your project using netbeans file finder. 

### Home State Choice and Other Button Text
1. In Home_Screen.java, select design, right click the button you wish to change.
2. Navigate to Properties > Properties > Text > click in the right column and type your desired text. 
3. You can also change font color and style, button color, and other settings in this Properties menu. 

### Home State Map Boundries
1. In Main.java, on line 73, change the geoposition inputs (lat, long) to the geographic center of your state. 
2. You may also want to change the zoom on line 75 depending on the size of your state. 

### Filepaths
There are 4 file paths you must change. The file paths currently start with "C:/PinMap/PinMap/...". You will need to add what goes between C:/ and the first PinMap depending on where you pulled the repo to. 
1. Path of the python script: In Main.java on line 128, the path that ends in "build/classes/generate_shelf_locs.py". 
2. Path for writing visitor data: In generate_shelf_locs.py on line 43, the path that ends in "Python Addition/Visitors.csv". 
3. Path for retrieving data on US locations: In generate_shelf_locs.py on line 34, the path that ends in "Python Addition/US-counties.xlsx", copy your US data file to the python addition folder and change the file path accordingly. 
4. Path for retrieving data on international locations: In generate_shelf_locs.py on line 28, the path that ends in "Python Addition/countries.xls", copy your international data file to the python addition folder and change the file path accordingly. 

### Heatmap Link
1. In Home_Screen.java select source, on line 183, replace the url string to your online heatmap or other resource. 

### Pop Up Customization 
1. The pop up window is designed and deployed in Main.java on lines 142-148.
2. To change the window name, go to line 148, change the third input labeled title, the current text is find a map. 
3. To change the button option text, go to line 146, change the options array elements. If you wish to change the order or behavior of the buttons you will have to modify the switch statement on lines 152-161, remember that the buttons index at 0, so the left most botton is selection 0, the next is selection 1, etc. 
4. To change the dimensions of the pop up, change the height and width inputs on line 142.
5. To change the font size or style of the main text, change the inputs on line 143.
6. To change the font size or style of the button text, change the inputs on line 144. 

### Window Names 
1. To change the window name of the map screen, change the input on line 48 of Main.java.
2. To change the window name of the home screen, change the input on line 32 of Home_screen.java. 

## How to export to executable jar file
1. Check / add the python files to the build: right click on the project in Netbeans > Properties > Sources > Source Package Folders > Add Folder > it should open in the project folder, select the Python Addition folder > Open > Ok). 
2. Clean and build the modified project (the hammer and broom icon on the top bar or Run > Clean & Build). 
3. Find the jar file (in file explorer find the unziped project folder you have modified > dist), double click the .jar file. 

## Limitations of the code
If you want to go beyond these limitation we recommend hiring someone familiar with python and java. 
1.  You must comply with the format of the files, please see the excel files in the python addition files and model your files on those. 
