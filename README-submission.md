# Team Name: 4

# Team Members

Last Name       | First Name      | GitHub User Name
--------------- | --------------- | --------------------
Wolf            | Aria            | ariajw
Coy             | Ernest          | Chromiumz
Velasquez       | Brandon         | BrandonFVelasquez

# Test Results
How many of the dumpfiles matched (using the check-dumpfiles.sh script)?
All of our dumpfiles using the script check-dumpfiles.sh, test0.gbk & test5.gbk, matched.

How many of the query files results matched (using the check-queries.sh script)?
All of our dumpfiles using the script check-queries.sh, test0.gbk & test5.gbk, matched.

# Cache Performance Results
For this test I ran this command: java -jar build/libs/GeneBankCreateBTree.jar --cache=1 --degree=0 --gbkfile=test5.gbk --length=20 --cachesize=100 --debug=0
on the boisestate budgie.
Here are the results:
For the test data `test5.gbk`, how much did a Cache of size 100 improve your performance compared to no cache.
Using a cachesize of 100 for the above command it took ~34 seconds.
For the test data `test5.gbk`, how much did a Cache of size 500 improve your performance compared to no cache? 
Using a cachesize of 500 for the above command, it took ~31 seconds.
For the test data `test5.gbk`, how much did a Cache of size 5000 improve your performance compared to no cache?
Using a cachesize of 5000 for the above command, it took ~16 seconds.

# AWS Notes
It was interesting working with AWS. The setup was a little tricky, especially when trying to clone the project
into AWS. However, it was interesting to work with it. It seemed like a standard console, similar to the boisestate budgie.
However, the difference in speed between the two in insane. When I ran ./create-btrees.sh test5.gbk in my boisestate budgie
it took almost 45 minutes for all the files to finish, while AWS took a significantly less amount of time. I could almost 
compare it to using a cache vs not using a cache.

# Reflection

Provide a reflection by each of the team member (in a separate subsection)

## Reflection (Team member name: Aria Wolf)
## Reflection (Team member name: Ernest Coy)
This project required a lot of high end decisions. Getting stuck on the BTree implemetion due to a simple math error during the disk read led to our BTree implemention citing another source rather than the projects psuedocode, just so we could know where exactly things were going wrong. For this project I was mostly in charge of manipulating data, selecting datastructures and styles to use. I proposed the solution of Regex to read in the test files and the LinkedHashMap for a lighting fast cache as well as an organized method to retrieve data in searches for both databases and BTree files. Brandon ensured that everything was ready to run before I came up with an implementation. When an issue occured I was quick to respond with potential issues and if my proposed solutions didn't fix it I would end up using some debugging techniques to get things under control. Unfortunately we did lack testing in the end. Though in a way we tried to maintain a sense of how our code was functioning by working up to the big tests files one at a time. This for the most part worked and I think it saved us a lot of time by the end which ultimately led to the success of all major tests.
## Reflection (Team member name: Brandon Velasquez)
This project was interesting to complete. Luckily, I had a great team and teammate in Ernest who has had a lot more experience than me coding.
I was able to understand the concepts better just by being able to work with him. This project was very hard, and a lot of different files, managing it, etc.
However, we were able to push through and finish the project and have everything working correctly, all tests pass, everything is created with no problem, etc.
In the project we did get stuck on the cache vs no cache. Our code when we tried to use cache just broke and did not work, but Ernest was able to figure it out
pretty quickly and were able to get back on track with the project. After the createbtree it was pretty quick to complete the project with a bumps that we were able to debug and fix. I think what made the project a little more stressful was having a week of it during finals week and having to worry about finals and study for those etc. Overall, the project was hard but we were able to complete it successfully!

# Additional Notes
Everything will work properly. We moved our gbk files to the main folder because it made running the program a lot easier. When you run ./create-btrees.sh (insert test here) it creates btrees in the folder of CS321-001-s23-team-4/results/ourBtrees. It also creates database files in this foler: CS321-001-s23-team-4/results/ourDatabase. Finally, it also creates the dumpfiles for the program here: CS321-001-s23-team-4/results/dumpfiles. After running create-btrees.sh, you run ./check-dumpfiles.sh *testname* which all pass, and than check-queries.sh *testname* which all pass. When you run the script it will create our query files in 
CS321-001-s23-team-4/data/queries.
