# Design Decisions
09/04 - Decided to return the drug interaction symptoms for males and females when a user's gender wasn't defined

14/04 - Decided to take the longest duration for each symptom if multiple were defined in the API response

17/04 - Decided to try passing medications to the drug interaction API in the opposite direction if an error occurs or the results were undefined the first time. We found that passing certain medications in certain orders would cause internal server errors

17/04 - Revised medication pane layout - the story 18 functionality takes up the leftmost 2/3 of the pane, the active ingredients text is placed in the top right of the pane, and the drug interactions text is placed in the bottom right.

26/04 - Added an abstract TestFX class to remove some duplicate code that had to be put into each TestFX test class. TestFX tests now all extend this class.

30/04 - Decided to use https://www.json-generator.com/ with a special template to generate random test data for our program instead of manually making test users that generally had very little information and were not an accurate representation of how the system will be used.
