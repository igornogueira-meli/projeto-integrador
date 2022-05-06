# Spring Bootcamp "Integrative project" by "ITB-Two-Factor"


The goal of this final project is to implement a REST API within the slogan and apply
the contents worked during BOOTCAMP MELI. (Git, Java, Spring, Database,
Quality and safety).


## Describe requirements



```
    ●  Be able to enter a batch of products in the distribution warehouse to record than existence in stock.

    ●  Have the information necessary to understand in which sector the merchandise so that it remains in good condition while in the warehouse and to  so that it can be shown to the employee who is going to look for the product (picking) where it is.
  
    ● Be able to detect if there are products that are about to expire to take some
    measure in this regard (it may be to return them to the Seller, throw them away or carry out
    some specific commercial action to settle them).

    ● To be able to consult the stock, list which products are in which warehouse and data a specific product, also understand in which warehouse it is stored.

    ● Being able to register the purchase order
    
    ● Being able to register Carriers and Shipping Orders for purchases
```
## About CI/CD this project
 We are using github actions is a continuous integration for build project and make tests after a pull request on branch develop. In case of problem look the github Actions .

    IS NOT ALLOWED DIRECT PUSH ON MAIN / DEVELOP


## How to execute 

You run it using the ```java -jar``` or the ```mvn spring-boot:run``` commands.

* Clone this repository 
* Create a db with docker-compose
* Make sure you are using JDK 1.8 and Maven 3.x
* You can build the project and run the tests by running ```mvn clean package```
* Once successfully built, you can run the service by one of these two methods:
```
       docker-compose up 
       mvn spring-boot:run 
```
* Check the stdout on your console to view logs, exceptions  and more 

Once the application runs you should confirm  something like this

```
http:/localhost:8080
```

## About the Service

An online product sales platform wants to improve search options and
filtering your products; For that, I decided to implement a search engine that,
of the options that the user determines, returns the product(s) that he
matches.

## ENDPOINTS 

    When do you run this application you can acess the swagger in your localhost


# About Spring Boot

Spring Boot is an "opinionated" application bootstrapping framework that makes it easy to create new RESTful services (among other types of applications). It provides many of the usual Spring facilities that can be configured easily usually without any XML. 



# Questions and Comments:
     Open a issue on this project 
