# Team Name: 4

# Team Members

Last Name       | First Name      | GitHub User Name
--------------- | --------------- | --------------------
TBD             | TBD             | TBD
TBD             | TBD             | TBD
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
Using a cachesize of 500 for the above command, it took ~16 seconds.

# AWS Notes
It was interesting working with AWS. The setup was a little tricky, especially when trying to clone the project
into AWS. However, it was interesting to work with it. It seemed like a standard console, similar to the boisestate budgie.
However, the difference in speed between the two in insane. When I ran ./create-btrees.sh test5.gbk in my boisestate budgie
it took almost 45 minutes for all the files to finish, while AWS took a significantly less amount of time. I could almost 
compare it to using a cache vs not using a cache.

# Reflection

Provide a reflection by each of the team member (in a separate subsection)

## Reflection (Team member name: )
## Reflection (Team member name: )
## Reflection (Team member name: Brandon Velasquez)
This project was interesting to complete. Luckily, I had a great team and teammate in Ernest who has had a lot more experience than me coding.
I was able to understand the concepts better just by being able to work with him. This project was very hard, and a lot of different files, managing it, etc.
However, we were able to push through and finish the project and have everything working correctly, all tests pass, everything is created with no problem, etc.
In the project we did get stuck on the cache vs no cache. Our code when we tried to use cache just broke and did not work, but Ernest was able to figure it out
pretty quickly and were able to get back on track with the project. After the createbtree it was pretty quick to complete the project with a bumps that we were able to debug and fix. I think what made the project a little more stressful was having a week of it during finals week and having to worry about finals and study for those etc. Overall, the project was hard but we were able to complete it successfully!

# Additional Notes
Everything will work properly. We moved our gbk files to the main folder because it made running the program a lot easier. When you run ./create-btrees.sh (insert test here) it creates btrees in the folder of CS321-001-s23-team-4/results/ourBtrees. It also creates database files in this foler: CS321-001-s23-team-4/results/ourDatabase. Finally, it also creates the dumpfiles for the program here: CS321-001-s23-team-4/results/dumpfiles. After running create-btrees.sh, you run ./check-dumpfiles.sh *testname* which all pass, and than check-queries.sh *testname* which all pass. When you run the script it will create our query files in 
CS321-001-s23-team-4/data/queries.
