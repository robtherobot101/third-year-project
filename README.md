

DESKTOP CLIENT
How to run the program:
Navigate a shell (cmd or bash) to the same directory of the jar file, and then execute the command "java -jar app0.0.jar"

Dependencies:
Java Runtime Environment at least version 8


MOBILE CLIENT
How to install:
Navigate to http://csse-s302g3.canterbury.ac.nz/ on browser and download the releases.apk file.
Install to an android device of version 4.1 (API 16) or higher
App should be in the device app list.

Dependencies:
Android 4.1 (API 16) or higher
Storage and Map Permisions.


----------------------------------------------------------

Available CLI commands:

addUser
This command adds a new donor with a name and date of birth.  
The syntax is: addUser `<name>` `<date of birth>`  
Rules:  
-The names must be comma separated without a space around the comma (eg. Andrew,Neil,Davidson)  
-If there are any spaces in the name, the name must be enclosed in quotation marks (")  
-The date of birth must be entered in the format: dd/mm/yyyy  
Example valid usage: addUser "Test,User with,SpacesIn Name" 01/05/1994  

addDonationOrgan  
This command adds one organ to donate to a user. To find the id of a user, use the listUser and describeUser commands.  
The syntax is: addDonationOrgan `<id>` `<organ>`  
Rules:  
-The id number must be a number that is 0 or larger  
-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, bone-marrow, or connective-tissue.  
Example valid usage: addDonationOrgan 0 skin  

deleteUser
This command deletes one donor. To find the id of a user, use the listUser and describeUser commands.  
The syntax is: delete `<id>`  
Rules:  
-The id number must be a number that is 0 or larger  
-You will be asked to confirm that you want to delete this user  
Example valid usage: deleteUser 1

deleteClinician
This command deletes one clinician. To find the id of a clinician, use the listClinician and describeClinician commands.
The syntax is: deleteClinician <id>
Rules:
-The id number must be a number that is 0 or larger
-You will be asked to confirm that you want to delete this clinician
Example valid usage: deleteClinician 1

removeDonationOrgan  
This command removes one offered organ from a user. To find the id of a user, use the listUser and describeUser commands.  
The syntax is: removeDonationOrgan `<id>` `<organ>`  
Rules:  
-The id number must be a number that is 0 or larger  
-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, bone-marrow, or connective-tissue.  
Example valid usage: removeDonationOrgan 5 kidney

addWaitingListOrgan
This command adds one organ which the user is waiting to receive. To find the id of a user, use the listUsers and describeUser commands.
The syntax is: addWaitingListOrgan <id> <organ>
Rules:
-The id number must be a number that is 0 or larger
-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, 
bone-marrow, or connective-tissue.
Example valid usage: addWaitingListOrgan 0 skin

removeWaitingListOrgan
This command removes one offered organ from a user. To find the id of a user, use the list and
describe commands.
The syntax is: removeDonationOrgan <id> <organ>
Rules:
-The id number must be a number that is 0 or larger
-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin,
bone-marrow, or connective-tissue.
Example valid usage: removeWaitingListOrgan 5 kidney

updateUser  
This command sets one attribute (apart from organs to be donated) of a user. To find the id of a user, use the listUsers and describeUser commands. To add or delete organs, instead use the addDonationOrgan and removeDonationOrgan commands.  
The syntax is: set `<id>` `<attribute>` `<value>`  
Rules:  
-The id number must be a number that is 0 or larger  
-The attribute must be one of the following (case insensitive): name, prefname, dateOfBirth, dateOfDeath, gender, height, weight, bloodType, region, currentAddress  
-If a name or names are used, all donors whose names contain the input names in order will be returned as matches  
-The gender must be: male, female, or other  
-The bloodType must be: A-, A+, B-, B+, AB-, AB+, O-, or O+  
-The height and weight must be numbers that are larger than 0  
-The date of birth and date of death values must be entered in the format: dd/mm/yyyy  
Example valid usage: updateUser 2 bloodtype ab+

updateClinician
This command sets one attribute of a clincian. To find the id of a clincian,
use the listClincian and describeClincian commands.
The syntax is: updateClinician <id> <attribute> <value>
Rules:
-The id number must be a number that is 0 or larger
-The attribute must be one of the following (case insensitive): name, workAddress, region, username, password
-If a name or names are used, all users whose names contain the input names in order will be returned as matches
Example valid usage: updateClincian 2 region Christchurch

describeUser
This command searches users and displays information about them. To find the id of a user, use the listUsers command.   
The syntax is: describeUser `<id>` OR describeUser `<name>`  
Rules:  
-If an id number is to be used as search criteria, it must be a number that is 0 or larger  
-If a name or names are used, the names must be comma separated without a space around the comma (eg. drew,david)  
-If a name or names are used, all donors whose names contain the input names in order will be returned as matches  
-If there are any spaces in the name, the name must be enclosed in quotation marks (")  
Example valid usage: describeUser "andrew,son"

describeClinician
This command searches clinicians and displays information about them. To find the id of a clinician, use the listClinicians "
command.
The syntax is: describeClinician <id>
Rules:
-If an id number is to be used as search criteria, it must be a number that is 0 or larger
Example valid usage: describeClinician 1

describeOrgans  
This command displays the organs which a donor will donate or has donated. To find the id of a donor, use the list and describe commands.  
The syntax is: describeOrgans `<id>`  
Rules:  
-The id number must be a number that is 0 or larger  
Example valid usage: describeOrgans 4

listUsers
This command lists all information about all users in a table.  
Example valid usage: listUser

listClinicians
This command lists all information about all clinicians in a table.
Example valid usage: listClinicians

listOrgans  
This command displays all of the organs that are currently offered by each donor. Donors that are not yet offering any organs are not shown.  
Example valid usage: listOrgans

import  
This command replaces all donor data in the system with an imported JSON object.  
The syntax is: import [-r] `<filename>`  
Rules:  
-If the -r flag is present, the filepath will be interpreted as relative  
-If the filepath has spaces in it, it must be enclosed with quotation marks (")  
-Forward slashes (/) should be used regardless of operating system. Double backslashes may also be used on Windows  
-The file must be of the same format as those saved from this application  
Example valid usage: import -r ../donor_list_FINAL.txt

save  
This command saves the current donor database to a file in JSON format.  
The syntax is: save [-r] <tye>`<filepath>`  
Rules:  
-If the -r flag is present, the filepath will be interpreted as relative  
-If the filepath has spaces in it, it must be enclosed with quotation marks (")  
-Forward slashes (/) should be used regardless of operating system. Double backslashes may also be used on Windows  
-The <type> argument denotes the type of user to save. This should be either 'users', for regular users, or 'clinicians'.
Example valid usage: save -r "new folder/donors.json"

help  
This command displays information about how to use this program.  
The syntax is: help OR help `<command>`  
Rules:  
-If the command argument is passed, the command must be: add, addOrgan, delete, deleteOrgan, set, describe, describeOrgans, list, listOrgans, import, save, help, or quit.  
Example valid usage: help help

quit  
This command exits the program.  
Example valid usage: quit