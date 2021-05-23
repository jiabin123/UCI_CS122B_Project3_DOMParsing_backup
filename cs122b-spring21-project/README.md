## CS 122B Project 1 



### Before running the example

#### If you do not have USER `mytestuser` setup in MySQL, follow the below steps to create it:

 - login to mysql as a root user 
    ```
    local> mysql -u root -p
    ```

 - create a test user and grant privileges:
    ```
    mysql> CREATE USER 'mytestuser'@'localhost' IDENTIFIED BY 'My6$Password';
    mysql> GRANT ALL PRIVILEGES ON * . * TO 'mytestuser'@'localhost';
    mysql> quit;
    ```

#### prepare the database 
`moviedb`
 
 
#### depoly on tomcat
after you done the git clone, 

copy your newly built war file:
cp ./target/*.war /var/lib/tomcat9/webapps/

### From Contribution

teammember : zhicheng ding,  jiabin xiong 
workload for both of us is 50 / 50
work very well. 
jiabin xiong takes the movie list part, sort, pagenation, and some html jump funcionality, advanced search and some css 
zhicheng ding takes the login part, payment part. shopping cart and some css


### Demo Video URL
https://drive.google.com/file/d/1vN6hcd4slyx8irepkedvoH5IWRnfeWx9/view?usp=sharing


### MySQL username/password
username: mytestuser
Password: My6$Password
