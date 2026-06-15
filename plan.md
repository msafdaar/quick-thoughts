Make an android app based on cordova/capacitor whatever you feel best. 
Set up the github actions for building that app. i will not install any tools on my local machine and all the building will be done on github
On start, it asks user to select a .md (markdown file) file on their disk (you need to figure out correct way to get permissions for that). once user selects a file, you must remember that file, and retain permission to read/write that file in future even after the app is killed and restarted.

after a file is selected, the app should instert some kind of wrapper to the very end of that file. 
--Draft Start

Draft End--
if there is a better way to wrap, please suggest. 
lets call the text inside this wrapper as 'Draft Text'

The main screen of the app should display this draft text in an editable textarea (or whatever element/component), it should completely ignore whats inside the markdown file and just show the draft part.
if user types anything in the app, edits/updates deletes or whatever, it should be saved inside draft wrapper of the file user selected. it should modify the actual file on storage. 
