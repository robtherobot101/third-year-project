How to run the program:
Navigate a shell (cmd or bash) to the same directory of the jar file, and then execute the command "java -jar app0.0.jar"


Dependencies:
Java Runtime Environment at least version 8


Available commands:

add
This command adds a new donor with a name and date of birth.
The syntax is: add <name> <date of birth>
Rules:
-The names must be comma separated without a space around the comma (eg. Andrew,Neil,Davidson)
-If there are any spaces in the name, the name must be enclosed in quotation marks (")
-The date of birth must be entered in the format: dd/mm/yyyy
Example valid usage: add "Test,User with,SpacesIn Name" 01/05/1994

addOrgan
This command adds one organ to donate to a donor. To find the id of a donor, use the list and describe commands.
The syntax is: addOrgan <id> <organ>
Rules:
-The id number must be a number that is 0 or larger
-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, bone-marrow, or connective-tissue.
Example valid usage: addOrgan 0 skin

delete
This command deletes one donor. To find the id of a donor, use the list and describe commands.
The syntax is: delete <id>
Rules:
-The id number must be a number that is 0 or larger
-You will be asked to confirm that you want to delete this donor
Example valid usage: delete 1

deleteOrgan
This command removes one offered organ from a donor. To find the id of a donor, use the list and describe commands.
The syntax is: deleteOrgan <id> <organ>
Rules:
-The id number must be a number that is 0 or larger
-The organ must be a donatable organ: liver, kidney, pancreas, heart, lung, intestine, cornea, middle-ear, skin, bone-marrow, or connective-tissue.
Example valid usage: deleteOrgan 5 kidney

set
This command sets one attribute (apart from organs to be donated) of a donor. To find the id of a donor, use the list and describe commands. To add or delete organs, instead use the addOrgan and deleteOrgan commands.
The syntax is: set <id> <attribute> <value>
Rules:
-The id number must be a number that is 0 or larger
-The attribute must be one of the following (case insensitive): name, dateOfBirth, dateOfDeath, gender, height, weight, bloodType, region, currentAddress
-If a name or names are used, all donors whose names contain the input names in order will be returned as matches
-The gender must be: male, female, or other
-The bloodType must be: A-, A+, B-, B+, AB-, AB+, O-, or O+
-The height and weight must be numbers that are larger than 0
-The date of birth and date of death values must be entered in the format: dd/mm/yyyy
Example valid usage: set 2 bloodtype ab+

describe
This command searches donors and displays information about them.
The syntax is: describe <id> OR describe <name>
Rules:
-If an id number is to be used as search criteria, it must be a number that is 0 or larger
-If a name or names are used, the names must be comma separated without a space around the comma (eg. drew,david)
-If a name or names are used, all donors whose names contain the input names in order will be returned as matches
-If there are any spaces in the name, the name must be enclosed in quotation marks (")
Example valid usage: describe "andrew,son\'

describeOrgans
This command displays the organs which a donor will donate or has donated. To find the id of a donor, The syntax is: describeOrgans <id>
Rules:
-The id number must be a number that is 0 or larger
Example valid usage: describeOrgans 4

list
This command lists all information about all donors in a table.
Example valid usage: list

listOrgans
This command displays all of the organs that are currently offered by each donor. Donors that are not yet offering any organs are not shown.
Example valid usage: listOrgans

import
This command replaces all donor data in the system with an imported JSON object.
The syntax is: import [-r] <filename>
Rules:
-If the -r flag is present, the filepath will be interpreted as relative
-If the filepath has spaces in it, it must be enclosed with quotation marks (")
-Forward slashes (/) should be used regardless of operating system. Double backslashes may also be used on Windows
-The file must be of the same format as those saved from this application
Example valid usage: import -r ../donor_list_FINAL.txt

save
This command saves the current donor database to a file in JSON format.
The syntax is: save [-r] <filepath>
Rules:
-If the -r flag is present, the filepath will be interpreted as relative
-If the filepath has spaces in it, it must be enclosed with quotation marks (")
-Forward slashes (/) should be used regardless of operating system. Double backslashes may also be used on Windows
Example valid usage: save -r "new folder/donors.json"

help
This command displays information about how to use this program.
The syntax is: help OR help <command>
Rules:
-If the command argument is passed, the command must be: add, addOrgan, delete, deleteOrgan, set, describe, describeOrgans, list, listOrgans, import, save, help, or quit.
Example valid usage: help help

quit
This command exits the program.
Example valid usage: quit