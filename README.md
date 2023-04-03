Crypto Investment

This project is about creating Crypto Recommendation Service that provide us possibility to get some recommendations
like this:
- return a descending sorted list of all the cryptos,
  comparing the normalized range (i.e. (max-min)/min);
- return the oldest/newest/min/max values for a requested
  crypto;
- return the crypto with the highest normalized range for a
  specific day.

The technologies that were used:
Spring Boot 2; Spring Web, Data, Cache; H2 database; Docker.

Build and run:
- Just use the 'run' button and spring boot built the app. You can use this app by this link: http://localhost:8080/swagger-ui/index.html
- Docker:
  - run this command "docker build -t cryptorecommendation:latest .";
  - run this command "docker run -p 8081:8080 cryptorecommendation";
  - open this link "http://localhost:8081/swagger-ui/index.html"