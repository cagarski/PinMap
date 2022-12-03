# this file is the python script which returns an answer to a user's query
# your machine must have python installed, also pip install the following libraries 
# pip install pandas
# pip install xlrd
# pip install openpyxl

# import libraries 
import pandas as pd
import sys
import csv
from datetime import datetime

# this function finds the return data from correct database
def get_maps(country, state, county, worldDB, usDB):
    country = country.upper()
    if country == "US":
        line = usDB[(usDB["County"] == county) & (usDB["State"] == state)]
        num = line["Drawer-case_number"].unique()
        return num
    else:
        line = worldDB[worldDB["Country_code"] == country]
        num = line["Drawer-case_number"].unique()
        return num

# this function inits the world database from excel sheet
# the file path is absolute - you must change it to each specific machine
def update_world_db():
    db = pd.read_excel("C:/PinMap/PinMap/Python Addition/countries.xls")
    return db

# this function inits the us database from excel sheet 
# the file path is absolute - you must change it to each specific machine
def update_us_db():
    db = pd.read_excel("C:/PinMap/PinMap/Python Addition/US-counties.xlsx")
    return db

# call the database inits 
worldDB = update_world_db()
usDB = update_us_db()

# open a file to write visitor data to
# the file path is absolute - you must change it to each specific machine
f = open("C:/PinMap/PinMap/Python Addition/Visitors.csv",'a')
writer = csv.writer(f)

# parse arguments which were passed by process builder in main.java
country = sys.argv[1];
state = sys.argv[2];
county = sys.argv[3];

# get a date for when the user queried the python script 
now = datetime.now();
date = now.strftime("%m/%d/%Y")

# remove the word county (last word) from the passed argument 
# if your database doesn't include the word county please comment out this code
county = county.rsplit(' ', 1)[0]

# get the return data from the query arguments 
dnum = get_maps(country, state, county, worldDB, usDB)

# return the data to main.java via print and create a row to be added to visitor sheet
if country == "us":
    country = "United States"
    print("Maps of your location can be found in DRAWER(s)# ", dnum)
    row = [country, state, county, date]
elif country == "null":
    print("Please click a valid location")
else:
    print("Maps of your location can be found in DRAWER(s)# ", dnum)
    row = [country, "", "", date]

# write data to visitor sheet and close file 
writer.writerow(row)
f.close()