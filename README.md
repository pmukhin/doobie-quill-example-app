# Workshop on `doobie` and `cats-effect`
A sample project to show examples of using `doobie`.

## Components
Every component demonstrates a particular idea or a component:

### pp1 
Simple select query, abstract repository algebra, generic effectful implementation, runnable app

### pp2 
Running queries in parallel and combining the results

### pp3 
Update queries 

### pp4
Fluent compile-time query builder using Quill and Doobie 

## Links
- https://tpolecat.github.io/doobie/ - Doobie official website
- https://typelevel.org/cats/ - Cats official website
- https://typelevel.org/cats-effect/ - Cats Effect official website

### Prerequisites:
Run the following query on `mariadb` instance:
```
LOAD DATA INFILE '/usr/data/MOCK_DATA.csv' 
INTO TABLE Products 
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS;
```
