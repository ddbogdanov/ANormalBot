# ANormalBot
A personal project that started out as a proof of concept to myself. I stopped working on this for a while but hopefully I can continue development soon!
ANormalBot is a discord bot that allows users to fetch prices of stocks and cryptocurrencies, periodically gather crypocurrency prices to chart, and even play some music.

## Commands
Abbreviations are used here.

### General

- `!help` - Get a PM containing a list of all commands and their usage.

- `!ping` - pong! See how long it takes for the bot to respond

### Cryptocurrencies

- `!ccp <IDENTIFIER>` - Check the price of a cryptocurrency by its identifier.

- `!cc <IDENTIFIER>` - Genereate a chart based on gathered cryptocurrency data.

- `!gcd <IDENTIFIER>` - Start a task to periodically gather cryptocurrency data for charting.

### Stocks

- `!csp <TICKER SYMBOL>` - Check the price of a stock by its ticker.

### Music

- `!p <URL>` - Search and play content in the requesting users active voice channel by URL.

### Planned for sometime in the future

- `!cw` - Allow users to create and manage a watchlist of cryptocurrencies

- `!sw` - Allow users to create and manage a watchlist of stocks

- `!sc` - Generate a chart using gathered stock data.
 
- `!gsd` - Start a task to periodically gather stock data.

- `!skip` - Allow users to skip currently playing content. Implies existence of content queues.


## Setup
After cloning the repository, you will need to:
- Create a discord bot token [here](https://discord.com/developers/docs/intro)
- Create coinmarketcap and tradier API tokens [here](https://coinmarketcap.com/api/), and [here](https://documentation.tradier.com/brokerage-api)
- Insert the above tokens into their appropriate places
  - Coinmarketcap and Tradier tokens go into their respective commands - CheckCryptoPrice/GatherCryptoData, and CheckStockPrice
  - Discord bot token should go into an application.properties file under `/src/resources/application.properties` <br>
    as `token = <TOKEN VALUE>`
    - You will also need to define `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password`
    - Additionally, you can use `spring.jpa.hibernate.ddl-auto = update` to automatically generate tables and such in your database.
      Keep in mind this requires the appropriate permissions in your database.
- Finally, allow maven to build the projects dependencies, compile, and run!
After adding the bot into your server, you should see it online, and it should respond to any of the above commands. Start with `!help` to get a list of information.

## Technologies Used
- Spring Data
- Java Discord API (JDA)
  
