# JasonPerdigao_DonutShop

**Project author:** Jason Perdigao

Java Swing + Apache Derby

A donut-shop conversion of the supplied Hot Dog Stand project. The app includes a donut menu, order entry, order history, customer management, and an embedded Derby database.

## Open and run
1. Extract the ZIP to a normal folder.
2. Open the extracted `JasonPerdigao_DonutShop` folder (or its `pom.xml`) in IntelliJ IDEA as a Maven project.
3. Set the Project SDK to JDK 17 or newer and allow Maven to download dependencies (internet is needed on first build).
4. Run `program.Main` from IntelliJ, or run `mvn clean compile exec:java` in a terminal from the project folder.

## Features
- Donut choices: Classic Glazed, Chocolate Frosted, Strawberry Sprinkle, Boston Cream, Maple Glazed, Cinnamon Sugar, Jelly Filled, Cookies & Cream, Old-Fashioned, Blueberry Cake, Apple Fritter, and Dozen Assorted.
- Order ID, customer ID, donut, quantity, date/time, calculated price, and order history.
- Customer create, update, select, and delete operations, including favorite donut.
- Order create, update, select, and delete operations.
- Input validation, duplicate-ID checks, customer/order relationship validation, and delete confirmations.
- Persistent embedded Apache Derby database stored in `oakdonutsdb` beside the working directory.

## Database note
This project uses a new database named `oakdonutsdb` and tables `DONUT_CUSTOMER` and `DONUT_ORDER`, so it does not collide with or alter the original hot-dog database. Prices are stored as integer cents to avoid floating-point rounding errors.
