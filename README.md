# TodoServer
This is a TODO server built purely in kotlin using the awesome Ktor and PostgreSQL.
This code is based on the tutorial of the awesome [raywenderlich](https://www.raywenderlich.com/7265034-ktor-rest-api-for-mobile). 
If you have any problems setting up PostgreSQL on windows, then [this](https://medium.com/@aeadedoyin/getting-started-with-postgresql-on-windows-201906131300-ee75f066df78) can help you.
On mac, then [this](https://www.codementor.io/@engineerapart/getting-started-with-postgresql-on-mac-osx-are8jcopb) should help you get started nicely with postgres using brew.

# Configuration error
If you encounter an error while running the server even after using the configuration from the ray wanderlich app above, then make sure you use the following 
```
JDBC_DRIVER=org.postgresql.Driver
JDBC_DATABASE_URL=jdbc:postgresql:todos?user=postgres
SECRET_KEY=898748674728934843
JWT_SECRET=898748674728934843
```

instead of 
```
JDBC_DRIVER=org.postgresql.Driver
JDBC_DATABASE_URL=jdbc:postgresql:todos?user=postgres;
SECRET_KEY=898748674728934843
JWT_SECRET=898748674728934843
```
Note the semi colon in the second configuration.


Enjoy!
